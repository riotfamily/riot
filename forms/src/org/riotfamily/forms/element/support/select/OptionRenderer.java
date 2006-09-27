package org.riotfamily.forms.element.support.select;

import java.io.PrintWriter;

/**
 *
 */
public interface OptionRenderer {

	public void renderOption(Option option, PrintWriter writer);

}