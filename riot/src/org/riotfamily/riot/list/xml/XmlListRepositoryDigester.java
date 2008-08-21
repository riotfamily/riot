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
package org.riotfamily.riot.list.xml;


import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.common.xml.DocumentDigester;
import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.ListRepository;
import org.riotfamily.riot.list.command.Command;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
public class XmlListRepositoryDigester implements DocumentDigester {
	
	public static final String NAMESPACE = "http://www.riotfamily.org/schema/riot/list-config";
	
	private static final String[] LIST_ATTRS = new String[] {
		"id", "id-property", "filterFormId=filter-form", 
		"defaultCommandId=default-command", "order-by",
		"label-property", "row-style-property", "search"
	};
	
	private static final String[] COLUMN_ATTRS = new String[] {
		"sortable", "lookup-level", "@renderer", "property", "case-sensitive"
	};
		
	private ListRepository listRepository;
	
	private AutowireCapableBeanFactory beanFactory;
		
	public XmlListRepositoryDigester(ListRepository listRepository,
			AutowireCapableBeanFactory beanFactory) {
		
		this.listRepository = listRepository;
		this.beanFactory = beanFactory;
	}

	public void digest(Document doc, Resource resource) {
		Element root = doc.getDocumentElement();
		List<Element> nodes = XmlUtils.getChildElementsByTagName(root, "list");
		Iterator<Element> it = nodes.iterator();
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
	private ListConfig digestListConfig(Element listElement) {
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
	private void digestDao(ListConfig listConfig, 
			Element listElement) {
		
		Element ele = DomUtils.getChildElementByTagName(listElement, "dao");
		
		RiotDao dao = null;
		boolean singleton = false;
		
		String ref = XmlUtils.getAttribute(ele, "ref");
		if (ref != null) {
			dao = (RiotDao) beanFactory.getBean(ref, RiotDao.class);
			singleton = beanFactory.isSingleton(ref);
		}
		else {
			String className = XmlUtils.getAttribute(ele, "class");
			if (className != null) {
				dao = instanciateDao(className);
			}
		}
		List<Element> nodes = XmlUtils.getChildElementsByTagName(ele, "property");
		if (!nodes.isEmpty()) {
			if (singleton) { 
				throw new RuntimeException("<property> must not be used with singleton beans.");
			}
			XmlUtils.populate(dao, nodes, beanFactory);
			beanFactory.initializeBean(dao, null);
		}
		listConfig.setDao(dao);
	}
	
	/**
	 * Creates a new instance of the specified Dao class.
	 */
	private RiotDao instanciateDao(String className) {
		return SpringUtils.createBean(className, beanFactory, 
				AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
	}

	/**
	 * Adds columns to the given ListConfig by digesting the &lt;columns&gt;
	 * child of the given &lt;list&gt; element.
	 */
	private void digestColumns(ListConfig listConfig, 
			Element listElement) {
		
		Element columns = DomUtils.getChildElementByTagName(listElement, "columns");
		
		List<Element> nodes = XmlUtils.getChildElementsByTagName(columns, "column");
		Iterator<Element> it = nodes.iterator();
		while (it.hasNext()) {
			listConfig.addColumnConfig(digestColumn(it.next()));
		}
		
		nodes = XmlUtils.getChildElementsByTagName(columns, "command");
		it = nodes.iterator();
		while (it.hasNext()) {
			listConfig.addColumnCommand(digestCommand(it.next()));
		}
	}
	
	/**
	 * Creates a ColumnConfig by digesting the given element.
	 */
	private ColumnConfig digestColumn(Element ele) {
		ColumnConfig columnConfig = new ColumnConfig();
		XmlUtils.populate(columnConfig, ele, COLUMN_ATTRS, beanFactory);	
		if (columnConfig.getRenderer() == null) {
			columnConfig.setRenderer(listRepository.getDefaultCellRenderer());
		}
		return columnConfig;
	}
	
	private void digestCommands(ListConfig listConfig, Element listElement) {
		List<Element> nodes = XmlUtils.getChildElementsByTagName(listElement, "command");
		Iterator<Element> it = nodes.iterator();
		while (it.hasNext()) {
			listConfig.addCommand(digestCommand(it.next()));
		}
	}
	
	private Command digestCommand(Element ele) {
		String commandId = XmlUtils.getAttribute(ele, "id");
		if (commandId != null) {
			return listRepository.getCommand(commandId);
		}
		String className = XmlUtils.getAttribute(ele, "class");
		Assert.notNull(className, "Either id or class must be specified");
		Command command = SpringUtils.createBean(className, beanFactory, 
				AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
		
		listRepository.addCommand(command);
		return command;
	}
		
}
