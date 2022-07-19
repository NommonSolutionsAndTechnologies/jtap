package projects.CTAP.outputAnalysis;

import core.dataset.DatasetI;
import projects.CTAP.dataset.AgentHomeLocationParameter;
import projects.CTAP.dataset.CitiesDsIndex;
import projects.CTAP.dataset.CitiesOsIndex;
import projects.CTAP.dataset.Ds2DsPathParameter;
import projects.CTAP.dataset.Ds2DsTravelCostParameter;
import projects.CTAP.dataset.Ds2OsPathParameter;
import projects.CTAP.dataset.Ds2OsTravelCostParameter;
import projects.CTAP.dataset.Os2DsPathParameter;
import projects.CTAP.dataset.Os2DsTravelCostParameter;

public class LinkTimeFlowDatasetSpain implements DatasetI  {
	
	private final CitiesDsIndex citiesDsIndex;
	private final CitiesOsIndex citiesOsIndex;
	private final Ds2OsPathParameter ds2OsPathParameter; 
	private final Ds2DsPathParameter ds2DsPathParameter; 
	private final Os2DsPathParameter os2DsPathParameter; 
	private final AgentHomeLocationParameter agentHomeLocationParameter;
	private final Ds2DsTravelCostParameter ds2DsTravelCostParameter;
	private final Ds2OsTravelCostParameter ds2OsTravelCostParameter;
	private final Os2DsTravelCostParameter os2DsTravelCostParameter;
	
	
	public LinkTimeFlowDatasetSpain(CitiesDsIndex citiesDsIndex,
							CitiesOsIndex citiesOsIndex,
							Ds2OsPathParameter ds2OsPathParameter, 
							Ds2DsPathParameter ds2DsPathParameter, 
							Os2DsPathParameter os2DsPathParameter,
							AgentHomeLocationParameter agentHomeLocationParameter,
							Ds2DsTravelCostParameter ds2DsTravelCostParameter,
							Ds2OsTravelCostParameter ds2OsTravelCostParameter,
							Os2DsTravelCostParameter os2DsTravelCostParameter){
		
		this.citiesDsIndex= citiesDsIndex;
		this.citiesOsIndex= citiesOsIndex;
		this.ds2OsPathParameter= ds2OsPathParameter;
		this.ds2DsPathParameter= ds2DsPathParameter;
		this.os2DsPathParameter= os2DsPathParameter;
		this.agentHomeLocationParameter = agentHomeLocationParameter;
		this.ds2DsTravelCostParameter = ds2DsTravelCostParameter;
		this.ds2OsTravelCostParameter = ds2OsTravelCostParameter;
		this.os2DsTravelCostParameter = os2DsTravelCostParameter;
	}
	
	public CitiesDsIndex getCitiesDsIndex() {
		return this.citiesDsIndex;
	}
	
	public CitiesOsIndex getCitiesOsIndex() {
		return citiesOsIndex;
	}
	
	public Ds2DsPathParameter getDs2DsPathParameter() {
		return ds2DsPathParameter;
	}
	
	public Ds2OsPathParameter getDs2OsPathParameter() {
		return ds2OsPathParameter;
	}
	
	public Os2DsPathParameter getOs2DsPathParameter() {
		return os2DsPathParameter;
	}
	
	public AgentHomeLocationParameter getAgentHomeLocationParameter() {
		return agentHomeLocationParameter;
	}
	
	public Ds2DsTravelCostParameter getDs2DsTravelCostParameter() {
		return ds2DsTravelCostParameter;
	}
	public Ds2OsTravelCostParameter getDs2OsTravelCostParameter() {
		return ds2OsTravelCostParameter;
	}
	
	public Os2DsTravelCostParameter getOs2DsTravelCostParameter() {
		return os2DsTravelCostParameter;
	}

}