package org.riotfamily.swarm;

public class NodeStatus {

	private static boolean master = false;

	public static boolean isMaster() {
		return master;
	}

	public static void setMaster(boolean master) {
		NodeStatus.master = master;
	}
}
