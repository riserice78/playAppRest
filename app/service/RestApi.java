package service;

import java.util.HashMap;
import java.util.Map;

import play.Play;

public class RestApi {
	private static String SERVICE_DATA_URL = "/services/data/";

	private static RestApi instance = null;
	private static Map<String, String> serviceUrl = null;

	@SuppressWarnings("unchecked")
	private RestApi() throws OAuthException {
		OAuthManager om = OAuthManager.getInstance();
		if (om.getAccessToken() != null) {
			String api_version = Play.application().configuration().getString("my.apiVersion");
			String url = om.getInstanceUrl() + SERVICE_DATA_URL + "v" + api_version;
			serviceUrl = (Map<String, String>)om.getJson(url);
		} else {
			throw new OAuthException("no access token.");
		}
	}

	public static RestApi getInstance() throws OAuthException {
		if (instance == null)
			instance = new RestApi();
		return instance;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> query(String soql) throws OAuthException {
		OAuthManager om = OAuthManager.getInstance();
		if (om.getAccessToken() == null || serviceUrl.get("query") == null) return null;
		String url = om.getInstanceUrl() + serviceUrl.get("query");
		Map<String, String> queryString = new HashMap<String, String>();
		queryString.put("q", soql);
		return (Map<String, Object>) om.getJson(url, queryString);
	}
}
