package projects.CTAP.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import config.Config;
import core.dataset.DatasetI;
import core.models.ModelI;
import core.solver.SolverImpl;
import projects.CTAP.dataset.Dataset;
import projects.CTAP.model.ObjectiveFunctionCTAP;
import projects.CTAP.population.Agent;
import projects.CTAP.population.Plan;
import projects.CTAP.population.Population;

public class Solver {
	
	private static final Logger log = LogManager.getLogger(Solver.class);
	
	private Config config;
	
	@Inject
	public Solver(Config config) {
		this.config = config;
	}
	
	
	public void run(Population population, DatasetI dataset) {
		
		int nThreads = this.config.getGeneralConfig().getThreads();
		ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		
		//split the agent list
		List<List<Agent>> agentsSL = new ArrayList<>();
		int nAgentsXThread = (int)Math.ceil(((double)population.getAgents().size())/((double)nThreads));
		int start = 0;
		int end = nAgentsXThread ;
		for(int i=0;i<nThreads;i++) {
			if (end >= population.getAgents().size()) {
				end = population.getAgents().size();
				agentsSL.add(population.getAgents().subList(start,end));
				break;
			}
			else {
				agentsSL.add(population.getAgents().subList(start,end));
				start = end;
				end = end+nAgentsXThread;
			}
		}	
		
		for(List<Agent> la: agentsSL) {
			executor.execute(new Task(la,dataset));
		}	
		
		awaitTerminationAfterShutdown(executor);
		
	}
	
	public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
	    threadPool.shutdown();
	    try {
	        if (!threadPool.awaitTermination(60, TimeUnit.DAYS)) {
	            threadPool.shutdownNow();
	        }
	    } catch (InterruptedException ex) {
	        threadPool.shutdownNow();
	        Thread.currentThread().interrupt();
	    }
	}
	
	private static final class Task implements Runnable {

		private List<Agent> agents;
		private Dataset dataset;

		private Task(List<Agent> agents,DatasetI dataset) {
			this.agents = agents;
			dataset = (Dataset)dataset;
		}
			

		@Override
		public void run() {
			log.info("Starting task optimization on thread: "+Thread.currentThread().getName()+". Number of agents :" + String.valueOf(agents.size()));
			for(Agent agent: this.agents) {
				List<Plan> agentPlans = new ArrayList<>();
				for (ModelI model: agent.getAgentModels()) {
					ObjectiveFunctionCTAP ofc = (ObjectiveFunctionCTAP) model.getObjectiveFunction();
					
//					System.out.print("New plan \n");
					
					SolverImpl si = new SolverImpl.Builder(model)
								.initialGuess(model.getInitialGuess())
								.build();
					
					PointValuePair pvp =  si.run();
					
					double[] t = pvp.getFirst();
					
					double[] ts = new double[t.length];
					double[] te = new double[t.length];
					
					for(int i =1;i<t.length;i++ ) {

						te[i-1] = ts[i-1] + t[i-1];
						ts[i] = te[i-1];
	
						if(i == t.length - 1 ) {
							te[i] = ts[i] + t[i];
						}
					}
					
					double[] output = new double[t.length * 2];
					
					for(int j =0; j<ts.length;j++) {
						output[j] = ts[j];
					}
					
					for(int j =0; j<te.length;j++) {
						output[j + ts.length ] = te[j];
					}					
					
					agentPlans.add(new Plan(ofc.getLocations(),ofc.getActivities(),output,pvp.getSecond()));
				}
				agent.setOptimalPlans(agentPlans);
			}
			log.info("Task optimization finished on thread: "+Thread.currentThread().getName());
		}
	}

}
