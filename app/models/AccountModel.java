package models;

import org.json.simple.JSONObject;

public class AccountModel {
	public String id;
	public String name;
	public String typeOfAccount;
	public String industry;
	public String annualRevenue;
	public String billingStreet;
	public String billingCity;	
	public String billingState;	
	public String billingCountry;

	public AccountModel() {
	}

	public AccountModel(JSONObject account) {
		if (account.get("Id") != null)
			id = account.get("Id").toString();
		if (account.get("Name") != null)
			name = account.get("Name").toString();
		if (account.get("Type") != null)
			typeOfAccount = account.get("Type").toString();
		if (account.get("Industry") != null)
			industry = account.get("Industry").toString();
		if (account.get("AnnualRevenue") != null)
			annualRevenue = account.get("AnnualRevenue").toString();
		if (account.get("BillingStreet") != null)
			billingStreet = account.get("BillingStreet").toString();
		if (account.get("BillingCity") != null)
			billingCity = account.get("BillingCity").toString();
		if (account.get("BillingState") != null)
			billingState = account.get("BillingState").toString();
		if (account.get("BillingCountry") != null)
			billingCountry = account.get("BillingCountry").toString();
	}
}
