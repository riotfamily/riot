package org.riotfamily.forms.element.core;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.element.DHTMLElement;
import org.riotfamily.forms.element.support.AbstractTextElement;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;


/**
 * A textarea widget.
 */
public class Textarea extends AbstractTextElement implements ResourceElement, 
		DHTMLElement {

	private static List RESOURCES = Collections.singletonList(
			new ScriptResource("riot-js/textarea.js", "TextArea"));
	
	private int rows = 10;

	private int cols = 80;

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void renderInternal(PrintWriter writer) {
		TagWriter tag = new TagWriter(writer);
		tag.start(Html.TEXTAREA)
			.attribute(Html.COMMON_CLASS, getStyleClass())
			.attribute(Html.COMMON_ID, getId())
			.attribute(Html.INPUT_NAME, getParamName())
			.attribute(Html.TEXTAREA_ROWS, rows)
			.attribute(Html.TEXTAREA_COLS, cols);

		tag.body(getText()).end();
	}
	
	public Collection getResources() {
		return RESOURCES;
	}
	
	public String getInitScript() {
		if (getMaxLength() != null) {
			return "TextArea.setMaxLength('" + getId() + "', " + getMaxLength() + ");";
		}
		else {
			return null;
		}
	}
	
	public String getPrecondition() {
		return "TextArea";
	}
}