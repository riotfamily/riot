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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.riot.form;

import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.forms.AbstractEditorBinder;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ContentContainerEditorBinder extends AbstractEditorBinder {

	private ContentContainer container;
	
	private Content previewVersion;
	
	public boolean isEditingExistingBean() {
		return container.getLatestVersion() != null;
	}

	public void setBackingObject(Object backingObject) {
		container = (ContentContainer) backingObject;
		if (container == null) {
			container = createContainer();
		}
		previewVersion = container.getPreviewVersion();
		if (previewVersion == null) {
			Content liveVersion = container.getLiveVersion();
			if (liveVersion != null) {
				previewVersion = new Content(liveVersion);
			}
			else {
				previewVersion = new Content();
			}
		}
	}
	
	public Object populateBackingObject() {
		if (container instanceof Component) {
			Component c = (Component) container;
			c.getList().getOrCreatePreviewContainers();
		}
		return super.populateBackingObject();
	}
	
	protected ContentContainer createContainer() {
		return new ContentContainer();
	}
		
	public Object getBackingObject() {
		container.setPreviewVersion(previewVersion);
		return container;
	}

	public Class getBeanClass() {
		return container.getClass();
	}
	
	public Class getPropertyType(String path) {
		return Object.class;
	}

	public Object getPropertyValue(String property) {
		return previewVersion.getValue(property);
	}

	public void setPropertyValue(String property, Object value) {
		previewVersion.setValue(property, value);
	}

}
