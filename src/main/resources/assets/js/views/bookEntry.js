var api = APIs.createNew();

var user_id = $("#userID").val();
var bookList = null;

function showCategoryByBook() {
    var bookSelector = $("#bookSelector").find(":selected");
    var book_id = bookSelector.val();
    var book_group_id = "";

    for (var i = 0; bookList && i < bookList.length; i++) {
        if (bookList[i]["id"] == book_id) {
            book_group_id = bookList[i]["group_id"];
        }
    }

    if (book_group_id.length == 0) {
        return;
    }

    var options = $(".category-option");

    for (var i = 0; i < options.length; i++) {
        var tmp = options[i].title.split(":");
        if (tmp.length < 2) {
            continue;
        }
        var id = tmp[0];
        if (id != book_group_id) {
            $(options[i]).prop("disabled", true);
            options[i].style.display = "none";
        } else {
            $(options[i]).prop("disabled", false);
            options[i].style.display = "";
            $("#categorySelector").val(options[i].value).change();
        }
    }
}
function setCategoryListFontColor() {
    var selector = $(".category-list");

    for (var i = 0; i < selector.length; i++) {
        var a = $(selector[i]).find("a");
        var rgb = getBackgroundColorRGB(selector[i]);
        if (a.length == 0 || rgb.length < 3) {
            continue;
        }

        var bright = getBrightness(rgb);
        adjustFontColor(bright, a[0]);
    }
}

function setCategoryOptionFontColor() {
    var selector = $(".category-option");
    for (var i = 0; i < selector.length; i++) {
        var rgb = getBackgroundColorRGB(selector[i]);
        var bright = getBrightness(rgb);

        adjustFontColor(bright, selector[i]);
    }

    var parent = selector.parent()[0];
    var rgb = getBackgroundColorRGB(parent);
    var bright = getBrightness(rgb);

    adjustFontColor(bright, parent);
}

/************************** jquery functions ********************************/
$(document).ready(function () {
    // this is to init category text field
    var selector = $(".category-option");
    if ($("#bookEntryCategoryName").val().length == 0 && selector.length > 0) {
        $('#categorySelector').val($(selector[0]).val()).change();
    }

    api.setGetAllBookSuccessCallback(function (data) {
        bookList = data;
        noBookWarning(bookList);
        $("#categorySelector").removeAttr("disabled");
        showCategoryByBook();
    });
    api.getAllBook(user_id);

    setCategoryListFontColor();
    setCategoryOptionFontColor();
});

$('#categorySelector').on('change', function(){
    var selected = $(this).find(":selected")[0];
    var tmp = selected.title.split(":");

    if (!tmp || tmp.length < 2) {
        console.log("Page Error!");
        return;
    }

    var category_id = tmp[1];

    $('#bookEntryCategoryName').val($(this).val()).change();
    $('#bookEntryCategoryName').text($(this).val());
    $('#bookEntryCategoryID').val(category_id).change();

    var selector = $(".category-option");
    if (selector.length > 0) {
        for (var i = 0; i < selector.length; i++) {
            if ($(selector[i]).val() == $(this).val()) {
                $(this).css("background", selector[i].style.background);
                setCategoryOptionFontColor();
                break;
            }
        }
    }
});

$('#bookSelector').on('change', function(){
    showCategoryByBook();
});

$(function() {
	$('.datepicker').datepicker( {
		format: 'mm/dd/yyyy',
		autoclose: true,
		immediateUpdates: true,
		assumeNearbyYear: true,
		todayHighlight: true
	});
});

$('.date').on('changeDate', function(ev){
	$(this).datepicker('hide');
});

