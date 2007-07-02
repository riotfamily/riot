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

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.editor.ui.EditorReference;
import org.riotfamily.riot.list.ListConfig;
import org.springframework.util.Assert;

/**
 *
 */
public class ListDefinition extends AbstractEditorDefinition {

	protected static final String TYPE_LIST = "list";

	private EditorDefinition displayDefinition;

	private String listId;

	public ListDefinition(EditorRepository repository) {
		this(repository, TYPE_LIST);
	}

	protected ListDefinition(EditorRepository repository, String editorType) {
		super(repository, editorType);
	}

	public ListDefinition(ListDefinition prototype,
			EditorRepository repository) {

		this(repository);
		displayDefinition = prototype.getDisplayDefinition();
		listId = prototype.getListId();
		setId(prototype.getId());
	}

	public String getListId() {
		return listId;
	}

	protected String getDefaultName() {
		return getListId();
	}

	public void setListId(String listId) {
		this.listId = listId;
		Assert.notNull(getListConfig(), "No such list: " + listId);
	}

	public Class getBeanClass() {
		return getListConfig().getItemClass();
	}

	public EditorDefinition getDisplayDefinition() {
		return displayDefinition;
	}

	public void setDisplayDefinition(EditorDefinition editorDef) {
		this.displayDefinition = editorDef;
	}

	public EditorReference createEditorPath(String objectId, String parentId,
			MessageResolver messageResolver) {

		EditorReference parent = null;
		if (getParentEditorDefinition() != null) {
			// Delegate call to parent editor passing the parentId as objectId
			parent = getParentEditorDefinition().createEditorPath(
					parentId, null, messageResolver);
		}

		EditorReference component = createReference(parentId, messageResolver);

		component.setParent(parent);
		return component;
	}

	public EditorReference createEditorPath(Object bean,
			MessageResolver messageResolver) {

		EditorReference component = null;
		EditorReference parent = null;

		if (getParentEditorDefinition() != null) {
			parent = getParentEditorDefinition().createEditorPath(bean, messageResolver);
			component = createReference(parent.getObjectId(), messageResolver);
			component.setParent(parent);
		}
		else {
			component = createReference(null, messageResolver);
			if (getParentEditorDefinition() != null) {
				parent = getParentEditorDefinition().createEditorPath(
						null, null, messageResolver);

				component.setParent(parent);
			}
		}
		return component;
	}

	/**
	 * Creates a reference to the list. The method is used by the {@link
	 * org.riotfamily.riot.form.ui.FormController FormController} to create
	 * links pointing to the child lists.
	 */
	public EditorReference createReference(String parentId,
			MessageResolver messageResolver) {

		EditorReference ref = new EditorReference();
		ref.setEditorType(getEditorType());
		ref.setIcon(getIcon());

		String defaultLabel = FormatUtils.camelToTitleCase(getListId());
		ref.setLabel(messageResolver.getMessage(
				getMessageKey().toString(), null, defaultLabel));

		ref.setDescription(messageResolver.getMessage(
				getMessageKey().append(".description").toString(), null, null));

		ref.setEditorUrl(getEditorUrl(null, parentId));
		return ref;
	}

	public ListConfig getListConfig() {
		return getEditorRepository().getListRepository().getListConfig(listId);
	}

	public String getEditorUrl(String objectId, String parentId) {
		StringBuffer sb = new StringBuffer();
		sb.append(getEditorRepository().getRiotServletPrefix());
		sb.append("/list/").append(getId());
		if (parentId != null) {
			sb.append('/').append(parentId);
		}
		return sb.toString();
	}

}
