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
package org.riotfamily.search.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

public class PageData {

    private static final int BUFFER_SIZE = 1024;
    
    private String url;

    private String html;
    
    private String redirectUrl;
    
    private String error;
    
    
    public PageData(String url) {
		this.url = url;
	}

	public void setContent(InputStream in, String charset) throws IOException {
        Reader reader = new InputStreamReader(in, charset);
        StringWriter writer = new StringWriter();
        int charCount = 0;
		char[] buffer = new char[BUFFER_SIZE];
		int charsRead = -1;
		while ((charsRead = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, charsRead);
			charCount += charsRead;
		}
		this.html = writer.toString();
    }

    public String getHtml() {
        return html;
    }
    
    public String getUrl() {
		return this.url;
	}

	public boolean isOk() {
    	return html != null && error == null;
    }
	
	public boolean isRedirect() {
    	return redirectUrl != null;
    }

	public String getRedirectUrl() {
		return this.redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getError() {
		return this.error;
	}

	public void setError(String error) {
		this.error = error;
	}

		
}
