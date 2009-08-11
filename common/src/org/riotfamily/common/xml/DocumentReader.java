package org.riotfamily.common.xml;

import org.riotfamily.common.util.RiotLog;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.core.io.Resource;
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
		
	private RiotLog log = RiotLog.get(DocumentReader.class);
		
	private Resource resource;
	
	private ErrorHandler errorHandler = new RiotSaxErrorHandler(log);

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