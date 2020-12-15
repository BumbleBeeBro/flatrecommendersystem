package recommender.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;

import org.json.JSONArray;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import recommender.Application;

public class ImmoScoutApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImmoScoutApi.class);
	
	public OAuthConsumer consumer;
	
	public ImmoScoutApi(String accessKey, String accessSecret) {
		consumer = new DefaultOAuthConsumer(accessKey, accessSecret);
	}

	/**
	 * Procedure for is24 Oauth copied from 
	 * https://github.com/OpenEstate/OpenEstate-IS24-REST/blob/master/OpenEstate-IS24-REST-examples/src/main/java/org/openestate/is24/restapi/examples/IS24OauthExample.java
	 */
	public void requestOAuth() {

		OAuthProvider provider = new DefaultOAuthProvider(
				"https://rest.immobilienscout24.de/restapi/security/oauth/request_token",
				"https://rest.immobilienscout24.de/restapi/security/oauth/access_token",
				"https://rest.immobilienscout24.de/restapi/security/oauth/confirm_access");

		LOGGER.info("Fetching request token...");

		String authUrl;

		try {
			authUrl = provider.retrieveRequestToken(consumer, "http://www.google.de");

			String requestToken = consumer.getToken();
			String requestTokenSecret = consumer.getTokenSecret();

			LOGGER.info("Request token: " + requestToken);
			LOGGER.info("Token secret: " + requestTokenSecret);

			LOGGER.info("Now visit:\n" + authUrl + "\n... and grant this app authorization");
			LOGGER.info("Enter the verification code and hit ENTER when you're done:");

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String verificationCode;

			verificationCode = br.readLine();

			provider.retrieveAccessToken(consumer, verificationCode.trim());

			LOGGER.info("Fetching access token...");

			String accessToken = consumer.getToken();
			String accessTokenSecret = consumer.getTokenSecret();
			LOGGER.info("Access token: " + accessToken);
			LOGGER.info("Token secret: " + accessTokenSecret);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * Helper function to see if the object was already authorized
	 * @return
	 */
	public boolean isLoggedIn() {
		if (consumer.getToken() == null && consumer.getTokenSecret() == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * function to request all data from an is34 url.
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws OAuthMessageSignerException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 * @throws UnsupportedEncodingException
	 * @throws JSONException
	 */
	public JSONObject requestObjectApi(URL url)
			throws MalformedURLException, IOException, OAuthMessageSignerException, OAuthExpectationFailedException,
			OAuthCommunicationException, UnsupportedEncodingException, JSONException {
		
		HttpURLConnection apiRequest = (HttpURLConnection) url.openConnection();

		apiRequest.setRequestProperty("Accept", "application/json");
		
		consumer.sign(apiRequest);
		
		LOGGER.info("Sending request...");
				
		apiRequest.connect();

		//hier kann viel schief gehen von der transformation von inputstream zu string zu json object
		StringWriter writer = new StringWriter();
		IOUtils.copy((InputStream) apiRequest.getContent(), writer, Charset.forName("UTF-8"));
		String response = writer.toString();
		
		JSONObject jObject  = new JSONObject(response);
		
		return jObject;
		
	}
	
	/**
	 * Main function to request all apartments for rent from lat: 51.96236, lng: 7.62571, radius 20km. (TODO make it dynamic)
	 * @return needed information for every apartment matching the criteria in JSON format.
	 * @throws JSONException
	 * @throws MalformedURLException
	 * @throws OAuthMessageSignerException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public String getAllApartments(double lng, double lat, String type) throws JSONException, MalformedURLException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, UnsupportedEncodingException, IOException {
		
		JSONArray apartmentInfo = new JSONArray();
		
		//initial call to pull first page and total number of pages
		JSONObject result = requestObjectApi(urlConstruction(type, (double) 20, lat, lng));
				
		//is sends 20 entries per page back, and says how many pages there are (thus it needs to be saved).
		//TODO here seems to be a bug, the last half of the results are always empty)
		int numberOfPages = result.getJSONObject("resultlist.resultlist").getJSONObject("paging").getInt("pageSize");
		
		//get the apartments out of the respnse json
		JSONArray apartments = result.getJSONObject("resultlist.resultlist").getJSONArray("resultlistEntries").getJSONObject(0).getJSONArray("resultlistEntry");
		
		//for each apartment get only the relevant iformation
		for (JSONObject apartment : getNecessaryDataFromJson(apartments)) {
			apartmentInfo.put(apartment);
		}
		
		//for each page after the initial one do the same as before
		for (int page = 2; page <= numberOfPages; page++) {
			
			JSONObject result2 = requestObjectApi(urlConstruction("apartmentrent", (double) 20, 51.96236, 7.62571, page));
			
			try {
				JSONArray apartments2 = result2.getJSONObject("resultlist.resultlist").getJSONArray("resultlistEntries").getJSONObject(0).getJSONArray("resultlistEntry");
				
				for (JSONObject apartment2 : getNecessaryDataFromJson(apartments2)) {
					apartmentInfo.put(apartment2);
				}
			} catch (Exception e) {
				LOGGER.info("API returned uncomplete JSON: \n"+ result2.toString());
			}
			
		}
		
		return apartmentInfo.toString();
	}
	
	//wrapper function to construct a proper URL for an is24 request
	public URL urlConstruction(String realestatetype, Double radius, Double lat, Double lng) throws MalformedURLException {
		return new URL(
				"https://rest.immobilienscout24.de/restapi/api/search/v1.0/search/radius?realestatetype=" + realestatetype + "&geocoordinates=" + lat + ";" + lng + ";" + radius);

	}
	
	//wrapper function to construct a proper URL for an is24 request for a next page.
	public URL urlConstruction(String realestatetype, Double radius, Double lat, Double lng, int pageNumber) throws MalformedURLException {
		return new URL(
				"https://rest.immobilienscout24.de/restapi/api/search/v1.0/search/radius?realestatetype=" + realestatetype + "&geocoordinates=" + lat + ";" + lng + ";" + radius + "&pagenumber=" + pageNumber);

	}
	
	/**
	 * helper function to get only necessary data from the result given by is24
	 * @param apartments
	 * @return
	 * @throws JSONException
	 */
	public ArrayList<JSONObject> getNecessaryDataFromJson(JSONArray apartments) throws JSONException {
		ArrayList<JSONObject> apartmentInfo = new ArrayList<JSONObject>();
		
		for (int i = 0; i < apartments.length(); i++) {
			apartmentInfo.add(getApartmentInfo(apartments.getJSONObject(i)));
		}
		
		return apartmentInfo;
	}
	
	/**
	 * another helper function to get only the necessary data from the is24 result
	 * @param apartment
	 * @return
	 * @throws JSONException
	 */
	public JSONObject getApartmentInfo(JSONObject apartment) throws JSONException {	

		JSONObject apartmentInfo = new JSONObject();
		
		String title = apartment.getJSONObject("resultlist.realEstate").getString("title");
		JSONObject address = apartment.getJSONObject("resultlist.realEstate").getJSONObject("address");
		int price = apartment.getJSONObject("resultlist.realEstate").getJSONObject("price").getInt("value");
		int livingspace = apartment.getJSONObject("resultlist.realEstate").getInt("livingSpace");
		boolean balcony = apartment.getJSONObject("resultlist.realEstate").getBoolean("balcony");
		boolean garden = apartment.getJSONObject("resultlist.realEstate").getBoolean("garden");
		
		apartmentInfo.put("title", title);
		apartmentInfo.put("address", address);
		apartmentInfo.put("price", price);
		apartmentInfo.put("title", title);
		apartmentInfo.put("livingspace", livingspace);
		apartmentInfo.put("balcony", balcony);
		apartmentInfo.put("garden", garden);
		
		return apartmentInfo;
	}
}
