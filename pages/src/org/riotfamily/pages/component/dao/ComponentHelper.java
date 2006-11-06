package org.riotfamily.pages.component.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.pages.component.Component;
import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.VersionContainer;
import org.riotfamily.pages.component.property.PropertyProcessor;

public class ComponentHelper {

	private ComponentRepository repository;	
	
	public ComponentHelper(ComponentRepository repository) {		
		this.repository = repository;
	}
	
	

	public ComponentList cloneComponentList(ComponentList list, String newPath) {
		ComponentList copy = new ComponentList();
		copy.setPath(newPath);
		copy.setKey(list.getKey());
		copy.setDirty(list.isDirty());
		copy.setLiveList(copyContainers(list.getLiveList()));;
		copy.setPreviewList(copyContainers(list.getPreviewList()));
		return copy;
	}
	
	private List copyContainers(List source) {
		if (source == null) {
			return null;
		}
		List dest = new ArrayList(source.size());
		Iterator it = source.iterator();
		while (it.hasNext()) {
			VersionContainer vc = (VersionContainer) it.next();
			VersionContainer copy = copyVersionContainer(vc);
			dest.add(copy);
		}
		return dest;
	}
	
	private VersionContainer copyVersionContainer(VersionContainer vc) {
		VersionContainer copy = new VersionContainer();
		if (vc.getLiveVersion() != null) {
			Component component = repository.getComponent(vc.getLiveVersion());
			copy.setLiveVersion(cloneComponentVersion(component, 
							vc.getLiveVersion()));
		}
		if (vc.getPreviewVersion() != null) {
			Component component = repository.getComponent(vc.getPreviewVersion());
			copy.setPreviewVersion(cloneComponentVersion(component, 
							vc.getPreviewVersion()));
		}
		return copy;
	}
	
	public ComponentVersion cloneComponentVersion(Component component,
				ComponentVersion version) {
		ComponentVersion copy = new ComponentVersion(version);
		if (component.getPropertyProcessors() != null) {
			Iterator it = component.getPropertyProcessors().iterator();
			while (it.hasNext()) {
				PropertyProcessor pp = (PropertyProcessor) it.next();
				pp.copy(version.getProperties(), copy.getProperties());
			}
		}
		return copy;
	}
	
	public void deleteComponentVersion(ComponentVersion version) {
		Component component = repository.getComponent(version);
		Iterator it = component.getPropertyProcessors().iterator();
		while (it.hasNext()) {
			PropertyProcessor pp = (PropertyProcessor) it.next();
			pp.delete(version.getProperties());
		}
	}
}