var form = document.getElementById("bookEntryForm");
form.noValidate = true;
$("#bookEntrySubmit").on("click", function() {
    $("#bookEntrySubmit").html("submitted");
    $("#bookEntrySubmit").attr("disabled", true);
    $("#categorySelector").attr("disabled", true);

    var amount = parseFloat($('#bookEntryAmount').val());
    $('#bookEntryAmount').val(amount.toFixed(2));

    for (var i = 0; i < form.length; i++) {
        if (!form[i].checkValidity()) {
            $("input").each(function () {
                console.log($(this).attr("name") + ":" + $(this).val());
                if ($(this).prop("required") == true && $(this).val() == "") {
                    $(this).parent().parent().addClass("has-error");

                    $('#bookEntrySubmit').html("submit");
                    $('#bookEntrySubmit').removeAttr("disabled");
                }
            });
            $("#categorySelector").attr("disabled", false);
            return;
        }
    }

    form.submit();
    $("#categorySelector").attr("disabled", false);
});

$("input").on('input', function() {
	$('#bookEntryForm div').removeClass("has-error");;
});

$(":input").on('change', function(){
	$('#bookEntryForm div').removeClass("has-error");;
});

$('#bookEntryDelete').on('click', function(){
	$('#bookEntryDelete').html("deleted");
	$(this).attr("disabled", true);
});

$("#bookEntryPhoto").on("change", function(e) {
    var canvas = document.getElementById("bookEntryShowPhoto");
    var ctx = canvas.getContext("2d");
    var reader = new FileReader();
    reader.onload = function(event) {
        var img = new Image();
        img.onload = function(){
            var scale = 300 / img.height;
            var height = img.height * scale;
            var width = img.width * scale;
            var size = {width: width, height: height};
            var rotation = 0;
            var deg2Rad=Math.PI/180;

            getOrientation(e.target.files[0], function (orientation) {
                switch(6) {
                    case 2:
                        // horizontal flip
                        ctx.translate(canvas.width, 0);
                        ctx.scale(-1, 1);
                        break;
                    case 3:
                        // 180° rotate left
                        ctx.translate(canvas.width, canvas.height);
                        ctx.rotate(Math.PI);
                        break;
                    case 4:
                        // vertical flip
                        ctx.translate(0, canvas.height);
                        ctx.scale(1, -1);
                        break;
                    case 5:
                        // vertical flip + 90 rotate right
                        ctx.rotate(0.5 * Math.PI);
                        ctx.scale(1, -1);
                        break;
                    case 6:
                        // 90° rotate right
                        //ctx.rotate(0.5 * Math.PI);
                        //ctx.translate(0, -canvas.height);
                        rotation = 90;
                        break;
                    case 7:
                        // horizontal flip + 90 rotate right
                        ctx.rotate(0.5 * Math.PI);
                        ctx.translate(canvas.width, -canvas.height);
                        ctx.scale(-1, 1);
                        break;
                    case 8:
                        // 90° rotate left
                        ctx.rotate(-0.5 * Math.PI);
                        ctx.translate(-canvas.width, 0);
                        break;
                }

                // new size
                var rads = rotation * Math.PI/180;
                var c = Math.cos(rads);
                var s = Math.sin(rads);
                if (s < 0) { s = -s; }
                if (c < 0) { c = -c; }
                size.width = height * s + width * c;
                size.height = height * c + width * s ;

                // draw
                canvas.width = size.width;
                canvas.height = size.height;

                // calculate the center point of the canvas
                var cx = canvas.width / 2;
                var cy = canvas.height / 2;

                // draw in the center of the newly sized canvas
                ctx.translate(cx, cy);
                ctx.rotate(rotation * deg2Rad);
                ctx.drawImage(img, -width / 2, -height / 2, width, height);
            });
            canvas.style.display = "inline";
        };
        img.src = event.target.result;
    };
    /*
    reader.onloadend = function() {
        getOrientation(e.target.files[0], function (orientation) {
            alert(orientation);
            switch(6){
                case 8:
                    ctx.rotate(90*Math.PI/180);
                    break;
                case 3:
                    ctx.rotate(180*Math.PI/180);
                    break;
                case 6:
                    ctx.rotate(-90*Math.PI/180);
                    break;
            }
        });
    };
    */
    reader.readAsDataURL(e.target.files[0]);
});
