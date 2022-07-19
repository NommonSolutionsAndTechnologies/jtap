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

public class Ds2DsParametersFactory extends RoutesMap implements ParametersFactoryI{
	
	private final Config config;
	private final RoutingManager rm;
	private final String RAIL_ROAD_GRAPH = "rail-road-graph";
	private final String RAIL_GRAPH = "rail-graph";
	private final String ROAD_GRAPH = "road-graph";
	private final String AIR_RAIL_ROAD_GRAPH = "air-rail-road-graph";
	private final String AIR_RAIL_GRAPH = "air-rail-graph";
	private final String AIR_ROAD_GRAPH = "air-road-graph";
	private final String AIR_GRAPH = "air-graph";
	private final List<Long> citiesDs_ids;

	public Ds2DsParametersFactory(Config config,RoutingManager rm, List<Long> citiesDs_ids) {
		super(config, rm);
		this.config = config;
		this.rm = rm;
		this.citiesDs_ids = citiesDs_ids; 
	}
	
	public List<ParameterI> run(){
		    List<ParameterI> res = new ArrayList<>();
		    Ds2DsTravelCostParameter ds2dsTravelCostParameter = null;
		    Ds2DsPathParameter ds2dsPathParameter = null;
			/*
			 * projections ---------------------------------------------------------
			 */
		    /////////////////////////////
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
			/////////////////////////////
		    
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
			List<SourceRoutesRequest> ds2dsAirRailRoad = new ArrayList<>();
			List<SourceRoutesRequest> ds2dsRailRoad = new ArrayList<>();
			List<SourceRoutesRequest> ds2dsRail = new ArrayList<>();
			List<SourceRoutesRequest> ds2dsRoad = new ArrayList<>();
			List<SourceRoutesRequest> ds2dsAirRail = new ArrayList<>();
			List<SourceRoutesRequest> ds2dsAirRoad = new ArrayList<>();
			List<SourceRoutesRequest> ds2dsAir = new ArrayList<>();
			
			CityNode cityNode = new CityNode();
			citiesDs_ids.forEach(city ->{
				ds2dsAirRailRoad.add(this.new SourceRoutesRequest(AIR_RAIL_ROAD_GRAPH,cityNode,city,"weight",citiesDs_ids));
				ds2dsRailRoad.add(this.new SourceRoutesRequest(RAIL_ROAD_GRAPH,cityNode,city,"weight",citiesDs_ids));
				ds2dsRail.add(this.new SourceRoutesRequest(RAIL_GRAPH,cityNode,city,"weight",citiesDs_ids));
				ds2dsRoad.add(this.new SourceRoutesRequest(ROAD_GRAPH,cityNode,city,"weight",citiesDs_ids));
				ds2dsAirRail.add(this.new SourceRoutesRequest(AIR_RAIL_GRAPH,cityNode,city,"weight",citiesDs_ids));
				ds2dsAirRoad.add(this.new SourceRoutesRequest(AIR_ROAD_GRAPH,cityNode,city,"weight",citiesDs_ids));
				ds2dsAir.add(this.new SourceRoutesRequest(AIR_GRAPH,cityNode,city,"weight",citiesDs_ids));
			});
			
			
			/*
			 * Collecting routes ---------------------------------------------------
			 */
			try {
				this.addSourceRoutesWithPathsFromNeo4j(ds2dsAirRailRoad);
				this.addSourceRoutesWithPathsFromNeo4j(ds2dsRailRoad);
				this.addSourceRoutesWithPathsFromNeo4j(ds2dsRail);
				this.addSourceRoutesWithPathsFromNeo4j(ds2dsRoad);
				this.addSourceRoutesWithPathsFromNeo4j(ds2dsAirRail);
				this.addSourceRoutesWithPathsFromNeo4j(ds2dsAirRoad);
				this.addSourceRoutesWithPathsFromNeo4j(ds2dsAir);
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
			parameterDescription.add(citiesDs_ids);
			double[][][] travelCostParameter = this.toArrayCost(parameterDescription);
			List<Long>[][][] pathParameter = this.toArrayPath(parameterDescription);
			ds2dsTravelCostParameter = new Ds2DsTravelCostParameter(travelCostParameter,parameterDescription);
			ds2dsPathParameter = new Ds2DsPathParameter(pathParameter,parameterDescription);
			res.add(ds2dsTravelCostParameter);
			res.add(ds2dsPathParameter);
			
			try {
				this.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			return res;
	}

}
