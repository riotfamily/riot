package org.riotfamily.forms.element.select;

import java.io.PrintWriter;






/**
 *
 */
public interface OptionRenderer {

	public void renderOption(OptionItem option, PrintWriter writer, boolean enabled);

}