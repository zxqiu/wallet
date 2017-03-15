/************************** jquery functions ********************************/
$('#booksEntryCategoryList li a').on('click', function(){
    $('#booksEntryCategory').val($(this).text()).change();
    $('#booksEntryCategory').text($(this).text());
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
    $('#booksEntrySubmit').html("submitted");
    $("#booksEntrySubmit").attr("disabled", true);

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
            return;
        }
    }

    form.submit();
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
