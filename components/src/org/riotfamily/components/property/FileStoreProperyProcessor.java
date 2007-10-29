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
package org.riotfamily.components.property;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.file.FileStore;
import org.riotfamily.common.web.file.FileUtils;

/**
 * PropertyProcessor for URIs resolvable by a {@link FileStore}. When a 
 * component model is copied, the referenced file is also copied and the new
 * file URI is put into the target model. Upon deletion the file is also deleted. 
 */
public class FileStoreProperyProcessor extends PropertyProcessorAdapter {

	private static Log log = LogFactory.getLog(FileStoreProperyProcessor.class);
	
	private FileStore fileStore;
	
	public FileStoreProperyProcessor() {
	}

	public FileStoreProperyProcessor(FileStore fileStore) {
		this.fileStore = fileStore;
	}

	public void setFileStore(FileStore fileStore) {
		this.fileStore = fileStore;
	}

	public Object copy(Object object) {
		if (object != null) {
			try {
				return FileUtils.copy(fileStore, (String) object);
			}
			catch (IOException e) {
				log.error("Error copying file", e);
			}
		}
		return null;
	}

	public void delete(Object object) {
		if (object != null) {
			fileStore.delete((String) object);
		}
	}

}
