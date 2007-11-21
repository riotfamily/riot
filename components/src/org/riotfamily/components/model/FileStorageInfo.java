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
package org.riotfamily.components.model;

import java.io.Serializable;

import org.springframework.util.ObjectUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class FileStorageInfo implements Serializable {

	private String type;
	
	private String property;
	
	private String fileStoreId;

	public FileStorageInfo() {
	}

	public FileStorageInfo(String type, String property, String fileStoreId) {
		this.type = type;
		this.property = property;
		this.fileStoreId = fileStoreId;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProperty() {
		return this.property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getFileStoreId() {
		return this.fileStoreId;
	}

	public void setFileStoreId(String fileStoreId) {
		this.fileStoreId = fileStoreId;
	}

	public int hashCode() {
		int hash = 0;
		if (type != null) {
			hash += type.hashCode();
		}
		if (property != null) {
			hash += property.hashCode();
		}
		return hash;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof FileStorageInfo) {
			FileStorageInfo other = (FileStorageInfo) obj;
			return ObjectUtils.nullSafeEquals(this.type, other.type)
					&& ObjectUtils.nullSafeEquals(this.property, other.property);
		}
		return false;
	}
}
