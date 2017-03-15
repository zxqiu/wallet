/**
 * Created by neo on 3/15/17.
 */

var form = document.getElementById("userForm");
form.noValidate = true;
$("#userSubmit").on("click", function() {
    $('#userSubmit').html("submitted");
    $("#userSubmit").attr("disabled", true);

    for (var i = 0; i < form.length; i++) {
        if (!form[i].checkValidity()) {
            $("input").each(function () {
                console.log($(this).attr("name") + ":" + $(this).val());
                if ($(this).prop("required") == true && $(this).val() == "") {
                    $(this).parent().parent().addClass("has-error");

                    $('#userSubmit').html("submit");
                    $('#userSubmit').removeAttr("disabled");
                }
            });
            return;
        }
    }

    form.submit();
});
