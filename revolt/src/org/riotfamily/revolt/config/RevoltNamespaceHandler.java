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
package org.riotfamily.revolt.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.revolt.ChangeSet;
import org.riotfamily.revolt.EvolutionException;
import org.riotfamily.revolt.EvolutionHistory;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.definition.ForeignKey;
import org.riotfamily.revolt.definition.Index;
import org.riotfamily.revolt.definition.RecordEntry;
import org.riotfamily.revolt.definition.Reference;
import org.riotfamily.revolt.definition.UniqueConstraint;
import org.riotfamily.revolt.definition.UpdateStatement;
import org.riotfamily.revolt.refactor.AddColumn;
import org.riotfamily.revolt.refactor.AddForeignKey;
import org.riotfamily.revolt.refactor.AddUniqueConstraint;
import org.riotfamily.revolt.refactor.CreateAutoIncrementSequence;
import org.riotfamily.revolt.refactor.CreateIndex;
import org.riotfamily.revolt.refactor.CreateTable;
import org.riotfamily.revolt.refactor.DropColumn;
import org.riotfamily.revolt.refactor.DropConstraint;
import org.riotfamily.revolt.refactor.DropForeignKey;
import org.riotfamily.revolt.refactor.DropIndex;
import org.riotfamily.revolt.refactor.DropTable;
import org.riotfamily.revolt.refactor.ExecSql;
import org.riotfamily.revolt.refactor.InsertData;
import org.riotfamily.revolt.refactor.ModifyColumn;
import org.riotfamily.revolt.refactor.RenameColumn;
import org.riotfamily.revolt.refactor.RenameTable;
import org.riotfamily.revolt.refactor.UpdateData;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.ManagedList;
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
			
			MutablePropertyValues pv = new MutablePropertyValues();
			pv.addPropertyValue("changeSets", parseChangeSets(element));
			
			pv.addPropertyValue("checkTableName", element.getAttribute("check-table-name"));
			
			String depends = element.getAttribute("depends");
			if (depends != null) {
				pv.addPropertyValue("depends",
					StringUtils.tokenizeToStringArray(depends, ","));
			}
			
			definition.setPropertyValues(pv);
			BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, element.getAttribute("module"));
			BeanDefinitionReaderUtils.registerBeanDefinition(holder, parserContext.getRegistry());
			
			return definition;
		}
		throw new IllegalArgumentException(
				"Expected <history> but found: " + element.getNodeName());
	}
	
	private List<ChangeSet> parseChangeSets(Element element) {
		ArrayList<ChangeSet> changeSets = Generics.newArrayList();
		Iterator<Element> it = DomUtils.getChildElementsByTagName(element, "change-set").iterator();
		while (it.hasNext()) {
			Element ele = it.next();
			changeSets.add(new ChangeSet(ele.getAttribute("id"), parseRefactorings(ele)));
		}
		return changeSets;
	}
	
	@SuppressWarnings("unchecked")
	private List<Refactoring> parseRefactorings(Element element) {
		ManagedList refactorings = new ManagedList();
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
		if (DomUtils.nodeNameEquals(ele, "add-column")) {
			return new AddColumn(ele.getAttribute("table"), 
					parseColumn(ele));
		}
		if (DomUtils.nodeNameEquals(ele, "add-foreign-key")) {
			return new AddForeignKey(ele.getAttribute("table"), 
					new ForeignKey(ele.getAttribute("name"), 
					ele.getAttribute("references"),
					parseReferences(ele), 
					parseOnDelete(ele), 
					parseOnUpdate(ele)));
		}
		if (DomUtils.nodeNameEquals(ele, "add-unique-constraint")) {
			return new AddUniqueConstraint(ele.getAttribute("table"), 
					 new UniqueConstraint(ele.getAttribute("name"),
					StringUtils.tokenizeToStringArray(ele.getAttribute("on"), ",")));
		}
		if (DomUtils.nodeNameEquals(ele, "create-index")) {
			return new CreateIndex(ele.getAttribute("table"), 
					new Index(ele.getAttribute("name"),
					StringUtils.tokenizeToStringArray(ele.getAttribute("on"), ","),
					Boolean.valueOf(ele.getAttribute("unique")).booleanValue()));
		}
		if (DomUtils.nodeNameEquals(ele, "create-table")) {
			return new CreateTable(ele.getAttribute("name"), 
					parseColumns(ele));
		}
		if (DomUtils.nodeNameEquals(ele, "drop-column")) {
			return new DropColumn(ele.getAttribute("table"),
					ele.getAttribute("column"));
		}
		if (DomUtils.nodeNameEquals(ele, "drop-foreign-key")) {
			return new DropForeignKey(ele.getAttribute("table"),
					ele.getAttribute("foreign-key"));
		}
		if (DomUtils.nodeNameEquals(ele, "drop-index")) {
			return new DropIndex(ele.getAttribute("table"),
					ele.getAttribute("index"));
		}
		if (DomUtils.nodeNameEquals(ele, "drop-table")) {
			return new DropTable(ele.getAttribute("table"),
					Boolean.valueOf(ele.getAttribute("cascade")).booleanValue());
		}
		if (DomUtils.nodeNameEquals(ele, "drop-constraint")) {
			return new DropConstraint(ele.getAttribute("table"),
					ele.getAttribute("constraint"));
		}
		if (DomUtils.nodeNameEquals(ele, "modify-column")) {
			return new ModifyColumn(ele.getAttribute("table"),
					parseColumn(ele));
		}
		if (DomUtils.nodeNameEquals(ele, "rename-column")) {
			return new RenameColumn(ele.getAttribute("table"),
					ele.getAttribute("column"), ele.getAttribute("rename-to"));
		}
		if (DomUtils.nodeNameEquals(ele, "rename-table")) {
			return new RenameTable(ele.getAttribute("table"), 
					ele.getAttribute("rename-to"));
		}
		if (DomUtils.nodeNameEquals(ele, "insert-data")) {
			return new InsertData(ele.getAttribute("table"), 
					parseEntries(ele));
		}
		if (DomUtils.nodeNameEquals(ele, "update-data")) {
			return new UpdateData(parseUpdateStatements(ele));
		}
		if (DomUtils.nodeNameEquals(ele, "exec-sql")) {
			return new ExecSql(ele.getTextContent());
		}
		if (DomUtils.nodeNameEquals(ele, "create-auto-increment-seq")) {
			return new CreateAutoIncrementSequence(
					ele.getAttribute("name"));
		}
		if (DomUtils.nodeNameEquals(ele, "custom")) {
			return SpringUtils.newInstance(ele.getAttribute("class"));
		}
		throw new EvolutionException("Unsupported refactoring: " + ele.getNodeName());
	}
	
	private String parseOnUpdate(Element ele) {
		Element e = DomUtils.getChildElementByTagName(ele, "on-update");
		return  e != null ? e.getAttribute("value") : null;
	}

	private String parseOnDelete(Element ele) {
		Element e = DomUtils.getChildElementByTagName(ele, "on-delete");
		return  e != null ? e.getAttribute("value") : null;
	}

	private List<Column> parseColumns(Element ele) {
		ArrayList<Column> columns = Generics.newArrayList();
		Iterator<Element> it = DomUtils.getChildElementsByTagName(ele, "column").iterator();
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
		if (StringUtils.hasLength(ele.getAttribute("default-value"))) {
			column.setDefaultValue(ele.getAttribute("default-value"));
		}
		return column;
	}
	
	private List<Reference> parseReferences(Element ele) {
		ArrayList<Reference> references = Generics.newArrayList();
		Iterator<Element> it = DomUtils.getChildElementsByTagName(ele, "ref").iterator();
		while (it.hasNext()) {
			Element e = it.next();
			references.add(new Reference(
					e.getAttribute("local"),
					e.getAttribute("foreign")));
		}
		return references;
	}

	private List<RecordEntry> parseEntries(Element ele) {
		ArrayList<RecordEntry> entries = Generics.newArrayList();
		Iterator<Element> it = DomUtils.getChildElementsByTagName(ele, "entry").iterator();
		while (it.hasNext()) {
			Element e = it.next();
			entries.add(new RecordEntry(
					e.getAttribute("column"), 
					e.getAttribute("value")));
		}
		return entries;
	}
	
	private List<UpdateStatement> parseUpdateStatements(Element ele) {
		ArrayList<UpdateStatement> statements = Generics.newArrayList();
		Iterator<Element> it = DomUtils.getChildElementsByTagName(ele, "statement").iterator();
		while (it.hasNext()) {
			Element e = it.next();
			statements.add(new UpdateStatement(
					e.getAttribute("dialects"), 
					e.getAttribute("sql")));
		}
		return statements;
	}
	
	public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder 
			holder, ParserContext parserContext) {
		
		throw new UnsupportedOperationException(
				"Bean decoration is not supported.");
	}

}
