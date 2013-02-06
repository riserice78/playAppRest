package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import service.OAuthException;
import service.OAuthManager;
import views.html.login;
import views.html.logout;

public class OAuth extends Controller {

	public static Result callback(String code) {
		try {
			OAuthManager.getInstance().getToken(code);
			return redirect("/index");
		} catch (OAuthException e) {
			return ok(login.render("Login Failed"));
		}
	}

	public static Result logout() {
		String message;
		try {
			message = OAuthManager.getInstance().revoke();
		} catch (OAuthException e) {
			message = "logout failed";
		}
		return ok(logout.render(message));
	}

}
