var QUESTION = "?";
var hostURL = "http://localhost:8080"
var QueryParam = "queryParam="
var apiInsertItem = "/api/books/newitem"
var apiGetCategories = "/api/books/getcategories"
var apiGetBooks = "/api/books/getbooks";

var Books = {
	createNew: function() {
		var books = {};

		books.supername = function(data) {};
		books.getCategoriesSuccess = function(data) {};
		//books.getCategoriesError = null;
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
				//success: function(data) { books.getCategoriesSuccess(data); },
				//error: function(data) { books.getCategoriesError(data); },
			}).then(function(jsonData) {
				console.log(JSON.stringify(jsonData));
				if (jsonData) {
					books.getCategoriesSuccess(jsonData);
				}
				retData = jsonData;
			});
			
			return retData;
		};

		books.postBooksItem = function() {
			var postURL = hostURL + apiInsertItem;
			console.log(postURL);
			console.log(item);
			
			$.ajax({
				type: "POST",
				url: postURL,
				data: item,
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
		books.getAllBooksItem = function(user_id) {
			var jsonArray = null;
			var request = hostURL + apiGetBooks + QUESTION + QueryParam + "=" + paramJSONString;
			$.ajax({
				type: "GET",
				url: request,
				dataType: "json",
				async: false,
			        success: books.getAllBooksItemSuccess,
				error: books.getAllBooksItemError,
			}).then(function(retData) {
				console.log(retData);
				jsonArray = retData;
			});

			return jsonArray;
		};

		return books;
	}
};
