package controllers;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;


import play.mvc.*;
import service.OAuthManager;
import views.html.*;
import play.mvc.Http.Context;


public class Application extends Controller {
  
  public static Result login() {	
	  try{
		  return OAuthManager.getOAuthManager().oAuthSessionProviderGrantAuth();	  
	  }catch(Exception e){
		  System.out.println("getOAuth fail: " + e.getMessage());
	  }
	  return ok(login.render("Login failed. Please try again."));
  }
  public static Result index() {
	  try {
		Account.getAccount().initAccountListRest();
	  } catch (ClientProtocolException e) {
		e.printStackTrace();
	  } catch (IOException e) {
		e.printStackTrace();
	  } catch (ParseException e) {
		e.printStackTrace();
	  }
	  System.out.println("to render index");
	  return ok(index.render("Account List"));
  }

}