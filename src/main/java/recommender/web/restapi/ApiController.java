package recommender.web.restapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.xml.ws.http.HTTPException;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceType;
import com.mashape.unirest.http.exceptions.UnirestException;

import ch.qos.logback.classic.Logger;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import recommender.api.Bahn;
import recommender.api.ForecastIo;
import recommender.api.GooglePlaces;
import recommender.api.ImmoScoutApi;
import recommender.api.OpenDataSoft;
import recommender.cities.City;
import recommender.cities.CityRepository;
import recommender.web.restapi.models.Address;
import tk.plogitech.darksky.forecast.ForecastException;


/**
 * Controller for Index View.
 */
@RestController
public class ApiController {
	
	
	//autowire an object of the ImmoScout Api class (done by the spring context)
	//only needed because for every request the same is24 object should be used.
	@Autowired
	private ImmoScoutApi is24;
	
	@Autowired
	private GooglePlaces googlePlaces;
	
	@Autowired
	private ForecastIo forecastIo;
	
	@Autowired
	private Bahn bahn;
	
	@Autowired
	private OpenDataSoft openDataSoft;
	

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(ApiController.class);

	/**
	 * Get number of Bars, Schools, Restaurants and Gyms at a specific location back
	 * @param lng Longitude parameter in the Url
	 * @param lat Latitude parameter in the Url
	 * @return number of Bars, Schools, Restaurants and Gyms in JSON form
	 * @throws ApiException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@RequestMapping("/google-api-locations/{lng}/{lat}")
    public HashMap<String, Object> bars(@PathVariable double lng, @PathVariable double lat) throws ApiException, InterruptedException, IOException{
    			
		log.info("Getting Locations...");
    	
    	HashMap<String, Object> numbers = new HashMap<>();
    	
    	numbers.put("Bars", googlePlaces.numberBars(lng, lat, PlaceType.BAR));
    	numbers.put("Schools", googlePlaces.numberBars(lng, lat, PlaceType.SCHOOL));
    	numbers.put("Restaurants", googlePlaces.numberBars(lng, lat, PlaceType.RESTAURANT));
    	numbers.put("Gyms", googlePlaces.numberBars(lng, lat, PlaceType.GYM));
    	
    	return numbers;
    }
	
	/**
	 * Post Request to get back the lng and lat of an address using the Google Places API
	 * @param address address as described in the address class.
	 * @return returns lat and lng in JSON format
	 * @throws ApiException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@PostMapping("/google-api-geocode")
    public HashMap<String, Double> geocode(@RequestBody Address address) throws ApiException, InterruptedException, IOException{
    	   	
    	log.info("Getting geocode...");
    	
    	return googlePlaces.geocode(address.getAddress());
    	
    }
	
	/**
	 * Get/Post request to get all flats from a specific location
	 *(currently only M�nster and flats for rent in a 20km radius.
	 * @param model not needed i think
	 * @return returns a JSON with all relevant results
	 * @throws MalformedURLException
	 * @throws OAuthMessageSignerException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws JSONException 
	 */
	@RequestMapping("/is24/{lng}/{lat}")
    public String is24(@PathVariable double lng, @PathVariable double lat) throws MalformedURLException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, UnsupportedEncodingException, IOException, JSONException {
		
		if (!is24.isLoggedIn()) {
			is24.requestOAuth();
		}
		
		return is24.getAllApartments(lng, lat, "apartmentrent");
	}
	
	/**
	 * Get/Post request to get the average weather at a specific location
	 * @param lat Latitude
	 * @param lng Longitude
	 * @return average weather in �C
	 * @throws ForecastException
	 * @throws JSONException 
	 */
	@RequestMapping("/forecast-avg/{lng}/{lat}")
    public HashMap<String, Object> forecast(@PathVariable double lat, @PathVariable double lng) throws ForecastException, JSONException {
    	
    	log.info("Getting Weather...");
    	
    	HashMap<String, Object> avg = new HashMap<>();
    	
    	avg.put("average", forecastIo.avgClimate(lng, lat));
    	
    	return avg;
    }
	
	@RequestMapping("/all-stations")
    public String bahnInfo() throws HTTPException, UnirestException, JSONException{
    	
    	log.info("Getting all stations...");
    	
    	return bahn.getAllStations();
    }
	
	@RequestMapping("/all-zipcodes/{city}")
    public String zipCodes(@PathVariable String city) throws HTTPException, UnirestException, JSONException{
    	
    	log.info("Getting all zipcodes for "+ city +"...");
    	
    	return openDataSoft.getZipcodes(city);
    }

}