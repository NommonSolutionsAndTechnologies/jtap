package projects.CTAP.transport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import config.CtapTransportLinkConfig;

public class SpainCTAPTransportLinkFactory extends AbstractCTAPTransportLinkFactory {

	@Override
	public double CTAPTransportCrossLinkValue(Object tlc_, Map<String, Object> inputData) {
		CtapTransportLinkConfig tlc = (CtapTransportLinkConfig)tlc_;
		return ((Long)inputData.get("distance")).doubleValue() * 1.42 * tlc.getMvTransferTime().getWalk();
	}

	@Override
	public double CTAPTransportRoadLinkValue(Object tlc_, Map<String, Object> inputData) {
		CtapTransportLinkConfig tlc = (CtapTransportLinkConfig)tlc_;
		return Double.parseDouble(inputData.get("avg_travel_time").toString()) *tlc.getMvTransferTime().getCar()
				+ (double)inputData.get("distance")/1000*tlc.getPriceXKm().getCar();
	}

	@Override
	public double CTAPTransportRailLinkValue(Object tlc_, Map<String, Object> inputData) {
		CtapTransportLinkConfig tlc = (CtapTransportLinkConfig)tlc_;
		String ldt = (String)inputData.get("last_dep_time");
		String fdt = (String)inputData.get("first_dep_time");
		
		if(ldt == null) {
			ldt = "23:59";
		}
		if(fdt == null) {
			fdt = "00:00";
		}
		
		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Date lastDepTime = null;
		Date firstDepTime = null;
		try {
			lastDepTime = dateFormat.parse(ldt);
			firstDepTime = dateFormat.parse(fdt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//Long diff = (lastDepTime.getTime() - firstDepTime.getTime())/1000;
		Long connPerDay = (Long)inputData.get("connections_per_day");
		
		if(connPerDay == null) {
			connPerDay = 1L;
		}
		
		double reliability = ((24*3600/connPerDay.doubleValue())/2)*tlc.getMvServicePerceivedReliability();
		
		// Stefano 110 km/h ->30.55 m/s
		// 200 km/h -> 55.55 m/s
		
		return (double)inputData.get("avg_travel_time")*tlc.getMvTransferTime().getTrain() 
				+ (double)inputData.get("avg_travel_time")*55.55/1000*tlc.getPriceXKm().getTrain()
				+ reliability
				+ 300 * tlc.getMvWaitingTime();
	}

	@Override
	public double CTAPTransportAirLinkValue(Object tlc_, Map<String, Object> inputData) {
		CtapTransportLinkConfig tlc = (CtapTransportLinkConfig)tlc_;
		String ldt = (String)inputData.get("last_dep_time");
		String fdt = (String)inputData.get("first_dep_time");
		
		if(ldt == null) {
			ldt = "23:59";
		}
		if(fdt == null) {
			fdt = "00:00";
		}
		
		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Date lastDepTime = null;
		Date firstDepTime = null;
		try {
			lastDepTime = dateFormat.parse(ldt);
			firstDepTime = dateFormat.parse(fdt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//Long diff = (lastDepTime.getTime() - firstDepTime.getTime())/1000;
		Long connPerDay = (Long)inputData.get("connections_per_day");
		
		if(connPerDay == null) {
			connPerDay = 1L;
		}
		
		double reliability = ((24*3600/connPerDay.doubleValue())/2)*tlc.getMvServicePerceivedReliability();
		
		// 1h20 bcn 504km -> 378km/h
		// 1h25 pmi 548km -> 385km/h
		// 2h55 tfn 1792km -> 633km/h
		
		// 450 km/h -> 125m/s
		
		
		double priceKm = tlc.getPriceXKm().getPlane();
		if((double)inputData.get("avg_travel_time") > 120) {
			priceKm = priceKm / 2;
			
		}
		
		return (double)inputData.get("avg_travel_time")*tlc.getMvTransferTime().getPlane() 
				+ (double)inputData.get("avg_travel_time")*60*125/1000*priceKm
				+ reliability
				+ 300 * tlc.getMvWaitingTime();
		}

}