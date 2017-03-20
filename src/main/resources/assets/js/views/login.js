/**
 * Created by zxqiu on 3/15/17.
 */

var api = new APIs.createNew();

function fbLoginSuccessCallback(data) {
    window.location.href = "/";
}
function fbLoginSuccess(response) {
    if (response.status != "connected" || !response.authResponse) {
        return;
    }

    var fbUser = new Object();
    fbUser.access_token = response.authResponse.accessToken;

    FB.api('/me', {fields: 'id,name,email'}, function(data) {
        if (data.id && data.name && data.email) {
            fbUser.user_id = data.id;
            fbUser.name = data.name;
            fbUser.email = data.email;

            api.setPostFBLoginSuccessCallback(fbLoginSuccessCallback);
            api.postFBLogin(fbUser);
        }
    });
}

function checkFBLoginState() {
    FB.getLoginStatus(function(response) {
        fbLoginSuccess(response);
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
            fbLoginSuccess(response);
        });
    });
});


var form = document.getElementById("loginForm");
form.noValidate = true;
$("#loginSubmit").on("click", function() {
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
