package recommender.crawler.immowelt.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EstatesList {

	@JsonProperty("estates")
	public List<Estate> estates;

}
