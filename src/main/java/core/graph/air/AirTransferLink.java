package core.graph.air;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import core.graph.LinkI;
import core.graph.annotations.GraphElementAnnotation.Neo4JLinkElement;
import core.graph.annotations.GraphElementAnnotation.Neo4JNodeElement;
import core.graph.annotations.GraphElementAnnotation.Neo4JPropertyElement;
import core.graph.annotations.GraphElementAnnotation.Neo4JType;

@Neo4JLinkElement(label="AirTransferLink")
public class AirTransferLink implements LinkI {
	
	@CsvBindByName(column = "stop_from")
	@CsvBindByPosition(position = 0)
	@Neo4JPropertyElement(key="from",type=Neo4JType.TOSTRING)
	private String from;
	@CsvBindByName(column = "stop_to")
	@CsvBindByPosition(position = 1)
	@Neo4JPropertyElement(key="to",type=Neo4JType.TOSTRING)
	private String to;
	@CsvBindByName(column = "avg_travel_time")
	@CsvBindByPosition(position = 2)
	@Neo4JPropertyElement(key="avg_travel_time",type=Neo4JType.TOFLOAT)
	private Double avgTravelTime;
	
	
	public AirTransferLink() {}
	
	public AirTransferLink( String from,String to,Double avgTravelTime) {
		this.from = from;
		this.to = to;
		this.avgTravelTime = avgTravelTime;
	}
	
	public String getFrom() {
		return this.from;
	}
	
	public String getTo(){
		return this.to; 
	}
	
	public Double getAvgTravelTime(){
		return this.avgTravelTime; 
	}
}
