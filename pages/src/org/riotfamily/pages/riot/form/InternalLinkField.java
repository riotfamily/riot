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
package org.riotfamily.pages.riot.form;

import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.forms.CompositeElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.element.TextField;
import org.riotfamily.forms.event.ChangeEvent;
import org.riotfamily.forms.event.ChangeListener;
import org.riotfamily.pages.model.Page;
import org.riotfamily.riot.form.element.ObjectChooser;

public class InternalLinkField extends CompositeElement 
		implements Editor, ChangeListener {

	private PathCompleter pathCompleter;
	
	private TextField textField;
	
	private ObjectChooser chooser;
	
	public InternalLinkField(PathCompleter pathCompleter) {
		this.pathCompleter = pathCompleter;
		textField = new TextField();
		chooser = new ObjectChooser();
		chooser.setTargetEditorId("page");
		chooser.addChangeListener(this);
		addComponent(textField);
		addComponent(chooser);
	}

	public void setCrossSite(boolean crossSite) {
		//FIXME
	}
	
	public void valueChanged(ChangeEvent event) {
		Page page = (Page) event.getNewValue();
		textField.setValue(page.getUrl(pathCompleter));
		getFormListener().elementChanged(textField);
	}

	public Object getValue() {
		return textField.getValue();
	}

	public void setValue(Object value) {
		textField.setValue(value);
	}
	
	/*
	public String getChooserQueryString() {
		StringBuffer sb = new StringBuffer();
		Object pageId = getForm().getAttribute("pageId");
		Object siteId = getForm().getAttribute("siteId");
		if (pageId == null && siteId == null && !crossSite) {
			crossSite = true;
		}
		sb.append("?crossSite=").append(crossSite);
		if (pageId != null) {
			sb.append('&').append(PageChooserController.PAGE_ID_PARAM)
					.append('=').append(pageId);
		}
		else if (siteId != null) {
			sb.append('&').append(PageChooserController.SITE_ID_PARAM)
					.append('=').append(siteId);
		}
		return sb.toString();
	}
	*/

}
