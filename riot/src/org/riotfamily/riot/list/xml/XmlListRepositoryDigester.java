package org.riotfamily.riot.list.xml;


import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.xml.DigesterUtils;
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
	
	private static final String COLUMN = "column|command";
	
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
		
		DigesterUtils.populate(listConfig, listElement, LIST_ATTRS);
		
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
		
		String ref = DigesterUtils.getAttribute(ele, DAO_REF);
		if (ref != null) {
			dao = (RiotDao) beanFactory.getBean(ref, RiotDao.class);
			singleton = beanFactory.isSingleton(ref);
		}
		else {
			String className = DigesterUtils.getAttribute(ele, DAO_CLASS);
			if (className != null) {
				dao = instanciateDao(className);
			}
		}
		List nodes = DomUtils.getChildElementsByTagName(ele, PROPERTY);
		if (singleton && !nodes.isEmpty()) {
			throw new RuntimeException(PROPERTY 
					+ " must not be applied to singleton beans.");
		}
		DigesterUtils.populate(dao, nodes, beanFactory);
		
		listConfig.setDao(dao);
	}
	
	/**
	 * Creates a new instance of the specified Dao class.
	 */
	protected RiotDao instanciateDao(String className) {
		try {
			Class clazz = Class.forName(className);
			return (RiotDao) clazz.newInstance();
		}
		catch (Exception e) {
			throw new RuntimeException("Error while instanciating Dao", e);
		}
	}

	/**
	 * Adds columns to the given ListConfig by digesting the &lt;columns&gt;
	 * child of the given &lt;list&gt; element.
	 */
	protected void digestColumns(ListConfig listConfig, 
			Element listElement) {
		
		Element ele = DomUtils.getChildElementByTagName(listElement, COLUMNS);
		List nodes = DigesterUtils.getChildElementsByRegex(ele, COLUMN);
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			listConfig.addColumnConfig(digestColumn((Element) it.next()));
		}
	}
	
	/**
	 * Creates a ColumnConfig by digesting the given element.
	 */
	protected ColumnConfig digestColumn(Element ele) {
		ColumnConfig columnConfig = new ColumnConfig();
		if (DomUtils.nodeNameEquals(ele, COMMAND)) {
			String commandId = DigesterUtils.getAttribute(ele, "id");
			columnConfig.setCommand(listRepository.getCommand(commandId));
			columnConfig.setRenderer(listRepository.getItemCommandRenderer());
		}
		else {
			DigesterUtils.populate(columnConfig, ele, COLUMN_ATTRS, beanFactory);	
			if (columnConfig.getRenderer() == null) {
				columnConfig.setRenderer(listRepository.getDefaultCellRenderer());
			}
		}
		columnConfig.setHeadingRenderer(listRepository.getDefaultHeadingRenderer());
		return columnConfig;
	}
	
	protected void digestCommands(ListConfig listConfig, Element listElement) {
		List nodes = DomUtils.getChildElementsByTagName(listElement, COMMAND);
		listConfig.setListCommandRenderer(
				listRepository.getListCommandRenderer());
		
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String commandId = DigesterUtils.getAttribute(ele, ID);
			listConfig.addCommand(listRepository.getCommand(commandId));
		}
	}
		
}
