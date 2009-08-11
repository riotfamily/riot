package org.riotfamily.forms.element;

import java.io.PrintWriter;

import org.riotfamily.forms.AbstractElement;

public class StaticText extends AbstractElement {

	private String text;
	
	public StaticText(String text) {
		this.text = text;
	}

	@Override
	protected void renderInternal(PrintWriter writer) {
		writer.print(text);
	}

}
