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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element.core;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.forms.ajax.JavaScriptEvent;
import org.riotfamily.forms.ajax.JavaScriptEventAdapter;
import org.riotfamily.forms.element.support.AbstractEditorBase;
import org.riotfamily.forms.event.ClickEvent;
import org.riotfamily.forms.event.ClickListener;
import org.riotfamily.forms.i18n.MessageUtils;


/**
 * A button widget.
 */
public class Button extends AbstractEditorBase 
		implements JavaScriptEventAdapter {

	private List listeners;
	
	private String labelKey;
	
	private String label;
	
	private String cssClass;
	
	private boolean submit = false;
	
	private boolean clicked;
	
	private int tabIndex;

	public String getLabelKey() {
		return labelKey;
	}
	
	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}
	
	public String getLabel() {
		if (label == null) {
			if (labelKey != null) {
				label = MessageUtils.getMessage(this, labelKey);
			}
			else {
				label = "Submit";
			}
		}
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getCssClass() {
		if (cssClass == null) {
			if (labelKey != null) {
				int i = labelKey.lastIndexOf('.');
				String s = i != -1 ? labelKey.substring(i + 1) : labelKey;
				cssClass = "button button-" + FormatUtils.toCssClass(s);
			}
			else {
				cssClass = "button button-" + FormatUtils.toCssClass(getLabel());
			}
		}
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public void setSubmit(boolean submit) {
		this.submit = submit;
	}
	
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void addClickListener(ClickListener listener) {
		if (listeners == null) {
			listeners = new ArrayList();
		}
		listeners.add(listener);
	}
	
	public void processRequest(HttpServletRequest request) {		
		String value = request.getParameter(getParamName());
		clicked = value != null;
		if (clicked) {
			onClick();
		}
	}
	
	public boolean isClicked() {
		return this.clicked;
	}

	public void renderInternal(PrintWriter writer) {
		TagWriter tag = new TagWriter(writer);
		tag.startEmpty(Html.INPUT)
				.attribute(Html.INPUT_TYPE, "submit")
				.attribute(Html.COMMON_CLASS, getCssClass())
				.attribute(Html.COMMON_ID, getId())
				.attribute(Html.COMMON_TABINDEX, tabIndex)
				.attribute(Html.INPUT_NAME, getParamName())
				.attribute(Html.INPUT_VALUE, getLabel())
				.end();
	}
	
	protected void onClick() {
		log.debug("Button " + getId() + " clicked.");
		fireClickEvent();
	}
	
	protected void fireClickEvent() {		
		if (listeners != null) {
			ClickEvent event = new ClickEvent(this);
			Iterator it = listeners.iterator();
			while (it.hasNext()) {
				ClickListener listener = (ClickListener) it.next();
				listener.clicked(event);				
			}
		}
	}

	public int getEventTypes() {
		if (!submit && listeners != null) {
			return JavaScriptEvent.ON_CLICK;
		}
		return 0;
	}
	
	public void handleJavaScriptEvent(JavaScriptEvent event) {
		if (event.getType() == JavaScriptEvent.ON_CLICK) {
			onClick();
		}
	}

}
