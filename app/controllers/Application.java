package controllers;

import play.mvc.*;
import service.OAuthManager;
import views.html.*;


public class Application extends Controller {
  
	public static Result login() {	
		try {
			String redirect_url = OAuthManager.getInstance().getAuthorizeRedirectUrl();	 
			return redirect(redirect_url);
		} catch(Exception e) {
			System.out.println("getOAuth fail: " + e.getMessage());
		}
		return ok(login.render("Login failed. Please try again."));
	}

	public static Result index() {
		System.out.println("to render index");
		return ok(index.render("Account List"));
	}

}
