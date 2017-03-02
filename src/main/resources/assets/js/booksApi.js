var QUESTION = "?";
var hostURL = "http://localhost:8080"
var QueryParam = "queryParam="
var apiInsertItem = "/api/books/newitem"
var apiGetCategories = "/api/books/getcategories"
var apiGetBooks = "/api/books/getbooks";

var Books = {
	createNew: function() {
		var books = {};

		books.getCategoriesSuccess;
		books.getCategoriesError;
		
		books.setGetCategoriesSuccessCallback = function(callback) {
			books.getCategoriesSuccess = callback;
		};
		
		books.setGetCategoriesErrorCallback = function(callback) {
			books.getCategoriesError = callback;
		};
		
		books.getCategoriesAsync = function(user_id) {
			var param = new Object();
			param.user_id = user_id;
			var paramJSONString = JSON.stringify(param);
			var retData = null;
			
			var request = hostURL + apiGetCategories + QUESTION + QueryParam + paramJSONString;
			$.ajax({
				type: "GET",
				url: request,
				dataType: "json",
				success: function(data) {
					if (books.getCategoriesSuccess && typeof(books.getCategoriesSuccess) == "function") {
						books.getCategoriesSuccess(data);
					}
				},
				error: function(data) {
					if (books.getCategoriesError && typeof(books.getCategoriesError) == "function") {
						books.getCategoriesError(data);
					}
				},
			}).then(function(jsonData) {
				console.log(JSON.stringify(jsonData));
				retData = jsonData;
			});
			
			return retData;
		};

		books.postBooksItem = function(jsonObj) {
			var postURL = hostURL + apiInsertItem;
			console.log(postURL);
			console.log(jsonObj);
			
			$.ajax({
				type: "POST",
				url: postURL,
				data: jsonObj,
				dataType: 'json',
				contentType: 'application/json',
				success: function(data, textStatus, jqXHR) {
					alert("success: " + data.message);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					alert("error with status: " + textStatus);
				}
			});
		};

		books.getAllBooksItemSuccess = null;
		books.getAllBooksItemError = null;
		
		books.setGetAllBooksItemSuccessCallback = function(callback) {
			books.getAllBooksItemSuccess = callback;
		};
		
		books.setGetAllBooksItemErrorCallback = function(callback) {
			books.getAllBooksItemError = callback;
		};
		
		books.getAllBooksItem = function(user_id) {
			var getUrl = hostURL + apiGetBooks;
			var jsonArray = null;
			var param = new Object();
			param.user_id = user_id;
			
			$.ajax({
				type: "GET",
				url: getUrl,
				dataType: "json",
				data: param,
				contentType: 'application/json',
				success: function(data) {
					if (books.getAllBooksItemSuccess && typeof(books.getAllBooksItemSuccess) == "function") {
						books.getAllBooksItemSuccess(data);
					}
				},
				error: function(data) {
					if (books.getAllBooksItemError && typeof(books.getAllBooksItemError) == "function") {
						books.getAllBooksItemError(data);
					}
				}
			}).then(function(retData) {
				console.log(retData);
				jsonArray = retData;
			});

			return jsonArray;
		};

		return books;
	}
};
