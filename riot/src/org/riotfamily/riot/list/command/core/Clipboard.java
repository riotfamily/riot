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
package org.riotfamily.riot.list.command.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.riotfamily.riot.dao.CopyAndPasteEnabledDao;
import org.riotfamily.riot.dao.CutAndPasteEnabledDao;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ui.EditorReference;
import org.riotfamily.riot.list.command.CommandContext;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.WebUtils;

public class Clipboard {

	public static final int MODE_EMPTY = 0;

	public static final int MODE_COPY = 1;

	public static final int MODE_CUT = 2;

	private static final String SESSION_ATTRIBUTE = Clipboard.class.getName();

	private int mode;

	private int itemsTotal;

	private List objectIds = new ArrayList();

	private List parentIds = new ArrayList();

	private List listDefinitions = new ArrayList();

	public static Clipboard get(CommandContext context) {
		return get(context.getRequest().getSession());
	}

	public static Clipboard get(HttpSession session) {
		return (Clipboard) WebUtils.getOrCreateSessionAttribute(
				session, SESSION_ATTRIBUTE, Clipboard.class);
	}

	public void cut(CommandContext context) {
		put(context, MODE_CUT);
	}

	public void copy(CommandContext context) {
		put(context, MODE_COPY);
	}

	private void put(CommandContext context, int mode) {
		if (context.getBatchIndex() == 0) {
			clear();
		}
		this.mode = mode;
		itemsTotal++;
		objectIds.add(context.getObjectId());
		parentIds.add(context.getParentId());
		listDefinitions.add(context.getListDefinition());
	}

	public void clear() {
		itemsTotal = 0;
		mode = MODE_EMPTY;
		objectIds.clear();
		parentIds.clear();
		listDefinitions.clear();
	}

	public boolean isEmpty() {
		return mode == MODE_EMPTY;
	}

	public List getObjects() {
		if (isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		List result = new ArrayList(itemsTotal);
		for (int index = 0; index < itemsTotal; index++) {
			ListDefinition listDefinition = (ListDefinition)
					listDefinitions.get(index);
			
			String objectId = (String) objectIds.get(index);
			result.add(listDefinition.getListConfig().getDao().load(objectId));
		}
		return result;
	}
	
	
	public boolean canCopy(CommandContext context) {
		return context.getDao() instanceof CopyAndPasteEnabledDao;
	}

	public boolean canCut(CommandContext context) {
		return context.getDao() instanceof CutAndPasteEnabledDao
				&& !isCut(context);
	}

	public boolean canPaste(CommandContext context) {
		return !isEmpty()
				&& isSupportedDao(context)
				&& isCompatibleEntityClass(context)
				&& (mode == MODE_COPY || !isSameParent(context))
				&& !isCutObjectAncestor(context);
	}

	public void paste(CommandContext context) {
		if (mode == MODE_CUT) {
			pasteCut(context);
		}
		else if (mode == MODE_COPY) {
			pasteCopied(context);
		}
		clear();
	}

	private void pasteCut(CommandContext context) {
		CutAndPasteEnabledDao dao = (CutAndPasteEnabledDao) context.getDao();
		for (int index = 0; index < itemsTotal; index++) {
			Object item = dao.load((String) objectIds.get(index));
			Object parent = context.getBean();
			dao.addChild(item, parent);
			String parentId = (String) parentIds.get(index);
			if (parentId != null) {
				ListDefinition listDefinition = (ListDefinition)
						listDefinitions.get(index);
				
				Object previousParent = EditorDefinitionUtils.loadParent(
						listDefinition, parentId);

				CutAndPasteEnabledDao previousDao = (CutAndPasteEnabledDao)
						listDefinition.getListConfig().getDao();

				previousDao.removeChild(item, previousParent);
			}
		}
	}

	private void pasteCopied(CommandContext context) {
		CopyAndPasteEnabledDao dao = (CopyAndPasteEnabledDao) context.getDao();
		for (int index = 0; index < itemsTotal; index++) {
			Object item = dao.load((String) objectIds.get(index));
			Object parent = context.getBean();
			dao.addCopy(item, parent);
		}
	}


	public boolean isCut(CommandContext context) {
		return mode == MODE_CUT && isInClipboard(context);
	}

	public boolean isCopied(CommandContext context) {
		return mode == MODE_COPY && isInClipboard(context);
	}
	
	private boolean isInClipboard(CommandContext context) {
		for (int index = 0; index < itemsTotal; index++) {
			if (ObjectUtils.nullSafeEquals((String) objectIds.get(index),
					context.getObjectId())) {
				
				return true;
			}
		}
		return false;
	}

	private boolean isSameParent(CommandContext context) {
		for (int index = 0; index < itemsTotal; index++) {
			if (!ObjectUtils.nullSafeEquals(parentIds.get(index),
					context.getParentId())) {

				return false;
			}
		}
		return true;
	}

	private boolean isCutObjectAncestor(CommandContext context) {
		if (mode == MODE_CUT) {
			EditorReference ref = context.getListDefinition().createEditorPath(
					null, context.getParentId(),
					context.getMessageResolver()).getParent();

			for (int index = 0; index < itemsTotal; index++) {
				String objectId = (String) objectIds.get(index);
				EditorReference itemRef = ref; 
			
				while (itemRef != null) {
					if (objectId.equals(itemRef.getObjectId())) {
						return true;
					}
					itemRef = itemRef.getParent();
				}
			}
		}
		return false;
	}

	private boolean isSupportedDao(CommandContext context) {
		RiotDao dao = context.getDao();
		return (mode == MODE_CUT && dao instanceof CutAndPasteEnabledDao)
				|| (mode == MODE_COPY && dao instanceof CopyAndPasteEnabledDao);
	}

	private boolean isCompatibleEntityClass(CommandContext context) {
		Class daoEntityClass = context.getDao().getEntityClass();
		for (int index = 0; index < itemsTotal; index++) {
			ListDefinition listDefinition = (ListDefinition)
					listDefinitions.get(index);
			
			RiotDao sourceDao = listDefinition.getListConfig().getDao();
			if (!daoEntityClass.equals(sourceDao.getEntityClass())) {
				return false;
			}
		}
		return true;
	}

}
