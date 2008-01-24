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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;

import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.riotfamily.common.io.IOUtils;

/**
 * Class that holds all information about a crawled page.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class PageData {

    private Href href;

    private String html;
    
    private String redirectUrl;
    
    private String error;
    
    private int statusCode;
    
    private HashMap headers = new HashMap();
    
    private NodeList nodes;
    
    public PageData(Href href) {
		this.href = href;
	}
    
    public void addHeader(String name, String value) {
    	headers.put(name.toLowerCase(), value);
    }
    
    public String getHeader(String name) {
    	return (String) headers.get(name.toLowerCase()); 
    }
    
	public void setContent(InputStream in, String charset) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer, charset);
		setHtml(writer.toString());
    }

	private void setHtml(String html) {
		this.html = html;
	}
	
    public String getHtml() {
        return html;
    }
    
    public Href getHref() {
		return href;
	}

    public String getUrl() {
		return href.getResolvedUri();
	}
    
    public String getReferrer() {
    	return href.getBaseUri();
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

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public void parse() throws ParserException {
		Parser parser = new Parser();
		parser.setInputHTML(html);
		nodes = parser.parse(null);
	}
	
	public NodeList getNodes() {
		return nodes;
	}
	
}
