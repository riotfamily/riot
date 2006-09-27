package org.riotfamily.riot;

/**
 * Class that exposes the Riot version. Fetches the Implementation-Version 
 * attribute from the manifest contained in the jar file.
 */
public final class RiotVersion {

	private static String versionString;
	
	private RiotVersion() {
	}
	
	/**
     * Return the full Riot version string.
     * @see java.lang.Package#getImplementationVersion
     */
    public static String getVersionString() {
    	if (versionString == null) {
    		versionString = RiotVersion.class.getPackage().getImplementationVersion();
    	}
    	return versionString;
    }
    
}
