/************************** jquery functions ********************************/
$(document).ready(function () {
    // this is to init category text field
    var selector = $(".category-option");
    if ($("#booksEntryCategory").val().length == 0 && selector.length > 0) {
        $('#categorySelector').val($(selector[0]).val()).change();
    } else {
        $('#categorySelector').val($("#booksEntryCategory").val()).change();
    }
});

$('#categorySelector').on('change', function(){
    $('#booksEntryCategory').val($(this).val()).change();
    $('#booksEntryCategory').text($(this).val());

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

var form = document.getElementById("booksEntryForm");
form.noValidate = true;
$("#booksEntrySubmit").on("click", function() {
    $("#booksEntrySubmit").html("submitted");
    $("#booksEntrySubmit").attr("disabled", true);
    $("#categorySelector").attr("disabled", true);

    var amount = parseFloat($('#booksEntryAmount').val());
    $('#booksEntryAmount').val(amount.toFixed(2));

    for (var i = 0; i < form.length; i++) {
        if (!form[i].checkValidity()) {
            $("input").each(function () {
                console.log($(this).attr("name") + ":" + $(this).val());
                if ($(this).prop("required") == true && $(this).val() == "") {
                    $(this).parent().parent().addClass("has-error");

                    $('#booksEntrySubmit').html("submit");
                    $('#booksEntrySubmit').removeAttr("disabled");
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
	$('#booksEntryForm div').removeClass("has-error");;
});

$(":input").on('change', function(){
	$('#booksEntryForm div').removeClass("has-error");;
});

$('#booksEntryDelete').on('click', function(){
	$('#booksEntryDelete').html("deleted");
	$(this).attr("disabled", true);
});
