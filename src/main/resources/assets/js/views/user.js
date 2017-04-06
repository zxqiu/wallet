/**
 * Created by zxqiu on 3/15/17.
 */

function passwordEdit() {
    if ($("#userPassword").hasClass("disabled")) {
        $("#userPassword").removeClass("disabled");
        $("#userPassword").removeAttr("disabled");
    } else {
        $("#userPassword").addClass("disabled");
        $("#userPassword").attr("disabled", "true");
    }
    $("#userPassword").attr("value", "");
}

var form = document.getElementById("userForm");
form.noValidate = true;
$("#userSubmit").on("click", function() {
    for (var i = 0; i < form.length; i++) {
        if (!form[i].checkValidity()) {
            $("input").each(function () {
                if ($(this).prop("required") == true && $(this).val() == "") {
                    $(this).parent().parent().addClass("has-error");
                }
            });
            return;
        }
    }

    form.submit();
});
