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
package org.riotfamily.revolt.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.revolt.ChangeSet;
import org.riotfamily.revolt.EvolutionHistory;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.definition.ForeignKey;
import org.riotfamily.revolt.definition.Index;
import org.riotfamily.revolt.definition.RecordEntry;
import org.riotfamily.revolt.definition.Reference;
import org.riotfamily.revolt.definition.UniqueConstraint;
import org.riotfamily.revolt.refactor.AddColumn;
import org.riotfamily.revolt.refactor.AddForeignKey;
import org.riotfamily.revolt.refactor.AddUniqueConstraint;
import org.riotfamily.revolt.refactor.CreateAutoIncrementSequence;
import org.riotfamily.revolt.refactor.CreateIndex;
import org.riotfamily.revolt.refactor.CreateTable;
import org.riotfamily.revolt.refactor.DropColumn;
import org.riotfamily.revolt.refactor.DropForeignKey;
import org.riotfamily.revolt.refactor.DropIndex;
import org.riotfamily.revolt.refactor.DropTable;
import org.riotfamily.revolt.refactor.DropUniqueConstraint;
import org.riotfamily.revolt.refactor.InsertData;
import org.riotfamily.revolt.refactor.ModifyColumn;
import org.riotfamily.revolt.refactor.RenameColumn;
import org.riotfamily.revolt.refactor.RenameTable;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * NamespaceHandler that handles the <code>revolt</code> 
 * namspace as defined in <code>revolt.xsd</code> which can be found in 
 * the same package.
 */
public class RevoltNamespaceHandler implements NamespaceHandler {

