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
package org.riotfamily.riot.editor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.i18n.MessageResolver;

/**
 * Abstract base class for {@link EditorDefinition EditorDefinitions}.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class AbstractEditorDefinition implements EditorDefinition {

	private String id;

	private String name;

	private EditorRepository editorRepository;

	private EditorDefinition parentEditorDefinition;

	private String icon;

	private boolean hidden;
	
	private EditorCondition condition;

	public void setEditorRepository(EditorRepository editorRepository) {
		this.editorRepository = editorRepository;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public final void setName(String name) {
		this.name = name;
		if (id == null) {
			id = name;
		}
	}

	public final String getName() {
		return name != null ? name : getDefaultName();
	}

	protected String getDefaultName() {
		return null;
	}

	public Class getBeanClass() {
		return null;
	}

	public EditorDefinition getParentEditorDefinition() {
		return this.parentEditorDefinition;
	}

	public void setParentEditorDefinition(EditorDefinition parentEditorDefinition) {
		this.parentEditorDefinition = parentEditorDefinition;
	}

	public void addReference(List refs, EditorDefinition parentDef,
			Object parent, MessageResolver messageResolver) {

		if (parent != null && (condition == null || condition.showEditor(parent))) {
			String parentId = EditorDefinitionUtils.getObjectId(parentDef, parent);
			refs.add(createReference(parentId, messageResolver));
		}
	}

	protected EditorRepository getEditorRepository() {
		return editorRepository;
	}

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public void setCondition(EditorCondition condition) {
		this.condition = condition;
	}
	
	protected EditorCondition getCondition() {
		return this.condition;
	}

	protected StringBuffer getMessageKey() {
		StringBuffer key = new StringBuffer();
		key.append(getEditorType());
		key.append('.');
		key.append(getName());
		return key;
	}

	public String getLabelProperty() {
		return null;
	}

	public String getLabel(Object object) {
		if (object == null) {
			return "New"; //TODO I18nize this
		}
		if (getLabelProperty() != null) {
			StringBuffer label = new StringBuffer();
			Pattern p = Pattern.compile("(\\w+)(\\W*)");
			Matcher m = p.matcher(getLabelProperty());
			while (m.find()) {
				String property = m.group(1);
				Object value = PropertyUtils.getProperty(object, property);
				if (value != null) {
					label.append(value);
					label.append(m.group(2));
				}
			}
			if (label.length() > 0) {
				return label.toString();
			}
		}
		return object.toString();
	}

}
