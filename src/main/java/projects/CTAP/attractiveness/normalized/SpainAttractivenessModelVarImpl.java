package projects.CTAP.attractiveness.normalized;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import projects.CTAP.attractiveness.AttractivenessModelVariablesI;

public class SpainAttractivenessModelVarImpl implements AttractivenessModelVariablesI {

	@Override
	public List<Double> getVariables(Map<String, Object> map) {
		
		List<Double> res =new ArrayList<>();
		// Sustenance
		res.add(((map.get("restaurant") == null? new Long(0): (Long)map.get("restaurant"))).doubleValue());
		res.add(((map.get("bar") == null? new Long(0): (Long)map.get("bar"))).doubleValue());
		
		// Accommodation
		res.add(((map.get("apartment") == null? new Long(0): (Long)map.get("apartment"))).doubleValue());
		res.add(((map.get("hostel") == null? new Long(0): (Long)map.get("hostel"))).doubleValue());
		res.add(((map.get("hotel") == null? new Long(0): (Long)map.get("hotel"))).doubleValue());
		
		// Interest for a tourist
		res.add(((map.get("attraction") == null? new Long(0): (Long)map.get("attraction"))).doubleValue());
		
		// cultural
		res.add(((map.get("museum") == null? new Long(0): (Long)map.get("museum"))).doubleValue());
		res.add(((map.get("theatre") == null? new Long(0): (Long)map.get("theatre"))).doubleValue());		
		
		// mountain
		res.add(((map.get("alpine_hut") == null? new Long(0): (Long)map.get("alpine_hut"))).doubleValue());
		res.add(((map.get("ski") == null? new Long(0): (Long)map.get("ski"))).doubleValue()); // check the correct name
		
		// coast
		res.add(((map.get("boat_rental") == null? new Long(0): (Long)map.get("boat_rental"))).doubleValue());
		res.add(((map.get("ferry_terminal") == null? new Long(0): (Long)map.get("ferry_terminal"))).doubleValue());
		return res;
	}

}
