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

import java.util.Map;

import org.riotfamily.components.model.ComponentVersion;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.VersionContainer;
import org.riotfamily.components.service.ContentFactory;
import org.riotfamily.forms.AbstractEditorBinder;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class VersionContainerEditorBinder extends AbstractEditorBinder {

	private ContentFactory contentFactory;
	
	private VersionContainer container;
	
	private ComponentVersion previewVersion;
	
	private Map properties;
	
	public VersionContainerEditorBinder(ContentFactory contentFactory) {
		this.contentFactory = contentFactory;
	}
	
	public boolean isEditingExistingBean() {
		return true;
	}

	public void setBackingObject(Object backingObject) {
		if (backingObject == null) {
			container = new VersionContainer();
		}
		else {
			Assert.isInstanceOf(VersionContainer.class, backingObject);
			container = (VersionContainer) backingObject;
		}
		previewVersion = container.getPreviewVersion();
		if (previewVersion == null) {
			ComponentVersion liveVersion = container.getLiveVersion();
			if (liveVersion != null) {
				previewVersion = new ComponentVersion(liveVersion);
			}
			else {
				previewVersion = new ComponentVersion();
			}
		}
		properties = previewVersion.getProperties();
	}
	
	public Object getBackingObject() {
		container.setPreviewVersion(previewVersion);
		return container;
	}

	public Class getBeanClass() {
		return VersionContainer.class;
	}
	
	public Class getPropertyType(String path) {
		return Object.class;
	}

	public Object getPropertyValue(String property) {
		if (getEditor(property) instanceof ContentEditor) {
			return properties.get(property);
		}
		Content content = (Content) properties.get(property);
		return content != null ? content.getValue() : null;
	}

	public void setPropertyValue(String property, Object value) {
		if (value == null) {
			properties.remove(property);
		}
		else if (value instanceof Content) {
			properties.put(property, value);
		}
		else {
			Content content = (Content) properties.get(property);
			if (content != null) {
				try {
					content.setValue(value);
					return;
				}
				catch (ClassCastException e) {
				}
			}
			content = contentFactory.createContent(value);
			Assert.notNull(content, "ContentFactory returned null for value: " + value);
			properties.put(property, content);
		}
	}

}
