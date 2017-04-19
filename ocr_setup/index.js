/**
 * Copyright 2016, Google, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

// [START functions_ocr_setup]
const config = require('./config.json');

// Get a reference to the Pub/Sub component
const pubsub = require('@google-cloud/pubsub')();
// Get a reference to the Cloud Storage component
const storage = require('@google-cloud/storage')();
// Get a reference to the Cloud Vision API component
const vision = require('@google-cloud/vision')();
// Get a reference to the Translate API component
const translate = require('@google-cloud/translate')();
// [END functions_ocr_setup]

const querystring = require('querystring');
const http = require('http');
const fs = require('fs');

// [START functions_ocr_publish]
/**
 * Publishes the result to the given pubsub topic and returns a Promise.
 *
 * @param {string} topicName Name of the topic on which to publish.
 * @param {object} data The message data to publish.
 */
function publishResult (topicName, data) {
  return pubsub.topic(topicName).get({ autoCreate: true })
    .then(([topic]) => topic.publish(data));
}
// [END functions_ocr_publish]

function getOcrAmountFromFile(text) {
    const buf = Buffer.from(text, 'utf8');
    var s = buf.toString('utf8');
    console.log(`s ${s}`);

    /* Find the last index of 'tax' */
    const taxReg = new RegExp(/[tT][aA][xX](?![\s\S]*[tT][aA][xX])/);
    var r = s.match(taxReg);
    if (r) {
        /* trim the string to after 'tax' */
        s = s.substring(r.index + 3);
    }

    /* Find first index of '*' */
    const starReg = new RegExp(/\*/);
    r = s.match(starReg);
    if (r) {
        s = s.substring(0, r.index);
    }

    const totalReg = new RegExp(/[tT][oO][tT][aA][lL]/);
    r = s.match(totalReg);
    if (r) {
        s = s.substring(r.index + 5);
    }

    //console.log(`s 33333333333333333333333333 ${s}`);
    const dotReg = new RegExp(/\./);
    var values = [];
    while (r = s.match(dotReg)) {
        var i, start = -1, end = -1;
        for (i = r.index - 1; i >= 0; i--) {
            var c = s.charAt(i);
                console.log(`start s.charAt(i) i = ${i} s.charAt(i) = ${c}`);
            if (c < '0' || c > '9') {
                start = i + 1;
                break;
            }
        }

        for (i = r.index + 2; i < s.length; i++) {
            var c = s.charAt(i);
            console.log(`end s.charAt(i) i = ${i} s.charAt(i) = ${c}`);
            if (c < '0' || c > '9') {
                end = i;
                break;
            }
        }
        if (start >= 0 && end >= 0) {
            var value = parseFloat(s.substring(start, end+1));
            if (!isNaN(value)) {
                values.push(value);
            }
            console.log(`value = ${value} substring = ${s.substring(start, end+1)}`);
        }

        if (r.index == s.length - 1)
            break;

        s = s.substring(r.index + 1);
    }

    var max = 0;
    if (values.length > 0) {
        max = Math.max(...values);
    }

    return max;
}

function postTranslateResult(filename, text, ocrAmount) {
    const nameBuf = Buffer.from(filename, 'utf8');

    const delim = '#';
    const userIdx = nameBuf.indexOf(delim);
    var user = nameBuf.slice(0, userIdx);
    var buf = nameBuf.slice(userIdx + 1);
    const urlIdx = buf.indexOf(delim);
    var url = buf.slice(0, urlIdx);
    buf = buf.slice(urlIdx + 1);
    const pictureTs = buf;

    const post_data = querystring.stringify({
        'compilation_level': 'ADVANCED_OPTIMIZATIONS',
        'output_format': 'json',
        'user_id': user.toString('utf8'),
        'picture_timestamp': pictureTs.toString('utf8'),
        'amount': ocrAmount,
    });

    const post_options = {
        host: url.toString('utf8'),
        path: '/books/postocramount',
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Content-Length': Buffer.byteLength(post_data)
        }
    };
    const post_req = http.request(post_options, function (res) {
        res.setEncoding('utf8');
        res.on('data', function (chunk) {
            console.log('Response: ' + chunk);
        });
    });

    post_req.write(post_data);
    post_req.end();
}


// [START functions_ocr_detect]
/**
 * Detects the text in an image using the Google Vision API.
 *
 * @param {object} file Cloud Storage File instance.
 * @returns {Promise}
 */
