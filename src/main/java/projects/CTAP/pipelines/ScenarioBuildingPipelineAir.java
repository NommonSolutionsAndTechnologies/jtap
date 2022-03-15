package projects.CTAP.pipelines;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import config.Config;
import controller.Controller;
import core.dataset.Dataset;
import core.dataset.DatasetI;
import core.dataset.RoutesMap;
import core.dataset.RoutesMap.SourceRoutesRequest;
import core.graph.LinkI;
import core.graph.NodeGeoI;
import core.graph.Activity.ActivityNode;
import core.graph.cross.CrossLink;
import core.graph.facility.osm.FacilityNode;
import core.graph.geo.CityNode;
import core.graph.population.StdAgentNodeImpl;
import core.graph.rail.RailLink;
import core.graph.rail.gtfs.GTFS;
import core.graph.rail.gtfs.RailNode;
import core.graph.air.AirLink;
import core.graph.air.gtfs.GTFSAir;
import core.graph.air.gtfs.AirNode;
import core.graph.road.osm.RoadLink;
import core.graph.road.osm.RoadNode;
import core.graph.routing.RoutingGraph;
import picocli.CommandLine;
import projects.CTAP.dataset.RoutesMapCTAP;

public class ScenarioBuildingPipelineAir implements Callable<Integer> {
	
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
	
	private static final Logger log = LogManager.getLogger(ScenarioBuildingPipelineAir.class);

	public static void main(String[] args) {
		System.exit(new CommandLine(new ScenarioBuildingPipelineAir()).execute(args));
	}

	@Override
	public Integer call() throws Exception {
		
		Config config = Config.of(configFile.toFile()); 
		Controller controller = new Controller(config);
		controller.run();
		controller.emptyTempDirectory();
		
		System.out.print("Road \n");
		//Road------------------------------------------------------------------
		// core.graph.road.osm.Utils.setOSMRoadNetworkIntoNeo4j();
		
		System.out.print("GTFS \n");
		//insert GTFS-----------------------------------------------------------
		GTFS gtfs = controller.getInjector().getInstance(GTFS.class);
		core.graph.rail.Utils.deleteRailGTFS();
		core.graph.rail.Utils.insertRailGTFSintoNeo4J(gtfs,"2019-10-06");
		
		System.out.print("AIR GTFS \n");
		//insert AIR GTFS-----------------------------------------------------------
		GTFSAir gtfsAir = controller.getInjector().getInstance(GTFSAir.class);
		core.graph.air.Utils.deleteAirGTFS();
		core.graph.air.Utils.insertAirGTFSintoNeo4J(gtfsAir,"20220210");
		
		System.out.print("Cities \n");
		//insert cities---------------------------------------------------------
		// core.graph.geo.Utils.insertCitiesIntoNeo4JFromCsv(CityNode.class);
		
		System.out.print("Facilities 1 \n");
		//create FacilityNodes from osm-----------------------------------------
		// core.graph.facility.osm.Utils.facilitiesIntoNeo4j(config);
		
		System.out.print("Facilities 2 \n");
		//connect FacilityNodes with Cities-------------------------------------
		Map<Class<? extends NodeGeoI>,String> facilityConnMap = new HashMap<>();
		facilityConnMap.put(CityNode.class,"city");
		// core.graph.Utils.setShortestDistCrossLink(FacilityNode.class,"node_osm_id",facilityConnMap,3);
		
		System.out.print("Facilities 3 \n");
		//create the CityFacStatNodes-------------------------------------------
		// core.graph.geo.Utils.addCityFacStatNode();
		
		System.out.print("Connections 1.1 \n");
		//Connections between RoadNetwork and RailNetwork-----------------------
		Map<Class<? extends NodeGeoI>,String> railConnMap = new HashMap<>();
		railConnMap.put(RoadNode.class,"node_osm_id");
		//core.graph.Utils.setShortestDistCrossLink(RailNode.class,"id",railConnMap,2);
		
		System.out.print("Connections 1.2 \n");
		//Connections between RoadNetwork and AirNetwork-----------------------
		Map<Class<? extends NodeGeoI>,String> airConnMap = new HashMap<>();
		airConnMap.put(RoadNode.class,"node_osm_id");
		core.graph.Utils.setShortestDistCrossLink(AirNode.class,"id",airConnMap,2);
		
		System.out.print("Connections 2 \n");
		//Connections between Cities and RoadNetwork/RailNetwork/AirNetwork----------------
		Map<Class<? extends NodeGeoI>,String> cityConnMap = new HashMap<>();
		cityConnMap.put(RoadNode.class,"node_osm_id");
		cityConnMap.put(RailNode.class, "id");
		cityConnMap.put(AirNode.class, "id");
		core.graph.Utils.setShortestDistCrossLink(CityNode.class,"city",cityConnMap,3);
		
		System.out.print("Activities \n");
		//insert activities-----------------------------------------------------
		// core.graph.Activity.Utils.insertActivitiesFromCsv(ActivityNode.class);
		
		System.out.print("Population \n");
		//insert population-----------------------------------------------------
		// core.graph.population.Utils.insertStdPopulationFromCsv(StdAgentNodeImpl.class);
		
		System.out.print("Attractiveness \n");
		//insert attractiveness-------------------------------------------------
		// projects.CTAP.attractiveness.normalized.Utils.insertAttractivenessNormalizedIntoNeo4j();
		
		System.out.print("OD matrix \n");
		//OD MATRIX-------------------------------------------------------------
		saveODMatrix();
		
		System.out.print("Finish");
		return 1;
	}
	
	public static void saveODMatrix() throws Exception {
		List<Class<? extends NodeGeoI>> nodes = new ArrayList<>();
		List<Class<? extends LinkI>> links = new ArrayList<>();
		nodes.add(CityNode.class);
		nodes.add(RoadNode.class);
		nodes.add(RailNode.class);
		nodes.add(AirNode.class);
		links.add(CrossLink.class);
		links.add(RoadLink.class);
		links.add(RailLink.class);
		links.add(AirLink.class);
		RoutingGraph rg = new RoutingGraph("air-rail-road-graph",nodes,links,"avg_travel_time");
		List<RoutingGraph> rgs = new ArrayList<RoutingGraph>();
		rgs.add(rg);
		Dataset dsi = (Dataset) Controller.getInjector().getInstance(DatasetI.class);
		RoutesMapCTAP rm = (RoutesMapCTAP) dsi.getMap(RoutesMap.ROUTES_MAP_KEY);
		rm.addProjections(rgs);
		List<SourceRoutesRequest> res = projects.CTAP.geolocClusters.Utils.getSRR_cluster1(rm,
				"air-rail-road-graph",
				Controller.getConfig().getCtapModelConfig().getPopulationThreshold());
		res = res.stream().skip(60).limit(3).collect(Collectors.toList());
		rm.addSourceRoutesFromNeo4j(res);
		rm.saveJson();
		rm.close();
	}
	
}