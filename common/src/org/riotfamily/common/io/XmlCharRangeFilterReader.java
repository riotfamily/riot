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
package org.riotfamily.common.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * FilterReader that silently swallows invalid XML characters like 0xb or 0x1a.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class XmlCharRangeFilterReader extends FilterReader {

	public XmlCharRangeFilterReader(Reader in) {
		super(in);
	}

	/**
	 * Returns whether the given unicode character is valid according to the 
     * XML 1.0 standard.
     * @see http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char
	 */
	protected boolean isValidXmlChar(int c) {
		return (c == 0x9) || (c == 0xA) || (c == 0xD)
				|| ((c >= 0x20) && (c <= 0xD7FF))
				|| ((c >= 0xE000) && (c <= 0xFFFD))
				|| ((c >= 0x10000) && (c <= 0x10FFFF));
	}
	
	@Override
	public int read() throws IOException {
		int c = super.read();
		while (c != -1 && !isValidXmlChar(c)) {
			c = super.read();
		}
		return c;
	}
	
	/**
     * Reads characters into a portion of an array.
     *
     * @param      cbuf  Destination buffer
     * @param      off   Offset at which to start storing characters
     * @param      len   Maximum number of characters to read
     *
     * @return     The number of characters read, or -1 if the end of the
     *             stream has been reached
     *
     * @exception  IOException  If an I/O error occurs
     */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		for (int i = 0; i < len; i++) {
            final int ch = read();
            if (ch == -1) {
            	return i == 0 ? -1 : i;
            }
            cbuf[off + i] = (char) ch;
        }
        return len;
	}
}
