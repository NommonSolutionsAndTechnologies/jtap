package projects.CTAP.pipelines;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

public class ParallelAgentOptPlanLocProbPipelineSpain implements Callable<Integer> {
	
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
	
	private static final Logger log = LogManager.getLogger(ParallelAgentOptPlanLocProbPipelineSpain.class);

	public static void main(String[] args) {
		System.exit(new CommandLine(new ParallelAgentOptPlanLocProbPipelineSpain()).execute(args));
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
		Solver ctapSolver = controller.getInjector().getInstance(Solver.class);
		ctapSolver.run(population,dataset);
		population.save();
		
		System.out.print("Flows \n");
		LinkTimeFlowDatasetJsonFactorySpain lfd = controller.getInjector().getInstance(LinkTimeFlowDatasetJsonFactorySpain.class);
		LinkTimeFlowSpain ltf = new LinkTimeFlowSpain(population,336d,lfd.run(),config);
		ltf.run();
		ltf.saveDb();
		
		return 1;
		
	}

}