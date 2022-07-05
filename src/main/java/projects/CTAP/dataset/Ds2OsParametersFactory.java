package projects.CTAP.dataset;

import java.util.ArrayList;
import java.util.List;

import config.Config;
import core.dataset.ParameterI;
import core.dataset.ParametersFactoryI;
import core.dataset.RoutesMap;
import core.dataset.RoutesMap.SourceRoutesRequest;
import core.graph.LinkI;
import core.graph.NodeGeoI;
import core.graph.cross.CrossLink;
import core.graph.geo.CityNode;
import core.graph.rail.RailLink;
import core.graph.rail.gtfs.RailNode;
import core.graph.road.osm.RoadLink;
import core.graph.road.osm.RoadNode;
import core.graph.air.AirNode;
import core.graph.routing.RoutingGraph;
import core.graph.routing.RoutingManager;
import projects.CTAP.graphElements.CTAPTransportLink;

public class Ds2OsParametersFactory extends RoutesMap implements ParametersFactoryI {

	private final Config config;
	private final RoutingManager rm;
	private final String RAIL_ROAD_GRAPH = "rail-road-graph";
	private final String RAIL_GRAPH = "rail-graph";
	private final String ROAD_GRAPH = "road-graph";
	private final String AIR_RAIL_ROAD_GRAPH = "air-rail-road-graph";
	private final String AIR_RAIL_GRAPH = "air-rail-graph";
	private final String AIR_ROAD_GRAPH = "air-road-graph";
	private final String AIR_GRAPH = "air-graph";
	private final List<Long> citiesOs_ids;
	private final List<Long> citiesDs_ids;
	
	
	public Ds2OsParametersFactory(Config config,RoutingManager rm,List<Long> citiesOs_ids,List<Long> citiesDs_ids) {
		super(config, rm);
		this.config = config;
		this.rm = rm;
		this.citiesOs_ids = citiesOs_ids;
		this.citiesDs_ids = citiesDs_ids;
		// TODO Auto-generated constructor stub
	}


