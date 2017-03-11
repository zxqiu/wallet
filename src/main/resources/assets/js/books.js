var QUESTION = "?";
var hostURL = window.location.protocol + "//" + window.location.host;
var apiInsertEntry = "/books/insertentry";
var apiInsertEntryView = "/books/entry";
var apiDeleteItem = "/books/deleteentry";
var apiGetCategories = "/books/getcategories";
var apiGetBooks = "/books/getentries";

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

		books.postBooksEntrySuccess = null;
		books.postBooksEntryError = null;

		books.setPostBooksEntrySuccessCallback = function(callback) {
			books.postBooksEntrySuccess = callback;
		}

		books.setPostBooksEntryErrorCallback = function(callback) {
			books.postBooksEntryError = callback;
		}

		books.postBooksEntry = function(jsonObj) {
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
					if (books.postBooksEntrySuccess && typeof(books.postBooksEntrySuccess) == "function") {
						books.postBooksEntrySuccess(data);
					}
				},
				error: function(jqXHR, textStatus, errorThrown) {
					if (books.postBooksEntryError && typeof(books.postBooksEntryError) == "function") {
						books.postBooksEntryError(textStatus);
					}
				}
			});
		};

		books.getAllBooksEntrySuccess = null;
		books.getAllBooksEntryError = null;
		
		books.setGetAllBooksEntrySuccessCallback = function(callback) {
			books.getAllBooksEntrySuccess = callback;
		};
		
		books.setGetAllBooksEntryErrorCallback = function(callback) {
			books.getAllBooksEntryError = callback;
		};
		
		books.getAllBooksEntry = function(user_id) {
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
					if (books.getAllBooksEntrySuccess && typeof(books.getAllBooksEntrySuccess) == "function") {
						books.getAllBooksEntrySuccess(data);
					}
				},
				error: function(data) {
					if (books.getAllBooksEntryError && typeof(books.getAllBooksEntryError) == "function") {
						books.getAllBooksEntryError(data);
					}
				}
			}).then(function(data) {
				console.log(data);
				retData = data;
			});

			return retData;
		};
		
		books.deleteBooksEntrySuccess = null;
		books.deleteBooksEntryError = null;
		
		books.setDeleteBooksEntrySuccessCallback = function(callback) {
			books.deleteBooksEntrySuccess = callback;
		}
		
		books.setDeleteBooksEntryErrorCallback = function(callback) {
			books.deleteBooksEntryError = callback;
		}
		
		books.deleteBooksEntry = function(id) {
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
					if (books.deleteBooksEntrySuccess && typeof(books.deleteBooksEntrySuccess) == "function") {
						books.deleteBooksEntrySuccess(data);
					}
				},
				error: function(data) {
					if (books.deleteBooksEntryError && typeof(books.deleteBooksEntryError) == "function") {
						books.deleteBooksEntryError(data);
					}
				}
			}).then(function(data) {
				console.log(data);
				retData = data;
			});

			return retData;
		};

		books.insertBooksEntryView = function () {
			jQuery.get(apiInsertEntryView, function (data) {
				document.write(data);
			});
		};

		return books;
	}
};
