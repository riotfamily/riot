/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
	 * @param previousUri The URI of another file being replaced
	 * @return The URI to access the stored file
	 */
	public String store(File file, String originalFileName, String previousUri) 
			throws IOException;
	
	public File retrieve(String uri);
	
	public void delete(String uri);
	
	public String copy(String uri) throws IOException;

}
