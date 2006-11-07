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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
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
