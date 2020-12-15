package recommender.api;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.NearbySearchRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;

import tk.plogitech.darksky.forecast.APIKey;
import tk.plogitech.darksky.forecast.DarkSkyClient;
import tk.plogitech.darksky.forecast.ForecastException;
import tk.plogitech.darksky.forecast.ForecastRequest;
import tk.plogitech.darksky.forecast.ForecastRequestBuilder;
import tk.plogitech.darksky.forecast.GeoCoordinates;
import tk.plogitech.darksky.forecast.model.Latitude;
import tk.plogitech.darksky.forecast.model.Longitude;

public class ForecastIo {

	private String key;

	private final Logger LOGGER = LoggerFactory.getLogger(ForecastIo.class);
	
	GeoApiContext context = new GeoApiContext.Builder().apiKey(key).build();

/**
 * Constructor
 * @param key API Key
 */
	public ForecastIo(String key) {
		this.key = key;
	}
	
	/**
	 * calculates the average temperature of the last 20 days using the Forecast IO api. Uses a wrapper library provided by forcast IO
	 * https://github.com/200Puls/darksky-forecast-api
	 * @param lng Longitude
	 * @param lat Latitude
	 * @return the average temparature of the last 10 Days
	 * @throws ForecastException if query is wrong
	 * @throws JSONException if result cant be mapped to a json object.
	 */
	public double avgClimate(double lng, double lat) throws ForecastException, JSONException {
		
		double avg = 0;
		
		// takes the min and max temparature of every second day in the last 20 days and calculates the average
		for (int i = 1; i < 11; i++) {
			// https://200puls.github.io/darksky-forecast-api/overview-summary.html for details
			ForecastRequest request = new ForecastRequestBuilder()
			        .key(new APIKey(this.key))
			        .time(Instant.now().minus((i*2), ChronoUnit.DAYS))
			        .language(ForecastRequestBuilder.Language.de)
			        .units(ForecastRequestBuilder.Units.auto)
			        .exclude(ForecastRequestBuilder.Block.currently)
			        .exclude(ForecastRequestBuilder.Block.hourly)
			        .location(new GeoCoordinates(new Longitude(lng), new Latitude(lat))).build();

			    DarkSkyClient client = new DarkSkyClient();
			    String forecast = client.forecastJsonString(request);
			    
				JSONObject jObject  = new JSONObject(forecast);
				
				avg += (jObject.getJSONObject("daily").getJSONArray("data").getJSONObject(0).getDouble("temperatureHigh") + 
						jObject.getJSONObject("daily").getJSONArray("data").getJSONObject(0).getDouble("temperatureLow")) / 2;
					
		}

		return (avg / 10);
	}
}
