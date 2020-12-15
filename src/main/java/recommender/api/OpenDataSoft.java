package recommender.api;

import javax.xml.ws.http.HTTPException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class OpenDataSoft {
	
	private final Logger LOGGER = LoggerFactory.getLogger(OpenDataSoft.class);

	public OpenDataSoft() {
		super();
	}

	public String getZipcodes(String city) throws UnirestException, JSONException, HTTPException {
		HttpResponse<String> response = Unirest.get("https://public.opendatasoft.com/api/records/1.0/search/?dataset=postleitzahlen-deutschland&lang=DE&rows=200&facet=plz&q="+ city)
				  .header("cache-control", "no-cache")
				  .asString();
		
		if (response.getStatus() != 200) {
			throw new HTTPException(response.getStatus());
		} else {
			
			JSONObject result = new JSONObject(response.getBody());
			
			JSONArray zipCodes = result.getJSONArray("records");
			
			JSONArray zipCodeInformation = new JSONArray();
			
			for (int i = 0; i < result.getInt("nhits"); i++) {
				zipCodeInformation.put(getInformation(zipCodes.getJSONObject(i)));
			}
			
			return zipCodeInformation.toString();
		}
	}

	private JSONObject getInformation(JSONObject zipCodes) throws JSONException {
				
		JSONObject information = new JSONObject();
		
		String note = zipCodes.getJSONObject("fields").getString("note");
		String zipCode = zipCodes.getJSONObject("fields").getString("plz");
		double lat = zipCodes.getJSONObject("fields").getJSONArray("geo_point_2d").getDouble(0);
		double lng = zipCodes.getJSONObject("fields").getJSONArray("geo_point_2d").getDouble(0);
		
		
		information.put("note", note);
		information.put("zipCode", zipCode);
		information.put("longitude", lng);
		information.put("latitude", lat);

		return information;
		
	}
	
	
	
	

}
