package core.graph.air;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.javatuples.Pair;
import org.neo4j.driver.AccessMode;
import config.Config;
import controller.Controller;
import core.graph.NodeGeoI;
import core.graph.air.gtfs.GTFSAir;
import core.graph.air.gtfs.AirNode;
import core.graph.air.gtfs.StopTime;
import core.graph.air.gtfs.Transfer;
import data.external.neo4j.Neo4jConnection;

/**
 * @author jbueno
 *
 */
public final class Utils {
	
	/**
	 * 
	 * @param gtfs
	 * @param database
	 * @param nodes : array containing the nodes types that have to be connected with the GTFS network
	 * @throws Exception
	 */
	public static void insertAirGTFSintoNeo4J(GTFSAir gtfs,String day) throws Exception {
		Config config = Controller.getConfig();
		String database = config.getNeo4JConfig().getDatabase();
		String tempDirectory = config.getGeneralConfig().getTempDirectory();
		data.external.neo4j.Utils.insertNodes(database,tempDirectory,gtfs.getStops());
		data.external.neo4j.Utils.insertLinks(database,tempDirectory,getAirLinks(gtfs,day)
				,AirLink.class,core.graph.air.gtfs.AirNode.class,"id","stop_from",core.graph.air.gtfs.AirNode.class,"id","stop_to");
		data.external.neo4j.Utils.insertLinks(database,tempDirectory,getAirTransferLinks(gtfs)
				,AirLink.class,core.graph.air.gtfs.AirNode.class,"id","stop_from",core.graph.air.gtfs.AirNode.class,"id","stop_to");
		try( Neo4jConnection conn = new Neo4jConnection()){  
			conn.query(database,"CREATE INDEX AirNodeIndex FOR (n:AirNode) ON (n.id)",AccessMode.WRITE);
		}
	}
	
	public static void insertAndConnectAirGTFSIntoNeo4J(GTFSAir gtfs,String day,Map<Class<? extends NodeGeoI>,String> nodeArrivalMap) throws Exception {
		Config config = Controller.getConfig();
		String database = config.getNeo4JConfig().getDatabase();
		insertAirGTFSintoNeo4J(gtfs,day);
		core.graph.Utils.setShortestDistCrossLink(AirNode.class,"id", nodeArrivalMap,1);
	}
	
	public static void deleteAirGTFS() throws Exception {
		Config config = Controller.getConfig();
		String database = config.getNeo4JConfig().getDatabase();
         try( Neo4jConnection conn = new Neo4jConnection()){  
        	conn.query(database,"Call apoc.periodic.iterate(\"cypher runtime=slotted Match (n:AirNode)-[r]->(m:AirNode) RETURN r limit 10000000\", \"delete r\",{batchSize:100000});",AccessMode.WRITE );
        	conn.query(database,"Call apoc.periodic.iterate(\"cypher runtime=slotted Match (n:AirNode)-[r]->(m) RETURN r limit 10000000\", \"delete r\",{batchSize:100000});",AccessMode.WRITE );
        	conn.query(database,"Call apoc.periodic.iterate(\"cypher runtime=slotted Match (n)-[r]->(m:AirNode) RETURN r limit 10000000\", \"delete r\",{batchSize:100000});",AccessMode.WRITE );
			conn.query(database,"Call apoc.periodic.iterate(\"cypher runtime=slotted Match (n:AirNode) RETURN n limit 10000000\", \"delete n\",{batchSize:100000});",AccessMode.WRITE );
			conn.query(database,"DROP INDEX AirNodeIndex IF EXISTS",AccessMode.WRITE );
         }
	}
	
