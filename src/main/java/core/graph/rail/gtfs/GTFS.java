package core.graph.rail.gtfs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.inject.Inject;

import config.Config;
import data.utils.io.CSV;

public final class GTFS {
	
	private List<Route> routes;
	private List<RailNode> railNodes;
	private List<StopTime> stopTimes;
	private List<Transfer> transfers;
	private List<Trip> trips;
	
	private final String ROUTESFILE = "routes.csv"; 
	private final String STOPSFILE = "stops.csv";
	private final String STOPSTIMESFILE = "stop_times.csv";
	private final String TRANSFERSFILE = "transfers.csv";
	private final String TRIPSFILE = "trips.csv";
	
	private final String gtfsDirectory;
	private Config config;
	
	@Inject
	public GTFS(Config config) throws IOException{
		
		this.config = config;
		gtfsDirectory = this.config.getRailConfig().getGTFSDirectory();
		initialize();
	} 
	
	private void initialize() throws IOException {
		this.routes = CSV.getList(new File(gtfsDirectory+ROUTESFILE),Route.class,1);
		this.railNodes = CSV.getListByHeaders(new File(gtfsDirectory+STOPSFILE),RailNode.class);
		this.stopTimes = CSV.getList(new File(gtfsDirectory+STOPSTIMESFILE),StopTime.class,1);
		this.transfers = CSV.getList(new File(gtfsDirectory+TRANSFERSFILE),Transfer.class,1);
		this.trips = CSV.getList(new File(gtfsDirectory+TRIPSFILE),Trip.class,1);
	}
	
	public List<Route> getRoutes(){
		return this.routes;
	}
	public List<RailNode> getStops(){
		return this.railNodes;
	}
	public List<StopTime> getStopTimes(){
		return this.stopTimes;
	}
	public List<Transfer> getTransfers(){
		return this.transfers;
	}
	public List<Trip> getTrips(){
		return this.trips;
	}

}
