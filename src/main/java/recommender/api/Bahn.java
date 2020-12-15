package recommender.api;

import javax.xml.ws.http.HTTPException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bahn {

	private String key;
	
	private final Logger LOGGER = LoggerFactory.getLogger(Bahn.class);

	public Bahn(String key) {
		this.key = key;
	}

	public String getAllStations() throws UnirestException, JSONException, HTTPException {
		HttpResponse<String> response = Unirest
				.get("https://api.deutschebahn.com/stada/v2/stations")
				.header("Authorization", "Bearer " + key)
				.header("Accept", "application/json")
				.asString();
		
		if (response.getStatus() != 200) {
			throw new HTTPException(response.getStatus());
		} else {
			
			JSONObject result = new JSONObject(response.getBody());
			
			JSONArray stations = result.getJSONArray("result");
			
			JSONArray StationsInformation = new JSONArray();
			
			for (int i = 0; i < result.getInt("total"); i++) {
				StationsInformation.put(getInformation(stations.getJSONObject(i)));
			}
			
			return StationsInformation.toString();
		}
	}

	private JSONObject getInformation(JSONObject station) throws JSONException {
				
		JSONObject information = new JSONObject();
		
		String name = station.getString("name");
		String city = station.getJSONObject("mailingAddress").getString("city");
		String zipCode = station.getJSONObject("mailingAddress").getString("zipcode");
		String street = station.getJSONObject("mailingAddress").getString("street");
		
		
		
		try {

			double lng = station.getJSONArray("ril100Identifiers").getJSONObject(0).getJSONObject("geographicCoordinates").getJSONArray("coordinates").getDouble(0);
			double lat = station.getJSONArray("ril100Identifiers").getJSONObject(0).getJSONObject("geographicCoordinates").getJSONArray("coordinates").getDouble(1);
			
			information.put("longitude", lng);
			information.put("latitude", lat);
			
		} catch (Exception e) {
			LOGGER.error("Failed to find coordinates: " + station.getJSONArray("ril100Identifiers").toString());
			
			information.put("longitude", JSONObject.NULL);
			information.put("latitude", JSONObject.NULL);
		}
		
		information.put("name", name);
		information.put("city", city);
		information.put("zipCode", zipCode);
		information.put("street", street);

		return information;
		
	}
	
	
	
	

}
