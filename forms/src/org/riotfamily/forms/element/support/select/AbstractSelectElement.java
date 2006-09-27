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
import org.riotfamily.forms.i18n.MessageUtils;
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
				options.add(new Option(value, label, this));
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