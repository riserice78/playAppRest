package service;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;

import play.Play;

public class OAuthManager {
	private static final String OAUTH_TOKEN_URL = "/services/oauth2/token";
	private static final String OAUTH_AUTHORIZE_URL = "/services/oauth2/authorize";
	private static final String OAUTH_REVOKE_URL = "/services/oauth2/revoke";

	private static OAuthManager instance = null;

	private static String loginHost = null;
	private static String clientId = null;
	private static String clientSecret = null;
	private static String redirectUri = null;
	private static String redirectUriLogout = "";
	
	private static String instanceUrl = "";
	private static String accessToken = "";

	private OAuthManager() {
		init();
	}

	public static OAuthManager getInstance() {
		if (instance == null)
			instance = new OAuthManager();
		return instance;
	}

	public static void init(){
		loginHost = Play.application().configuration().getString("my.loginHost");
		clientId = Play.application().configuration().getString("my.clientId");
		clientSecret = Play.application().configuration().getString("my.clientSecret");
		redirectUri = Play.application().configuration().getString("my.callbackUrl");
		redirectUriLogout = Play.application().configuration().getString("my.callbackUrlLogout");
	}

	public String getInstanceUrl() {
		return instanceUrl;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void getToken(String code) throws OAuthException {
		System.out.println("CODE: " + code);

		String url = loginHost + OAUTH_TOKEN_URL;
		Map<String, String> queryString = new HashMap<String, String>();
		queryString.put("grant_type", "authorization_code");
		queryString.put("client_id", clientId);
		queryString.put("client_secret", clientSecret);
		queryString.put("redirect_uri", redirectUri);
		queryString.put("code", code);
		String accessTokenUrl = createUrl(url, queryString);
		System.out.println("accessTokenUrl : " + accessTokenUrl);

		try {
			@SuppressWarnings("unchecked")
			Map<String, String> responseMap = (Map<String, String>) getJson(accessTokenUrl);
			accessToken = responseMap.get("access_token");
			instanceUrl = responseMap.get("instance_url");
			System.out.println("instance URL: " + instanceUrl);
			System.out.println("access token: " + accessToken);
		} catch (OAuthException e) {
			e.printStackTrace();
			throw new OAuthException(e.getMessage());
		}
	}

	public String getAuthorizeRedirectUrl() {
		String url = loginHost + OAUTH_AUTHORIZE_URL;
		Map<String, String> queryString = new HashMap<String, String>();
		queryString.put("response_type", "code");
		queryString.put("client_id", clientId);
		queryString.put("redirect_uri", redirectUri);
		String authUrl = createUrl(url, queryString);
		System.out.println("authUrl : " + authUrl);
		return authUrl;
	}

	public String revoke() throws OAuthException {
		String revokeUrl = loginHost + OAUTH_REVOKE_URL;
		System.out.println("revokeUrl: " + revokeUrl);
		System.out.println("token: " + accessToken);

		try {
			List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
			parametersBody.add(new BasicNameValuePair("token", accessToken));

			return post(revokeUrl, new UrlEncodedFormEntity(parametersBody));
		} catch (ParseException e) {
			throw new OAuthException(e.getMessage());
		} catch (IOException e) {
			throw new OAuthException(e.getMessage());
		}
	}

	public Object getJson(String url, Map<String, String> queryString) throws OAuthException {
		return getJson(createUrl(url, queryString));
	}
	public Object getJson(String url) throws OAuthException {
		String response_body = get(url);
		try {
			JSONParser jp = new JSONParser();
			return jp.parse(response_body);
		} catch (org.json.simple.parser.ParseException e) {
			throw new OAuthException(e.getMessage());
		}
	}

	public String get(String url, Map<String, String> queryString) throws OAuthException {
		return get(createUrl(url, queryString));
	}
	public String get(String url) throws OAuthException {
		return send(new HttpGet(url));
	}

	public String post(String url, HttpEntity entity) throws OAuthException {
		HttpPost request = new HttpPost(url);
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		if (entity != null)
			request.setEntity(entity);
		if (accessToken != null)
			request.setHeader("Authorization", "Bearer " + accessToken);
		return send(request);	
	}

	public String send(HttpRequestBase request) throws OAuthException {
		HttpClient httpclient = new DefaultHttpClient();
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		request.setHeader("X-PrettyPrint", "1"); 
		if (accessToken != null)
			request.setHeader("Authorization", "Bearer " + accessToken);
			System.out.println("request url = " + request.getURI().toString());
		try {
			HttpResponse response = httpclient.execute(request);
			return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			throw new OAuthException(e.getMessage());
		} catch (IOException e) {
			throw new OAuthException(e.getMessage());
		}
	}

	private String createUrl(String url, Map<String, String> queryString) {
		StringBuilder sb = new StringBuilder(url);
		int count = 0;
		
		for (Map.Entry<String,String> entry : queryString.entrySet()) {
			if (count == 0) sb.append("?");
			else sb.append("&");
			try {
				sb.append(String.format("%s=%s",
					URLEncoder.encode(entry.getKey().toString(), "UTF-8"),
					URLEncoder.encode(entry.getValue().toString(), "UTF-8")
				));
				count++;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sb.toString(); 
	}
}
