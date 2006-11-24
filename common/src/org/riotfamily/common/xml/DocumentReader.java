/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

/**
 * Convinience class to read a XML document from a 
 * {@link org.springframework.core.io.Resource resource}.
 */
public class DocumentReader {

	protected static final int VALIDATION_MODE_NONE = 0;
		
	private static Log log = LogFactory.getLog(DocumentReader.class);
		
	private Resource resource;
	
	private ErrorHandler errorHandler = new SimpleSaxErrorHandler(log);

	private DocumentLoader loader = new DefaultDocumentLoader();
	
	public DocumentReader(Resource resource) {
		this.resource = resource;
	}
	
	protected int getValidationMode() {
		return VALIDATION_MODE_NONE;
	}
	
	protected EntityResolver getEntityResolver() {
		return null;
	}
	
	public Document readDocument() {
		try {
			InputSource source = new InputSource(resource.getInputStream());
			return loader.loadDocument(source, getEntityResolver(), errorHandler, 
					getValidationMode(), true);
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new DocumentReaderException(resource, e);
		}
	}

}