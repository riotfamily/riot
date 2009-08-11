package org.riotfamily.core.runtime;

public final class RiotVersion {

	private static String versionString;
	
	private RiotVersion() {
	}
	
	/**
	 * Returns the full Riot version string.
	 * @see java.lang.Package#getImplementationVersion
	 */
	public static String getVersionString() {
		if (versionString == null) {
			versionString = RiotRuntime.class.getPackage().getImplementationVersion();
		}
		return versionString;
	}
	
}
