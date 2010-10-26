/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.forms.element.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.forms.AbstractEditorBase;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.MessageUtils;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.event.JavaScriptEventAdapter;
import org.riotfamily.forms.options.OptionsModel;
import org.riotfamily.forms.options.OptionsModelUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Abstract superclass for elements that let the user choose from a set of
 * options like selectboxes or radio button groups.
 */
public abstract class AbstractSelectElement extends AbstractEditorBase 
		implements Editor, SelectElement, JavaScriptEventAdapter {

	private Object options;
	
	private String valueProperty;
	
	private String labelProperty;
	
	private String styleClassProperty;
	
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
	
	public void setStyleClassProperty(String styleClassProperty) {
		this.styleClassProperty = styleClassProperty;
	}

	public void setLabelMessageKey(String labelMessageKey) {
		this.labelMessageKey = labelMessageKey;
	}
	
	public void setAppendLabel(boolean appendLabelToMessageKey) {
		this.appendLabel = appendLabelToMessageKey;
	}

	public void setOptions(Object options) {
		this.options = options;
		this.optionsModel = null;
		reset();
	}
	
	public void setHideIfEmpty(boolean hideIfEmpty) {
		this.hideIfEmpty = hideIfEmpty;
	}
	
	public Object getOptions() {
		return options;
	}
	
	protected boolean hasOptionValues() {
		return !CollectionUtils.isEmpty(getOptionValues());
	}
	
	@Override
	public boolean isRequired() {		
		return super.isRequired() && hasOptionValues();
	}
	
	@Override
	public boolean isVisible() {		
		return super.isVisible() && !(hideIfEmpty && !hasOptionValues());
	}
	
	public void reset() {
		if (getFormContext() != null) {
			optionItems = null;
			optionValues = null;
			if (getFormListener() != null) {
				getFormListener().elementChanged(this);
			}
		}
	}
	
	/**
	 * Returns the list of OptionItems. If the list has not been populated yet,
	 * {@link #createOptionItems()} is invoked. 
	 */
	protected List<OptionItem> getOptionItems() {
		if (optionItems == null) {
			optionItems = createOptionItems(getOptionValues());
			updateSelection(optionItems);
		}
		return optionItems;
	}
	
	/**
	 * Creates a list of OptionItems from the given values. The collection of
	 * values is obtained using {@link #getOptionValues()}.
	 */
	protected List<OptionItem> createOptionItems(Collection<?> values) {
		List<OptionItem> items = new ArrayList<OptionItem>();
		if (values != null) {
			for (Object item : values) {
				String label = getOptionLabel(item);
				Object value = getOptionValue(item);
				String styleClass = getOptionStyleClass(item);
				items.add(new OptionItem(item, value, label, styleClass, this));
			}
		}
		return items;
	}
	
	/**
	 * Returns a collection of objects that is used to create the OptionItems.
	 * If the collection has not been populated yet, it is obtained from the
	 * {@link #getOptionsModel() OptionsModel}.
	 */
	private Collection<?> getOptionValues() {
		if (optionValues == null) {
			optionValues = getOptionsModel().getOptionValues(this);
		}
		return optionValues;
	}
	
	/**
	 * Returns the OptionsModel. If the model has not been created yet,
	 * it is created based on the {@link #getOptions() options}.
	 */
	private OptionsModel getOptionsModel() {
		if (optionsModel == null) {
			optionsModel = OptionsModelUtils.adapt(options, this);
		}
		return optionsModel;
	}
	
	protected abstract void updateSelection(List<OptionItem> items);
	
	private String getOptionLabel(Object obj) {
		if (labelProperty != null) {
			obj = PropertyUtils.getProperty(obj, labelProperty);
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
	
	private Object getOptionValue(Object obj) {
		if (obj == null) {
			return null;
		}
		if (valueProperty != null) {
			return PropertyUtils.getProperty(obj, valueProperty);
		}
		else {
			return obj;
		}
	}
	
	private String getOptionStyleClass(Object obj) {
		if (styleClassProperty != null) {
			return PropertyUtils.getPropertyAsString(obj, styleClassProperty);
		}
		return null;
	}
	
	public final int getOptionIndex(OptionItem item) {
		Assert.notNull(optionItems);
		return optionItems.indexOf(item);
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
