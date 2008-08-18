package org.riotfamily.statistics.dao;

import java.util.LinkedHashMap;
import java.util.Map;

public class SwarmStatisticsDao extends AbstractPropertiesDao {

//	private RiotChannel riotChannel;
//	
//	public RiotChannel getRiotChannel() {
//		return riotChannel;
//	}
//	
//	public void setRiotChannel(RiotChannel channel) {
//		this.riotChannel = channel;
//	}

	protected Map getProperties() {
		Map result = new LinkedHashMap();
		//result.putAll(getRiotChannel.getInfo());
		result.put("State", "Not implemented yet");
		return result;
	}

}
