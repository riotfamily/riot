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
package org.riotfamily.forms.element.support.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.forms.ajax.JavaScriptEvent;
import org.riotfamily.forms.ajax.JavaScriptEventAdapter;
import org.riotfamily.forms.bind.Editor;
import org.riotfamily.forms.element.SelectElement;
import org.riotfamily.forms.element.support.AbstractEditorBase;
import org.riotfamily.forms.error.ErrorUtils;
import org.riotfamily.forms.event.ChangeEvent;
import org.riotfamily.forms.event.ChangeListener;
import org.riotfamily.forms.support.MessageUtils;
import org.springframework.util.Assert;

/**
 * Abstract superclass for elements that let the user choose from a set of
 * options like selectboxes or radio button groups.
 */
public abstract class AbstractSelectElement extends AbstractEditorBase implements
		Editor, SelectElement, JavaScriptEventAdapter {

	
	private OptionsModel optionsModel;
	
	private String valueProperty;
	
	private String labelProperty;
	
	private String labelMessageKey;
	
	private boolean appendLabel;

	private OptionRenderer optionRenderer;
	
	private List options;
	
	private List listeners;
	

	public void setOptionRenderer(OptionRenderer optionRenderer) {
		this.optionRenderer = optionRenderer;
	}

	public void renderOption(Option option) {
		optionRenderer.renderOption(option, getFormContext().getWriter());
	}
	
	public void setValueProperty(String valueProperty) {
		this.valueProperty = valueProperty;
	}

	public void setLabelProperty(String labelProperty) {
		this.labelProperty = labelProperty;
	}

	public void setLabelMessageKey(String labelMessageKey) {
		this.labelMessageKey = labelMessageKey;
	}
	
	public void setAppendLabel(boolean appendLabelToMessageKey) {
		this.appendLabel = appendLabelToMessageKey;
	}

	public void setOptionsModel(OptionsModel optionsModel) {
		this.optionsModel = optionsModel;
		options = null;
		setValue(null);
	}

	public void setOptionValues(Collection optionValues) {
		setOptionsModel(new StaticOptionsModel(optionValues));
	}
	
	public OptionsModel getOptionsModel() {
		return optionsModel;
	}

	protected final List getOptions() {
		if (options == null) {
			options = createOptions();
		}
		return options;
	}
	
	protected List createOptions() {
		List options = new ArrayList();
		if (optionsModel != null) {			
			Iterator it = optionsModel.getOptionValues().iterator();
			for (int i = 0; it.hasNext(); i++) {
				Object item = it.next();
				String label = getOptionLabel(item);
				Object value = getOptionValue(item);
				options.add(new Option(item, value, label, this));
			}
		}
		return options;
	}
	
	protected String getOptionLabel(Object item) {
		Object obj = item;
		if (labelProperty != null) {
			obj = PropertyUtils.getProperty(item, labelProperty);
		}
		String label = obj != null ? obj.toString() : null;
		if (labelMessageKey != null) {
			if (appendLabel) {
				return MessageUtils.getMessage(this, labelMessageKey + label,
						null, label);
			}
			else {
				return MessageUtils.getMessage(this, labelMessageKey, 
						new Object[] {obj}, label);
			}
		}
		else {
			return label;
		}
	}
	
	protected Object getOptionValue(Object item) {
		if (item == null) {
			return null;
		}
		if (valueProperty != null) {
			return PropertyUtils.getProperty(item, valueProperty);
		}
		else {
			return item;
		}
	}
	
	public int getOptionIndex(Option option) {
		Assert.notNull(options);
		return options.indexOf(option);
	}
	
	protected abstract boolean hasSelection();
			
	protected void validate() {
		if (isRequired() && !hasSelection()) {
			ErrorUtils.rejectRequired(this);
		}
	}
	
	public int getEventTypes() {
		if (listeners != null) {
			return JavaScriptEvent.ON_CHANGE;
		}
		return 0;
	}

	public void addChangeListener(ChangeListener listener) {
		if (listeners == null) {
			listeners = new ArrayList();
		}
		listeners.add(listener);
	}

	protected void fireChangeEvent(Object newValue, Object oldValue) {
		if (listeners != null) {
			ChangeEvent event = new ChangeEvent(this, newValue, oldValue);
			Iterator it = listeners.iterator();
			while (it.hasNext()) {
				ChangeListener listener = (ChangeListener) it.next();
				listener.valueChanged(event);
			}
		}
	}

}