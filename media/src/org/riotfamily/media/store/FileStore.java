/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
