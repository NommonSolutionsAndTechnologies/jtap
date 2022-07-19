package projects.CTAP.pipelines;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import config.Config;
import controller.Controller;
import core.graph.NodeGeoI;
import core.graph.Activity.ActivityNode;
import core.graph.air.AirNode;
import core.graph.facility.osm.FacilityNode;
import core.graph.geo.CityNode;
import core.graph.population.StdAgentNodeImpl;
import core.graph.rail.gtfs.GTFS;
import core.graph.rail.gtfs.RailNode;
import core.graph.road.osm.RoadNode;
import picocli.CommandLine;
import projects.CTAP.attractiveness.normalized.DefaultAttractivenessModelImpl;
import projects.CTAP.attractiveness.normalized.DefaultAttractivenessModelVarImpl;
import projects.CTAP.attractiveness.normalized.SpainAttractivenessModelImpl;
import projects.CTAP.attractiveness.normalized.SpainAttractivenessModelVarImpl;
import projects.CTAP.graphElements.ActivityCityLink;
import projects.CTAP.transport.DefaultCTAPTransportLinkFactory;

public class ScenarioBuildingPipelineSpain_1 implements Callable<Integer> {
	
	@CommandLine.Command(
			name = "JTAP",
			description = "",
			showDefaultValues = true,
			mixinStandardHelpOptions = true
	)
	
	@CommandLine.Option(names = {"--configFile","-cf"}, description = "The .xml file containing the configurations")
	private Path configFile;
	
	@CommandLine.Option(names = "--threads", defaultValue = "4", description = "Number of threads to use concurrently")
	private int threads;
	
	private static final Logger log = LogManager.getLogger(ScenarioBuildingPipelineSpain_1.class);

	public static void main(String[] args) {
		System.exit(new CommandLine(new ScenarioBuildingPipelineSpain_1()).execute(args));
	}

	@Override
	public Integer call() throws Exception {
		
		Config config = Config.of(configFile.toFile()); 
		Controller controller = new Controller(config);
		controller.run();
		controller.emptyTempDirectory();
		
		System.out.print("Road \n");
		//Road------------------------------------------------------------------
		//core.graph.road.osm.Utils.setOSMRoadNetworkIntoNeo4j();
		
		System.out.print("GTFS \n");
		//insert GTFS-----------------------------------------------------------
		GTFS gtfs = controller.getInjector().getInstance(GTFS.class);
		core.graph.rail.Utils.deleteRailGTFS();
		core.graph.rail.Utils.insertRailGTFSintoNeo4J(gtfs,"2022-07-13");
		
		/*
		
		System.out.print("Air network \n");
		//insert air network
		core.graph.air.Utils.insertAirNetworkNeo4j();
		
		System.out.print("Cities \n");
		//insert cities---------------------------------------------------------
		core.graph.geo.Utils.insertCitiesIntoNeo4JFromCsv(CityNode.class);
		
		System.out.print("Facilities 1 \n");
		//create FacilityNodes from osm-----------------------------------------
		core.graph.facility.osm.Utils.facilitiesIntoNeo4jSpain(config);
		
		System.out.print("Facilities 2 \n");
		//connect FacilityNodes with Cities-------------------------------------
		Map<Class<? extends NodeGeoI>,String> facilityConnMap = new HashMap<>();
		facilityConnMap.put(CityNode.class,"city_id");
		core.graph.Utils.setShortestDistCrossLinkSpainFact(FacilityNode.class,"node_osm_id",facilityConnMap,3);
		
		System.out.print("Facilities 3 \n");
		//create the CityFacStatNodes-------------------------------------------
		core.graph.geo.Utils.addCityFacStatNodeSpain();
		
		System.out.print("Connections 0 \n");
		//Connections between AirNetwork RoadNetwork/RailNetwork---------------- and Cities
		Map<Class<? extends NodeGeoI>,String> airConnMap = new HashMap<>();
		airConnMap.put(RoadNode.class,"node_osm_id");
		//airConnMap.put(RailNode.class,"stop_id");
		///////////////////////////////////////
		airConnMap.put(CityNode.class,"city_id");
		//////////////////////////////////////
		core.graph.Utils.setShortestDistCrossLink(AirNode.class,"airport_id",airConnMap,3);
		
		System.out.print("Connections 1 \n");
		//Connections between RoadNetwork and RailNetwork-----------------------
		Map<Class<? extends NodeGeoI>,String> railConnMap = new HashMap<>();
		railConnMap.put(RoadNode.class,"node_osm_id");
		core.graph.Utils.setShortestDistCrossLink(RailNode.class,"stop_id",railConnMap,2);
		
		//////////////////////////////
		System.out.print("Connections 1-2 \n");
		//Connections between RailNetwork and Cities-----------------------
		Map<Class<? extends NodeGeoI>,String> railConnMap_city = new HashMap<>();
		railConnMap_city.put(CityNode.class,"city_id");
		core.graph.Utils.setShortestDistCrossLink(RailNode.class,"stop_id",railConnMap_city,3);
		//////////////////////////////
		
		System.out.print("Connections 2 \n");
		//Connections between Cities and RoadNetwork/RailNetwork----------------
		Map<Class<? extends NodeGeoI>,String> cityConnMap = new HashMap<>();
		cityConnMap.put(RoadNode.class,"node_osm_id");
		//cityConnMap.put(RailNode.class, "stop_id");
		core.graph.Utils.setShortestDistCrossLink(CityNode.class,"city_id",cityConnMap,3);
		
		System.out.print("Activities \n");
		//insert activities-----------------------------------------------------
		core.graph.Activity.Utils.insertActivitiesFromCsv(ActivityNode.class);
		core.graph.Activity.Utils.insertActivitiesLocFromCsv(ActivityCityLink.class);
		
		System.out.print("Population \n");
		//insert population---------------------------------------						--------------
		core.graph.population.Utils.insertStdPopulationFromCsv(StdAgentNodeImpl.class);
		
		System.out.print("Attractiveness \n");
		//insert attractiveness-------------------------------------------------
		projects.CTAP.attractiveness.normalized.Utils.insertAttractivenessNormalizedIntoNeo4jSpain(
				(SpainAttractivenessModelImpl)Controller.getInjector().getInstance(SpainAttractivenessModelImpl.class),
				new SpainAttractivenessModelVarImpl());
		
//		System.out.print("Transport links \n");
//		//insert transport links------------------------------------------------
//		DefaultCTAPTransportLinkFactory ctapTranspFactory = new DefaultCTAPTransportLinkFactory();
//		ctapTranspFactory.insertCTAPTransportLinkFactory(config.getCtapModelConfig()
//				.getTransportConfig().getCtapTransportLinkConfig());
//		
//		System.out.print("Destination Prob Link \n");
//		//insert destinationProbLinks-------------------------------------------
//		projects.CTAP.activityLocationSequence.Utils.insertDestinationProbIntoNeo4j();
		
		*/
		return 1;
	}
}