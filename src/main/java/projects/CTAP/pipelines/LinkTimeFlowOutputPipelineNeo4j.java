package projects.CTAP.pipelines;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.AccessMode;

import config.Config;
import controller.AbstractModule;
import controller.Controller;
import core.dataset.DatasetFactoryI;
import core.dataset.DatasetI;
import core.population.PopulationFactoryI;
import picocli.CommandLine;
import projects.CTAP.model.ActivityLocationI;
import projects.CTAP.model.ProbDistEvenHomeActivityLocation;
import projects.CTAP.outputAnalysis.LinkTimeFlowSpain;
import projects.CTAP.outputAnalysis.LinkTimeFlowDatasetJsonFactorySpain;
import projects.CTAP.population.Population;
import projects.CTAP.population.PopulationFactory;
import projects.CTAP.population.PopulationSingleAgentFactory;
import projects.CTAP.solver.Solver;

public class LinkTimeFlowOutputPipelineNeo4j implements Callable<Integer> {
	
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
	
	private final String OUTPUT_FILE = "LinkTimeFlow";
	
	private static final Logger log = LogManager.getLogger(LinkTimeFlowOutputPipelineNeo4j.class);

	public static void main(String[] args) {
		System.exit(new CommandLine(new LinkTimeFlowOutputPipelineNeo4j()).execute(args));
	}

	@Override
	public Integer call() throws Exception {
		
		Config config = Config.of (configFile.toFile()); 
		Controller controller = new Controller(config);
		
		controller.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
            	 binder().bind(ActivityLocationI.class).to(ProbDistEvenHomeActivityLocation.class);
            }
        });
		
		controller.run();
		controller.emptyTempDirectory();
		
		System.out.print("Factory \n");
		PopulationFactoryI populationFactory = controller.getInjector().getInstance(PopulationFactoryI.class);
		//PopulationSingleAgentFactory populationFactory = controller.getInjector().getInstance(PopulationSingleAgentFactory .class);
		DatasetFactoryI datasetFactory = controller.getInjector().getInstance(DatasetFactoryI.class);
		DatasetI dataset = datasetFactory.run();
		Population population = (Population) populationFactory.run(dataset);
		
		System.out.print("Solver \n");
		//Solver ctapSolver = controller.getInjector().getInstance(Solver.class);
		//ctapSolver.run(population,dataset);
		//population.save();
		
		System.out.print("Flows \n");
		//LinkTimeFlowDatasetJsonFactorySpain lfd = controller.getInjector().getInstance(LinkTimeFlowDatasetJsonFactorySpain.class);
		//LinkTimeFlowSpain ltf = new LinkTimeFlowSpain(population,336d,lfd.run(),config);
		//ltf.run();
		//ltf.saveDb();
		
		data.external.neo4j.Utils.runQuery("match (n)-[r:CTAPTransportLink]->(m) SET r.flows = null", AccessMode.WRITE);
		data.external.neo4j.Utils.runQuery("USING PERIODIC COMMIT 1000 LOAD CSV FROM \"file:///"+config.getGeneralConfig().getOutputDirectory()+OUTPUT_FILE+".csv"+"\" AS row match (n)-[r]->(m) where ID(r) = toInteger(row[0]) set r.flows = apoc.convert.toIntList(row[1..])", AccessMode.WRITE);
	
		
		return 1;
		
	}

}