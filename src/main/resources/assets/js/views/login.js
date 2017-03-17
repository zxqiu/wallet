/**
 * Created by neo on 3/15/17.
 */

function checkLoginState() {
    FB.getLoginStatus(function(response) {
        console.log(response);
    });
}

$(document).ready(function() {
    $.ajaxSetup({ cache: true });
    $.getScript('//connect.facebook.net/en_US/sdk.js', function(){
        FB.init({
            appId: '{your-app-id}',
            version: 'v2.7' // or v2.1, v2.2, v2.3, ...
        });
        $('#loginbutton,#feedbutton').removeAttr('disabled');
        FB.getLoginStatus(function(response) {
            console.log(response);
        });
    });
});


var form = document.getElementById("loginForm");
form.noValidate = true;
$("#loginSubmit").on("click", function() {
    for (var i = 0; i < form.length; i++) {
        if (!form[i].checkValidity()) {
            $("input").each(function () {
                console.log($(this).attr("name") + ":" + $(this).val());
                if ($(this).prop("required") == true && $(this).val() == "") {
                    $(this).parent().parent().addClass("has-error");
                }
            });
            return;
        }
    }

    form.submit();
});
