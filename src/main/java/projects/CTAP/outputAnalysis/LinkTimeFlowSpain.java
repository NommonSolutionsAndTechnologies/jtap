package projects.CTAP.outputAnalysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.neo4j.driver.AccessMode;
import org.neo4j.driver.Record;

import config.Config;
import core.dataset.DatasetI;
import projects.CTAP.population.Agent;
import projects.CTAP.population.Plan;
import projects.CTAP.population.Population;


public class LinkTimeFlowSpain {
	
	private final double timeInterval;
	private final Population population;
	private final LinkTimeFlowDatasetSpain linkTimeFlowDataset;
	private Map<Long,AtomicIntegerArray> resMap = new ConcurrentHashMap<>();
	private final Config config;
	private final String OUTPUT_FILE = "LinkTimeFlow";
	private final static Integer YEAR_HOURS = 8760;
	
	public  LinkTimeFlowSpain(Population population,double timeInterval,LinkTimeFlowDatasetSpain linkTimeFlowDataset,Config config) {
		this.timeInterval = timeInterval;
		this.population = population;
		this.linkTimeFlowDataset = linkTimeFlowDataset;
		this.config = config;
		//kkkk
	}
	
	public void run() throws Exception {
		
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
			executor.execute(new Task(la,this.timeInterval,this.linkTimeFlowDataset,this.resMap));
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
	
	public void saveCsvBase() throws IOException {
		File file = new File(config.getGeneralConfig().getOutputDirectory()+OUTPUT_FILE+".csv");
		 try {
		        FileWriter writer = new FileWriter(file);
		        for(Map.Entry<Long,AtomicIntegerArray> entry : resMap.entrySet()) {
		        	StringBuffer s = new StringBuffer();
		        	s.append(entry.getKey().toString());
		        	s.append(",");
		        	for(int i = 0; i < entry.getValue().length()-1 ;i++) {
		        		s.append(entry.getValue().get(i));
		        		s.append(",");
		        	}
		        	s.append(entry.getValue().get(entry.getValue().length()-1));
		        	s.append("\n");
		        	writer.write(s.toString());
		        }
		        writer.close();
	    } catch(Exception e) {
	        file.delete();
	    }
	}
	
	public void saveDb() throws Exception {
		saveCsvBase();
		data.external.neo4j.Utils.runQuery("match (n)-[r:CTAPTransportLink]->(m) SET r.flows = null", AccessMode.WRITE);
		data.external.neo4j.Utils.runQuery("USING PERIODIC COMMIT 1000 LOAD CSV FROM \"file:///"+config.getGeneralConfig().getOutputDirectory()+OUTPUT_FILE+".csv"+"\" AS row match (n)-[r]->(m) where ID(r) = toInteger(row[0]) set r.flows = apoc.convert.toIntList(row[1..])", AccessMode.WRITE);
	}
	
	private static final class Task implements Runnable {
		
		private List<Agent> agents;
		private LinkTimeFlowDatasetSpain dataset;
		private Map<Long,AtomicIntegerArray> resMap;
		private double timeInterval;

		private Task(List<Agent> agents,double timeInterval,DatasetI dataset,Map<Long,AtomicIntegerArray> resMap ) {
			this.agents = agents;
			this.dataset = (LinkTimeFlowDatasetSpain)dataset;
			this.resMap = resMap;
			this.timeInterval = timeInterval;
		}
			

		@Override
		public void run() {
			double lambda = 2;
			for(Agent agent: this.agents) {
				boolean homeDs = dataset.getCitiesDsIndex().getIndex().contains(agent.getLocationId());
				Plan bestPlan = agent.getOptimalPlans().stream()
						.max(Comparator.comparingDouble(Plan::getValue)).get();
				
				int agentSize = dataset.getAgentHomeLocationParameter()
								.getParameter()[dataset.getAgentHomeLocationParameter().getParameterDescription().get(0).indexOf(agent.getAgentId())]
												[dataset.getAgentHomeLocationParameter().getParameterDescription().get(1).indexOf(agent.getLocationId())];
				
				for(int i = 0;i<bestPlan.getLocations().length-1;i++) {
					List<Long> links = null;
					
					// mode_list = ['air-rail-road', 'rail-road', 'rail', 'road', 'air-rail', 'air-road', 'air']
					
					if(i%2 == 0) {
						if(homeDs) {
							/////
							double cost_all = dataset.getDs2DsTravelCostParameter()
									.getParameter()[0][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							double cost_rail = dataset.getDs2DsTravelCostParameter()
									.getParameter()[2][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							double cost_road = dataset.getDs2DsTravelCostParameter()
									.getParameter()[3][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							double cost_air = dataset.getDs2DsTravelCostParameter()
									.getParameter()[6][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							String projection_str = modalSplitNew(lambda, cost_all, cost_rail, cost_road, cost_air);
							System.out.println(projection_str);
							
							int projection = 0;
							if(projection_str == "all") {
								projection = 0;
							}
							else if(projection_str == "rail") {
								projection = 2;
								System.out.println("rail");
							}
							else if(projection_str == "road") {
								projection = 3;
							}
							else if(projection_str == "air") {
								projection = 6;
							}
							/////							
							
							links = dataset.getDs2DsPathParameter()
                        			.getParameter()[projection][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
						}
						else {
							/////
							double cost_all = dataset.getOs2DsTravelCostParameter()
									.getParameter()[0][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							double cost_rail = dataset.getOs2DsTravelCostParameter()
									.getParameter()[2][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							double cost_road = dataset.getOs2DsTravelCostParameter()
									.getParameter()[3][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							double cost_air = dataset.getOs2DsTravelCostParameter()
									.getParameter()[6][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							String projection_str = modalSplitNew(lambda, cost_all, cost_rail, cost_road, cost_air);
							System.out.println(projection_str);
							
							int projection = 0;
							if(projection_str == "all") {
								projection = 0;
							}
							else if(projection_str == "rail") {
								projection = 2;
								System.out.println("rail");
							}
							else if(projection_str == "road") {
								projection = 3;
							}
							else if(projection_str == "air") {
								projection = 6;
							}
							/////
							
							links = dataset.getOs2DsPathParameter()
                        			.getParameter()[projection][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
						}
					}
					else {
                        if(homeDs) {
                        	
							/////
							double cost_all = dataset.getDs2DsTravelCostParameter()
									.getParameter()[0][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							double cost_rail = dataset.getDs2DsTravelCostParameter()
									.getParameter()[2][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							double cost_road = dataset.getDs2DsTravelCostParameter()
									.getParameter()[3][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							double cost_air = dataset.getDs2DsTravelCostParameter()
									.getParameter()[6][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							String projection_str = modalSplitNew(lambda, cost_all, cost_rail, cost_road, cost_air);
							System.out.println(projection_str);
							
							int projection = 0;
							if(projection_str == "all") {
								projection = 0;
							}
							else if(projection_str == "rail") {
								projection = 2;
								System.out.println("rail");
							}
							else if(projection_str == "road") {
								projection = 3;
							}
							else if(projection_str == "air") {
								projection = 6;
							}
							/////
                   
                        	
                        	links = dataset.getDs2DsPathParameter()
                        			.getParameter()[projection][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
						}
						else {
							
							/////
							double cost_all = dataset.getDs2OsTravelCostParameter()
									.getParameter()[0][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							double cost_rail = dataset.getDs2OsTravelCostParameter()
									.getParameter()[2][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							double cost_road = dataset.getDs2OsTravelCostParameter()
									.getParameter()[3][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							double cost_air = dataset.getDs2OsTravelCostParameter()
									.getParameter()[6][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
							
							String projection_str = modalSplitNew(lambda, cost_all, cost_rail, cost_road, cost_air);
							System.out.println(projection_str);
							int projection = 0;
							if(projection_str == "all") {
								projection = 0;
							}
							else if(projection_str == "rail") {
								projection = 2;
								System.out.println("rail");
							}
							else if(projection_str == "road") {
								projection = 3;
							}
							else if(projection_str == "air") {
								projection = 6;
							}
							////							
							
							
							links = dataset.getDs2OsPathParameter()
                        			.getParameter()[projection][bestPlan.getLocations()[i]][bestPlan.getLocations()[i+1]];
						}
					}
					
					for(Long link: links) {
						//TODO change 27 
						resMap.putIfAbsent(link, new AtomicIntegerArray((int) Math.ceil(YEAR_HOURS/timeInterval)));
						//TODO avoid negative index
						//resMap.get(link).addAndGet(0, 1);
						//resMap.get(link).incrementAndGet((int) Math.floor(bestPlan.getTs()[i]/timeInterval));
						if(bestPlan.getTs()[i] >= 8760 ) {
							System.out.print("error \n");
						}
						resMap.get(link).addAndGet((int) Math.floor(bestPlan.getTs()[i]/timeInterval), agentSize);
					}
				}
			}
			
		}
		
		
	}
	
	public static String modalSplitNew(double lambda, double cost_all, double cost_rail, double cost_road, double cost_air) {
		List<Double> costAll = new ArrayList<>();
		boolean air = false;
		boolean road = false;
		boolean rail = false;
		boolean all = false;
		
		if( cost_all == cost_rail || cost_all == cost_road || cost_all == cost_air) {
		}
		else {
			cost_all = cost_all + (120 + 60) / 2;
			all = true;
		}
		
		if( cost_air > -1) {
			cost_air = cost_air + 120;
			air = true;
		}
		
		if( cost_road > -1) {
			cost_road = cost_road + 0;
			road = true;
		}
		if( cost_rail > -1) {
			cost_rail = cost_rail - 60 + 60;
			rail = true;
		}
		
		
		if( air) {
			costAll.add(cost_air / cost_all);
		}
		
		if( road) {
			costAll.add(cost_road / cost_all);
		}
		if( rail) {
			costAll.add(cost_rail / cost_all);
		}
		
		if( all) {
			costAll.add(cost_all / cost_all);
		}
		
		double prob0 = 0;
		double prob1 = 0;
		double prob2 = 0;
		double prob3 = 0;
		
		if(air) {
			prob0 = logitModel(lambda, cost_air / cost_all, costAll);
		}
		else {
			prob0 = 0;
		}
		
		if(road) {
			prob1 = logitModel(lambda, cost_road / cost_all, costAll) + prob0;
		}
		else {
			prob1 = prob0;
		}
		
		if(rail) {
			prob2 = logitModel(lambda, cost_rail / cost_all, costAll) + prob1;
		}
		else {
			prob2 = prob1;
		}
		
		if(all) {
			prob3 = logitModel(lambda, cost_all / cost_all, costAll) + prob2;
		}
		else {
			prob3 = prob2;
		}
		
		double rr = Math.random();
		
		if(rr < prob0) {
        	return "air";
        }
		else if (rr < prob1) {
			return "road";
		}
		else if (rr < prob2) {
			return "rail";
		}
		else {
			return "all";
		}
		
	}
	
	public static String modalSplit(double lambda, double cost_all, double cost_rail, double cost_road, double cost_air) {
		if(cost_air == -1) {
			int projection = modalSplit3(lambda, cost_all, cost_rail, cost_road);
			if(projection == 2) {
				return "road";
			}
			else if(projection == 1) {
				return "rail";
			}
			else {
				return "all";
			}
			
		}
		else if(cost_road == -1) {
			int projection = modalSplit3(lambda, cost_all, cost_rail, cost_air);
			if(projection == 2) {
				return "air";
			}
			else if(projection == 1) {
				return "rail";
			}
			else {
				return "all";
			}
			
		}
		else if(cost_rail == -1) {
			int projection = modalSplit3(lambda, cost_all, cost_road, cost_air);
			if(projection == 2) {
				return "air";
			}
			else if(projection == 1) {
				return "road";
			}
			else {
				return "all";
			}
			
		}
		else if(cost_all == cost_air || cost_all == cost_rail || cost_all == cost_road) {
			int projection = modalSplit3(lambda, cost_rail, cost_road, cost_air);
			if(projection == 2) {
				return "air";
			}
			else if(projection == 1) {
				return "road";
			}
			else {
				return "rail";
			}
		}
		
		else {
			List<Double> costAll = new ArrayList<>(){
	            {
	                add(cost_all / cost_all);
	                add(cost_rail / cost_all);
	                add(cost_road / cost_all);
	                add(cost_air / cost_all);
	            }
	        };
	        
	        double prob0 = logitModel(lambda, cost_all / cost_all, costAll);
	        double prob1 = logitModel(lambda, cost_rail / cost_all, costAll) + prob0;
	        double prob2 = logitModel(lambda, cost_road / cost_all, costAll) + prob0 + prob1;
	        double rr = Math.random();
	        
	        if(rr < prob0) {
	        	return "all";
	        }
	        else {
	        	if(rr < prob1) {
	        		return "rail";
	        	}
	        	else {
	        		if(rr < prob2) {
		        		return "road";
		        	}
		        	else {
		        		return "air";
		        	}
	        	}
	        }
			
		}
		
	}
		
	public static int modalSplit3(double lambda, double cost0, double cost1, double cost2) {
		if(cost2 == -1) {
        	if(cost1 == -1) {
        		return 0;
        	}
        	else {
        		if (cost0 == cost1) {
        			return 0;
        		}
        		else {
        			List<Double> costAll = new ArrayList<>(){
        	            {
        	                add(cost0 / cost1);
        	                add(cost1 / cost1);
        	            }
        	        };
        	        
        	        double prob0 = logitModel(lambda, cost0 / cost1, costAll);
        	        double rr = Math.random();
        	        
        	        if(rr < prob0) {
        	        	return 0;
        	        }
        	        else {
        	        	return 1;
        	        }
        	        		
        			
        		}
        	}
        	
        }
		else if(cost1 == -1) {
			if(cost2 == cost0) {
				return 0;
			}
			else {
				List<Double> costAll = new ArrayList<>(){
    	            {
    	                add(cost0 / cost2);
    	                add(cost2 / cost2);
    	            }
    	        };
    	        
    	        double prob0 = logitModel(lambda, cost0 / cost2, costAll);
    	        double rr = Math.random();
    	        
    	        if(rr < prob0) {
    	        	return 0;
    	        }
    	        else {
    	        	return 2;
    	        }
				
			}
			
		}
		else {
			if(cost0 == cost1) {
				List<Double> costAll = new ArrayList<>(){
    	            {
    	                add(cost0 / cost2);
    	                add(cost2 / cost2);
    	            }
    	        };
    	        
    	        double prob0 = logitModel(lambda, cost0 / cost2, costAll);
    	        double rr = Math.random();
    	        
    	        if(rr < prob0) {
    	        	return 0;
    	        }
    	        else {
    	        	return 2;
    	        }
				
			}
			else if(cost0 == cost2) {
				List<Double> costAll = new ArrayList<>(){
    	            {
    	                add(cost0 / cost1);
    	                add(cost1 / cost1);
    	            }
    	        };
    	        
    	        double prob0 = logitModel(lambda, cost0 / cost1, costAll);
    	        double rr = Math.random();
    	        
    	        if(rr < prob0) {
    	        	return 0;
    	        }
    	        else {
    	        	return 1;
    	        }
			}
			else {
				List<Double> costAll = new ArrayList<>(){
    	            {
    	                add(cost0 / cost2);
    	                add(cost1 / cost2);
    	                add(cost2 / cost2);
    	            }
    	        };
    	        
    	        double prob0 = logitModel(lambda, cost0 / cost2, costAll);
    	        double prob1 = logitModel(lambda, cost0 / cost2, costAll) + prob0;
    	        double rr = Math.random();
    	        
    	        if(rr < prob0) {
    	        	return 0;
    	        }
    	        else {
    	        	if(rr < prob1) {
    	        		return 1;
    	        	}
    	        	else {
    	        		return 2;
    	        	}
    	        }
				
			}
		}	
		
	}
	
	public static double logitModel(double lambda, double cost0, List<Double> costAll) {
		double denominator = 0;
		for(Double cost: costAll) {
			denominator = denominator + Math.exp(-lambda*cost);
		}
		return Math.exp(-lambda*cost0) / denominator;
	}

}