package recommender;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestController;

import recommender.api.Bahn;
import recommender.api.ForecastIo;
import recommender.api.GooglePlaces;
import recommender.api.ImmoScoutApi;
import recommender.api.OpenDataSoft;

import org.slf4j.Logger;


//TODO: Quarter Tabelle
//TODO: Zipcode Tabelle
//TODO: Address Tabelle
//TODO: Accomondation Tabelle

@SpringBootApplication(scanBasePackages = { "recommender.web", "recommender", "recommender.api" })
@RestController
public class Application {

	final static Logger LOGGER = LoggerFactory.getLogger(Application.class);
	
	@Autowired
	Environment environment;

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	/**
	 * Provider of Class needed to call is24 API. Should only exist once, so that authentication only needs to be once and every subsequent call
	 * uses the same Object.
	 * @return ImmoScoutApi Object, used for querieng
	 */
	@Bean
	@Scope("singleton")
	public ImmoScoutApi getImmoscout() {	
		return new ImmoScoutApi(environment.getProperty("Is24Key"), environment.getProperty("Is24Secret"));
	}
	
	/**
	 * Provider of Class needed to call googlePlaces API. Bean is needed, so that values from the applications.properties file can be injected here.
	 * That allows saving sensible data in one place.
	 * @return GooglePlaces Object used for quering 
	 */
	@Bean
	public GooglePlaces getGooglePlaces() {	
		return new GooglePlaces(environment.getProperty("GoogleApiKey"));
	}
	
	/**
	 * Provider of Class needed to call forcastIO API. Bean is needed, so that values from the applications.properties file can be injected here.
	 * That allows saving sensible data in one place.
	 * @return ForecastI Object used for quering 
	 */
	@Bean
	public ForecastIo getForeCastIo() {	
		return new ForecastIo(environment.getProperty("ForecastIoKey"));
	}
	
	@Bean
	public Bahn getBahn() {	
		return new Bahn(environment.getProperty("BahnKey"));
	}
	
	@Bean
	public OpenDataSoft getOpenDataSoft() {	
		return new OpenDataSoft();
	}
}
