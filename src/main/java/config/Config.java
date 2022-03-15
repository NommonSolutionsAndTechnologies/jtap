package config;

import java.io.File;

import com.google.inject.Inject;

import data.utils.io.XML;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;


@XmlRootElement(name = "config")
//@XmlAccessorType(XmlAccessType.FIELD)
public final class Config {
	
	private RailConfig railConfig;
	private GeneralConfig generalConfig;
	private GeoLocConfig geoLocConfig;
	private PopulationConfig populationConfig;
	private Neo4JConfig neo4JConfig;
	private RoutingConfig routingConfig;
	private ActivitiesConfig activitiesConfig;
	private CtapModelConfig ctapModelConfig;
	private AirConfig airConfig;
	
	@XmlElement(name = "railConfig")
	public RailConfig getRailConfig() {
		return this.railConfig;
	}
	
	@XmlElement(name = "airConfig")
	public AirConfig getAirConfig() {
		return this.airConfig;
	}
	
	@XmlElement(name = "generalConfig")
	public GeneralConfig getGeneralConfig() {
		return this.generalConfig;
	}
	
	@XmlElement(name = "geoLocConfig")
	public GeoLocConfig getGeoLocConfig() {
		return this.geoLocConfig;
	}
	
	@XmlElement(name = "populationConfig")
	public PopulationConfig getPopulationConfig() {
		return this.populationConfig;
	}
	
	@XmlElement(name = "neo4JConfig")
	public Neo4JConfig getNeo4JConfig() {
		return this.neo4JConfig;
	}
	
	@XmlElement(name = "routingConfig")
	public RoutingConfig getRoutingConfig() {
		return this.routingConfig;
	}
	
	@XmlElement(name = "activitiesConfig")
	public ActivitiesConfig getActivitiesConfig() {
		return this.activitiesConfig;
	}
	
	@XmlElement(name = "ctapModelConfig")
	public CtapModelConfig getCtapModelConfig() {
		return this.ctapModelConfig;
	}
	
	public void setRailConfig(RailConfig railConfig) {
		this.railConfig = railConfig;
	}
	public void setAirConfig(AirConfig airConfig) {
		this.airConfig = airConfig;
	}
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}
	public void setGeoLocConfig(GeoLocConfig geoLocConfig) {
		this.geoLocConfig = geoLocConfig;
	}
	public void setPopulationConfig(PopulationConfig populationConfig) {
		this.populationConfig = populationConfig;
	}
	public void setNeo4JConfig(Neo4JConfig neo4JConfig) {
		this.neo4JConfig = neo4JConfig;
	}
	public void setRoutingConfig(RoutingConfig routingConfig) {
		this.routingConfig = routingConfig;
	}
	public void setActivitiesConfig(ActivitiesConfig activitiesConfig) {
		this.activitiesConfig = activitiesConfig;
	}
	public void setCtapModelConfig(CtapModelConfig ctapModelConfig) {
		this.ctapModelConfig = ctapModelConfig;
	}
	
	@Inject
	public Config() {}
	
	public Config(RailConfig railConfig,AirConfig airConfig,GeneralConfig generalConfig,
			GeoLocConfig geoLocConfig,PopulationConfig populationConfig,Neo4JConfig neo4JConfig,
			RoutingConfig routingConfig,ActivitiesConfig activitiesConfig,
			CtapModelConfig ctapModelConfig) {
		this.railConfig = railConfig;
		this.airConfig = airConfig;
		this.generalConfig = generalConfig;
		this.geoLocConfig = geoLocConfig;
		this.populationConfig = populationConfig;
		this.neo4JConfig = neo4JConfig;
		this.routingConfig = routingConfig;
		this.activitiesConfig = activitiesConfig;
		this.ctapModelConfig = ctapModelConfig;
	}
	
	public static Config of(File file) {
	      return XML.read(file, Config.class);
	}
	
	public void write(File file) throws JAXBException {
		XML.write(file,this);
	}
}
