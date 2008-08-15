package org.riotfamily.statistics.dao;

import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryStatisticsDao extends AbstractPropertiesDao {

	protected Map getProperties() {
		Map result = new LinkedHashMap();
		Runtime rt = Runtime.getRuntime();
		long freeMem = rt.freeMemory() / 1024 / 1024;
		long totalMem = rt.totalMemory() / 1024 / 1024;
		long usedMem = totalMem - freeMem;
		int numProcs = rt.availableProcessors();
		long maxMem = rt.maxMemory()  / 1024 / 1024;
		long activeThreads = Thread.activeCount();
		result.put("Free memory", freeMem + " MB");
		result.put("Total memory", totalMem + " MB");
		result.put("Used memory", usedMem + " MB");
		result.put("Max memory", maxMem + " MB");
		result.put("Number of processors", numProcs + "");
		result.put("Active threads", activeThreads + "");
		return result; 
	}

}