	@Override
	public List<ParameterI> run() {
		
		List<ParameterI> res = new ArrayList<>();
		Ds2OsTravelCostParameter ds2osTravelCostParameter = null;
		Ds2OsPathParameter ds2osPathParameter = null;
		/*
		 * projections ---------------------------------------------------------
		 */
		///////////////////////
		//air-rail-road-graph
		List<Class<? extends NodeGeoI>> nodesAirRailRoadGraph = new ArrayList<>();
		List<Class<? extends LinkI>> linksAirRailRoadGraph = new ArrayList<>();
		nodesAirRailRoadGraph.add(CityNode.class);
		nodesAirRailRoadGraph.add(RoadNode.class);
		nodesAirRailRoadGraph.add(RailNode.class);
		nodesAirRailRoadGraph.add(AirNode.class);
		linksAirRailRoadGraph.add(CTAPTransportLink.class);
		
		try {
			this.addProjection(new RoutingGraph(AIR_RAIL_ROAD_GRAPH,nodesAirRailRoadGraph,linksAirRailRoadGraph,"weight"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		/////////////////////////
		
		//rail-road-graph
		List<Class<? extends NodeGeoI>> nodesRailRoadGraph = new ArrayList<>();
		List<Class<? extends LinkI>> linksRailRoadGraph = new ArrayList<>();
		nodesRailRoadGraph.add(CityNode.class);
		nodesRailRoadGraph.add(RoadNode.class);
		nodesRailRoadGraph.add(RailNode.class);
		linksRailRoadGraph.add(CTAPTransportLink.class);
		
		try {
			this.addProjection(new RoutingGraph(RAIL_ROAD_GRAPH,nodesRailRoadGraph,linksRailRoadGraph,"weight"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//rail-graph
		List<Class<? extends NodeGeoI>> nodesRail = new ArrayList<>();
		List<Class<? extends LinkI>> linksRail = new ArrayList<>();
		nodesRail.add(CityNode.class);
		nodesRail.add(RailNode.class);
		linksRail.add(CTAPTransportLink.class);
		
		try {
			this.addProjection(new RoutingGraph(RAIL_GRAPH,nodesRail,linksRail,"weight"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		//road-graph
		List<Class<? extends NodeGeoI>> nodesRoadGraph = new ArrayList<>();
		List<Class<? extends LinkI>> linksRoadGraph = new ArrayList<>();
		nodesRoadGraph.add(CityNode.class);
		nodesRoadGraph.add(RoadNode.class);
		linksRoadGraph.add(CTAPTransportLink.class);
		
		try {
			this.addProjection(new RoutingGraph(ROAD_GRAPH,nodesRoadGraph,linksRoadGraph,"weight"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//////////////////////////////////////////////////

		//air-rail-graph
		List<Class<? extends NodeGeoI>> nodesAirRailGraph = new ArrayList<>();
		List<Class<? extends LinkI>> linksAirRailGraph = new ArrayList<>();
		nodesAirRailGraph.add(CityNode.class);
		nodesAirRailGraph.add(RailNode.class);
		nodesAirRailGraph.add(AirNode.class);
		linksAirRailGraph.add(CTAPTransportLink.class);
		
		try {
			this.addProjection(new RoutingGraph(AIR_RAIL_GRAPH,nodesAirRailGraph,linksAirRailGraph,"weight"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		//air-road-graph
		List<Class<? extends NodeGeoI>> nodesAirRoadGraph = new ArrayList<>();
		List<Class<? extends LinkI>> linksAirRoadGraph = new ArrayList<>();
		nodesAirRoadGraph.add(CityNode.class);
		nodesAirRoadGraph.add(RoadNode.class);
		nodesAirRoadGraph.add(AirNode.class);
		linksAirRoadGraph.add(CTAPTransportLink.class);
		
		try {
			this.addProjection(new RoutingGraph(AIR_ROAD_GRAPH,nodesAirRoadGraph,linksAirRoadGraph,"weight"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		//air-graph
		List<Class<? extends NodeGeoI>> nodesAirGraph = new ArrayList<>();
		List<Class<? extends LinkI>> linksAirGraph = new ArrayList<>();
		nodesAirGraph.add(CityNode.class);
		nodesAirGraph.add(AirNode.class);
		linksAirGraph.add(CTAPTransportLink.class);
		
		try {
			this.addProjection(new RoutingGraph(AIR_GRAPH,nodesAirGraph,linksAirGraph,"weight"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		///////////////////////////////////////////////////
		
		/*
		 * SourceRoutesRequest -------------------------------------------------
		 */
		List<SourceRoutesRequest> os2dsAirRailRoad = new ArrayList<>();
		List<SourceRoutesRequest> os2dsRailRoad = new ArrayList<>();
		List<SourceRoutesRequest> os2dsRail = new ArrayList<>();
		List<SourceRoutesRequest> os2dsRoad = new ArrayList<>();
		List<SourceRoutesRequest> os2dsAirRail = new ArrayList<>();
		List<SourceRoutesRequest> os2dsAirRoad = new ArrayList<>();
		List<SourceRoutesRequest> os2dsAir = new ArrayList<>();
		
		CityNode cityNode = new CityNode();
		citiesDs_ids.forEach(city ->{
			os2dsAirRailRoad.add(this.new SourceRoutesRequest(AIR_RAIL_ROAD_GRAPH,cityNode,city,"weight",citiesOs_ids));
			os2dsRailRoad.add(this.new SourceRoutesRequest(RAIL_ROAD_GRAPH,cityNode,city,"weight",citiesOs_ids));
			os2dsRail.add(this.new SourceRoutesRequest(RAIL_GRAPH,cityNode,city,"weight",citiesOs_ids));
			os2dsRoad.add(this.new SourceRoutesRequest(ROAD_GRAPH,cityNode,city,"weight",citiesOs_ids));
			os2dsAirRail.add(this.new SourceRoutesRequest(AIR_RAIL_GRAPH,cityNode,city,"weight",citiesOs_ids));
			os2dsAirRoad.add(this.new SourceRoutesRequest(AIR_ROAD_GRAPH,cityNode,city,"weight",citiesOs_ids));
			os2dsAir.add(this.new SourceRoutesRequest(AIR_GRAPH,cityNode,city,"weight",citiesOs_ids));
		});
		
		/*
		 * Collecting routes ---------------------------------------------------
		 */
		try {
			this.addSourceRoutesWithPathsFromNeo4j(os2dsAirRailRoad);
			this.addSourceRoutesWithPathsFromNeo4j(os2dsRailRoad);
			this.addSourceRoutesWithPathsFromNeo4j(os2dsRail);
			this.addSourceRoutesWithPathsFromNeo4j(os2dsRoad);
			this.addSourceRoutesWithPathsFromNeo4j(os2dsAirRail);
			this.addSourceRoutesWithPathsFromNeo4j(os2dsAirRoad);
			this.addSourceRoutesWithPathsFromNeo4j(os2dsAir);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		/*
		 * Parameter array -----------------------------------------------------
		 */
		List<List<Long>> parameterDescription = new ArrayList<>();
		List<Long> projections = new ArrayList<>();
		projections.add(0L);
		projections.add(1L);
		projections.add(2L);
		projections.add(3L);
		projections.add(4L);
		projections.add(5L);
		projections.add(6L);
		parameterDescription.add(projections);
		parameterDescription.add(citiesDs_ids);
		parameterDescription.add(citiesOs_ids);
		double[][][] parameter = this.toArrayCost(parameterDescription);
		List<Long>[][][] pathParameter = this.toArrayPath(parameterDescription);
		ds2osTravelCostParameter = new Ds2OsTravelCostParameter(parameter,parameterDescription);
		ds2osPathParameter = new Ds2OsPathParameter(pathParameter,parameterDescription);
		res.add(ds2osTravelCostParameter);
		res.add(ds2osPathParameter);
		
		try {
			this.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return res;
	}

}
