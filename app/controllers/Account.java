package controllers;

import static play.libs.Json.toJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import models.AccountModel;
import play.mvc.Controller;
import play.mvc.Result;
import service.OAuthManager;

import views.html.*;

public class Account extends Controller{
	private static Account ref;
	private static ArrayList<AccountModel> amList;
	private Account() { }
	
	public static Account getAccount() {
		if (ref == null)
			ref = new Account();
		return ref;
	}
		
	public static Result getAccountDetails(String id){	
		AccountModel am = null;
		System.out.println("id parameter: " + id);
		if(amList==null){
			try{
				initAccountListRest();
			}catch(Exception e){
				System.out.println("in getAccountDetails " + e.getMessage());
			}
		}
		if(amList!=null){
			for(int i=0; i<amList.size(); i++){
				if(id.equals(amList.get(i).id)){
					am = amList.get(i);
					System.out.println("FOUND!");
					break;
				}
			}
			if(am==null){
				am = new AccountModel();
				am.name = "";
			}
			System.out.println("am : " + am);	
			return ok(accountviewmore.render("Account Details", am));
		}else{
			return redirect("/");
		}
	}
	  
	public static Result getAccounts(){
		return ok(toJson(amList));
	}
	
	@SuppressWarnings("unchecked")
	public static void initAccountListRest() throws ClientProtocolException, IOException, org.json.simple.parser.ParseException{
		String instanceUrl = OAuthManager.getOAuthManager().getInstanceUrl();
		String accessToken = OAuthManager.getOAuthManager().getAccessToken();
		if(instanceUrl!=null && accessToken!=null){
		    String query = instanceUrl + "/services/data/v20.0/query?q=SELECT+Id,+Name,+Type,+Industry,+AnnualRevenue,+BillingStreet,+BillingCity,+BillingState,+BillingCountry+from+Account";
	
			HttpClient httpclient = new DefaultHttpClient();
		    HttpGet request = new HttpGet(query);
		    request.setHeader("Authorization", "Bearer " + accessToken);
		    request.setHeader("X-PrettyPrint", "1");    
		    HttpResponse response = httpclient.execute(request);
	 
		    JSONParser jParser = new JSONParser();
		    Map<String, Object> responseMap = (Map<String, Object>) jParser.parse(EntityUtils.toString(response.getEntity()));
	        JSONArray jsonArray = (JSONArray)responseMap.get("records");
		    amList = new ArrayList<AccountModel>();
	        for(int i=0; i<jsonArray.size(); i++){
				try {
					JSONObject account = (JSONObject)jsonArray.get(i);
		            System.out.println("Record" + i +" : " + account.toString());
		            AccountModel model = new AccountModel();
					model.id = account.get("Id").toString();
					System.out.println("id: "+model.id);
					model.name =  account.get("Name").toString();
					System.out.println("name: "+model.name);
					model.typeOfAccount =  account.get("Type").toString();
					System.out.println("type: "+model.typeOfAccount);
					model.industry =  account.get("Industry").toString();
					System.out.println("type: "+model.industry);
					model.annualRevenue =  account.get("AnnualRevenue").toString();
					System.out.println("type: "+model.annualRevenue);
					model.billingStreet =  account.get("BillingStreet").toString();
					System.out.println("type: "+model.billingStreet);
					model.billingCity =  account.get("BillingCity").toString();
					System.out.println("type: "+model.billingCity);
					model.billingState =  account.get("BillingState").toString();
					System.out.println("type: "+model.billingState);
					model.billingCountry =  account.get("BillingCountry").toString();
					System.out.println("type: "+model.billingCountry);					
					
					System.out.println("adding to array : " + model.id + " " + model.name + ";");
					amList.add(model);
				} catch (Exception e) {
					System.out.println("error: " + e.getMessage());
				}
	    	}

		}
	}

}