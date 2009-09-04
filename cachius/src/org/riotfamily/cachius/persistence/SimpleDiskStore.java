package org.riotfamily.cachius.persistence;

import java.io.File;
import java.io.IOException;

public class SimpleDiskStore implements DiskStore {

	private File dir;

	public SimpleDiskStore() {
	}
	
	public SimpleDiskStore(File dir) {
		this.dir = dir;
	}

	public File getFile() throws IOException {
		return File.createTempFile("item", "", dir);
	}
	
	
}
