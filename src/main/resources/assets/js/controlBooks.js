var SPLITER = "!";
var QUESTION = "?";
var QueryParam = "queryParam";
var hostURL = "http://localhost:8080";
var apiInsertBooks = "/api/books/newitem";
var apiGetBooks = "/api/books/getbooks";

var postEachLine = 2;
var test_user_cnt = 0;

function insertBooks() {
	console.log("insert new books to server start");

	var param = new Object();

	param.user_id = "webuser";
	param.event_time = 19900329;
	param.category = "buy";
	param.amount = 10 + test_user_cnt++;
	param.note = "note";
	param.picture_url = "";

	var paramJSONString = JSON.stringify(param);

	var request = hostURL + apiInsertBooks + QUESTION + QueryParam + "=" + paramJSONString;
	$.ajax({
		type: "POST",
		url: request,
		dataType: "json",
	}).then(function(jsonData) {
		if(jsonData != null) {
			console.log(request);
		}
	});
	console.log("insert new books to server done");
}


function getBooks() {
	console.log("get books from server start");

	var param = new Object();

	param.user_id = "webuser";

	var paramJSONString = JSON.stringify(param);

	var request = hostURL + apiGetBooks + QUESTION + QueryParam + "=" + paramJSONString;
	$.ajax({
		type: "GET",
		url: request,
		dataType: "json",
	}).then(function(retData) {
		if(retData != null) {
			console.log(retData);
			createPost(retData);
		}
	});
	console.log("get books from server done");
}


function createPost(retData) {
	var cnt = 0, parentDiv, postCol, postRow;
        var jsonArray = retData;//JSON.parse(retData);
	parentDiv = document.getElementById("postList");

	while (parentDiv.firstChild) {
		parentDiv.removeChild(parentDiv.firstChild);
	}

	console.log("received " + jsonArray.length + " items");
	for (var i = 0; i < jsonArray.length; i++) {
                var jsonString = jsonArray[i];
		var jsonObj = JSON.parse(jsonString);

		if (cnt % postEachLine == 0) {
			postRow = document.createElement("div");
			postRow.className = "row";
			parentDiv.appendChild(postRow);
		}

		postCol = document.createElement("div");
		postCol.className = "col-md-" + (12/postEachLine);
		postRow.appendChild(postCol);

		console.log("generating : " + JSON.stringify(jsonObj));
		var post = createSinglePost(jsonObj);

		postCol.appendChild(post);
		
		cnt++;
	}
	console.log(cnt);
}

function createSinglePost(postInfo) {
	var postDiv = document.createElement("div");
	var postA = document.createElement("a");
	//var postImg = document.createElement("img");
	var postSpanDetail = document.createElement("span");
	var postSpanDetailInfo = document.createElement("span");
	var postSpanDetailInfoEventDate = document.createElement("span");
	var postSpanDetailInfoAmount = document.createElement("span");
	var postSpanDetailInfoCategory = document.createElement("span");
	var postSpanDetailInfoNote = document.createElement("span");
	//var postSpanDetailTitle = document.createElement("span");

	postDiv.className = "vehicle-list";
	postA.href = "#";
	//postImg.className = "img-rounded";
	//postImg.style.cssText = "width: 100%;";
	postSpanDetail.className = "vehicle-list-text";
	postSpanDetailInfo.className = "vehicle-list-text-info";
	postSpanDetailInfoEventDate.id = "bookEventDate";
	postSpanDetailInfoAmount.id = "bookAmount";
	postSpanDetailInfoCategory.id = "bookCategory";
	postSpanDetailInfoNote.id = "bookNote";
	//postSpanDetailTitle.className = "vehicle-list-text-title";
	//postSpanDetailTitle.id = "vehicleListTitle";

	postDiv.appendChild(postA);
	//postA.appendChild(postImg);
	postA.appendChild(postSpanDetail);
	postSpanDetail.appendChild(postSpanDetailInfo);
	postSpanDetailInfo.appendChild(postSpanDetailInfoEventDate);
	postSpanDetailInfo.appendChild(postSpanDetailInfoAmount);
	postSpanDetailInfo.appendChild(postSpanDetailInfoCategory);
	postSpanDetailInfo.appendChild(postSpanDetailInfoNote);
	//postSpanDetail.appendChild(postSpanDetailTitle);

	console.log(postInfo);
	//postImg.src = "http://media.caranddriver.com/images/media/51/2019-lamborghini-asterion-concept-top-inline-photo-657624-s-original.jpg"; //postInfo.Image
	//postSpanDetailTitle.innerHTML =
			//postInfo.Year + " " +
			//postInfo.AutoMaker + " " +
			//postInfo.AutoModel;
	postSpanDetailInfoEventDate.innerHTML = postInfo.event_time + "?";
	postSpanDetailInfoAmount.innerHTML = "$" + postInfo.amount + "?";
	postSpanDetailInfoCategory.innerHTML = "category: " + postInfo.category + "?";
	postSpanDetailInfoNote.innerHTML = "note: " + postInfo.note + "?";

	return postDiv;
}


