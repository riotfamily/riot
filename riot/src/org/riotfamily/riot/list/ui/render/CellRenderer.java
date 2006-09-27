package org.riotfamily.riot.list.ui.render;

import java.io.PrintWriter;


/**
 * Renders a list cell to the given writer.
 */
public interface CellRenderer {

	public void render(RenderContext context, PrintWriter writer);

}
