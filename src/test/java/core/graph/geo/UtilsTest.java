package core.graph.geo;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import config.Config;
import controller.Controller;
import core.graph.NodeGeoI;
import core.graph.rail.gtfs.GTFS;
import core.graph.rail.gtfs.Stop;


/**
 * @author jbueno
 *
 */

class UtilsTest {

	@Test
	void citiesTest() throws Exception {
		Config config = Config.of(new File("C:\\Users\\jbueno\\Desktop\\jtap\\config_.xml")); 
		System.out.print(config);
		Controller controller = new Controller(config);
		controller.run();
		controller.emptyTempDirectory();
		String db = "hellojtap";
		
		//insert city
		core.graph.geo.Utils.insertCitiesIntoNeo4JFromCsv(db,controller.getInjector().getInstance(Config.class),City.class);
		
		//connections
		Map<Class<? extends NodeGeoI>,String> cityConnMap = new HashMap<>();
		cityConnMap.put(Stop.class, "id");
		core.graph.Utils.setShortestDistCrossLink(db, config.getGeneralConfig().getTempDirectory(),City.class,"city",cityConnMap,true);
		
	}

}
