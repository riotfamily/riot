package org.riotfamily.cachius.persistence;

import java.io.File;
import java.io.IOException;

public interface DiskStore {
	
	public File getFile() throws IOException;

}
