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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.file;

import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class FileStoreLocator implements ApplicationContextAware {

	private static final String DEFAULT_FILE_STORE_ID = "defaultFileStore";

	private Map fileStores;
	
	private FileStore defaultFileStore;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		fileStores = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, FileStore.class);
		
		if (fileStores.size() == 1) {
			defaultFileStore = (FileStore) fileStores.values().iterator().next();
		}
		else {
			defaultFileStore = (FileStore) fileStores.get(DEFAULT_FILE_STORE_ID);
		}
	}
	
	public FileStore getFileStore(String id) {
		if (id == null) {
			return defaultFileStore;
		}
		return (FileStore) fileStores.get(id);
	}
	
}
