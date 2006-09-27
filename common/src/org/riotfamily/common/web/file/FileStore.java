package org.riotfamily.common.web.file;

import java.io.File;
import java.io.IOException;

public interface FileStore {

	/**
	 * Moves the given file into the store and returns an URI that can be
	 * used to request the file via HTTP.
	 * 
	 * @param file The file to store
	 * @param originalFileName A file name provided by the user
	 * @param previousUri The URI of another file beeing replaced
	 * @return The URI to access the stored file
	 */
	public String store(File file, String originalFileName, String previousUri) 
			throws IOException;
	
	public File retrieve(String uri);
	
	public void delete(String uri);
	
	public String copy(String uri) throws IOException;

}
