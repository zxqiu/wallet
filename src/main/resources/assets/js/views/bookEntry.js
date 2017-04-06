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
