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
		String rootEditorId = (String) request.getAttribute("rootEditorId");
		String rootId = (String) request.getAttribute("rootId");

		EditorReference rootRef = null;
		EditorReference prev = null;
		
		Iterator<EditorReference> it = path.getComponents().iterator();
		
		if (rootEditorId != null) {
			while (it.hasNext()) {
				EditorReference ref = it.next();
				if (rootEditorId.equals(ref.getEditorId()) 
						&& (rootId == null || rootId.equals(ref.getObjectId()))) {
					
					rootRef = ref;
					break;
				}
				prev = ref;
				it.remove();
			}
		}
		
		it = path.getComponents().iterator();
		while (it.hasNext()) {
			EditorReference ref = (EditorReference) it.next();
			String url = ref.getEditorUrl();
			
			if (rootRef != null && ref.getEditorType().equals("node")) {
				url = rootRef.getEditorUrl();
			}
				
			// Add choose parameter ...
			url = ServletUtils.setParameter(url, "choose", 
					(String) request.getAttribute("targetEditorId"));
			
			// Add riootEditorId parameter ...
			if (rootEditorId != null) { 
				url = ServletUtils.setParameter(url, "rootEditorId", rootEditorId);
			}
			
			ref.setEditorUrl(url);
			
			
			if (ref.getEditorType().equals("list")
					|| ref.getEditorType().equals("node")) {
				
				if (prev != null) {
					ref.setLabel(prev.getLabel());
					prev = null;
				}
			}
			else {
				prev = ref;
				it.remove();
			}
		}
	}
}
