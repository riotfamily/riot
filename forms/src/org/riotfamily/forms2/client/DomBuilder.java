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
package org.riotfamily.forms2.client;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DomBuilder<T extends DomBuilder<T>> {

	private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	
	private static TransformerFactory transformerFactory = TransformerFactory.newInstance();
	
	private Document document;
	
	private Node node;
	
	private T parent;
	
	public DomBuilder() {
	    this(newDocument().createDocumentFragment());
	}
	
    public DomBuilder(String element) {
    	this(newDocument().createElement(element));
    }
    
    protected DomBuilder(Node node, T parent) {
		this.node = node;
		this.parent = parent;
		this.document = node.getOwnerDocument();
	}
    
	private DomBuilder(Node node) {
		this(node, null);
	}
	
	protected Element element() {
		return (Element) node;	
	}
	
	public T attr(String name, String value) {
		if (value != null) {
			element().setAttribute(name, value);
		}
        return getThis();
    }
	
	public T attr(String name, String format, Object... args) {
		return attr(name, String.format(format, args));
	}
	
	protected abstract T getThis();

	public T elem(String name) {
		Element child = document.createElement(name);
		node.appendChild(child);
        return createNested(child);        
    }
	
	protected abstract T createNested(Element child);
	
	public T text(String value) {
		node.appendChild(document.createTextNode(value));
        return getThis();        
    }
	
	public T cdata(String value) {
		node.appendChild(document.createCDATASection(value));
        return getThis();        
    }
	
	public T up() {
		return up(1);
	}
	
	public T up(int n) {
		T ancestor = getThis();
		for (int i = 0; i < n; i++) {
			ancestor = ancestor.parent;
		}
		return ancestor;
	}
	
	public void writeTo(Writer writer) {
		try {
			DOMSource source = new DOMSource(node);
			Transformer t = transformerFactory.newTransformer();
			
			t.setOutputProperty(OutputKeys.METHOD, "html");
			//t.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD XHTML 1.0 Transitional//EN");
			//t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
			//t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.transform(source, new StreamResult(writer));
		}
		catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}
	 
	@Override
	public String toString() {
		 StringWriter sw = new StringWriter();
		 writeTo(sw);
		 return sw.toString();
	}
	
	private static Document newDocument() {
		try {
			return documentBuilderFactory.newDocumentBuilder().newDocument();
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
}
