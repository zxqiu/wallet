var apiRestoreSession = "/api/session/restoresession";

function restoreSession() {
	var form = document.getElementById("sessionCookieForm");
	var cookie = document.getElementById("sessionCookieValue");
	cookie.value = getCookie(SESSION_COOKIE_NAME);
	form.submit();
}

/************************** jquery functions ********************************/

$( document ).ready(function() {
	restoreSession();
});
