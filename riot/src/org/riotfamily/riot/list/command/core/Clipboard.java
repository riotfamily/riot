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

	private String objectId;

	private String parentId;

	private ListDefinition listDefinition;

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
		this.mode = mode;
		objectId = context.getObjectId();
		parentId = context.getParentId();
		listDefinition = context.getListDefinition();
	}

	public void clear() {
		mode = MODE_EMPTY;
		objectId = null;
		parentId = null;
		listDefinition = null;
	}

	public boolean isEmpty() {
		return mode == MODE_EMPTY;
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
		Object item = dao.load(objectId);
		Object parent = context.getBean();
		dao.addChild(item, parent);
		if (parentId != null) {
			Object previousParent = EditorDefinitionUtils.loadParent(
					listDefinition, parentId);

			CutAndPasteEnabledDao previousDao = (CutAndPasteEnabledDao)
					listDefinition.getListConfig().getDao();

			previousDao.removeChild(item, previousParent);
		}
	}

	private void pasteCopied(CommandContext context) {
		CopyAndPasteEnabledDao dao = (CopyAndPasteEnabledDao) context.getDao();
		Object item = dao.load(objectId);
		Object parent = context.getBean();
		dao.addCopy(item, parent);
	}


	public boolean isCut(CommandContext context) {
		return mode == MODE_CUT && ObjectUtils.nullSafeEquals(
				objectId, context.getObjectId());
	}

	public boolean isCopied(CommandContext context) {
		return mode == MODE_COPY && ObjectUtils.nullSafeEquals(
				objectId, context.getObjectId());
	}

	private boolean isSameParent(CommandContext context) {
		return ObjectUtils.nullSafeEquals(parentId, context.getParentId());
	}

	private boolean isCutObjectAncestor(CommandContext context) {
		if (mode == MODE_CUT) {
			EditorReference ref = context.getListDefinition().createEditorPath(
					null, context.getParentId(),
					context.getMessageResolver()).getParent();

			while (ref != null) {
				if (objectId.equals(ref.getObjectId())) {
					return true;
				}
				ref = ref.getParent();
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
		RiotDao sourceDao = listDefinition.getListConfig().getDao();
		RiotDao dao = context.getDao();
		return dao.getEntityClass().equals(sourceDao.getEntityClass());
	}

}
