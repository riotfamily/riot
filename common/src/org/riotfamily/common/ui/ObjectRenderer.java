package org.riotfamily.common.ui;

import java.io.PrintWriter;


/**
 * Renders an object to the given writer.
 */
public interface ObjectRenderer {

	public void render(Object obj, RenderContext context, PrintWriter writer);

}
