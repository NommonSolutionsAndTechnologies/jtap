import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.external.neo4j.Neo4jConnection;
import org.neo4j.driver.Record;

import config.Config;
import controller.Controller;
import core.graph.NodeGeoI;
import core.graph.geo.City;
import core.graph.population.StdAgentImpl;
import core.graph.rail.gtfs.GTFS;
import core.graph.rail.gtfs.Stop;
import core.graph.road.osm.RoadNode;
import core.graph.facility.osm.FacilityNode;

import org.neo4j.driver.AccessMode;
import data.external.neo4j.Utils;

public class NommonTestMain {
	public static void main(String[] args) throws Exception {
		Config config = Config.of(new File("C:\\Users\\jbueno\\Desktop\\jtap\\config_.xml")); 
		System.out.print(config);
		Controller controller = new Controller(config);
		controller.run();
		controller.emptyTempDirectory();
		String db = "spaintest2";
		
		//insert cities----------------------------------------------------------------
		core.graph.geo.Utils.insertCitiesIntoNeo4JFromCsv(db,controller.getInjector().getInstance(Config.class),City.class);
		
		//create FacilityNodes from osm----------------------------------------------
		core.graph.facility.osm.Utils.facilitiesIntoNeo4j(db);
		
		//connect FacilityNodes with Cities----------------------------------------------
		Map<Class<? extends NodeGeoI>,String> facilityConnMap = new HashMap<>();
		facilityConnMap.put(City.class,"city");
		core.graph.Utils.setShortestDistCrossLink(db,config.getGeneralConfig().getTempDirectory(),FacilityNode.class,"node_osm_id",facilityConnMap,3);
		
		//create the CityFacStatNodes------------------------------------------------
		core.graph.geo.Utils.addCityFacStatNode(db);
		
		//insert agents--------------------------------------------------------------
		core.graph.population.Utils.insertStdPopulationFromCsv(db,controller.getInjector().getInstance(Config.class),StdAgentImpl.class);
		
		//Road-----------------------------------------------------------------------
		core.graph.road.osm.Utils.setOSMRoadNetworkIntoNeo4j(db);
		
		//insert GTFS
		GTFS gtfs = controller.getInjector().getInstance(GTFS.class);
		//core.graph.rail.Utils.deleteRailGTFS(db);
		String tempDirectory = config.getGeneralConfig().getTempDirectory();
		//data.external.neo4j.Utils.insertNodes(db,tempDirectory,gtfs.getStops());
		core.graph.rail.Utils.insertRailGTFSintoNeo4J(gtfs,db,controller.getInjector().getInstance(Config.class));
		
		//connections between subgraphs---------------------------------------------
		//connection between RoadNetwork and RailNetwork----------------------------
		Map<Class<? extends NodeGeoI>,String> railConnMap = new HashMap<>();
		railConnMap.put(RoadNode.class,"node_osm_id");
		core.graph.Utils.setShortestDistCrossLink(db, config.getGeneralConfig().getTempDirectory(),Stop.class,"id",railConnMap,2);
		
		//connection between Cities and RoadNetwork/RailNetwork----------------------------
		Map<Class<? extends NodeGeoI>,String> cityConnMap = new HashMap<>();
		cityConnMap.put(RoadNode.class,"node_osm_id");
		cityConnMap.put(Stop.class, "id");
		core.graph.Utils.setShortestDistCrossLink(db, config.getGeneralConfig().getTempDirectory(),City.class,"city",cityConnMap,3);

	}

}
