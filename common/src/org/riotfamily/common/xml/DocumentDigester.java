package org.riotfamily.common.xml;

import org.springframework.core.io.Resource;
import org.w3c.dom.Document;

/**
 */
public interface DocumentDigester {
	
	/**
	 * Digests the given document.
	 */
	public void digest(Document doc, Resource resource);

}
