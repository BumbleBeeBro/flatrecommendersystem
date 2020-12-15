package recommender.crawler.immowelt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Estate {

	@JsonProperty("id")
	public String id;

	@JsonProperty("title")
	public String title;

	@JsonProperty("address")
	public String address;

	@JsonProperty("price_cold")
	public Double priceCold;

	@JsonProperty("extra_costs")
	public Double extraCosts;

	@JsonProperty("living_space")
	public Double livingSpace;

	@JsonProperty("balcony")
	public boolean balcony;

	@JsonProperty("garden")
	public boolean garden;

	@JsonProperty("num_rooms")
	public Double numRooms;
}
