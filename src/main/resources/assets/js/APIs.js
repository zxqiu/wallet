/**
 * Created by zxqiu on 3/14/17.
 */

var hostURL = window.location.protocol + "//" + window.location.host;
var apiInsertEntry = "/books/insertentry";
var apiDeleteItem = "/books/deleteentry";
var apiGetBooks = "/books/getentries";
var apiInsertCategoryList = "/books/insertcategorylist";
var apiFaceBooksLogin = "/fblogin";

var APIs = {
    createNew: function() {
        var api_ = {};

        api_.postBooksEntrySuccess = null;
        api_.postBooksEntryError = null;

        api_.setPostBooksEntrySuccessCallback = function(callback) {
            api_.postBooksEntrySuccess = callback;
        }

        api_.setPostBooksEntryErrorCallback = function(callback) {
            api_.postBooksEntryError = callback;
        }

        api_.postBooksEntry = function(jsonObj) {
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
                success: function(data, textStatus, jqXHR) {
                    if (api_.postBooksEntrySuccess && typeof(api_.postBooksEntrySuccess) == "function") {
                        api_.postBooksEntrySuccess(data);
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    if (api_.postBooksEntryError && typeof(api_.postBooksEntryError) == "function") {
                        api_.postBooksEntryError(textStatus);
                    }
                }
            });
        };

        api_.getAllBooksEntrySuccess = null;
        api_.getAllBooksEntryError = null;

        api_.setGetAllBooksEntrySuccessCallback = function(callback) {
            api_.getAllBooksEntrySuccess = callback;
        };

        api_.setGetAllBooksEntryErrorCallback = function(callback) {
            api_.getAllBooksEntryError = callback;
        };

        api_.getAllBooksEntry = function(user_id) {
            var param = new Object();
            param.user_id = user_id;
            var retData = null;

            var request = hostURL + apiGetBooks;
            $.ajax({
                type: "GET",
                url: request,
                dataType: "json",
                data: param,
                contentType: 'application/json',
                success: function(data) {
                    if (api_.getAllBooksEntrySuccess && typeof(api_.getAllBooksEntrySuccess) == "function") {
                        api_.getAllBooksEntrySuccess(data);
                    }
                },
                error: function(data) {
                    if (api_.getAllBooksEntryError && typeof(api_.getAllBooksEntryError) == "function") {
                        api_.getAllBooksEntryError(data);
                    }
                }
            }).then(function(data) {
                console.log(data);
                retData = data;
            });

            return retData;
        };

        api_.deleteBooksEntrySuccess = null;
        api_.deleteBooksEntryError = null;

        api_.setDeleteBooksEntrySuccessCallback = function(callback) {
            api_.deleteBooksEntrySuccess = callback;
        }

        api_.setDeleteBooksEntryErrorCallback = function(callback) {
            api_.deleteBooksEntryError = callback;
        }

        api_.deleteBooksEntry = function(id) {
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
                success: function(data) {
                    if (api_.deleteBooksEntrySuccess && typeof(api_.deleteBooksEntrySuccess) == "function") {
                        api_.deleteBooksEntrySuccess(data);
                    }
                },
                error: function(data) {
                    if (api_.deleteBooksEntryError && typeof(api_.deleteBooksEntryError) == "function") {
                        api_.deleteBooksEntryError(data);
                    }
                }
            }).then(function(data) {
                console.log(data);
                retData = data;
            });

            return retData;
        };

        api_.postCategoryListSuccess = null;
        api_.postCategoryListError = null;

        api_.setPostCategoryListSuccessCallback = function(callback) {
            api_.postCategoryListSuccess = callback;
        }

        api_.setPostCatregoryListErrorCallback = function(callback) {
            api_.postCategoryListError = callback;
        }

        api_.postCategoryList = function(jsonObj) {
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
                success: function(data, textStatus, jqXHR) {
                    if (api_.postCategoryListSuccess && typeof(api_.postCategoryListSuccess) == "function") {
                        api_.postCategoryListSuccess(data);
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    if (api_.postCategoryListError && typeof(api_.postCategoryListError) == "function") {
                        api_.postCategoryListError(textStatus);
                    }
                }
            });
        };


        api_.postFBLoginSuccess = null;
        api_.postFBLoginError = null;

        api_.setPostFBLoginSuccessCallback = function(callback) {
            api_.postFBLoginSuccess = callback;
        }

        api_.setPostFBLoginErrorCallback = function(callback) {
            api_.postFBLoginError = callback;
        }

        api_.postFBLogin = function(jsonObj) {
            var postURL = hostURL + apiFaceBooksLogin;
            console.log(postURL);
            console.log(jsonObj);
            JSONString = JSON.stringify(jsonObj);

            $.ajax({
                type: "POST",
                url: postURL,
                data: JSONString,
                dataType: 'json',
                contentType: 'application/json',
                success: function(data, textStatus, jqXHR) {
                    if (api_.postFBLoginSuccess && typeof(api_.postFBLoginSuccess) == "function") {
                        api_.postFBLoginSuccess(data);
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    if (api_.postFBLoginError && typeof(api_.postFBLoginError) == "function") {
                        api_.postFBLoginError(textStatus);
                    }
                }
            });
        };

        return api_;
    }
};
