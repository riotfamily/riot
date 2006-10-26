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
	}

	public ComponentVersion getPreviewVersion() {
		return this.previewVersion;
	}

	public void setPreviewVersion(ComponentVersion previewVersion) {
		this.previewVersion = previewVersion;
	}

	public Set getVersions() {
		return this.versions;
	}

	public void setVersions(Set versions) {
		this.versions = versions;
	}
	
	public VersionContainer copy(ComponentRepository repository) {
		VersionContainer copy = new VersionContainer();
		if (liveVersion != null) {
			Component component = repository.getComponent(liveVersion);
			copy.setLiveVersion(component.copy(liveVersion));
		}
		if (previewVersion != null) {
			Component component = repository.getComponent(previewVersion);
			copy.setPreviewVersion(component.copy(previewVersion));
		}
		return copy;
	}

}
