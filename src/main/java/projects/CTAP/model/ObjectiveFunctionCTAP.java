package projects.CTAP.model;

import java.util.Arrays;

import core.models.ObjectiveFunctionI;

public class ObjectiveFunctionCTAP implements ObjectiveFunctionI {
	
	
	private final int nActivities;
	private final int[] activities;
	private final int[] locations;
	private final double[] percentageOfTimeTarget;
	private final double[] timeDuration;
	private final double[] locationPerception;
	private final double[] sigmaActivityCalibration;
	private final double[] tauActivityCalibration;
	private final double[] durationDiscomfort;
	private final double[] travelCost;
	private final double[] travelTime;
	private final float[][] attractiveness;
	
	private final double attractivenessTimeInterval;
	
	private final double monetaryBudget;
	private final double timeRelatedBudget;
	private final double valueOfTime;
	
	
	
	public ObjectiveFunctionCTAP(int nActivities,
			                     int[] activities,
								 int[] locations,
								 double[] percentageOfTimeTarget,
								 double[] timeDuration,
								 double[] locationPerception,
								 double[] sigmaActivityCalibration,
								 double[] tauActivityCalibration,
								 double[] durationDiscomfort,
								 double[] travelCost,
								 double[] travelTime,
								 float[][] attractiveness,
								 double valueOfTime,
								 double monetaryBudget,
								 double timeRelatedBudget,
								 double attractivenessTimeInterval
								 ) {
		
		 this.nActivities = nActivities;
		 this.activities = activities;
		 this.locations = locations;
		 this.percentageOfTimeTarget = percentageOfTimeTarget;
		 this.timeDuration = timeDuration;
		 this.locationPerception = locationPerception;
		 this.sigmaActivityCalibration = sigmaActivityCalibration;
		 this.tauActivityCalibration = tauActivityCalibration;
		 this.durationDiscomfort = durationDiscomfort;
		 this.travelCost = travelCost;
		 this.travelTime = travelTime;
		 this.monetaryBudget = monetaryBudget;
		 this.timeRelatedBudget = timeRelatedBudget;
		 this.attractiveness = attractiveness ;
		 this.valueOfTime = valueOfTime;
		 this.attractivenessTimeInterval = attractivenessTimeInterval;
	}
	
	public double getValue(double[] ts, double[] te) {
		double res = 0;
		res +=  200 * getDiscomfortPercentageOfTimeTarget(ts, te);
		//res +=  1.5 / 50 / 50 / 10 * getDiscomfortDurationTarget(ts, te) / 10;
		
		res +=  100 * getDiscomfortDurationTarget(ts, te);
		
		
		res += getDiscomfortBudget(ts, te);
		//res += getLagrangeMultipliers_1(ts, te);
		//res += getLagrangeMultipliers_2(ts, te);
		//res += getLagrangeMultipliers_3(ts, te);
		//res += getLagrangeMultipliers_4(ts, te);
		res += 1 * getLagrangeMultipliers_5(ts, te);
		
//		System.out.print("Iter \n");
//		System.out.print(getStateValue(0,ts,te));
//		System.out.print(" \n");
//		System.out.print(getStateValue(1,ts,te));
//		System.out.print(" \n");
//		System.out.print(getStateValue(2,ts,te));
//		System.out.print(" \n");
//		System.out.print(res);
//		System.out.print(" \n");
		
		return res;
	}
	
	@Override
	public double getValue(double[] t) {
		double[] ts = new double[t.length];
		double[] te = new double[t.length];
		
		for(int i =1;i<t.length;i++ ) {
			te[i-1] = ts[i-1] + t[i-1];
			ts[i] = te[i-1];
			if(i == t.length - 1) {
				te[i] = ts[i] + t[i];
			}
		}
		
		//double[] ts = Arrays.copyOfRange(t,0,t.length/2);
		//double[] te = Arrays.copyOfRange(t,t.length/2,t.length);
		return getValue(ts,te); 
		
	}
	
	
	private double getDiscomfortPercentageOfTimeTarget(double[] ts, double[] te) {
		double res = 0;
		for(int i = 0;i<percentageOfTimeTarget.length;i++) {
			for(int j = 1;j<ts.length+1;j++) {
				double[] ts_subset = Arrays.copyOfRange(ts,0,j);
				double[] te_subset = Arrays.copyOfRange(te,0,j);
				res += Math.pow((percentageOfTimeTarget[i] - getStateValue(i,ts_subset,te_subset)) ,2);
			}
		}
		return res;
	}
	
