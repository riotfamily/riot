package org.riotfamily.forms.element.support.select;

import java.util.ArrayList;
import java.util.Collection;

public class SequenceOptionsModel implements OptionsModel {

	private int start = 0;
	
	private int end = 1;
	
	private ArrayList options = null;
	
	public void setStart(int start) {
		this.start = start;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public Collection getOptionValues() {
		if (options == null) {
			options = new ArrayList(end - start);
			for (int i = start; i <= end; i++) {
				options.add(new Integer(i));
			}
		}
		return options;
	}
}
