package controllers;

import static play.libs.Json.toJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import models.AccountModel;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import service.OAuthManager;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

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
	}
	  
	public static Result getAccounts(){
		return ok(toJson(amList));
	}
	
	@SuppressWarnings("unchecked")
	public static void initAccountListRest() throws ClientProtocolException, IOException, org.json.simple.parser.ParseException{
		String instanceUrl = OAuthManager.getOAuthManager().getInstanceUrl();
		String accessToken = OAuthManager.getOAuthManager().getAccessToken();
		if(instanceUrl!=null && accessToken!=null){
		    String query = instanceUrl + "/services/data/v20.0/query?q=SELECT+id,+name,+Description,+ProductCode+from+Product2";
	
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
					JSONObject product = (JSONObject)jsonArray.get(i);
		            System.out.println("Record" + i +" : " + product.toString());
		            AccountModel model = new AccountModel();
					model.id = product.get("Id").toString();
					System.out.println("id: "+model.id);
					model.name =  product.get("Name").toString();
					System.out.println("name: "+model.name);
					System.out.println("adding to array : " + model.id + " " + model.name + ";");
					amList.add(model);
				} catch (Exception e) {
					System.out.println("error: " + e.getMessage());
				}
	    	}

		}
	}

}