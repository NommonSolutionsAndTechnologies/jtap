package projects.CTAP.population;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.inject.Inject;

import config.Config;
import core.models.ConstraintI;
import core.models.ModelI;
import core.population.AgentFactoryI;
import projects.CTAP.dataset.Dataset;
import projects.CTAP.model.ActivityLocationI;
import projects.CTAP.model.LowerBoundCTAP;
import projects.CTAP.model.ModelCTAP;
import projects.CTAP.model.ObjectiveFunctionCTAP_01;
import projects.CTAP.model.UpperBoundCTAP;

public class AgentFactory implements AgentFactoryI {
	
	private Config config;
	private ActivityLocationI activityLocation;
	
	@Inject
	public AgentFactory (Config config, ActivityLocationI activityLocation) {
		this.config = config;
		this.activityLocation = activityLocation;
	}
	
	@Override
	public Agent run(Long agentId,Long homeLocationId,Dataset dataset){
		
		int agentIndex = dataset.getAgentsIndex().getIndex().indexOf(agentId);
		int nPlanActivities = this.config.getCtapModelConfig().getCtapPopulationConfig().getCtapAgentConfig().getPlanSize();
		
		boolean homeDs = dataset.getCitiesDsIndex().getIndex().contains(homeLocationId); 
		List<ModelI> models = new ArrayList<>();
		
		//cost
		double monetaryBudget = dataset.getMonetaryBudgetParameter().getParameter()[agentIndex];
		double timeRelatedBudget = dataset.getMonetaryBudgetParameter().getParameter()[agentIndex];
		double valueOfTime = dataset.getValueOfTimeParameter().getParameter()[agentIndex];
		
		//activities index
		double[] percOfTimeTargetParameter = dataset.getPercOfTimeTargetParameter().getParameter()[agentIndex];
		
		//time
		double attractivenessTimeInterval = this.config.getCtapModelConfig().getAttractivenessModelConfig().getAttractivenessNormalizedConfig().getIntervalTime();
		
		for(int[][] al: this.activityLocation.run(agentId, homeLocationId, dataset)) {
			
			//activities seq
			double[] timeDuration =  new double[nPlanActivities];
			
			//locations seq
			double[] locationPerception = new double[nPlanActivities];
			double[] sigmaActivityCalibration = new double[3];
			double[] tauActivityCalibration = new double[3];
			double[] gammaActivityCalibration = new double[nPlanActivities];
			double[] travelCost = new double[nPlanActivities];
			double[] travelTime = new double[nPlanActivities];
			double[] activityLocationCostRate = new double[nPlanActivities];
			float[][] attractiveness = new float[nPlanActivities][dataset.getTimeIndex().getIndex().size()];
			
			sigmaActivityCalibration[0] = 0.008 * 1 / 3 / 50 * 5;
    		tauActivityCalibration[0] = 0.001 / 2 / 100 * 1.4 * 5;
			
			sigmaActivityCalibration[1] = 0.0018 / 10 / 1;
    		tauActivityCalibration[1] = 0.04 / 15 / 2;

			sigmaActivityCalibration[2] = 0.0004 / 10 / 4 * 10;
    		tauActivityCalibration[2] = 0.006 / 6 / 2;
			
			//factory
            for(int i = 0;i< nPlanActivities ;i++) {
            	
            	int actIndex = al[0][i];
            	int locIndex = al[1][i];
            	
				timeDuration[i] = dataset.getTimeDurationParameter().getParameter()[agentIndex][actIndex];
            	
            	//locationPerception[i] = this.dataset.getLocationPerceptionParameter().getParameter()[al[1][i]];
				locationPerception[i] = 0.5 + Math.random() * (1.5 - 0.5);
//				if(actIndex == 0) {
//					sigmaActivityCalibration[i] = 0.008 * 1;
//	    			tauActivityCalibration[i] = 0.001;
//				}
//				else {
//					
//				if(actIndex == 1) {
//					sigmaActivityCalibration[i] = 0.0018 * 1;
//	    			tauActivityCalibration[i] = 0.04;
//				}
//				else {
//					sigmaActivityCalibration[i] = 0.0004 / 1;
//	    			tauActivityCalibration[i] = 0.006 * 1;
//				}
//				
//				}

    			//assuming first activity is always the default one
    			if(i%2==0 && i < nPlanActivities-1) {
    				int nextLocIndex = al[1][i+1];
    				if(homeDs) {
    					travelCost[i] = dataset.getDs2DsTravelCostParameter().getParameter()[0][locIndex][nextLocIndex];   
            			travelTime[i] = 0;
    				}
    				else {
    					travelCost[i] = dataset.getOs2DsTravelCostParameter().getParameter()[0][locIndex][nextLocIndex];  
            			travelTime[i] = 0;
    				}
    			}
    			//TODO use the same travel cost A->B B->A
    			else if(i%2!=0 && i < nPlanActivities-1) {
    				int nextLocIndex = al[1][i+1];
    				if(homeDs) {
	    				travelCost[i] = dataset.getDs2DsTravelCostParameter().getParameter()[0][locIndex][nextLocIndex];  
	        			travelTime[i] = 0;
    				}
    				else {
    					travelCost[i] = dataset.getDs2OsTravelCostParameter().getParameter()[0][locIndex][nextLocIndex];  
	        			travelTime[i] = 0;
    				}
    				activityLocationCostRate[i] = dataset.getActivityLocationCostParameter().getParameter()[locIndex][actIndex];
        			attractiveness[i] = dataset.getAttractivenessParameter().getParameter()[agentIndex][locIndex][actIndex];
    			}
			}
			
			ObjectiveFunctionCTAP_01 objF = new ObjectiveFunctionCTAP_01(nPlanActivities,
					al[0], al[1], percOfTimeTargetParameter, timeDuration, locationPerception,
					sigmaActivityCalibration,tauActivityCalibration,gammaActivityCalibration,
					travelCost,travelTime,monetaryBudget, timeRelatedBudget, activityLocationCostRate,
					valueOfTime,attractiveness,attractivenessTimeInterval);
			List<ConstraintI> constraints = new ArrayList<>();
			double[] lb = new double[nPlanActivities];
			double[] ub = new double[nPlanActivities];
			Arrays.fill(lb, 0d);
			Arrays.fill(ub, 8760d);
			constraints.add(new LowerBoundCTAP(lb));
			constraints.add(new UpperBoundCTAP(ub));
			double[] initGuess = new double[nPlanActivities];
			for(int i =0;i<initGuess.length-1;i++ ) {
				if(al[0][i] == 0) {
					initGuess[i] = Math.random() * 60 * 24;
				}
				
				if(al[0][i] == 1) {
					initGuess[i] = Math.random() * 6 * 24;
				}
				else {
					initGuess[i] = Math.random() * 30 * 24;
				}
				
			}
			
			for(int i =0;i<initGuess.length;i++ ) {
				if(al[0][i] == 0) {
					initGuess[i] = 30 * 24;
				}
				else {
					
				
				if(al[0][i] == 1) {
					initGuess[i] = 2 * 24;
				}
				else {
					initGuess[i] = 10 * 24;
				}
				
				}
				
			}
			
			
			Arrays.fill(ub, 1200d);
			
			models.add(new ModelCTAP(objF,constraints,initGuess));
			
		}
		
		return new Agent(agentId,homeLocationId,models);
	}
	
}
