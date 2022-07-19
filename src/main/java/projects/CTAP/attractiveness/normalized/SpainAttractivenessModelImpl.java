package projects.CTAP.attractiveness.normalized;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import config.Config;
import core.graph.Activity.ActivityNode;
import core.graph.geo.CityNode;
import core.graph.population.AgentNodeI;
import core.graph.population.StdAgentNodeImpl;
import projects.CTAP.attractiveness.AttractivenessAbstract;
import projects.CTAP.graphElements.CTAPCityStatNode;

public class SpainAttractivenessModelImpl extends AttractivenessAbstract {
	
	//private List<String> paramsVector_ = new ArrayList<>(Arrays.asList("restaurant", "theater", "parking_space","parking"));
	private List<String> paramsVector_ = new ArrayList<>(Arrays.asList("sustenance", "accomodation", "attraction","cultural", "mountain", "coast"));
	private final List<String> paramsVector;
	
	private Config config;
	private Map<Integer,Map<String,Double[]>> parametersMap = new HashMap<>();
	
	@Inject
	public SpainAttractivenessModelImpl (Config config) throws IOException {
		super((double)config.getCtapModelConfig().getAttractivenessModelConfig().getAttractivenessNormalizedConfig().getInitialTime(),
				(double)config.getCtapModelConfig().getAttractivenessModelConfig().getAttractivenessNormalizedConfig().getFinalTime());
		this.config = config;
		paramsVector = List.of(paramsVector_.toArray(new String[]{}));
		initialize();
	}
	
	/**
	 * @throws IOException
	 */
	private void initialize() throws IOException {
		//import model params as JSON
		ObjectMapper mapper = new ObjectMapper();
    	FileInputStream inputStream = new FileInputStream(config.getCtapModelConfig().getAttractivenessModelConfig().getAttractivenessNormalizedConfig().getAttractivenessFile());
    	List<ModelParametersAgent> parameters;
    	try {
    	    String json = IOUtils.toString(inputStream);
    	    parameters = new ObjectMapper()
    	      .readerFor(new TypeReference<List<ModelParametersAgent>>(){})
    	      .readValue(json); 
    	} finally {
    	    inputStream.close();
    	}
    	parameters.forEach(parameter->{
    		Integer agId = parameter.getAgentId();
    		parametersMap.put(agId,new HashMap<String,Double[]>());
    		parameter.getActivities().forEach(activity->{
    			Double[] pa = new Double[paramsVector.size()];
    			int i = 0;
    			for(String value: paramsVector){
    				try {
	    				pa[i] = Double.valueOf(activity.getParameters().get(value));
	    				i++;
    				}
    				catch(NullPointerException e) {
    					System.out.println("model parameter '" +value+ "' not found for agent "+ agId.toString() +" and activity "+ activity.getActivity());
    					throw new RuntimeException(e);
    				}
    			}
    			parametersMap.get(agId).put(activity.getActivity(),pa);
    		});
    	});
	}
	
	@Override
	public Double getAttractiveness(Double[] params, Double[] variables) {
		
		// Sustenance
		Double attractiveness = params[0]*(variables[0] + variables[1]);
		// Accommodation
		attractiveness += params[1]*(variables[2] + variables[3] + variables[4]);
		// Interest for a tourist
		attractiveness += params[2]*(variables[5]);
		// cultural
		attractiveness += params[3]*(variables[6] + variables[7]);
		// mountain
		attractiveness += params[4]*variables[8] + seasonalityWinterSinglePeakSineFunction(params[4]*(variables[8] + variables[9]),variables[12]);
		// coast
		attractiveness += seasonalitySummerSinglePeakSineFunction(params[5]*(variables[10] + variables[11]),variables[12]);

		return attractiveness;
	}
	
	@Override
	public Double getAttractiveness(Double[] variables,Integer agentId, String activity) {
		return getAttractiveness(parametersMap.get(agentId).get(activity),variables);
	}

}
