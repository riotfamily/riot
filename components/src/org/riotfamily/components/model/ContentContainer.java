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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.FetchMode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.criterion.Restrictions;
import org.riotfamily.common.hibernate.ActiveRecordSupport;
import org.riotfamily.components.model.wrapper.ComponentListWrapper;
import org.riotfamily.components.model.wrapper.ValueWrapper;
import org.riotfamily.core.security.AccessController;

@Entity
@Table(name="riot_content_containers")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="components")
public class ContentContainer extends ActiveRecordSupport {

	private Content liveVersion;

	private Content previewVersion;
	
	private boolean dirty;
	
	public ContentContainer() {
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="live_version")
	@Cascade(CascadeType.ALL)
	public Content getLiveVersion() {
		return this.liveVersion;
	}

	public void setLiveVersion(Content liveVersion) {
		this.liveVersion = liveVersion;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="preview_version")
	@Cascade(CascadeType.ALL)
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
	public Content getContent(boolean preview) {
		if (!preview && liveVersion != null) {
			return liveVersion;
		}
		return previewVersion;
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

	// -----------------------------------------------------------------------
	
	public void publish() {
		if (isDirty()) {
			Content preview = getPreviewVersion();
			if (preview != null) {
				AccessController.assertIsGranted("publish", this);
				Content liveVersion = getLiveVersion();
				setLiveVersion(preview.createCopy());
				if (liveVersion != null) {
					liveVersion.delete();
				}
				setDirty(false);
				//FIXME ComponentCacheUtils.invalidateContainer(cacheService, this);
			}
		}
	}
	
	public void discard() {		
		Content live = getLiveVersion();
		if (live != null) {
			Content preview = getPreviewVersion();
			setPreviewVersion(live.createCopy());
			if (preview != null) {
				preview.delete();
			}
			setDirty(false);
			//FIXME ComponentCacheUtils.invalidateContainer(cacheService, this);
		}
	}
	
	public static ContentContainer load(Long id) {
		return load(ContentContainer.class, id);
	}
	
	public static ContentContainer findByComponent(Component component) {
		ComponentListWrapper wrapper = (ComponentListWrapper)
				getSession().createCriteria(ComponentListWrapper.class)
				.add(Restrictions.eq("value", component.getList()))
				.setFetchMode("value", FetchMode.SELECT)
				.uniqueResult();

		return findByWrapper(wrapper);
	}
	
	public static ContentContainer findByWrapper(ValueWrapper<?> wrapper) {
		if (wrapper == null) {
			return null;
		}
		Content content = load("select c from Content c join c.wrappers w " 
				+ "where w = ?", wrapper);
		
		if (content != null) {
			return load("from ContentContainer contentContainer" 
					+ " where contentContainer.liveVersion = ?"
					+ " or contentContainer.previewVersion = ?",
					content, content);
		}
		else {
			ValueWrapper<?> parent = load("select l from ListWrapper l " 
					+ "join l.wrapperList w where w = ?", wrapper);
			
			if (parent == null) {			
				parent = load("select m from MapWrapper m " 
						+ "join m.wrapperMap w where w = ?", wrapper);
			}
			if (parent != null) {
				return findByWrapper(parent);
			}
		}
		return null;
	}
}
