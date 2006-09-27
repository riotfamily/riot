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
	
	private ComponentList list;
	
	private ComponentVersion liveVersion;
	
	private ComponentVersion previewVersion;
	
	private Set versions;

	public VersionContainer() {
	}
	
	public VersionContainer(ComponentList list) {
		this.list = list;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ComponentList getList() {
		return this.list;
	}
	
	public void setList(ComponentList list) {
		this.list = list;
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
