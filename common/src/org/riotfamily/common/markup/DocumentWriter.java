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
package org.riotfamily.common.markup;

import java.io.PrintWriter;

/**
 * Utility class to generate markup code. This example ...
 * <pre>
 *  DocumentWriter doc = new DocumentWriter(writer);
 *  doc.start("body");
 *  doc.start("p").attribute("class", "foo");
 *  doc.body("Hello ");
 *  doc.start("strong").body("World");
 *  doc.closeAll();
 * </pre>
 * ... will produce the following code:
 * <pre>
 * &lt;body&gt;&lt;p class="foo"&gt;Hello &lt;strong&gt;World&lt;/strong&gt;&lt;/p&gt;&lt;/body&gt;
 * </pre>
 */
public class DocumentWriter {

	private TagWriter tagWriter;
	
	public DocumentWriter(PrintWriter writer) {
		this(writer, true);
	}
	
	public DocumentWriter(PrintWriter writer, boolean xhtml) {
		tagWriter = new TagWriter(writer);
		tagWriter.setXhtml(xhtml);
	}
	
    public DocumentWriter start(String tagName) {
    	tagWriter = tagWriter.start(tagName);
    	return this;
    }
    
    public DocumentWriter startEmpty(String tagName) {
    	tagWriter = tagWriter.startEmpty(tagName);
    	return this;
    }
        
    public DocumentWriter start(String tagName, boolean empty) {
    	tagWriter = tagWriter.start(tagName, empty);
    	return this;
    }
    
    public DocumentWriter attribute(String name) {
    	tagWriter.attribute(name);
    	return this;
    }

    public DocumentWriter attribute(String name, int value) {
    	tagWriter.attribute(name, value);
    	return this;
    }

    public DocumentWriter attribute(String name, boolean present) {
    	tagWriter.attribute(name, present);
    	return this;
    }
    
    public DocumentWriter attribute(String name, String value) {
    	tagWriter.attribute(name, value);
    	return this;
    }

    public DocumentWriter attribute(String name, String value, boolean renderEmpty) {
    	tagWriter.attribute(name ,value, renderEmpty);
    	return this;
    }

    public DocumentWriter body() {
    	tagWriter.body();
    	return this;
    }
    
    public DocumentWriter body(String body) {
        tagWriter.body(body);
        return this;
    }
    
    public DocumentWriter body(String body, boolean escapeHtml) {
        tagWriter.body(body, escapeHtml);
        return this;
    }
    
    public DocumentWriter println(String s) {
    	tagWriter.println(s);
    	return this;
    }
    
    public DocumentWriter end() {
        tagWriter = tagWriter.end();
        return this;
    }
    
    public void closeAll() {
    	tagWriter.closeAll();
    }
}
