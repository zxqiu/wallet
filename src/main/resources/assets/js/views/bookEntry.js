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

function compress(source_img_obj, quality, maxWidth, output_format) {
    var mime_type = "image/jpeg";
    if (typeof output_format !== "undefined" && output_format == "png") {
        mime_type = "image/png";
    }

    maxWidth = maxWidth || 1000;
    var natW = source_img_obj.naturalWidth;
    var natH = source_img_obj.naturalHeight;
    var ratio = natH / natW;
    if (natW > maxWidth) {
        natW = maxWidth;
        natH = ratio * maxWidth;
    }

    var cvs = document.createElement('canvas');
    cvs.width = natW;
    cvs.height = natH;

    var ctx = cvs.getContext("2d").drawImage(source_img_obj, 0, 0, natW, natH);
    var newImageData = cvs.toDataURL(mime_type, quality / 100);
    var result_image_obj = new Image();
    result_image_obj.src = newImageData;
    return result_image_obj;
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

$('#categorySelector').on('change', function () {
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

$('#bookSelector').on('change', function () {
    showCategoryByBook();
});

$(function () {
    $('.datepicker').datepicker({
        format: 'mm/dd/yyyy',
        autoclose: true,
        immediateUpdates: true,
        assumeNearbyYear: true,
        todayHighlight: true
    });
});

$('.date').on('changeDate', function (ev) {
    $(this).datepicker('hide');
});

var form = document.getElementById("bookEntryForm");
form.noValidate = true;
$("#bookEntrySubmit").on("click", function () {
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


$("input").on('input', function () {
    $('#bookEntryForm div').removeClass("has-error");
    ;
});

$(":input").on('change', function () {
    $('#bookEntryForm div').removeClass("has-error");
    ;
});

$('#bookEntryDelete').on('click', function () {
    $('#bookEntryDelete').html("deleted");
    $(this).attr("disabled", true);
});

$("#bookEntryPhoto").on("change", function (e) {
    var canvas = document.getElementById("bookEntryShowPhoto");
    var ctx = canvas.getContext("2d");
    ctx.clearRect(-canvas.width/2, -canvas.height/2, canvas.width, canvas.height);
    canvas.width = 0;
    canvas.height = 0;
    var reader = new FileReader();
    reader.onload = function (event) {
        var img = new Image();
        img.onload = function () {
            //compress(img, 30, 300, "jpg");
            var scale = 300 / img.height;
            var height = img.height * scale;
            var width = img.width * scale;
            var size = {width: width, height: height};
            var rotation = 0;
            var deg2Rad = Math.PI / 180;

            getOrientation(e.target.files[0], function (orientation) {
                switch (orientation) {
                    case 3:
                        // 180° rotate left
                        rotation = 180;
                        break;
                    case 6:
                        // 90° rotate right
                        rotation = 90;
                        break;
                    case 8:
                        // 90° rotate left
                        rotation = -90;
                        break;
                }

                // new size
                var rads = rotation * Math.PI / 180;
                var c = Math.cos(rads);
                var s = Math.sin(rads);
                if (s < 0) {
                    s = -s;
                }
                if (c < 0) {
                    c = -c;
                }
                size.width = height * s + width * c;
                size.height = height * c + width * s;

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

        api.postBookEntryPicture(e.target.files[0]);
    };
    reader.readAsDataURL(e.target.files[0]);
});
