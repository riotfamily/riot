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
package org.riotfamily.forms.element.select;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.forms.AbstractEditorBase;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.MessageUtils;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.event.JavaScriptEventAdapter;
import org.riotfamily.forms.options.OptionsModel;
import org.riotfamily.forms.options.OptionsModelUtils;
import org.springframework.util.Assert;

/**
 * Abstract superclass for elements that let the user choose from a set of
 * options like selectboxes or radio button groups.
 */
public abstract class AbstractSelectElement extends AbstractEditorBase implements
		Editor, SelectElement, JavaScriptEventAdapter {

	
	private Object options;
	
	private String valueProperty;
	
	private String labelProperty;
	
	private String labelMessageKey;
	
	private boolean appendLabel;

	private OptionRenderer optionRenderer;
	
	private OptionsModel optionsModel;
	
	private Collection<?> optionValues;
	
	private List<OptionItem> optionItems;
	
	private boolean hideIfEmpty = false;	
	
	public String getEventTriggerId() {		
		return getId() + "-event-source";
	}
	
	public void setOptionRenderer(OptionRenderer optionRenderer) {
		this.optionRenderer = optionRenderer;
	}

	public void renderOption(OptionItem option) {
		optionRenderer.renderOption(option, getFormContext().getWriter(), isEnabled());
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

	public void setOptions(Object options) {
		this.options = options;
		reset();
	}
	
	public void setHideIfEmpty(boolean hideIfEmpty) {
		this.hideIfEmpty = hideIfEmpty;
	}
	
	public Object getOptions() {
		return options;
	}
	
	protected boolean hasOptionValues() {
		if (optionValues == null) {
			if (optionsModel == null) {
				optionsModel = OptionsModelUtils.createOptionsModel(options, this);
			}
			optionValues = optionsModel.getOptionValues(this);
		}
		return !optionValues.isEmpty();
	}
	
	@Override
	public boolean isRequired() {		
		return super.isRequired() && hasOptionValues();
	}
	
	@Override
	public boolean isVisible() {		
		return super.isVisible() && !(hideIfEmpty && !hasOptionValues());
	}

	public final void renderInternal(PrintWriter writer) {
		resetOptionItems();
		renderSelectElement(writer);
	}
	
	protected abstract void renderSelectElement(PrintWriter writer);
	
	public void reset() {
		resetOptionItems();
		resetOptionValues();
		setValue(null);
		if (getFormListener() != null) {
			getFormListener().elementChanged(this);
		}
	}
	
	protected void resetOptionItems() {
		optionItems = null;
	}
	
	protected void resetOptionValues() {
		optionValues = null;
	}
	
	protected final List<OptionItem> getOptionItems() {
		if (optionItems == null) {
			optionItems = createOptionItems();
		}
		return optionItems;
	}
	
	protected List<OptionItem> createOptionItems() {
		if (optionsModel == null) {
			optionsModel = OptionsModelUtils.createOptionsModel(options, this);
		}
		List<OptionItem> items = new ArrayList<OptionItem>();
		if (options != null) {			
			optionValues = optionsModel.getOptionValues(this);			
			Iterator it = optionValues.iterator();
			for (int i = 0; it.hasNext(); i++) {
				Object item = it.next();
				String label = getOptionLabel(item);
				Object value = getOptionValue(item);
				items.add(new OptionItem(item, value, label, this));
			}
		}
		return items;
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
	
	public int getOptionIndex(OptionItem option) {
		Assert.notNull(optionItems);
		return optionItems.indexOf(option);
	}
	
	protected abstract boolean hasSelection();
			
	protected void validate() {
		if (isRequired() && !hasSelection()) {
			ErrorUtils.rejectRequired(this);
		}
	}
	
	public int getEventTypes() {
		if (hasListeners()) {
			return JavaScriptEvent.ON_CHANGE;
		}
		return 0;
	}

}