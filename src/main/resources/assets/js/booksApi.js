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
			var retData = null;
			
			var request = hostURL + apiGetCategories;
			$.ajax({
				type: "GET",
				url: request,
				dataType: "json",
				data: param,
				contentType: 'application/json',
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
			}).then(function(data) {
				console.log(data);
				retData = data;
			});
			
			return retData;
		};

		books.postBooksItem = function(jsonObj) {
			var postURL = hostURL + apiInsertItem;
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
					if (books.getAllBooksItemSuccess && typeof(books.getAllBooksItemSuccess) == "function") {
						books.getAllBooksItemSuccess(data);
					}
				},
				error: function(data) {
					if (books.getAllBooksItemError && typeof(books.getAllBooksItemError) == "function") {
						books.getAllBooksItemError(data);
					}
				}
			}).then(function(data) {
				console.log(data);
				retData = data;
			});

			return retData;
		};

		return books;
	}
};
