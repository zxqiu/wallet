var SPLITER = "!";
var QUESTION = "?";
var QueryParam = "queryParam";
var hostURL = "http://localhost:8080";
var apiGetPost = "/api/books/newitem";

function insertBooks() {
	console.log("insert new books to server start");

	var param = new Object();

	param.user_id = "webuser";
	param.event_time = 19900329;
	param.category = "buy";
	param.amount = 10;
	param.note = "note";
	param.picture_url = "";

	var paramJSONString = JSON.stringify(param);

	var request = hostURL + apiGetPost + QUESTION + QueryParam + "=" + paramJSONString;
	$.ajax({
		type: "POST",
		url: request,
		dataType: "json",
	}).then(function(jsonData) {
		if(jsonData != null) {
			//createPost(jsonData);
			console.log(request);
		}
	});
	console.log("insert new books to server done");
}
