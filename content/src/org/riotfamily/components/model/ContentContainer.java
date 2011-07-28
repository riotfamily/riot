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
package org.riotfamily.components.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.riotfamily.common.hibernate.ActiveRecordBeanSupport;
import org.riotfamily.common.web.cache.CascadeCacheInvalidation;
import org.riotfamily.common.web.cache.TagCacheItems;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.core.security.auth.RiotUser;

/**
 * Entity that holds references to multiple Content versions.
 */
@Entity
@Table(name="riot_content_containers")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="components")
@TagCacheItems
public class ContentContainer extends ActiveRecordBeanSupport {

	@CascadeCacheInvalidation
	private ContentContainerOwner owner;
	
	private Content liveVersion;

	private Content previewVersion;
	
	private Date lastPublished;
	
	private String lastPublishedBy;

	
	protected ContentContainer() {
	}
	
	public ContentContainer(ContentContainerOwner owner) {
		this.owner = owner;
	}

	@Type(type="org.riotfamily.common.hibernate.AnyIdAnyType")
	@Columns(columns = {
	    @Column(name="owner_class"),
	    @Column(name="owner_id")
	})
	public ContentContainerOwner getOwner() {
		return owner;
	}
	
	protected void setOwner(ContentContainerOwner owner) {
		this.owner = owner;
	}

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="live_version")
	public Content getLiveVersion() {
		return this.liveVersion;
	}

	public void setLiveVersion(Content liveVersion) {
		if (liveVersion != null) {
			liveVersion.setContainer(this);
		}
		this.liveVersion = liveVersion;
	}
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="preview_version")
	public Content getPreviewVersion() {
		if (previewVersion == null) {
			previewVersion = new Content(this);
		}
		return previewVersion;
	}
	
	public void setPreviewVersion(Content previewVersion) {
		if (previewVersion != null) {
			previewVersion.setContainer(this);
		}
		this.previewVersion = previewVersion;
	}
	
	public Date getLastPublished() {
		return lastPublished;
	}
	
	public void setLastPublished(Date lastPublished) {
		this.lastPublished = lastPublished;
	}
	
	public String getLastPublishedBy() {
		return lastPublishedBy;
	}
	
	public void setLastPublishedBy(String lastPublishedBy) {
		this.lastPublishedBy = lastPublishedBy;
	}
	
	public Content getContent(boolean preview) {
		if (!preview && liveVersion != null) {
			return liveVersion;
		}
		return getPreviewVersion();
	}
	
	@Transient
	public boolean isPublished() {
		return liveVersion != null;
	}
	
	@Transient
	public boolean isDirty() {
		return !isPublished()|| getPreviewVersion().isDirty();
	}
		
	// -----------------------------------------------------------------------
	
	public void publish() {
		if (isDirty()) {
			unpublish();
			Content preview = getPreviewVersion();
			liveVersion = new Content(preview);
			liveVersion.save();
			preview.setDirty(false);
			lastPublished = new Date();
			RiotUser currentUser = AccessController.getCurrentUser();
			if (currentUser != null) {
				lastPublishedBy = currentUser.getUserId();
			}
		}
	}
	
	public void unpublish() {
		if (liveVersion != null) {
			liveVersion.delete();
			liveVersion = null;
		}
	}
	
	public void discard() {		
		if (liveVersion != null) {
			previewVersion.delete();
			previewVersion = new Content(liveVersion);
			previewVersion.save();
		}
	}
	
	// -----------------------------------------------------------------------
	
	public static ContentContainer load(Long id) {
		return load(ContentContainer.class, id);
	}
	
	public static ContentContainer loadByContent(Content content) {
		return query(ContentContainer.class,
				"from {} where previewVersion = ? or liveVersion = ?", 
				content, content).load();
	}

}
