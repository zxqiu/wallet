gcloud beta functions deploy ocr-translate --stage-bucket wallet-staging --trigger-topic wallet-ocr-translate --entry-point translateText
gcloud beta functions deploy ocr-extract --stage-bucket wallet-staging --trigger-bucket wallet-image --entry-point processImage
gcloud beta functions deploy ocr-save --stage-bucket wallet-staging --trigger-topic wallet-ocr-result --entry-point saveResult
