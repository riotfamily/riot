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
package org.riotfamily.components.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.riotfamily.common.web.file.FileStore;
import org.riotfamily.common.web.file.FileStoreLocator;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.ComponentVersion;
import org.riotfamily.components.model.FileStorageInfo;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ComponentUpdate {

	private ComponentDao dao;
	
	private ComponentVersion version;
	
	private FileStoreLocator fileStoreLocator;
	
	private Map fileStores;
	
	
	public ComponentUpdate(ComponentDao dao, ComponentVersion version, 
			FileStoreLocator fileStoreLocator) {
		
		this.dao = dao;
		this.version = version;
		this.fileStoreLocator = fileStoreLocator;
	}

	private FileStore getFileStore(String property) {
		if (fileStores == null) {
			fileStores = new HashMap();
			Iterator it = dao.getFileStorageInfos(version.getType()).iterator();
			while (it.hasNext()) {
				FileStorageInfo fsi = (FileStorageInfo) it.next();
				FileStore fs = fileStoreLocator.getFileStore(fsi.getFileStoreId());
				fileStores.put(fsi.getProperty(), fs);
			}
		}
		return (FileStore) fileStores.get(property);
	}
	
	public String getString(String property) {
		return version.getProperty(property);
	}
	
	public void setString(String property, String value) {
		version.setProperty(property, value);
	}
	
	public File getFile(String property) {
		String uri = getString(property);
		if (uri == null) {
			return null;
		}
		return getFileStore(property).retrieve(uri);
	}
	
	public void setFile(String property, File file) throws IOException {
		setFile(property, file, null);
	}
	
	public void setFile(String property, File file, String fileStoreId) 
			throws IOException {
		
		FileStore fs = getFileStore(property);
		if (fs == null) {
			dao.saveFileStorageInfo(version.getType(), property, fileStoreId);
			fs = fileStoreLocator.getFileStore(fileStoreId);
		}
		
		String oldUri = getString(property);
		if (oldUri != null) {
			fs.delete(oldUri);
		}
		String uri = null;
		if (file != null) {
			uri = fs.store(file, null);
		}
		setString(property, uri);
	}
		
}
