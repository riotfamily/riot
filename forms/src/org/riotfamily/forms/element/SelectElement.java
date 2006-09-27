package org.riotfamily.forms.element;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.element.support.select.Option;
import org.riotfamily.forms.element.support.select.OptionsModel;

/**
 * Interface to be implemented by elements that provide options the user
 * can choose from.
 */
public interface SelectElement extends Element {
		
	public String getParamName();
	
	public void renderOption(Option option);

	public boolean isSelected(Option option);
	
	public void setOptionsModel(OptionsModel model);
	
	public int getOptionIndex(Option option);

}