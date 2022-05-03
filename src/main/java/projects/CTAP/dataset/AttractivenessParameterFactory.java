package projects.CTAP.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.neo4j.driver.AccessMode;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;

import core.dataset.ParameterFactoryI;
import core.dataset.ParameterI;

public class AttractivenessParameterFactory implements ParameterFactoryI {

	private final List<List<Long>> parameterDescription;

	public AttractivenessParameterFactory(List<Long> agents_ids, List<Long> activities_ids, List<Long> citiesDs_ids,
			List<Long> time) {
		this.parameterDescription = new ArrayList<>() {
			{
				add(agents_ids);
				add(citiesDs_ids);
				add(activities_ids);
				add(time);
			}
		};
	}

	@Override
	public ParameterI run() {

		float[][][][] parameter = new float[this.parameterDescription.get(0).size()][this.parameterDescription.get(1)
				.size()][this.parameterDescription.get(2).size()][this.parameterDescription.get(3).size()];

		StringBuilder cities = new StringBuilder();
		cities.append(" [");
		for (Long i : this.parameterDescription.get(1)) {
			System.out.print(i);
			cities.append("");
			cities.append(Long.toString(i));
			cities.append(",");
		}
		cities.setLength(cities.length() - 1);
		cities.append("] ");

		
		String query = "match (n:AgentNode)-[r:AttractivenessNormalizedLink]->(m:CityNode)" +
		                 " where m.city_id IN " + cities.toString()
		                 +" return n.agent_id,m.city_id,r.activity_id,r.time,r.attractiveness";
		
		System.out.print(query);
		System.out.print(" \n ");  
		List<Record> queryRes = null;
		try {
			queryRes = data.external.neo4j.Utils.runQuery(query, AccessMode.READ);
			System.out.print(" Read query\n ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Map<String,Float> map = new HashMap<>();
		for(Record rec:queryRes) {
			String key = String.valueOf(rec.values().get(0).asInt())+
					String.valueOf(rec.values().get(1).asInt())+
					String.valueOf(rec.values().get(2).asInt())+
					String.valueOf(rec.values().get(3).asInt());
			map.put( key,(float)rec.values().get(4).asDouble());
			System.out.print("point 0 \n");
			System.out.print(key);
			System.out.print(map);
		}
		System.out.print(map);
		System.out.print(" \n ");
		System.out.print(queryRes);
		System.out.print(" \n ");
		
		System.out.print("point A \n");
		for(int i=0;i<this.parameterDescription.get(0).size();i++) {
			System.out.print("point B \n");
			for(int j=0;j<this.parameterDescription.get(1).size();j++) {
				System.out.print("point C \n");
				for(int k=0;k<this.parameterDescription.get(2).size();k++) {
					System.out.print("point D \n");
					for(int t=0;t<this.parameterDescription.get(3).size();t++) {
						System.out.print("point E \n");
						
						System.out.print(i);
						System.out.print(" \n ");
						System.out.print(j);
						System.out.print(" \n ");
						System.out.print(k);
						System.out.print(" \n ");
						System.out.print(t);
						System.out.print(" \n ");
						
						String key = parameterDescription.get(0).get(i).toString()+
								parameterDescription.get(1).get(j).toString()+
								parameterDescription.get(2).get(k).toString()+
								parameterDescription.get(3).get(t).toString();
						System.out.print("point F \n");
						System.out.print(key);
						System.out.print(" \n ");
						parameter[i][j][k][t] = map.get(key);
					}
				}
			}
		}
		
		return new AttractivenessParameter(parameter,parameterDescription);
	}

}
