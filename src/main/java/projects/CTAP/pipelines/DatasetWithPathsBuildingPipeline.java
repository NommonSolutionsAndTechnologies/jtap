package projects.CTAP.pipelines;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import config.Config;
import controller.Controller;
import core.dataset.ModelElementI;
import core.dataset.ParameterFactoryI;
import core.dataset.ParameterI;
import core.graph.Activity.ActivityNode;
import core.graph.geo.CityNode;
import core.graph.population.StdAgentNodeImpl;
import core.graph.routing.RoutingManager;
import picocli.CommandLine;
import projects.CTAP.dataset.ActivitiesIndex;
import projects.CTAP.dataset.ActivityLocationCostParameterFactory;
import projects.CTAP.dataset.AgentActivityParameterFactory;
import projects.CTAP.dataset.AgentHomeLocationParameterFactory;
import projects.CTAP.dataset.AgentParametersFactory;
import projects.CTAP.dataset.AgentsIndex;
import projects.CTAP.dataset.AttractivenessParameterFactory;
import projects.CTAP.dataset.CitiesDsIndex;
import projects.CTAP.dataset.CitiesOsIndex;
import projects.CTAP.dataset.Ds2DsParametersFactory;
import projects.CTAP.dataset.Ds2OsParametersFactory;
import projects.CTAP.dataset.Os2DsParametersFactory;
import projects.CTAP.dataset.TimeIndex;

public class DatasetWithPathsBuildingPipeline implements Callable<Integer> {
	
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
	
	private static final Logger log = LogManager.getLogger(DatasetWithPathsBuildingPipeline.class);

	public static void main(String[] args) {
		System.exit(new CommandLine(new DatasetWithPathsBuildingPipeline()).execute(args));
	}

	@Override
	public Integer call() throws Exception {
		
		Config config = Config.of (configFile.toFile()); 
		Controller controller = new Controller(config);
		controller.run();
		controller.emptyTempDirectory();
		
		RoutingManager rm = controller.getInjector().getInstance(RoutingManager.class);
		
		System.out.print("indexes \n");
		//indexes
		List<Long> agents_ids = data.external.neo4j.Utils.importNodes(StdAgentNodeImpl.class).stream().map(x -> x.getId()).collect(Collectors.toList());
		List<Long> activities_ids = data.external.neo4j.Utils.importNodes(ActivityNode.class).stream().map(x -> x.getActivityId()).collect(Collectors.toList());
		List<CityNode> cities = data.external.neo4j.Utils.importNodes(CityNode.class);
		List<Long> citiesDs_ids = cities.stream().filter(e -> e.getId() == 0L).map(CityNode::getId).collect(Collectors.toList());    //just for test
		List<Long> citiesOs_ids = cities.stream().filter(e -> e.getId() == 3L).map(CityNode::getId).collect(Collectors.toList());     //just for test
		Integer initialTime = config.getCtapModelConfig().getAttractivenessModelConfig().getAttractivenessNormalizedConfig().getInitialTime();
		Integer finalTime = config.getCtapModelConfig().getAttractivenessModelConfig().getAttractivenessNormalizedConfig().getFinalTime();
		Integer intervalTime = config.getCtapModelConfig().getAttractivenessModelConfig().getAttractivenessNormalizedConfig().getIntervalTime();
		List<Long> time = LongStream.iterate(initialTime,i->i+intervalTime).limit(Math.round(finalTime/intervalTime)).boxed().collect(Collectors.toList());
		
		System.out.print(agents_ids);
		System.out.print(" \n");
		System.out.print(activities_ids);
		System.out.print(" \n");
		System.out.print(cities);
		System.out.print(" \n");
		System.out.print(citiesDs_ids);
		System.out.print(" \n");
		System.out.print(citiesOs_ids);
		System.out.print(" \n");
		
		
		
		
		System.out.print("factories \n");
		//factories
		//indexes
		AgentsIndex agentIndex = new AgentsIndex(agents_ids);
		ActivitiesIndex activitiesIndex = new ActivitiesIndex(activities_ids);
		CitiesDsIndex citiesDsIndex = new CitiesDsIndex(citiesDs_ids);
		CitiesOsIndex citiesOsIndex = new CitiesOsIndex(citiesOs_ids);
		TimeIndex timeIndex = new TimeIndex(time);
		//params
		List<? extends ParameterFactoryI> agentActivtyParams = AgentActivityParameterFactory.getAgentActivityParameterFactories(agents_ids,activities_ids);
		List<? extends ParameterFactoryI> agentParams = AgentParametersFactory.getAgentParameterFactories(agents_ids);
		AttractivenessParameterFactory attractivenessParams = new AttractivenessParameterFactory(agents_ids,activities_ids,citiesDs_ids,time);
		Os2DsParametersFactory osds = new Os2DsParametersFactory(config,rm,citiesOs_ids,citiesDs_ids);
		Ds2OsParametersFactory dsos = new Ds2OsParametersFactory(config,rm,citiesOs_ids,citiesDs_ids);
		Ds2DsParametersFactory dsds = new Ds2DsParametersFactory(config,rm,citiesDs_ids);
		List<Long> testCities = new ArrayList<>() {{add(3L);}};                     //just for test
		AgentHomeLocationParameterFactory agLoc = new AgentHomeLocationParameterFactory(agents_ids,testCities);
		List<Long> testActLoc = new ArrayList<>() {{add(0L);}};            //just for test
		ActivityLocationCostParameterFactory actLoc = new ActivityLocationCostParameterFactory(testActLoc,activities_ids);
		
		List<ParameterFactoryI> res = Stream.of(agentActivtyParams, agentParams)
                 .flatMap(x -> x.stream())
                 .collect(Collectors.toList());
		
		res.add(attractivenessParams);
		res.add(agLoc);
		res.add(actLoc);
		
		System.out.print("parameters \n");
		//parameters
		List<ModelElementI> prs = new ArrayList<>();
		
		List<ParameterI> osdsParams = osds.run();
		List<ParameterI> dsosParams = dsos.run();
		List<ParameterI> dsdsParams = dsds.run();
		prs.add(osdsParams.get(0));
		prs.add(osdsParams.get(1));
		prs.add(dsosParams.get(0));
		prs.add(dsosParams.get(1));
		prs.add(dsdsParams.get(0));
		prs.add(dsdsParams.get(1));
		System.out.print("1... \n");
		for(ParameterFactoryI pi:res) {
			prs.add(pi.run());
		}
		System.out.print("2... \n");
		prs.add(agentIndex);
		prs.add(activitiesIndex);
		prs.add(citiesDsIndex);
		prs.add(citiesOsIndex);
		prs.add(timeIndex);
		
		System.out.print("saving... \n");
		for(ModelElementI pr:prs) {
			pr.save();
		}
		
		rm.close();
		System.out.print("Finish \n");
		return 1;
	}

}

