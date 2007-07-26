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
 *   "Felix Gnass [fgnass at neteye dot de]"
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.export;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.components.ComponentList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Exports {@link ComponentList ComponentLists} as XML.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class XmlExport {

	private boolean useCustomTagNames = true;
	
	private SimpleListBuilder builder = new SimpleListBuilder();
	
	/**
	 * Sets whether component-types and property-names should be used as tag 
	 * names. If set to <code>false</code>, the tags will be called 
	 * &lt;component&gt; and &lt;property&gt;. The default is <code>true</code>. 
	 */
	public void setUseCustomTagNames(boolean useCustomTagNames) {
		this.useCustomTagNames = useCustomTagNames;
	}
	
	public Element createElement(Collection lists, boolean preview) {
		Document doc = XmlUtils.createDocument();
		Element ele = doc.createElement("lists");
		Iterator it = lists.iterator();
		while (it.hasNext()) {
			ComponentList list = (ComponentList) it.next();
			SimpleComponentList simpleList = builder.buildSimpleList(list, preview);
			ele.appendChild(createListElement(simpleList, doc));
		}
		return ele;
	}
	
	public Element createElement(ComponentList list, boolean preview) {
		SimpleComponentList simpleList = builder.buildSimpleList(list, preview);
		Document doc = XmlUtils.createDocument();
		return createListElement(simpleList, doc);
	}
	
	private Element createListElement(SimpleComponentList list, Document doc) {
		Element ele = doc.createElement("list");
		ele.setAttribute("path", list.getLocation().getPath());
		ele.setAttribute("slot", list.getLocation().getSlot());
		ele.setAttribute("type", list.getLocation().getType());
		Iterator it = list.getComponents().iterator();
		while (it.hasNext()) {
			SimpleComponent component = (SimpleComponent) it.next();
			ele.appendChild(createComponentElement(component, doc));
		}
		return ele;
	}
	
	private Element createComponentElement(SimpleComponent component, Document doc) {
		Element ele;
		if (useCustomTagNames) {
			ele = doc.createElement(component.getType());
		}
		else {
			ele = doc.createElement("component");
			ele.setAttribute("type", component.getType());
		}
		Iterator it = component.getStringProperties().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			ele.appendChild(createPropertyElement(key, value, doc));
		}
		return ele;
	}
	
	private Element createPropertyElement(String name, String value, Document doc) {
		Element ele;
		if (useCustomTagNames) {
			ele = doc.createElement(name);
		}
		else {
			ele = doc.createElement("property");
			ele.setAttribute("name", name);
		}
		ele.appendChild(doc.createCDATASection(value));
		return ele;
	}
}
