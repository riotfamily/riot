package org.riotfamily.components.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.common.hibernate.ActiveRecordBeanSupport;
import org.riotfamily.components.cache.ComponentCacheUtils;
import org.riotfamily.core.security.AccessController;

@Entity
@Table(name="riot_content_containers")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="components")
public class ContentContainer extends ActiveRecordBeanSupport {

	private Content liveVersion;

	private Content previewVersion;
	
	private boolean dirty;

	private CacheService cacheService;
	
	public ContentContainer() {
	}

	@Transient
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="live_version")
	public Content getLiveVersion() {
		return this.liveVersion;
	}

	public void setLiveVersion(Content liveVersion) {
		this.liveVersion = liveVersion;
	}

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
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
	public Content getContent(boolean preview) {
		if (!preview && liveVersion != null) {
			return liveVersion;
		}
		return getPreviewVersion();
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
				ComponentCacheUtils.invalidateContainer(cacheService, this);
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
			ComponentCacheUtils.invalidateContainer(cacheService, this);
		}
	}
	
	public static ContentContainer load(Long id) {
		return load(ContentContainer.class, id);
	}
	
	public static ContentContainer loadByContent(Content content) {
		return load("from " + ContentContainer.class.getName()
				+ " where previewVersion = ? or liveVersion = ?", 
				content, content);
	}

}
