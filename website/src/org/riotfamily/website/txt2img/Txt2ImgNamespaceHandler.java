package org.riotfamily.website.txt2img;

import org.riotfamily.common.beans.namespace.GenericNamespaceHandlerSupport;

/**
 * NamespceHandler that handles the txt2img namespace.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class Txt2ImgNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		register("rule", ReplacementRule.class);
		register("button", ButtonRenderer.class);
	}
}