function detectText (file) {
  let text;

  console.log(`Looking for text in image ${file.name}`);
  return vision.detectText(file)
    .then(([_text]) => {
      if (Array.isArray(_text)) {
        text = _text[0];
      } else {
        text = _text;
      }
      console.log(`Extracted text from image (${text.length} chars)`);
      return translate.detect(text);
    })
    .then(([detection]) => {
      if (Array.isArray(detection)) {
        detection = detection[0];
      }
      console.log(`Detected language "${detection.language}" for ${file.name}`);

      // Submit a message to the bus for each language we're going to translate to
      const tasks = config.TO_LANG.map((lang) => {
        let topicName = config.TRANSLATE_TOPIC;
        if (detection.language === lang) {
          topicName = config.RESULT_TOPIC;
        }
        const messageData = {
          text: text,
          filename: file.name,
          lang: lang,
          from: detection.language
        };

        return publishResult(topicName, messageData);
      });

      return Promise.all(tasks);
    });
}
// [END functions_ocr_detect]

// [START functions_ocr_rename]
/**
 * Appends a .txt suffix to the image name.
 *
 * @param {string} filename Name of a file.
 * @param {string} lang Language to append.
 * @returns {string} The new filename.
 */
function renameImageForSave (filename, lang) {
  return `${filename}_to_${lang}.txt`;
}
// [END functions_ocr_rename]

// [START functions_ocr_process]
/**
 * Cloud Function triggered by Cloud Storage when a file is uploaded.
 *
 * @param {object} event The Cloud Functions event.
 * @param {object} event.data A Google Cloud Storage File object.
 */
exports.processImage = function processImage (event) {
  let file = event.data;

  return Promise.resolve()
    .then(() => {
      if (file.resourceState === 'not_exists') {
        // This was a deletion event, we don't want to process this
        return;
      }

      if (!file.bucket) {
        throw new Error('Bucket not provided. Make sure you have a "bucket" property in your request');
      }
      if (!file.name) {
        throw new Error('Filename not provided. Make sure you have a "name" property in your request');
      }

      file = storage.bucket(file.bucket).file(file.name);

      return detectText(file);
    })
    .then(() => {
      console.log(`File ${file.name} processed.`);
    });
};
// [END functions_ocr_process]

// [START functions_ocr_translate]
/**
 * Translates text using the Google Translate API. Triggered from a message on
 * a Pub/Sub topic.
 *
 * @param {object} event The Cloud Functions event.
 * @param {object} event.data The Cloud Pub/Sub Message object.
 * @param {string} event.data.data The "data" property of the Cloud Pub/Sub
 * Message. This property will be a base64-encoded string that you must decode.
 */
exports.translateText = function translateText (event) {
  const pubsubMessage = event.data;
  const jsonStr = Buffer.from(pubsubMessage.data, 'base64').toString();
  const payload = JSON.parse(jsonStr);

  return Promise.resolve()
    .then(() => {
      if (!payload.text) {
        throw new Error('Text not provided. Make sure you have a "text" property in your request');
      }
      if (!payload.filename) {
        throw new Error('Filename not provided. Make sure you have a "filename" property in your request');
      }
      if (!payload.lang) {
        throw new Error('Language not provided. Make sure you have a "lang" property in your request');
      }

      const options = {
        from: payload.from,
        to: payload.lang
      };

      console.log(`Translating text into ${payload.lang}`);
      return translate.translate(payload.text, options);
    })
    .then(([translation]) => {
      const messageData = {
        text: translation,
        filename: payload.filename,
        lang: payload.lang
      };

      return publishResult(config.RESULT_TOPIC, messageData);
    })
    .then(() => {
      console.log(`Text translated to ${payload.lang}`);
    });
};
// [END functions_ocr_translate]

// [START functions_ocr_save]
/**
 * Saves the data packet to a file in GCS. Triggered from a message on a Pub/Sub
 * topic.
 *
 * @param {object} event The Cloud Functions event.
 * @param {object} event.data The Cloud Pub/Sub Message object.
 * @param {string} event.data.data The "data" property of the Cloud Pub/Sub
 * Message. This property will be a base64-encoded string that you must decode.
 */
exports.saveResult = function saveResult (event) {
  const pubsubMessage = event.data;
  const jsonStr = Buffer.from(pubsubMessage.data, 'base64').toString();
  const payload = JSON.parse(jsonStr);

  return Promise.resolve()
    .then(() => {
      if (!payload.text) {
        throw new Error('Text not provided. Make sure you have a "text" property in your request');
      }
      if (!payload.filename) {
        throw new Error('Filename not provided. Make sure you have a "filename" property in your request');
      }
      if (!payload.lang) {
        throw new Error('Language not provided. Make sure you have a "lang" property in your request');
      }

      console.log(`Received request to save file ${payload.filename}`);

      const bucketName = config.RESULT_BUCKET;
      const filename = renameImageForSave(payload.filename, payload.lang);
      const file = storage.bucket(bucketName).file(filename);

      var ocrAmount = getOcrAmountFromFile(payload.text);

      postTranslateResult(payload.filename, payload.text, ocrAmount);

      console.log(`Saving result to ${filename} in bucket ${bucketName}`);

      return file.save(payload.text);
    })
    .then(() => {
      console.log(`File saved.`);
    });
};
// [END functions_ocr_save]
