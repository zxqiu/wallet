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
        if (options[i].title != book_group_id) {
            $(options[i]).hide();
        } else {
            $(options[i]).show();
        }
    }
}

/************************** jquery functions ********************************/
$(document).ready(function () {
    // this is to init category text field
    var selector = $(".category-option");
    if ($("#bookEntryCategory").val().length == 0 && selector.length > 0) {
        $('#categorySelector').val($(selector[0]).val()).change();
    }

    api.setGetAllBookSuccessCallback(function (data) {
        bookList = data;
        $("#categorySelector").removeAttr("disabled");
        showCategoryByBook();
    });
    api.getAllBook(user_id);
});

$('#categorySelector').on('change', function(){
    $('#bookEntryCategory').val($(this).val()).change();
    $('#bookEntryCategory').text($(this).val());

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
