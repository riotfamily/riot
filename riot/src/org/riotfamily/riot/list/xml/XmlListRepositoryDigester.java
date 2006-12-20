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
package org.riotfamily.riot.list.xml;


import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.common.xml.DocumentDigester;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.ListRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
public class XmlListRepositoryDigester implements DocumentDigester {
	
	public static final String NAMESPACE = "http://www.riotfamily.org/schema/riot/list-config";
	
	private static final String LIST = "list";
	
	private static final String ID = "id";
	
	private static final String[] LIST_ATTRS = new String[] {
		"id", "id-property", "filterFormId=filter-form", 
		"defaultCommandId=default-command", "order-by",
		"row-style-property"
	};
	
	private static final String DAO = "dao";
	
	private static final String DAO_REF = "ref";
	
	private static final String DAO_CLASS = "class";
	
	private static final String PROPERTY = "property";
	
	private static final String COLUMNS = "columns";
	
	private static final String COLUMN = "column";
	
	private static final String[] COLUMN_ATTRS = new String[] {
		"sortable", "lookup-level", "@renderer", "property", "case-sensitive"
	};
		
	private static final String COMMAND = "command";
	
	private ListRepository listRepository;
	
	private BeanFactory beanFactory;
		
	public XmlListRepositoryDigester(ListRepository listRepository,
			BeanFactory beanFactory) {
		
		this.listRepository = listRepository;
		this.beanFactory = beanFactory;
	}

	public void digest(Document doc, Resource resource) {
		Element root = doc.getDocumentElement();
		List nodes = DomUtils.getChildElementsByTagName(root, LIST);
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String namespace = ele.getNamespaceURI();
			if (namespace == null || namespace.equals(NAMESPACE)) {
				listRepository.addListConfig(digestListConfig(ele));
			}
		}
	}
	
	/**
	 * Creates a ListConfig by digesting a &lt;list&gt tag.
	 */
	protected ListConfig digestListConfig(Element listElement) {
		ListConfig listConfig = new ListConfig();
		
		XmlUtils.populate(listConfig, listElement, LIST_ATTRS);
		
		digestDao(listConfig, listElement);
		digestColumns(listConfig, listElement);
		digestCommands(listConfig, listElement);
		
		return listConfig;
	}

	/**
	 * Creates (or retrieves) a RiotDao by digesting a &lt;dao&gt; tag.
	 */
	protected void digestDao(ListConfig listConfig, 
			Element listElement) {
		
		Element ele = DomUtils.getChildElementByTagName(listElement, DAO);
		
		RiotDao dao = null;
		boolean singleton = false;
		
		String ref = XmlUtils.getAttribute(ele, DAO_REF);
		if (ref != null) {
			dao = (RiotDao) beanFactory.getBean(ref, RiotDao.class);
			singleton = beanFactory.isSingleton(ref);
		}
		else {
			String className = XmlUtils.getAttribute(ele, DAO_CLASS);
			if (className != null) {
				dao = instanciateDao(className);
			}
		}
		List nodes = DomUtils.getChildElementsByTagName(ele, PROPERTY);
		if (singleton && !nodes.isEmpty()) {
			throw new RuntimeException(PROPERTY 
					+ " must not be applied to singleton beans.");
		}
		XmlUtils.populate(dao, nodes, beanFactory);
		
		listConfig.setDao(dao);
	}
	
	/**
	 * Creates a new instance of the specified Dao class.
	 */
	protected RiotDao instanciateDao(String className) {
		return (RiotDao) PropertyUtils.newInstance(className);
	}

	/**
	 * Adds columns to the given ListConfig by digesting the &lt;columns&gt;
	 * child of the given &lt;list&gt; element.
	 */
	protected void digestColumns(ListConfig listConfig, 
			Element listElement) {
		
		Element columns = DomUtils.getChildElementByTagName(listElement, COLUMNS);
		
		List nodes = DomUtils.getChildElementsByTagName(columns, COLUMN);
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			listConfig.addColumnConfig(digestColumn((Element) it.next()));
		}
		
		nodes = DomUtils.getChildElementsByTagName(columns, COMMAND);
		it = nodes.iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			String commandId = XmlUtils.getAttribute(e, ID);
			listConfig.addColumnCommand(listRepository.getCommand(commandId));
		}
	}
	
	/**
	 * Creates a ColumnConfig by digesting the given element.
	 */
	protected ColumnConfig digestColumn(Element ele) {
		ColumnConfig columnConfig = new ColumnConfig();
		XmlUtils.populate(columnConfig, ele, COLUMN_ATTRS, beanFactory);	
		if (columnConfig.getRenderer() == null) {
			columnConfig.setRenderer(listRepository.getDefaultCellRenderer());
		}
		return columnConfig;
	}
	
	protected void digestCommands(ListConfig listConfig, Element listElement) {
		List nodes = DomUtils.getChildElementsByTagName(listElement, COMMAND);
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String commandId = XmlUtils.getAttribute(ele, ID);
			listConfig.addCommand(listRepository.getCommand(commandId));
		}
	}
		
}
