package core.graph.population;

import org.neo4j.driver.AccessMode;
import config.Config;
import core.graph.geo.City;
import data.external.neo4j.Neo4jConnection;


public class Utils {
	
	public static void insertStdPopulationFromCsv(String database,Config config,Class<StdAgentImpl> sai) throws Exception {
		//insert nodes 
		core.graph.Utils.insertNodesIntoNeo4J(database,config.getPopulationConfig().getAgentFile(),config.getGeneralConfig().getTempDirectory(),sai);
		data.external.neo4j.Utils.createIndex(database,"AgentNode","agent_id");
		
		try( Neo4jConnection conn = new Neo4jConnection()){  
			//city
			conn.query(database,data.external.neo4j.Utils.getLoadCSVLinkQuery(config.getPopulationConfig().getResidenceFile(),
					AgentGeoLink.class,StdAgentImpl.class,"agent_id","agent_id",City.class,"city","city"),AccessMode.WRITE );
			//families
			conn.query(database,data.external.neo4j.Utils.getLoadCSVLinkQuery(config.getPopulationConfig().getFamilyFile(),
					AgentFamilyLink.class,StdAgentImpl.class,"agent_id","agent_from_id",StdAgentImpl.class,"agent_id","agent_to_id"),AccessMode.WRITE );
			//friends
			conn.query(database,data.external.neo4j.Utils.getLoadCSVLinkQuery(config.getPopulationConfig().getFriendFile(),
					AgentFamilyLink.class,StdAgentImpl.class,"agent_id","agent_from_id",StdAgentImpl.class,"agent_id","agent_to_id"),AccessMode.WRITE );
		}
	}
}