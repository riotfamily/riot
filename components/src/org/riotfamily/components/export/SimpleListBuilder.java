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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   "Felix Gnass [fgnass at neteye dot de]"
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.riotfamily.components.Component;
import org.riotfamily.components.ComponentList;
import org.riotfamily.components.ComponentRepository;
import org.riotfamily.components.ComponentVersion;
import org.riotfamily.components.VersionContainer;


/**
 * Transforms a ComponentList into a SimpleComponentList.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SimpleListBuilder {

	private ComponentRepository componentRepository;
	
	/**
	 * Use this constructor if you don't need to resolve the components' string
	 * properties. Note that invoking {@link SimpleComponent#getProperties()}
	 * will return <code>null</code>, 
	 * use {@link SimpleComponent#getStringProperties()} instead.
	 */
	public SimpleListBuilder() {
	}
	
	/**
	 * Use this constructor if you want the components' properties to be 
	 * resolved. In this case {@link SimpleComponent#getProperties()} will
	 * return the map created by {@link Component#buildModel(ComponentVersion)}. 
	 */
	public SimpleListBuilder(ComponentRepository componentRepository) {
		this.componentRepository = componentRepository;
	}

	/**
	 * Transforms the given ComponentList into a {@link SimpleComponentList}.
	 * @param list the ComponentList to transform
	 * @param preview whether to use the preview version 
	 */
	public SimpleComponentList buildSimpleList(ComponentList list, 
			boolean preview) {
		
		SimpleComponentList simpleList = new SimpleComponentList();
		simpleList.setLocation(list.getLocation());
		List containers = preview 
				? list.getLiveContainers()
				: list.getLiveContainers();
				
		simpleList.setComponents(buildSimpleComponents(containers, preview));
		return simpleList;
	}
	
	private List buildSimpleComponents(List containers, boolean preview) {
		ArrayList result = new ArrayList();
		Iterator it = containers.iterator();
		while (it.hasNext()) {
			VersionContainer container = (VersionContainer) it.next();
			ComponentVersion version = preview 
					? container.getLatestVersion()
					: container.getLiveVersion();
					
			result.add(buildSimpleComponent(version, preview));
		}
		return result;
	}
	
	private SimpleComponent buildSimpleComponent(ComponentVersion version, 
			boolean preview) {
		
		SimpleComponent simpleComponent = new SimpleComponent();
		simpleComponent.setType(version.getType());
		simpleComponent.setStringProperties(version.getProperties());
		if (componentRepository != null) {
			Component component = componentRepository.getComponent(version);
			simpleComponent.setProperties(component.buildModel(version));
		}
		Set childLists = version.getContainer().getChildLists();
		if (childLists != null && !childLists.isEmpty()) {
			simpleComponent.setChildLists(buildSimpleLists(childLists, preview));
		}
		return simpleComponent;
	}
	
	private List buildSimpleLists(Collection lists, boolean preview) {
		ArrayList result = new ArrayList();
		Iterator it = lists.iterator();
		while (it.hasNext()) {
			ComponentList list = (ComponentList) it.next();
			result.add(buildSimpleList(list, preview));
		}
		return result;
	}
}
