package projects.CTAP.outputAnalysis;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import config.Config;
import core.dataset.DatasetFactoryI;
import projects.CTAP.dataset.AgentHomeLocationParameter;
import projects.CTAP.dataset.CitiesDsIndex;
import projects.CTAP.dataset.CitiesOsIndex;
import projects.CTAP.dataset.Ds2DsPathParameter;
import projects.CTAP.dataset.Ds2DsTravelCostParameter;
import projects.CTAP.dataset.Ds2OsPathParameter;
import projects.CTAP.dataset.Ds2OsTravelCostParameter;
import projects.CTAP.dataset.Os2DsPathParameter;
import projects.CTAP.dataset.Os2DsTravelCostParameter;

public class LinkTimeFlowDatasetJsonFactorySpain implements DatasetFactoryI {
	
    private Config config;
	
    @Inject
	public LinkTimeFlowDatasetJsonFactorySpain(Config config) {
		this.config = config;
	}

	@Override
	public LinkTimeFlowDatasetSpain run() {
		ObjectMapper mapper = new ObjectMapper();
		LinkTimeFlowDatasetSpain dataset = null;
	    String dir = config.getCtapModelConfig().getDatasetConfig().getImportDirectory();
	    try {
	    	
	    	CitiesDsIndex citiesDsIndex = mapper.readValue(new File(dir+"CitiesDsIndex.json"), CitiesDsIndex.class);
			CitiesOsIndex citiesOsIndex = mapper.readValue(new File(dir+"CitiesOsIndex.json"), CitiesOsIndex.class);
	    	
	    	Ds2OsPathParameter ds2OsPathParameter =  mapper.readValue(new File(dir+"Ds2OsPathParameter.json"), Ds2OsPathParameter.class);
	    	Ds2DsPathParameter ds2DsPathParameter =  mapper.readValue(new File(dir+"Ds2DsPathParameter.json"), Ds2DsPathParameter.class);
	    	Os2DsPathParameter os2DsPathParameter =  mapper.readValue(new File(dir+"Os2DsPathParameter.json"), Os2DsPathParameter.class);
	    	
	    	AgentHomeLocationParameter agentHomeLocationParameter = mapper.readValue(new File(dir+"AgentHomeLocationParameter.json"), AgentHomeLocationParameter.class);
	    	
			Ds2DsTravelCostParameter ds2DsTravelCostParameter = mapper.readValue(new File(dir+"Ds2DsTravelCostParameter.json"), Ds2DsTravelCostParameter.class);
			Ds2OsTravelCostParameter ds2OsTravelCostParameter =  mapper.readValue(new File(dir+"Ds2OsTravelCostParameter.json"), Ds2OsTravelCostParameter.class);
			Os2DsTravelCostParameter os2DsTravelCostParameter = mapper.readValue(new File(dir+"Os2DsTravelCostParameter.json"), Os2DsTravelCostParameter.class);
			
	    	
	    	dataset = new LinkTimeFlowDatasetSpain(citiesDsIndex,
												citiesOsIndex,
												ds2OsPathParameter, 
												ds2DsPathParameter, 
												os2DsPathParameter,
												agentHomeLocationParameter,
												ds2DsTravelCostParameter,
												ds2OsTravelCostParameter,
												os2DsTravelCostParameter);
	    }
	    catch (IOException e) {
		   e.printStackTrace();
	    }
	    
		return dataset;
	}
}