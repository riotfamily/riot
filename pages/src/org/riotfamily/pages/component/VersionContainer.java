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
package org.riotfamily.pages.component;

import java.util.Set;

/**
 * Class to support staging and versioning of components. Each
 * {@link ComponentList ComponentList} has two collections of VersionContainers,
 * one containing the published (live) components, the other one containing
 * preview versions. Each container may in turn have a live and a preview
 * version of the actual component data.
 */
public class VersionContainer {

	private Long id;
	
	private ComponentList liveList;
	
	private ComponentList previewList;
	
	private ComponentVersion liveVersion;
	
	private ComponentVersion previewVersion;
	
	private Set versions;

	public VersionContainer() {
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ComponentList getList() {
		return this.previewList != null ? previewList : liveList;
	}
	
	public void setList(ComponentList list) {
		this.liveList = list;
		this.previewList = list;
	}
	
	public ComponentVersion getLiveVersion() {
		return this.liveVersion;
	}

	public void setLiveVersion(ComponentVersion liveVersion) {
		this.liveVersion = liveVersion;
		if (liveVersion != null) {
			liveVersion.setContainer(this);
		}
	}

	public ComponentVersion getPreviewVersion() {
		return this.previewVersion;
	}

	public void setPreviewVersion(ComponentVersion previewVersion) {
		this.previewVersion = previewVersion;
		if (previewVersion != null) {
			previewVersion.setContainer(this);
		}
	}

	public Set getVersions() {
		return this.versions;
	}

	public void setVersions(Set versions) {
		this.versions = versions;
	}	

}
