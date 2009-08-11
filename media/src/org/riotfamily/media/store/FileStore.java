package org.riotfamily.media.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface FileStore extends Iterable<String> {

	/**
	 * Stores data and returns an URI that can be used to request the file via HTTP.
	 * @param in InputStream to read the data from, or <code>null</code> if an empty 
	 *        file should be created
	 * @param fileName The desired target file name, or <code>null</code> if it
	 * 		  should be up to the store to choose a name 
	 * @return The URI to access the stored file
	 */
	public String store(InputStream in, String fileName) throws IOException;

    /**
	 * Retrieves a file that was previously added via the
	 * {@link #store(InputStream, String) store()} method. 
	 */
	public File retrieve(String uri);
	
	/**
	 * Deletes the file denoted by the given URI from the store.
	 */
	public void delete(String uri);

}
