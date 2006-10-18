package org.riotfamily.forms.element.dom;

import java.util.Iterator;

import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.forms.element.core.ListEditor;
import org.springframework.util.Assert;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlSequence extends ListEditor {

	private String name;
	
	private Node node;
	
	public void setName(String name) {
		this.name = name;
	}
	
	protected void afterBindingSet() {
		if (name == null) {
			name = getEditorBinding().getProperty();
		}
	}
	
	/**
	 * 
	 */
	public void setValue(Object value) {
		if (value != null) {
			Assert.isInstanceOf(Node.class, value, 
					"Value must implement the org.w3c.Node interface");
			
			node = (Node) value;
			NodeList childNodes = node.getChildNodes();			
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node child = childNodes.item(i);
				ListItem item = addItem();
				item.getElement().setValue(child);
			}
		}
	}
	
	public Object getValue() {
		if (node != null) {
			XmlUtils.removeAllChildNodes(node);
		}
		else {
			node = XmlUtils.createDocument().createElement(name);
		}
		Iterator it = getItems().getElements().iterator();
		while (it.hasNext()) {
			ListItem item = (ListItem) it.next();
			if (item.getElement().getValue() != null) {
				Object value = item.getElement().getValue();
				Assert.isInstanceOf(Node.class, value, 
						"Value must implement the org.w3c.Node interface");
				
				XmlUtils.importAndAppend((Node) value, node);
			}
		}
		return node;
	}

}
