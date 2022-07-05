package config;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "newDatasetParams")
public class NewDatasetParams {
	
	private Integer destinationsPopThreshold;
	private String destinationsIDs;
	private String destinationsFacilityIDs;
	
	public NewDatasetParams() {}
	
	public NewDatasetParams(Integer destinationsPopThreshold,String destinationsIDs, String destinationsFacilityIDs) {
		this.destinationsPopThreshold = destinationsPopThreshold;
		this.destinationsIDs = destinationsIDs;
		this.destinationsFacilityIDs = destinationsFacilityIDs;
	}
	
	@XmlElement(name = "destinationsPopThreshold",required = true)
	public Integer getDestinationsPopThreshold() {
		return destinationsPopThreshold;
	}
	
	@XmlElement(name = "destinationsIDs",required = true)
	public String getDestinationsIDs() {
		return destinationsIDs;
	}
	
	@XmlElement(name = "destinationsFacilityIDs",required = true)
	public String getDestinationsFacilityIDs() {
		return destinationsFacilityIDs;
	}
	
	public void setDestinationsPopThreshold(Integer destinationsPopThreshold) {
		this.destinationsPopThreshold = destinationsPopThreshold;
	}

	public void setDestinationsIDs(String destinationsIDs) {
		this.destinationsIDs = destinationsIDs;
	}
	
	public void setDestinationsFacilityIDs(String destinationsFacilityIDs) {
		this.destinationsFacilityIDs = destinationsFacilityIDs;
	}
}
