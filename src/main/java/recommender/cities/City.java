package recommender.cities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *  Class to model a City object.
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class City implements java.io.Serializable {

	private static final long serialVersionUID = 42L;
	
	//Attributes
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	private String name;
	
	@NotNull
	private String federalstate;
	
	@NotNull
	private double longitude;
	
	@NotNull
	private double latitude;
	
	//in km²
	private double expanse;
	
	private int population;
	
	//in people per km²
	private double density;
	
	private int unemployment;
	
	//in people per population
	private double unemploymentRate;
	
	
	public City(@NotNull String name, @NotNull String federalstate, @NotNull double longitude, @NotNull double latitude,
			double expanse, int population, int unemployment) {
		super();
		this.name = name;
		this.federalstate = federalstate;
		this.longitude = longitude;
		this.latitude = latitude;
		this.expanse = expanse;
		this.population = population;
		this.unemployment = unemployment;
		this.unemploymentRate = unemployment/population;
		this.density = population/expanse;
	}
	
	//for JPA
	public City() {
		
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the federalstate
	 */
	public String getFederalstate() {
		return federalstate;
	}


	/**
	 * @param federalstate the federalstate to set
	 */
	public void setFederalstate(String federalstate) {
		this.federalstate = federalstate;
	}


	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}


	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}


	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	/**
	 * @return the expanse
	 */
	public double getExpanse() {
		return expanse;
	}


	/**
	 * @param expanse the expanse to set
	 */
	public void setExpanse(double expanse) {
		this.expanse = expanse;
	}


	/**
	 * @return the population
	 */
	public int getPopulation() {
		return population;
	}


	/**
	 * @param population the population to set
	 */
	public void setPopulation(int population) {
		this.population = population;
	}


	/**
	 * @return the density
	 */
	public double getDensity() {
		return density;
	}


	/**
	 * @param density the density to set
	 */
	public void setDensity(double density) {
		this.density = density;
	}


	/**
	 * @return the unemployment
	 */
	public int getUnemployment() {
		return unemployment;
	}


	/**
	 * @param unemployment the unemployment to set
	 */
	public void setUnemployment(int unemployment) {
		this.unemployment = unemployment;
	}


	/**
	 * @return the unemploymentRate
	 */
	public double getUnemploymentRate() {
		return unemploymentRate;
	}


	/**
	 * @param unemploymentRate the unemploymentRate to set
	 */
	public void setUnemploymentRate(double unemploymentRate) {
		this.unemploymentRate = unemploymentRate;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "City [id=" + id + ", name=" + name + ", federalstate=" + federalstate + ", longitude=" + longitude
				+ ", latitude=" + latitude + ", expanse=" + expanse + ", population=" + population + ", density="
				+ density + ", unemployment=" + unemployment + ", unemploymentRate=" + unemploymentRate + "]";
	}
	
}
