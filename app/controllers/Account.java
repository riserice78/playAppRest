package controllers;

import static play.libs.Json.toJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import models.AccountModel;
import play.mvc.Controller;
import play.mvc.Result;
import service.OAuthException;
import service.RestApi;

import views.html.*;

public class Account extends Controller {
	private static Account ref;
	private static ArrayList<AccountModel> amList = null;
	private static Map<String, AccountModel> amMap = null;
	private Account() { }
	
	public static Account getAccount() {
		if (ref == null)
			ref = new Account();
		return ref;
	}

	public static Result getAccountDetails(String id) {	
		System.out.println("id parameter: " + id);
		if (amMap == null) {
			try {
				initAccountListRest();
			} catch (OAuthException e) {
			}
		}
		if (amMap == null)
			return redirect("/");

		AccountModel am = amMap.get(id);
		if (am == null) 
			return redirect("/");

		System.out.println("am : " + am);	
		return ok(accountviewmore.render("Account Details", am));
	}
	  
	public static Result getAccounts(){
		if(amList == null){
			try {
				initAccountListRest();
			} catch (OAuthException e) {
				System.out.println("in getAccountDetails " + e.getMessage());
			}
		}
		return ok(toJson(amList));
	}

	public static void initAccountListRest() throws OAuthException {
		RestApi rest = RestApi.getInstance();

	    String soql = "SELECT Id, Name, Type, Industry, AnnualRevenue, BillingStreet, BillingCity, BillingState, BillingCountry from Account";
	    Map<String, Object> responseMap = rest.query(soql);
        JSONArray jsonArray = (JSONArray)responseMap.get("records");
	    amList = new ArrayList<AccountModel>();
	    amMap = new HashMap<String, AccountModel>();
        for (Object jo : jsonArray) {
			JSONObject account = (JSONObject)jo;
			AccountModel am = new AccountModel(account);
			amList.add(am);
			amMap.put(am.id, am);
    	}
	}

}