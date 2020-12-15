package recommender;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import recommender.cities.City;
import recommender.cities.CityRepository;

/**
 * Class to populate the database that stores cities that we focus on (currently not used).  
 */
@Component
public class PopulatingDatabases implements ApplicationRunner {

	@Autowired
	private CityRepository cityRepository;	
	
	/**
	 * Function to store cities in the city table of the database.
	 */
	public void run(ApplicationArguments args) {
						
		cityRepository.save(new City("Münster", "Nordrhein-Westfalen", 7.62571, 51.96236, 303.28, 311846, 7927));
		cityRepository.save(new City("Berlin", 	"Berlin", 52.520008, 13.404954, 891.12, 3574830, 149253));
		cityRepository.save(new City("München", "Bayern", 48.137154, 11.576124,  310.71, 1464301, 34857));
    }
}