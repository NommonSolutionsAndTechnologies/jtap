package config;

import java.io.Serializable;

import jakarta.xml.bind.annotation.*;


@XmlRootElement(name = "airConfig")
public class AirConfig implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String gtfsDirectory;
	private Boolean directConnections;
	
	public AirConfig() {
		
	}
	
    public AirConfig(String gtfsDirectory,Boolean directConnections) {
    	this.gtfsDirectory = gtfsDirectory;
    	this.directConnections = directConnections;
	}
	
    @XmlElement(name = "gtfsDirectory",required = true)
	public String getGTFSDirectory() {
		return this.gtfsDirectory;
	}
    
    @XmlElement(name = "directConnections",required = true)
   	public Boolean getDirectConnections() {
   		return this.directConnections;
   	}
    
    public void setGTFSDirectory(String gtfsDirectory) {
    	this.gtfsDirectory = gtfsDirectory;
    }
    
    public void setDirectConnections(Boolean directConnections) {
    	this.directConnections = directConnections ;
    }
	
	
	@Override
    public String toString() {
        return "airConfig{" +
                "gtfsDirectory='" + this.gtfsDirectory + "\n"+
                "directConnections='" + this.directConnections + "\n"+
                '}';
    }
	

}