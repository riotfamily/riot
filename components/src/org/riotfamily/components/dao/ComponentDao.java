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
package org.riotfamily.components.dao;

import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.model.wrapper.ValueWrapper;

/**
 * DAO interface that provides methods to access
 * {@link ComponentList ComponentList}s,
 * {@link Content ComponentVersion}s and
 * {@link Component VersionContainer}s.
 */
public interface ComponentDao {

	/**
	 * Loads the ComponentList specified  by the given id.
	 */
	public ComponentList loadComponentList(Long id);

	/**
	 * Loads the Component specified  by the given id.
	 */
	public Component loadComponent(Long id);
	
	/**
	 * Loads the VersionContainer specified  by the given id.
	 */
	public ContentContainer loadContentContainer(Long id);

	/**
	 * Loads the Content specified  by the given id.
	 */
	public Content loadContent(Long id);

	/**
	 * Saves the given ComponentList.
	 */
	public void saveComponentList(ComponentList list);
		
	/**
	 * Saves the given ContentContainer.
	 */
	public void saveContentContainer(ContentContainer container);

	/**
	 * Saves the given Content.
	 */
	public void saveContent(Content content);
	
	/**
	 * Merges the given Content.
	 */
	public Content mergeContent(Content content);
	
	/**
	 * Deletes the given ComponentList.
	 */
	public void deleteComponentList(ComponentList list);

	/**
	 * Deletes the given Content.
	 */
	public void deleteContent(Content content);

	/**
	 * Deletes the given ContentContainer.
	 */
	public void deleteContentContainer(ContentContainer container);
	
	public boolean publishContainer(ContentContainer container);
	
	public boolean discardContainer(ContentContainer container);
	
	public ContentContainer findContainerForComponent(Component component);
	
	public ContentContainer findContainerForWrapper(ValueWrapper<?> wrapper);
	
}
