/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.crawler;

import java.util.HashMap;

import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

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
    
    private HashMap<String, String> headers = new HashMap<String, String>();
    
    private NodeList nodes;
    
    public PageData(Href href) {
		this.href = href;
	}
    
    public void addHeader(String name, String value) {
    	headers.put(name.toLowerCase(), value);
    }
    
    public String getHeader(String name) {
    	return headers.get(name.toLowerCase()); 
    }
    
	public void setHtml(String html) {
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
