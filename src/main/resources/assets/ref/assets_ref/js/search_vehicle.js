var SPLITER = "!";
var QUESTION = "?";
var QueryParam = "queryParam"
var hostURL = "http://localhost:8080"
var apiGetPost = "/api/neworder"
var postEachLine = 3;

var vehicleColors = "";
var vehicleYears = "";
var vehicleBodyType = "";
var vehicleDriveTrain = "";
var vehicleTransmission = "";
var vehicleDoors = "";


function searchSubmit() {
	var param = new Object();
	param.product_id = "testproduct";
	param.amount = 1;
	param.address = "addresssssss";
	param.color = "red";
	param.size = "XXXXXXL";
	param.description = "goooooood";

	var paramJSONString = JSON.stringify(param);

	var request = hostURL + apiGetPost + QUESTION + QueryParam + "=" + paramJSONString;
	$.ajax({
		type: "GET",
		url: request,
		dataType: "json",
	}).then(function(jsonData) {
		if(jsonData != null) {
			//createPost(jsonData);
		}
	});
}

function createPost(retData) {
	var key, cnt = 0, parentDiv, postCol, postRow;
	parentDiv = document.getElementById("postList");

	while (parentDiv.firstChild) {
		parentDiv.removeChild(parentDiv.firstChild);
	}

	for (key in retData) {
		if (key.substring(0, 4) == "Post") {
			console.log(key);

			if (cnt % postEachLine == 0) {
				postRow = document.createElement("div");
				postRow.className = "row";
				parentDiv.appendChild(postRow);
			}

			postCol = document.createElement("div");
			postCol.className = "col-md-" + (12/postEachLine);
			postRow.appendChild(postCol);

			var post = createSinglePost(retData[key]);

			postCol.appendChild(post);
			
			cnt++;
		}
	}
	console.log(cnt);
}

function createSinglePost(postInfo) {
	var postDiv = document.createElement("div");
	var postA = document.createElement("a");
	var postImg = document.createElement("img");
	var postSpanDetail = document.createElement("span");
	var postSpanDetailInfo = document.createElement("span");
	var postSpanDetailInfoPrice = document.createElement("span");
	var postSpanDetailInfoMileage = document.createElement("span");
	var postSpanDetailInfoExColor = document.createElement("span");
	var postSpanDetailInfoInColor = document.createElement("span");
	var postSpanDetailTitle = document.createElement("span");

	postDiv.className = "vehicle-list";
	postA.href = "#";
	postImg.className = "img-rounded";
	postImg.style.cssText = "width: 100%;";
	postSpanDetail.className = "vehicle-list-text";
	postSpanDetailInfo.className = "vehicle-list-text-info";
	postSpanDetailInfoPrice.id = "vehicleListPrice";
	postSpanDetailInfoMileage.id = "vehicleListMileage";
	postSpanDetailInfoExColor.id = "vehicleListExteriorColor";
	postSpanDetailInfoInColor.id = "vehicleListInteriorColor";
	postSpanDetailTitle.className = "vehicle-list-text-title";
	postSpanDetailTitle.id = "vehicleListTitle";

	postDiv.appendChild(postA);
	postA.appendChild(postImg);
	postA.appendChild(postSpanDetail);
	postSpanDetail.appendChild(postSpanDetailInfo);
	postSpanDetailInfo.appendChild(postSpanDetailInfoPrice);
	postSpanDetailInfo.appendChild(postSpanDetailInfoMileage);
	postSpanDetailInfo.appendChild(postSpanDetailInfoExColor);
	postSpanDetailInfo.appendChild(postSpanDetailInfoInColor);
	postSpanDetail.appendChild(postSpanDetailTitle);

	console.log(JSON.stringify(postInfo));
	postImg.src = "http://media.caranddriver.com/images/media/51/2019-lamborghini-asterion-concept-top-inline-photo-657624-s-original.jpg"; //postInfo.Image
	postSpanDetailTitle.innerHTML =
			postInfo.Year + " " +
			postInfo.AutoMaker + " " +
			postInfo.AutoModel;
	postSpanDetailInfoPrice.innerHTML = "$" + postInfo.Price;
	postSpanDetailInfoMileage.innerHTML = postInfo.MileAge + " mi";
	postSpanDetailInfoExColor.innerHTML = "exterior color: " + postInfo.ColorExterior;
	postSpanDetailInfoInColor.innerHTML = "interior color: " + postInfo.ColorInterior;

	return postDiv;
}


