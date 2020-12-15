package recommender.web.restapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Implements REST model to send the contract.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {

	private String address;

	/**
	 * Constructor for ContractSending.
	 * @param customer_id, String. ID of associated customer.
	 * @param offer_id2, Integer. ID of selected insurance.
	 * @param contract, String. Text of associated contract.
	 */
	public Address(String address) {
		super();
		this.address = address;
	}

	public Address() {
		
	}

	//Attributes
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "address [address=" + address + "]";
	}
	
	
}
