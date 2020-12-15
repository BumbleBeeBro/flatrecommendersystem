package recommender.crawler.mietcheck.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Plz {

	@JsonProperty("plz")
	public int plz;

	@JsonProperty("price")
	public double price;
}