function getWatValFromBtnGroup(btnPressed, retVal, idAll, idAllSel, btnSel) {
	var hasAnyActive = 0;

	if ($(btnPressed).attr('id') == idAll) {
		$(btnPressed).addClass('active').siblings().removeClass('active');
	} else {
		if ($(btnPressed).hasClass('active')) {
			$(btnPressed).removeClass('active');
		} else {
			$(btnPressed).addClass('active');
			$(btnPressed).siblings(idAllSel).removeClass('active');
		}
	}

	retVal = "";
	$(btnSel).each(function(i, btn){
		if($(btn).hasClass('active')){
			vehicleColors += SPLITER + $(btn).data('wat');
			hasAnyActive = 1;
		}
	});

	if (hasAnyActive == 0) {
		$(btnPressed).siblings(idAllSel).addClass('active');
	}
}

$('#vehicleColor button').click(function() {
	getWatValFromBtnGroup(this, vehicleColors, 'vehicleColors-all', '#vehicleColors-all', '#vehicleColors button.btn');
});


$( document ).ready(function() {
	var currentYear = new Date().getFullYear();
	document.getElementById("vehicleYear-cur-0").innerHTML = currentYear;
	document.getElementById("vehicleYear-cur-1").innerHTML = currentYear - 1;
	document.getElementById("vehicleYear-cur-2").innerHTML = currentYear - 2;
	document.getElementById("vehicleYear-cur-3").innerHTML = currentYear - 3;
	document.getElementById("vehicleYear-cur-4").innerHTML = currentYear - 4;
	document.getElementById("vehicleYear-cur-5").innerHTML = currentYear - 5;
});

$('#vehicleYear button').click(function() {
	var hasAnyActive = 0;

	if ($(this).attr('id') == 'vehicleYear-all') {
		$(this).addClass('active').siblings().removeClass('active');
	} else {
		if ($(this).hasClass('active')) {
			$(this).removeClass('active');
		} else {
			$(this).addClass('active');
			$(this).siblings('#vehicleYear-all').removeClass('active');
		}
	}

	vehicleYears = "";
	$('#vehicleYear button.btn').each(function(i, btn){
		if($(btn).hasClass('active')){
			vehicleYears += SPLITER + $(btn).text();
			hasAnyActive = 1;
		}
	});

	if (hasAnyActive == 0) {
		$(this).siblings('#vehicleYear-all').addClass('active');
	}
});


$('#vehicleBodyType button').click(function() {
	getWatValFromBtnGroup(this, vehicleBodyType, 'vehicleBodyType-all', '#vehicleBodyType-all', '#vehicleBodyType button.btn');
});


$('#vehicleDriveTrain button').click(function() {
	getWatValFromBtnGroup(this, vehicleDriveTrain, 'vehicleDriveTrain-all', '#vehicleDriveTrain-all', '#vehicleDriveTrain button.btn');
});


$('#vehicleTransmission button').click(function() {
	getWatValFromBtnGroup(this, vehicleTransmission, 'vehicleTransmission-all', '#vehicleTransmission-all', '#vehicleTransmission button.btn');
});


$('#vehicleDoors button').click(function() {
	getWatValFromBtnGroup(this, vehicleDoors, 'vehicleDoors-all', '#vehicleDoors-all', '#vehicleDoors button.btn');
});
