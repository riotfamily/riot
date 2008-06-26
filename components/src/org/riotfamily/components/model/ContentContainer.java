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

import java.util.Collections;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="riot_content_containers")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="components")
public class ContentContainer {

	private Long id;

	private Content liveVersion;

	private Content previewVersion;
	
	private boolean dirty;
	
	public ContentContainer() {
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="live_version")
	public Content getLiveVersion() {
		return this.liveVersion;
	}

	public void setLiveVersion(Content liveVersion) {
		this.liveVersion = liveVersion;
	}

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="preview_version")
	public Content getPreviewVersion() {
		if (previewVersion == null) {
			previewVersion = new Content();
		}
		return previewVersion;
	}

	public void setPreviewVersion(Content previewVersion) {
		this.previewVersion = previewVersion;
	}

	@Transient
	public Content getLatestVersion() {
		return previewVersion != null ? previewVersion : liveVersion;
	}

	@Transient
	public Content getContent(boolean preview) {
		if (preview && previewVersion != null) {
			return previewVersion;
		}
		return liveVersion;
	}
	
	public Map<String, Object> unwrap(boolean preview) {
		Content content = getContent(preview);
		if (content != null) { 
			return content.unwrap();
		}
		return Collections.emptyMap();
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	@Transient
	public boolean isPublished() {
		return liveVersion != null;
	}
	
}
