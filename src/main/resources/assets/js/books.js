var QUESTION = "?";
var hostURL = "http://localhost:8080"
var apiInsertItem = "/api/books/newitem"
var apiDeleteItem = "/api/books/deleteitem"
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

		books.postBooksItemSuccess = null;
		books.postBooksItemError = null;

		books.setPostBooksItemSuccessCallback = function(callback) {
			books.postBooksItemSuccess = callback;
		}

		books.setPostBooksItemErrorCallback = function(callback) {
			books.postBooksItemError = callback;
		}

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
					if (books.postBooksItemSuccess && typeof(books.postBooksItemSuccess) == "function") {
						books.postBooksItemSuccess(data);
					}
				},
				error: function(jqXHR, textStatus, errorThrown) {
					if (books.postBooksItemError && typeof(books.postBooksItemError) == "function") {
						books.postBooksItemError(textStatus);
					}
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
		
		books.deleteBooksItemSuccess = null;
		books.deleteBooksItemError = null;
		
		books.setDeleteBooksItemSuccessCallback = function(callback) {
			books.deleteBooksItemSuccess = callback;
		}
		
		books.setDeleteBooksItemErrorCallback = function(callback) {
			books.deleteBooksItemError = callback;
		}
		
		books.deleteBooksItem = function(id) {
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
					if (books.deleteBooksItemSuccess && typeof(books.deleteBooksItemSuccess) == "function") {
						books.deleteBooksItemSuccess(data);
					}
				},
				error: function(data) {
					if (books.deleteBooksItemError && typeof(books.deleteBooksItemError) == "function") {
						books.deleteBooksItemError(data);
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
