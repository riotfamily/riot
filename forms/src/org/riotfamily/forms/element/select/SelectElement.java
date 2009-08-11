package org.riotfamily.forms.element.select;

import org.riotfamily.forms.Editor;


/**
 * Interface to be implemented by elements that provide options the user
 * can choose from.
 */
public interface SelectElement extends Editor {
		
	public String getParamName();
	
	public void renderOption(OptionItem option);

	public boolean isSelected(OptionItem option);
	
	public Object getOptions();
	
	public void setOptions(Object model);
	
	public int getOptionIndex(OptionItem option);

	public void reset();

}