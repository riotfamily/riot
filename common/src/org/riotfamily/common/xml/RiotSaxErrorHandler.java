package org.riotfamily.common.xml;

import org.riotfamily.common.util.RiotLog;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class RiotSaxErrorHandler implements ErrorHandler {

	private final RiotLog logger;

	/**
	 * Create a new RiotSaxErrorHandler for the given logger.
	 */
	public RiotSaxErrorHandler(RiotLog logger) {
		this.logger = logger;
	}

	public void warning(SAXParseException ex) throws SAXException {
		logger.warn("Ignored XML validation warning", ex);
	}

	public void error(SAXParseException ex) throws SAXException {
		throw ex;
	}

	public void fatalError(SAXParseException ex) throws SAXException {
		throw ex;
	}

}
