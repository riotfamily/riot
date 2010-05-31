package org.riotfamily.cachius.persistence;

import java.io.File;
import java.io.IOException;

public class SimpleDiskStore implements DiskStore {

	private File dir;

	public SimpleDiskStore() {
		setBaseDir(new File(System.getProperty("java.io.tmpdir")));
	}
	
	public SimpleDiskStore(File dir) {
		setBaseDir(dir);
	}
	
	private void setBaseDir(File baseDir) {
		this.dir = new File(baseDir, "items");
		delete(this.dir);
		this.dir.mkdirs();
	}

	private static void delete(File f) {
        if (f.isDirectory()) {
            File[] entries = f.listFiles();
            for (int i = 0; i < entries.length; i++) {
            	delete(entries[i]);
            }
        }
        f.delete();
    }
	
	public File getFile() throws IOException {
		return File.createTempFile("item", "", dir);
	}
	
	
}