	public void init() {
	}

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		if (DomUtils.nodeNameEquals(element, "history")) {
			RootBeanDefinition definition = new RootBeanDefinition();
			definition.setBeanClass(EvolutionHistory.class);
			
			ConstructorArgumentValues ctorArgs = new ConstructorArgumentValues();
			ctorArgs.addGenericArgumentValue(new RuntimeBeanReference(
					element.getAttribute("data-source")));
			
			definition.setConstructorArgumentValues(ctorArgs);
			MutablePropertyValues pv = new MutablePropertyValues();
			pv.addPropertyValue("changeSets", parseChangeSets(element));
			definition.setPropertyValues(pv);
			BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, element.getAttribute("module"));
			BeanDefinitionReaderUtils.registerBeanDefinition(holder, parserContext.getRegistry());
			return definition;
		}
		throw new IllegalArgumentException(
				"Expected <history> but found: " + element.getNodeName());
	}
	
	private List parseChangeSets(Element element) {
		ArrayList changeSets = new ArrayList();
		Iterator it = DomUtils.getChildElementsByTagName(element, "change-set").iterator();
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			changeSets.add(new ChangeSet(ele.getAttribute("id"), parseRefactorings(ele)));
		}
		return changeSets;
	}
	
	private List parseRefactorings(Element element) {
		ArrayList refactorings = new ArrayList();
		NodeList childNodes = element.getChildNodes();
		
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			if (child instanceof Element) {
				Refactoring refactoring = parseRefactoring((Element) child);
				if (refactoring != null) {
					refactorings.add(refactoring);
				}
			}
		}
		return refactorings;
	}
	
	private Refactoring parseRefactoring(Element ele) {
		Refactoring refactoring = null;
		if (DomUtils.nodeNameEquals(ele, "add-column")) {
			refactoring = new AddColumn(ele.getAttribute("table"), 
					parseColumn(ele));
		}
		if (DomUtils.nodeNameEquals(ele, "add-foreign-key")) {
			refactoring = new AddForeignKey(ele.getAttribute("table"), 
					new ForeignKey(ele.getAttribute("name"), 
					ele.getAttribute("references"),
					parseReferences(ele)));
		}
		if (DomUtils.nodeNameEquals(ele, "add-unique-constraint")) {
			refactoring = new AddUniqueConstraint(ele.getAttribute("table"), 
					 new UniqueConstraint(ele.getAttribute("name"),
					StringUtils.tokenizeToStringArray(ele.getAttribute("on"), ",")));
		}
		if (DomUtils.nodeNameEquals(ele, "create-index")) {
			refactoring = new CreateIndex(ele.getAttribute("table"), 
					new Index(ele.getAttribute("name"),
					StringUtils.tokenizeToStringArray(ele.getAttribute("on"), ","),
					Boolean.valueOf(ele.getAttribute("unique")).booleanValue()));
		}
		if (DomUtils.nodeNameEquals(ele, "create-table")) {
			refactoring = new CreateTable(ele.getAttribute("name"), 
					parseColumns(ele));
		}
		if (DomUtils.nodeNameEquals(ele, "drop-column")) {
			refactoring = new DropColumn(ele.getAttribute("table"),
					ele.getAttribute("column"));
		}
		if (DomUtils.nodeNameEquals(ele, "drop-foreign-key")) {
			refactoring = new DropForeignKey(ele.getAttribute("table"),
					ele.getAttribute("foreign-key"));
		}
		if (DomUtils.nodeNameEquals(ele, "drop-index")) {
			refactoring = new DropIndex(ele.getAttribute("table"),
					ele.getAttribute("index"));
		}
		if (DomUtils.nodeNameEquals(ele, "drop-table")) {
			refactoring = new DropTable(ele.getAttribute("table"));
		}
		if (DomUtils.nodeNameEquals(ele, "drop-unique-constraint")) {
			refactoring = new DropUniqueConstraint(ele.getAttribute("table"),
					ele.getAttribute("constraint"));
		}
		if (DomUtils.nodeNameEquals(ele, "modify-column")) {
			refactoring = new ModifyColumn(ele.getAttribute("table"),
					parseColumn(ele));
		}
		if (DomUtils.nodeNameEquals(ele, "rename-column")) {
			refactoring = new RenameColumn(ele.getAttribute("table"),
					ele.getAttribute("column"), ele.getAttribute("rename-to"));
		}
		if (DomUtils.nodeNameEquals(ele, "rename-table")) {
			refactoring = new RenameTable(ele.getAttribute("table"), 
					ele.getAttribute("rename-to"));
		}
		if (DomUtils.nodeNameEquals(ele, "insert-data")) {
			refactoring = new InsertData(ele.getAttribute("table"), 
					parseEntries(ele));
		}
		if (DomUtils.nodeNameEquals(ele, "create-auto-increment-seq")) {
			refactoring = new CreateAutoIncrementSequence(
					ele.getAttribute("name"));
		}
		return refactoring;
	}
	
	private List parseColumns(Element ele) {
		ArrayList columns = new ArrayList();
		Iterator it = DomUtils.getChildElementsByTagName(ele, "column").iterator();
		while (it.hasNext()) {
			columns.add(parseColumn((Element) it.next()));
		}
		return columns;
	}
	
	private Column parseColumn(Element ele) {
		Column column = new Column();
		column.setName(ele.getAttribute("name"));
		if (StringUtils.hasLength(ele.getAttribute("type"))) {
			column.setType(ele.getAttribute("type"));
		}
		if (StringUtils.hasLength(ele.getAttribute("length"))) {
			column.setLength(Integer.valueOf(
					ele.getAttribute("length")).intValue());
		}
		if (StringUtils.hasLength(ele.getAttribute("primary-key"))) {
			column.setPrimaryKey(Boolean.valueOf(
					ele.getAttribute("primary-key")).booleanValue());
		}
		if (StringUtils.hasLength(ele.getAttribute("not-null"))) {
			column.setNotNull(Boolean.valueOf(
					ele.getAttribute("not-null")).booleanValue());
		}
		if (StringUtils.hasLength(ele.getAttribute("auto-increment"))) {
			column.setAutoIncrement(Boolean.valueOf(
					ele.getAttribute("auto-increment")).booleanValue());
		}
		return column;
	}
	
	private List parseReferences(Element ele) {
		ArrayList references = new ArrayList();
		Iterator it = DomUtils.getChildElementsByTagName(ele, "ref").iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			references.add(new Reference(
					e.getAttribute("local"),
					e.getAttribute("foreign")));
		}
		return references;
	}
	
	private List parseEntries(Element ele) {
		ArrayList entries = new ArrayList();
		Iterator it = DomUtils.getChildElementsByTagName(ele, "entry").iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			entries.add(new RecordEntry(
					e.getAttribute("column"), 
					e.getAttribute("value")));
		}
		return entries;
	}
	
	public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder 
			holder, ParserContext parserContext) {
		
		throw new UnsupportedOperationException(
				"Bean decoration is not supported.");
	}

}
