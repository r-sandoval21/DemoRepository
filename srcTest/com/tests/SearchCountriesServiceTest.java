package com.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.model.Country;

import junit.framework.Assert;

public class SearchCountriesServiceTest 
{
	private BufferedReader getServiceData(URL url, String method)
	{
		BufferedReader br = null;
		HttpURLConnection conection;
		try {
			conection = (HttpURLConnection) url.openConnection();
			conection.setRequestMethod("GET");
			conection.setRequestProperty("Accept", "application/json");
			
			
			if (conection.getResponseCode() != 200) 
			{
				throw new RuntimeException("Failed : HTTP error code : "
						+ conection.getResponseCode());
			}
			
			br = new BufferedReader(new InputStreamReader(
					(conection.getInputStream())));
			
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return br;
	}
	
	private JSONObject parseServiceDataToJson(BufferedReader br) throws IOException 
	{
		
		StringBuilder sb = new StringBuilder();			
		String output;		
		
		while ((output = br.readLine()) != null)
		{
			sb.append(output);	
		}
		
		return new JSONObject(sb.toString());
		
	}
	
	private HashMap<String,Country> parseJsonToModel(JSONObject obj)
	{
		JSONObject json = obj.getJSONObject("RestResponse");
		JSONArray jArray = (JSONArray) json.get("result");
		
		HashMap<String,Country> countryDic = new HashMap<String, Country>();
		for (Object jCountry : jArray) 
		{
			Country country = new Country();
			
			country.setName(((JSONObject) jCountry).get("name").toString());		
			country.setAlpha2_code(((JSONObject) jCountry).get("alpha2_code").toString());
			country.setAlpha3_code(((JSONObject) jCountry).get("alpha3_code").toString());
			countryDic.put(country.getName(), country);
			
		}

		return countryDic;
	}
	
	
	@Test
	public void APIShouldRetrieveData() throws IOException
	{
		try 
		{
			URL url = new URL("http://services.groupkt.com/country/get/all");
			BufferedReader br = getServiceData(url, "GET");
			JSONObject data = parseServiceDataToJson(br);
			
			HashMap<String,Country> dicc = parseJsonToModel(data);
			
			Assert.assertTrue("Albania exists in the dictionary" ,dicc.containsKey("Albania"));
			Assert.assertFalse("Guadalajara does exists in the dictionary" ,dicc.containsKey("Guadalajaragit"));

		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
	}
}
