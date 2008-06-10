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
package org.riotfamily.components.model;

import java.util.Map;


public class ContentContainer {

	private Long id;

	private Content liveVersion;

	private Content previewVersion;
	
	public ContentContainer() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Content getLiveVersion() {
		return this.liveVersion;
	}

	public void setLiveVersion(Content liveVersion) {
		this.liveVersion = liveVersion;
	}

	public Content getPreviewVersion() {
		if (previewVersion == null) {
			previewVersion = new Content();
		}
		return previewVersion;
	}

	public void setPreviewVersion(Content previewVersion) {
		this.previewVersion = previewVersion;
	}

	public Content getLatestVersion() {
		return previewVersion != null ? previewVersion : liveVersion;
	}

	public Content getContent(boolean preview) {
		if (preview && previewVersion != null) {
			return previewVersion;
		}
		return liveVersion;
	}
	
	public Map<String, Object> unwrapValues(boolean preview) {
		return getContent(preview).unwrapValues();
	}
	
	public boolean isDirty() {
		return previewVersion != null && previewVersion.isDirty();
	}
	
	public boolean isPublished() {
		return liveVersion != null;
	}

	public void setDirty(boolean dirty) {
		previewVersion.setDirty(dirty);
	}
	
}
