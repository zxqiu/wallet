/**
 * Created by zxqiu on 3/14/17.
 */

var hostURL = window.location.protocol + "//" + window.location.host;
var apiInsertEntry = "/books/insertentry";
var apiGetPicture = "/books/getpicture";
var apiUploadPicture = "/books/uploadpicture";
var apiDeleteItem = "/books/deleteentry";
var apiGetBookEntries = "/books/getentries";
var apiGetBook = "/books/getbook";
var apiInsertBookList = "/books/insertbooklist";
var apiInsertCategoryList = "/books/insertcategorylist";
var apiFaceBookLogin = "/fblogin";
var apiTinyUrlToShort = "/t/s";

var APIs = {
    createNew: function () {
        var api_ = {};

        api_.postBookEntrySuccess = null;
        api_.postBookEntryError = null;

        api_.setPostBookEntrySuccessCallback = function (callback) {
            api_.postBookEntrySuccess = callback;
        }

        api_.setPostBookEntryErrorCallback = function (callback) {
            api_.postBookEntryError = callback;
        }

        api_.postBookEntry = function (jsonObj) {
            var postURL = hostURL + apiInsertEntry;
            console.log(postURL);
            console.log(jsonObj);
            paramJSONString = JSON.stringify(jsonObj);

            $.ajax({
                type: "POST",
                url: postURL,
                data: paramJSONString,
                dataType: 'json',
                contentType: 'application/json',
                success: function (data, textStatus, jqXHR) {
                    if (api_.postBookEntrySuccess && typeof(api_.postBookEntrySuccess) == "function") {
                        api_.postBookEntrySuccess(data);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (api_.postBookEntryError && typeof(api_.postBookEntryError) == "function") {
                        api_.postBookEntryError(textStatus);
                    }
                }
            });
        };

        api_.getAllBookEntrySuccess = null;
        api_.getAllBookEntryError = null;

        api_.setGetAllBookEntrySuccessCallback = function (callback) {
            api_.getAllBookEntrySuccess = callback;
        };

        api_.setGetAllBookEntryErrorCallback = function (callback) {
            api_.getAllBookEntryError = callback;
        };

        api_.getAllBookEntry = function (user_id) {
            var param = new Object();
            param.user_id = user_id;
            var retData = null;

            var request = hostURL + apiGetBookEntries;
            $.ajax({
                type: "GET",
                url: request,
                dataType: "json",
                data: param,
                contentType: 'application/json',
                success: function (data) {
                    if (api_.getAllBookEntrySuccess && typeof(api_.getAllBookEntrySuccess) == "function") {
                        api_.getAllBookEntrySuccess(data);
                    }
                },
                error: function (data) {
                    if (api_.getAllBookEntryError && typeof(api_.getAllBookEntryError) == "function") {
                        api_.getAllBookEntryError(data);
                    }
                }
            }).then(function (data) {
                console.log(data);
                retData = data;
            });

            return retData;
        };

        api_.deleteBookEntrySuccess = null;
        api_.deleteBookEntryError = null;

        api_.setDeleteBookEntrySuccessCallback = function (callback) {
            api_.deleteBookEntrySuccess = callback;
        }

        api_.setDeleteBookEntryErrorCallback = function (callback) {
            api_.deleteBookEntryError = callback;
        }

        api_.deleteBookEntry = function (id) {
            var param = new Object();
            param.id = id;
            var retData = null;

            var request = hostURL + apiDeleteItem;
            $.ajax({
                type: "GET",
                url: request,
                dataType: "json",
                data: param,
                contentType: 'application/json',
                success: function (data) {
                    if (api_.deleteBookEntrySuccess && typeof(api_.deleteBookEntrySuccess) == "function") {
                        api_.deleteBookEntrySuccess(data);
                    }
                },
                error: function (data) {
                    if (api_.deleteBookEntryError && typeof(api_.deleteBookEntryError) == "function") {
                        api_.deleteBookEntryError(data);
                    }
                }
            }).then(function (data) {
                console.log(data);
                retData = data;
            });

            return retData;
        };

        api_.getAllBookSuccess = null;
        api_.getAllBookError = null;

        api_.setGetAllBookSuccessCallback = function (callback) {
            api_.getAllBookSuccess = callback;
        };

        api_.setGetAllBookErrorCallback = function (callback) {
            api_.getAllBookError = callback;
        };

        api_.getAllBook = function (user_id) {
            var param = new Object();
            param.user_id = user_id;
            var retData = null;

            var request = hostURL + apiGetBook;
            $.ajax({
                type: "GET",
                url: request,
                dataType: "json",
                data: param,
                contentType: 'application/json',
                success: function (data) {
                    if (api_.getAllBookSuccess && typeof(api_.getAllBookSuccess) == "function") {
                        api_.getAllBookSuccess(data);
                    }
                },
                error: function (data) {
                    if (api_.getAllBookError && typeof(api_.getAllBookError) == "function") {
                        api_.getAllBookError(data);
                    }
                }
            }).then(function (data) {
                retData = data;
            });

            return retData;
        };

        api_.postCategoryListSuccess = null;
        api_.postCategoryListError = null;

        api_.setPostCategoryListSuccessCallback = function (callback) {
            api_.postCategoryListSuccess = callback;
        }

        api_.setPostCategoryListErrorCallback = function (callback) {
            api_.postCategoryListError = callback;
        }

        api_.postCategoryList = function (jsonObj) {
            var postURL = hostURL + apiInsertCategoryList;
            console.log(postURL);
            console.log(jsonObj);
            JSONString = JSON.stringify(jsonObj);

            $.ajax({
                type: "POST",
                url: postURL,
                data: JSONString,
                dataType: 'json',
                contentType: 'application/json',
                success: function (data, textStatus, jqXHR) {
                    if (api_.postCategoryListSuccess && typeof(api_.postCategoryListSuccess) == "function") {
                        api_.postCategoryListSuccess(data);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (api_.postCategoryListError && typeof(api_.postCategoryListError) == "function") {
                        api_.postCategoryListError(textStatus);
                    }
                }
            });
        };

        api_.postBookListSuccess = null;
        api_.postBookListError = null;

        api_.setPostBookListSuccessCallback = function (callback) {
            api_.postBookListSuccess = callback;
        }

        api_.setPostBookListErrorCallback = function (callback) {
            api_.postBookListError = callback;
        }

        api_.postBookList = function (jsonObj) {
            var postURL = hostURL + apiInsertBookList;
            console.log(postURL);
            console.log(jsonObj);
            JSONString = JSON.stringify(jsonObj);

            $.ajax({
                type: "POST",
                url: postURL,
                data: JSONString,
                dataType: 'json',
                contentType: 'application/json',
                success: function (data, textStatus, jqXHR) {
                    if (api_.postBookListSuccess && typeof(api_.postBookListSuccess) == "function") {
                        api_.postBookListSuccess(data);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (api_.postBookListError && typeof(api_.postBookListError) == "function") {
                        api_.postBookListError(textStatus);
                    }
                }
            });
        };

        api_.postFBLoginSuccess = null;
        api_.postFBLoginError = null;

        api_.setPostFBLoginSuccessCallback = function (callback) {
            api_.postFBLoginSuccess = callback;
        }

        api_.setPostFBLoginErrorCallback = function (callback) {
            api_.postFBLoginError = callback;
        }

        api_.postFBLogin = function (jsonObj) {
            var postURL = hostURL + apiFaceBookLogin;
            console.log(postURL);
            console.log(jsonObj);
            JSONString = JSON.stringify(jsonObj);

            $.ajax({
                type: "POST",
                url: postURL,
                data: JSONString,
                dataType: 'json',
                contentType: 'application/json',
                success: function (data, textStatus, jqXHR) {
                    if (api_.postFBLoginSuccess && typeof(api_.postFBLoginSuccess) == "function") {
                        api_.postFBLoginSuccess(data);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (api_.postFBLoginError && typeof(api_.postFBLoginError) == "function") {
                        api_.postFBLoginError(textStatus);
                    }
                }
            });
        };

        api_.postTinyUrlToShortSuccess = null;
        api_.postTinyUrlToShortError = null;

        api_.setPostTinyUrlToShortSuccessCallback = function (callback) {
            api_.postTinyUrlToShortSuccess = callback;
        }

        api_.setPostTinyUrlToShortErrorCallback = function (callback) {
            api_.postTinyUrlToShortError = callback;
        }

        api_.postTinyUrlToShort = function (jsonObj) {
            var postURL = hostURL + apiTinyUrlToShort;
            console.log(postURL);
            console.log(jsonObj);
            JSONString = JSON.stringify(jsonObj);

            $.ajax({
                type: "POST",
                url: postURL,
                data: JSONString,
                dataType: 'json',
                contentType: 'application/json',
                success: function (data, textStatus, jqXHR) {
                    if (api_.postTinyUrlToShortSuccess && typeof(api_.postTinyUrlToShortSuccess) == "function") {
                        api_.postTinyUrlToShortSuccess(data);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (api_.postTinyUrlToShortError && typeof(api_.postTinyUrlToShortError) == "function") {
                        api_.postTinyUrlToShortError(textStatus);
                    }
                }
            });
        };

        api_.getBookEntryPictureSuccess = null;
        api_.getBookEntryPictureError = null;

        api_.setGetBookEntryPictureSuccessCallback = function (callback) {
            api_.getBookEntryPictureSuccess = callback;
        }

        api_.setGetBookEntryPictureErrorCallback = function (callback) {
            api_.getBookEntryPictureError = callback;
        }

        api_.getBookEntryPicture = function (user_id, picture_id) {
            var param = new Object();
            param.user_id = user_id;
            param.picture_id = picture_id;
            param.host_url = hostURL;
            var retData = null;

            var request = hostURL + apiGetPicture;
            $.ajax({
                type: "GET",
                url: request,
                dataType: "json",
                data: param,
                contentType: 'application/json',
                success: function (data) {
                    console.log("enter get picture success");
                    if (api_.getBookEntryPictureSuccess && typeof(api_.getBookEntryPictureSuccess) == "function") {
                        api_.getBookEntryPictureSuccess(data);
                    }
                },
                error: function (data) {
                    if (api_.getBookEntryPictureError && typeof(api_.getBookEntryPictureError) == "function") {
                        api_.getBookEntryPictureError(data);
                    }
                }
            }).then(function (data) {
                console.log("enter get picture then");
                retData = data;
            });

            return retData;
            /*
            console.log("enter api getBookEntryPicture");
            var param = new Object();
            param.user_id = user_id;
            param.picture_id = picture_id;
            param.host_url = hostURL;
            var retData = null;

            var request = hostURL + apiGetPicture;
            $.ajax({
                type: "GET",
                url: request,
                dataType: "json",
                data: param,
                contentType: 'multipart/form-data',
                success: function (data) {
                    console.log("enter get image success");
                    if (api_.getBookEntryPictureSuccess && typeof(api_.getBookEntryPictureSuccess) == "function") {
                        api_.getBookEntryPictureSuccess(data);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (api_.getBookEntryPictureError && typeof(api_.getBookEntryPictureError) == "function") {
                        api_.getBookEntryPictureError(textStatus);
                    }
                }
            }).then(function (data) {
                console.log("then enter");
                //console.log(data);
                retData = data;
            });
            return retData;*/

        }

        api_.postBookEntryPictureSuccess = null;
        api_.postBookEntryPictureError = null;

        api_.setPostBookEntryPictureSuccessCallback = function (callback) {
            api_.postBookEntryPictureSuccess = callback;
        }

        api_.setPostBookEntryPictureErrorCallback = function (callback) {
            api_.postBookEntryPictureError = callback;
        }
        api_.postBookEntryPicture = function (name, dataURI) {
            var byteString;
            if (dataURI.split(',')[0].indexOf('base64') >= 0)
                byteString = atob(dataURI.split(',')[1]);
            else
                byteString = decodeURI(dataURI.split(',')[1]);

            // separate out the mime component
            var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

            // write the bytes of the string to a typed array
            var ia = new Uint8Array(byteString.length);
            for (var i = 0; i < byteString.length; i++) {
                ia[i] = byteString.charCodeAt(i);
            }

            var file = new File([ia], name, {type: mimeString});

            var postURL = hostURL + apiUploadPicture;
            //console.log("postURL = "+postURL);
            var formData = new FormData();
            formData.append('role', "form");
            formData.append('action', apiUploadPicture);
            formData.append('method', "post");
            formData.append('hosturl', hostURL);
            formData.append('image', file);

            $.ajax({
                type: "POST",
                url: postURL,
                data: formData,
                processData: false,
                contentType: false,
                success: function (data, textStatus, jqXHR) {
                    console.log("enter post image success");
                    if (api_.postBookEntryPictureSuccess && typeof(api_.postBookEntryPictureSuccess) == "function") {
                        api_.postBookEntryPictureSuccess(data);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (api_.postBookEntryPictureError && typeof(api_.postBookEntryPictureError) == "function") {
                        api_.postBookEntryPictureError(textStatus);
                    }
                }
            });
        };

        return api_;
    }
};
