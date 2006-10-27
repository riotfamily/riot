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
package org.riotfamily.forms.fileupload;

import java.io.IOException;

import javax.servlet.ServletInputStream;

/**
 * ServletInputStream that counts the number of bytes read. This class is used
 * to provide progress information for file uploads.
 */
public class CountingServletInputStream extends ServletInputStream {

	private ServletInputStream sourceStream;

	private long bytesRead;
	
	public CountingServletInputStream() {
	}

	public void setSourceStream(ServletInputStream sourceStream) {
		this.sourceStream = sourceStream;
	}
	
	public int read() throws IOException {
		if (sourceStream == null) {
			throw new IllegalMonitorStateException("No sourceStream set");
		}
		int i = sourceStream.read();
		if (i >= 0) {
			bytesRead ++;
		}
		return i;
	}

	public long getBytesRead() {
		return bytesRead;
	}
}
