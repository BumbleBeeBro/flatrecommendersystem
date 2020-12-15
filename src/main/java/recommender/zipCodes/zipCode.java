package recommender.zipCodes;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import recommender.cities.City;

/**
 *  Class to model a zipCode object. currently not used
 */
//@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class zipCode implements java.io.Serializable {

	private static final long serialVersionUID = 42L;
	
	//Attributes
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@OneToOne(fetch = FetchType.EAGER)
	private City city;
	
	@NotNull
	private String note;
	
	@NotNull
	private double latitude;
	
	@NotNull
	private double longitude;
	
	@NotNull
	private String zipCode;

	public zipCode( @NotNull City city, @NotNull String note,
			@NotNull double latitude, @NotNull double longitude, @NotNull String zipCode) {
		super();
		this.city = city;
		this.note = note;
		this.latitude = latitude;
		this.longitude = longitude;
		this.zipCode = zipCode;
	}
	
	//for JPA
	public zipCode() {

	}

	/**
	 * @return the city
	 */
	public City getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(City city) {
		this.city = city;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
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
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "zipCode [id=" + id + ", city=" + city + ", note=" + note
				+ ", latitude=" + latitude + ", longitude=" + longitude + ", zipCode=" + zipCode + "]";
	}
	
	
	
}