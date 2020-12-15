package recommender.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.NearbySearchRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;

public class GooglePlaces {
	
	private String key;
	
	private GeoApiContext context;
	
	private final Logger LOGGER = LoggerFactory.getLogger(GooglePlaces.class);
	
	public GooglePlaces(String key) {
		this.key = key;
	}
	
	/**
	 * gets geocode of an address using the google places api
	 * uses a wrapper library: https://github.com/googlemaps/google-maps-services-java
	 * @param address as described in the api Models
	 * @return The coordinates in JSON format
	 * @throws ApiException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public HashMap<String, Double> geocode(String address) throws ApiException, InterruptedException, IOException {
		
		//not efficient
		context = new GeoApiContext.Builder().apiKey(key).build();
		
		GeocodingResult[] results =  GeocodingApi.geocode(context, address).await();
		
		HashMap<String, Double> coordinates = new HashMap<String, Double>();
		
		coordinates.put("latitude", results[0].geometry.viewport.northeast.lat);
		coordinates.put("longitude", results[0].geometry.viewport.northeast.lng);
		
		//not tested
		context.shutdown();
	
		return coordinates;
	}
	
	/**
	 * gets number of a placetype near a location again using the client library for Java by google ( https://github.com/googlemaps/google-maps-services-java)
	 * @param longitude Longitude
	 * @param latitude	Latitude
	 * @param placeType (List of PlaceTypes https://developers.google.com/places/web-service/supported_types)
	 * @return the number of a placetype near the location (max 20, because otherwise one has to switch to the next page)
	 * @throws ApiException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public int numberBars(double longitude, double latitude, PlaceType placeType) throws ApiException, InterruptedException, IOException {
		
		//not efficient
		context = new GeoApiContext.Builder().apiKey(key).build();
		
		GeoApiContext context = new GeoApiContext.Builder().apiKey(key).build();
		
		PlacesSearchResponse response = new NearbySearchRequest(context).location(new LatLng(latitude, longitude)).radius(500).type(placeType).await();

		//not tested
		context.shutdown();
		
		//if there is a next page token, there are more than 20 bars
		if (response.nextPageToken != null) {
			LOGGER.warn("more than 20 bars nearby");
			return 21;
		}
		else {

			return response.results.length;
			
		}
	}
	

}
