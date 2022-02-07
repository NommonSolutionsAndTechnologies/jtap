package core.graph.population;

import com.opencsv.bean.CsvBindByName;

import core.graph.NodeI;
import core.graph.annotations.GraphElementAnnotation.Neo4JNodeElement;
import core.graph.annotations.GraphElementAnnotation.Neo4JPropertyElement;
import core.graph.annotations.GraphElementAnnotation.Neo4JType;

@Neo4JNodeElement(labels={"AgentNode"})
public class StdAgentImpl implements NodeI {
	
	@CsvBindByName(column = "agent_id")
	@Neo4JPropertyElement(key="agent_id",type=Neo4JType.TOINTEGER)
	private Integer id;
	
	@CsvBindByName(column = "city")
	@Neo4JPropertyElement(key="city",type=Neo4JType.TOSTRING)
	private String city;
	
	@CsvBindByName(column = "l_age")
	@Neo4JPropertyElement(key="l_age",type=Neo4JType.TOINTEGER)
	private Integer lAge;
	
	@CsvBindByName(column = "h_age")
	@Neo4JPropertyElement(key="h_age",type=Neo4JType.TOINTEGER)
	private Integer hAge;
	
	@CsvBindByName(column = "gender")
	@Neo4JPropertyElement(key="gender",type=Neo4JType.TOSTRING)
	private String gender;
	
	@CsvBindByName(column = "l_income")
	@Neo4JPropertyElement(key="l_income",type=Neo4JType.TOFLOAT)
	private Double lIncome;
	
	@CsvBindByName(column = "h_income")
	@Neo4JPropertyElement(key="h_income",type=Neo4JType.TOFLOAT)
	private Double hIncome;
	
	@CsvBindByName(column = "count")
	@Neo4JPropertyElement(key="count",type=Neo4JType.TOFLOAT)
	private Integer count;
	
	public Integer getId() {
		return this.id;
	}
	
	public String getCity() {
		return this.city;
	}
	
	public Integer getLAge() {
		return this.lAge;
	}
	
	public Integer getHAge() {
		return this.hAge;
	}
	
	public String getGender() {
		return this.gender;
	}
	
	public Double getLIncome() {
		return this.lIncome;
	}
	
	public Double getHIncome() {
		return this.hIncome;
	}
	
	public Integer getCount() {
		return this.count;
	}
}
