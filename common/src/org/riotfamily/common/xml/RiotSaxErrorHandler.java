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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
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