	private double getDiscomfortDurationTarget(double[] ts, double[] te) {
		double res = 0;
		for(int i = 0;i<activities.length;i++) {
			double res_iter = 0;
			if(te[i]-ts[i] < 1 * 24) { 
				res_iter = 0; // if the duration of an activity is 0 we consider that the solver has tried to eliminate it
				
			}
			else {
				res_iter += Math.pow(Math.abs(timeDuration[i] - (te[i]-ts[i])) / timeDuration[i]  ,4);
			}
			
			if(activities[i] == 0) {
				res_iter = res_iter*0;
			}
			else {
				res_iter = res_iter * 1; // Math.pow(timeDuration[i] ,2);
			}
			res += res_iter;
			//res = res*1 / Math.pow(timeDuration[i],2);//this.durationDiscomfort[i];
		}
		return res;
	}
	
	private double getDiscomfortBudget(double[] ts, double[] te) {
		double res = 0;
		for(int i =1;i<this.activities.length;i+=2 ) {
			//res += costActivityLocation(i,ts[i],te[i]);
			if(te[i]-ts[i] > 1 * 24) {
				res += travelCost[i-1];
				res += travelCost[i];
			}
			
		}
		res = Math.pow(res, 2)/Math.pow(monetaryBudget,2);
		//res += Math.pow(costOfTime(ts[0],te[te.length-1]), 2)/Math.pow(timeRelatedBudget, 2);
		return res;
	}
	
	private double getPullFactor(int i,double ts,double te) {
	    if(te < ts) {
			return Math.abs(te-ts)*1000;
		}
	    if(te > 8736) {
			return 1;
		}
	    
	    if(te > 8760) {
			return Math.abs(te-8760)*1000;
		}
	    
		int ts_ = (int) Math.floor(ts / attractivenessTimeInterval);
		int _te = (int) Math.ceil(te / attractivenessTimeInterval);
		if(ts_ >= _te - 1) {
			return locationPerception[i] *  attractiveness[i][ts_] ;     
		}
		else {
			double res = 0;
			for(int j = ts_+1;j < _te - 1; j++) {
				res += attractivenessTimeInterval*(attractiveness[i][j]);
			}
			res += attractiveness[i][ts_] * ((ts_+1)*attractivenessTimeInterval-ts);
			res += attractiveness[i][_te - 1] * (te - attractivenessTimeInterval*(_te-1));
			return locationPerception[i] * res / (te-ts)  ;     
		}                         
	}
	
	public double costActivityLocation(int i,double ts, double te) {
		return 0;
	}
	
	public double costOfTime(double ts, double te) {
		return 0;
	}
	
	public double attractiveness(int i,double t) {
		return 1;
	}
	
	private double getStateValue(int activity,double[] ts, double[] te) {
		double res = percentageOfTimeTarget[activity];
		for(int i =0;i<ts.length;i++ ) {
			if(activities[i] == activity) {
				if(activity == 0) {
					res = 1 + (res-1) * Math.pow(Math.E, -tauActivityCalibration[activity]*(te[i]-ts[i])* 1); 
				}
				else {
					if(te[i]-ts[i] >= 1 * 24) {
						res = 1 + (res-1) * Math.pow(Math.E, -tauActivityCalibration[activity]*(te[i]-ts[i])* getPullFactor(i,ts[i], te[i]));						
					}
					
					 //res = 1 + (res-1) * Math.pow(Math.E, -tauActivityCalibration[activity]*(te[i]-ts[i])* 1); 
				}
				
			}
			else {
				res = res * Math.pow(Math.E, -sigmaActivityCalibration[activity]*(te[i]-ts[i])); 
			}
		}
		return res;
	}
	
	public double getLagrangeMultipliers_1(double[] ts, double[] te) {
	   double res = 0;
       for(int i =0;i<ts.length;i++) {
			double diff = te[i] - ts[i];
			if(diff <= 0) {
				res += (1+Math.abs(diff));
			}
		}
		return res;
	}
	
	public double getLagrangeMultipliers_2(double[] ts, double[] te) {
		double res = 0;
		for(int j =0;j<ts.length-1;++j) {
			int k = j+1;
			double diff = ts[k] - te[j];
			if(diff <= 0) {
				res = res + (1 + Math.abs(diff));
			}
		}
		return res;
	}
	
	public double getLagrangeMultipliers_3(double[] ts, double[] te) {
		double res = 0;
		for(int j =0;j<ts.length-1;++j) {
			res += costActivityLocation(activities[j],ts[j],te[j]);
			res += travelCost[j];
		}
		if(res > monetaryBudget) {
			return (res-monetaryBudget)*1000;
		}
		else {
			return 0;
		}
		
	}
	
	public double getLagrangeMultipliers_4(double[] ts, double[] te) {
		double res = 0;
		return res;
	}
	
	public double getLagrangeMultipliers_5(double[] ts, double[] te) {
		double res = 0;
		double diff = te[te.length-1] - 8760 ;
		res = Math.abs(diff);
		return res;
	}


	@Override
	public int getVariablesLength() {
		return this.nActivities*2;
	}
	
	public int[] getActivities() {
		return activities;
	}
	
	public int[] getLocations() {
		return locations;
	}
	
}
