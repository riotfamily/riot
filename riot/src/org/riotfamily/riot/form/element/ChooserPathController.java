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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.form.element;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ui.EditorPath;
import org.riotfamily.riot.editor.ui.EditorReference;
import org.riotfamily.riot.editor.ui.PathController;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ChooserPathController extends PathController {

	public ChooserPathController(EditorRepository repository) {
		super(repository);
	}

	protected String getViewName() {
		return ResourceUtils.getPath(ChooserPathController.class, 
				"ChooserPathView.ftl");
	}
	
	protected void processPath(EditorPath path, HttpServletRequest request) {
		String root = (String) request.getAttribute("rootEditorId");
		EditorReference prev = null;
		Iterator it = path.getComponents().iterator();
		while (it.hasNext()) {
			EditorReference ref = (EditorReference) it.next();
			if (root != null && !root.equals(ref.getEditorId())) {
				it.remove();
			}
			else {
				root = null;
				ref.setEditorUrl(ServletUtils.addParameter(ref.getEditorUrl(), 
						"choose", (String) request.getAttribute("targetEditorId")));
				
				if (ref.getEditorType().equals("list")) {
					if (prev != null) {
						ref.setLabel(prev.getLabel());
						prev = null;
					}
				}
				else {
					if (!ref.getEditorType().equals("group")) {
						prev = ref;
					}
					if (!ref.getEditorType().equals("node")) {
						it.remove();
					}
				}
			}
		}
	}
}
