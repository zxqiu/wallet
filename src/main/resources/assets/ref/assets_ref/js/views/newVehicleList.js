var QUESTION = "?";
var hostURL = "http://localhost:8080"
var QueryParam = "queryParam="
var apiInsertPost = "/api/insertPost"
var apiGetMakeModel = "/api/getMakeModel"
var hostURL = "http://localhost:8080"

var allMakes = "allMakes"

$( document ).ready(function() {
	var param = new Object();
	param.getAllMakes = "1";
	var paramJSONString = JSON.stringify(param);

	var request = hostURL + apiGetMakeModel + QUESTION + QueryParam + paramJSONString;
        $.ajax({
                type: "GET",
                url: request,
                dataType: "json",
        }).then(function(jsonData) {
                if(jsonData != null) {
			console.log(JSON.stringify(jsonData[allMakes]));
                }
        });

});


function listNewVehicle() {
	var param = new Object();

	param.zipcode = (document.getElementById("vehicleZipcode").value == "") ?
			"all" : document.getElementById("vehicleZipcode").value;
	param.vehicleYears = (document.getElementById("vehicleYear").value == "") ?
			"all" : document.getElementById("vehicleYear").value;
	param.vehicleMaker = (document.getElementById("vehicleMaker").value == "") ?
			"all" : document.getElementById("vehicleMaker").value;
	param.vehicleModel = (document.getElementById("vehicleModel").value == "") ?
			"all" : document.getElementById("vehicleModel").value;
	param.vehicleSalePrice = (document.getElementById("vehicleSalePrice").value == "") ?
			"all" : document.getElementById("vehicleSalePrice").value;

	paramJSONObj = {"ZipCode":param.zipcode,
			"Year":param.vehicleYears,
			"AutoMaker":param.vehicleMaker,
			"AutoModel":param.vehicleModel,
			"Price":param.vehicleSalePrice};
	//paramJSONString = JSON.stringify(paramJSONObj);

	postURL = hostURL + apiInsertPost;
	console.log(postURL);
	//console.log(paramJSONString);

	$.ajax({
		type: "POST",
		url: postURL,
		data: paramJSONObj,//paramJSONString,
		dataType: 'json',
		contentType: 'application/json',
		success: function(data, textStatus, jqXHR) {
			alert("server: " + data.status);
		},
		error: function(jqXHR, textStatus, errorThrown) {
			alert("error with status: " + textStatus);
		}
	});
}
