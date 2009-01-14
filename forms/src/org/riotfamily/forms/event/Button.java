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
package org.riotfamily.forms.event;

import java.io.PrintWriter;
import java.util.List;

import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.AbstractEditorBase;
import org.riotfamily.forms.MessageUtils;
import org.riotfamily.forms.request.FormRequest;


/**
 * A button widget.
 */
public class Button extends AbstractEditorBase 
		implements JavaScriptEventAdapter {

	private List<ClickListener> listeners;
	
	private String labelKey;
	
	private String label;
	
	private boolean submit = false;
	
	private String partitialSubmit;
	
	private boolean clicked;
	
	private int tabIndex;
	
	
	public Button() {
		setInline(true);
	}

	public String getEventTriggerId() {		
		return getId() + "-event-source";
	}

	public String getLabelKey() {
		return labelKey;
	}
	
	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}
	
	public String getLabel() {
		if (label != null) {
			return label;
		}
		if (getLabelKey() != null) {
			return FormatUtils.stripTags(MessageUtils.getMessage(this, getLabelKey()));
		}
		return "Submit";
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	protected String getSystemStyleClass() {
		String suffix = null;
		if (getLabelKey() != null) {
			int i = getLabelKey().lastIndexOf('.');
			String s = i != -1 ? getLabelKey().substring(i + 1) : getLabelKey();
			suffix = FormatUtils.toCssClass(s);
		}
		else {
			suffix = FormatUtils.toCssClass(getLabel());
		}
		return "button button-" + suffix;
	}
	
	@Override
	protected String getWrapperStyleClass() {
		return FormatUtils.join(" ", "button-wrapper", 
				isEnabled() ? null : "disabled");
	}

	public void setSubmit(boolean submit) {
		this.submit = submit;
	}
	
	public void setPartitialSubmit(String partitialSubmit) {
		this.partitialSubmit = partitialSubmit;
		this.submit = partitialSubmit != null;
	}
	
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void addClickListener(ClickListener listener) {
		if (listeners == null) {
			listeners = Generics.newArrayList();
		}
		listeners.add(listener);
	}
	
	public void processRequest(FormRequest request) {		
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
		tag.startEmpty("input")
				.attribute("type", submit ? "submit" : "button")
				.attribute("id", getEventTriggerId())
				.attribute("class", getSystemStyleClass())				
				.attribute("tabindex", tabIndex)
				.attribute("disabled", !isEnabled())
				.attribute("name", getParamName())
				.attribute("value", getLabel());
		
		if (partitialSubmit != null && isEnabled()) {
			tag.attribute("onclick", "submitElement('" + 
					partitialSubmit + "', this); return false;");
		}
		tag.end();
	}
	
	protected void onClick() {
		log.debug("Button " + getId() + " clicked.");
		fireClickEvent();
	}
	
	protected void fireClickEvent() {		
		if (listeners != null) {
			ClickEvent event = new ClickEvent(this);
			for (ClickListener listener : listeners) {
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