	/**
	 * The connections are generated considering the trip. This means that connection
     * between two not consecutive stops are also considered if in the same trip
     * 
	 * a--->b--->c--->d
	 * \        /    /
	 *   ----->     /
	 *   \         /
	 *     ------>
	 *     
	 * If the not direct connections are not intended to be considered, this must 
	 * be specified in the config file 
	 *  
	 * 
	 * @param gtfs
	 * @return
	 */
	public static Map<Pair<String,String>, List<Connection>> getAirConnections(GTFSAir gtfs, Boolean directConnections, String day){
		List<Connection> connections = new ArrayList<>();
		List<StopTime> stopTime = gtfs.getStopTimes();
		Map<String, List<StopTime>> tripStops = null;
		if(day != null) {
			tripStops = stopTime.stream()
					 .filter(x -> x.getTripId().contains(day))
					 .sorted(Comparator.comparing(StopTime::getDepartureTime))
					 .collect(Collectors.groupingBy(StopTime::getTripId));
		}
		else {
			tripStops = stopTime.stream()
					 .sorted(Comparator.comparing(StopTime::getDepartureTime))
					 .collect(Collectors.groupingBy(StopTime::getTripId));
		}
		
		for (var entry : tripStops.entrySet()) {
			List<StopTime> st = entry.getValue();
		    for(int j=0;j<st.size()-1;j++) {
		    	Connection c = new Connection(st.get(j).getStopId(),
		    			st.get(j+1).getStopId(),
		    			st.get(j).getDepartureTime(),
		    			st.get(j+1).getArrivalTime().toSecondOfDay()-
	                    		  st.get(j).getDepartureTime().toSecondOfDay());
		    	connections.add(c);
		    }
		    //TODO avg travel time 
		    if(directConnections) {
		    	for(int j=2;j<st.size()-1;j++) {
			    	Connection c = new Connection(st.get(0).getStopId(),
			    			st.get(j).getStopId(),
			    			st.get(0).getDepartureTime(),
			    			st.get(j).getArrivalTime().toSecondOfDay()-
		                    		  st.get(0).getDepartureTime().toSecondOfDay());
			    	connections.add(c);
			    }
		    }
		}
		Map<Pair<String,String>, List<Connection>> connectionsMap = connections.stream()
				  .collect(Collectors.groupingBy(c -> new Pair<String,String>(c.getFrom(), c.getTo())));
		return connectionsMap;
	}
	
	
	/**
	 * @param gtfs
	 * @return
	 */
	public static List<AirLink> getAirLinks(GTFSAir gtfs,String day){
		List<AirLink> links = new ArrayList<>();
		Map<Pair<String,String>, List<Connection>> connections = getAirConnections(gtfs,false,day);
		for (var entry : connections.entrySet()) {
			String from = entry.getKey().getValue0();
			String to = entry.getKey().getValue1();
			Double avgTravelTime = entry.getValue().stream().mapToInt(Connection::getDuration).average().orElse(Double.NaN);
			Integer connectionsPerDay = (int)entry.getValue().size();
			LocalTime firstDepartureTime = entry.getValue().stream().
					map(Connection::getDepartureTime).min(LocalTime::compareTo).orElse(null);
			LocalTime lastDepartureTime = entry.getValue().stream().
					map(Connection::getDepartureTime).max(LocalTime::compareTo).orElse(null);
			links.add(new AirLink(from,to,avgTravelTime,connectionsPerDay,firstDepartureTime,lastDepartureTime,"air"));
		}
		return links;
	}
	
	/**
	 * @param gtfs
	 * @return
	 */
	public static List<AirTransferLink> getAirTransferLinks(GTFSAir gtfs){
		List<AirTransferLink> transferLinks = new ArrayList<>();
		List<Transfer> transfers = gtfs.getTransfers();
		List<Transfer> filteredTransfers = transfers
				  .stream()
				  .filter(e -> !e.getFromRouteId().equals(e.getToRouteId()))
				  .collect(Collectors.toList());
		Map<Pair<String,String>, List<Transfer>> transferMap = filteredTransfers.stream()
				  .collect(Collectors.groupingBy(c -> new Pair<String,String>(c.getFromStopId(), c.getToStopId())));
        for (var entry : transferMap.entrySet()) {
			String from = entry.getKey().getValue0();
			String to = entry.getKey().getValue1();
			Double avgTravelTime = entry.getValue().stream().mapToInt(Transfer::getMinTransferTime).average().orElse(0.);
			transferLinks.add(new AirTransferLink(from,to,avgTravelTime));
		}
		return transferLinks;
	}
	

}
