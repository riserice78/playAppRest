package service;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;

import play.mvc.*;
import views.html.*;
import play.Play;

public class OAuthManager extends Controller{
		private static String loginHost = null;
		private static String clientId = null;
		private static String clientSecret = null;
		private static String redirectUri = null;
		private static OAuthManager ref;
		private static String instanceUrl = "";
		private static String accessToken = "";		
		private static String redirectUriLogout = "";
		
		private OAuthManager() { }
		public static OAuthManager getOAuthManager() {
			if (ref == null)
				ref = new OAuthManager();
			init();
			return ref;
		}

		public static void init(){
			loginHost = "https://login.salesforce.com";
			clientId = Play.application().configuration().getString("my.clientId");
			clientSecret = Play.application().configuration().getString("my.clientSecret");
		    redirectUri = Play.application().configuration().getString("my.callbackUrl");
		    redirectUriLogout = Play.application().configuration().getString("my.callbackUrlLogout");
		}
		
		public String getInstanceUrl(){
			return instanceUrl;
		}
		
		public String getAccessToken(){
			return accessToken;
		}		
		
    	public static Result callback(String code) {
    		System.out.println("IN CALLBACK1");
		    System.out.println("CODE: " + code);
			String accessTokenUrl = loginHost + "/services/oauth2/token?grant_type=authorization_code&client_id="
		    		+ clientId + "&client_secret=" + clientSecret + "&redirect_uri=" + redirectUri
		    		+ "&code="+code;
			System.out.println("accessTokenUrl : " + accessTokenUrl);
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost request = new HttpPost(accessTokenUrl);
		    request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		    
		    try {
			    HttpResponse response = httpclient.execute(request);
			    JSONParser jParser = new JSONParser();			    
			    Map<String, String> responseMap = (Map<String, String>) jParser.parse(EntityUtils.toString(response.getEntity()));
			    accessToken = responseMap.get("access_token");
			    instanceUrl = responseMap.get("instance_url");
		    } catch (ParseException e) {
				e.printStackTrace();
				return ok(login.render("Login Failed"));
			} catch (IOException e) {
				e.printStackTrace();
				return ok(login.render("Login Failed"));
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return ok(login.render("Login Failed"));
			}
			
			//set token
			System.out.println("instance URL: " + instanceUrl);
			System.out.println("access token: " + accessToken);
			
			return redirect("/index");
		}

		public static Result oAuthSessionProviderGrantAuth () throws HttpException, IOException {
			String authUrl = loginHost + "/services/oauth2/authorize?response_type=code&client_id="
		    		+ clientId + "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8"); 
			System.out.println("authUrl : " + authUrl);
			return redirect(authUrl);
		}    	

		public static Result logout () {
			String revokeUrl = loginHost + "/services/oauth2/revoke";
			System.out.println("revokeUrl: " + revokeUrl);
			System.out.println("token: " + accessToken);
			String message = "";
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost request = new HttpPost(revokeUrl);
			    request.setHeader("Content-Type", "application/x-www-form-urlencoded");
				List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
			    parametersBody.add(new BasicNameValuePair("token", accessToken));
			    request.setEntity(new UrlEncodedFormEntity(parametersBody));
			    HttpResponse response = httpclient.execute(request);
			    message = EntityUtils.toString(response.getEntity());
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println("logout failed");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("logout failed");
			}
		    return ok(logout.render(message));
		}    	

}
