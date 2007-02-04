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
package org.riotfamily.riot.editor.xml;

import java.util.Iterator;

import org.riotfamily.common.xml.DocumentDigester;
import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.riot.editor.AbstractEditorDefinition;
import org.riotfamily.riot.editor.CustomEditorDefinition;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.FormChooserDefinition;
import org.riotfamily.riot.editor.FormDefinition;
import org.riotfamily.riot.editor.GroupDefinition;
import org.riotfamily.riot.editor.IntermediateDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.TreeDefinition;
import org.riotfamily.riot.editor.ViewDefinition;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
public class XmlEditorRepositoryDigester implements DocumentDigester {

	public static final String NAMESPACE = "http://www.riotfamily.org/schema/riot/editor-config";
	
	private static final String GROUP = "group";
	
	private static final String[] GROUP_ATTR = new String[] {
		"name", "key", "icon", "hidden"
	};
	
	private static final String LIST = "list";
	
	private static final String TREE = "tree";
	
	private static final String LIST_EDITOR = LIST + '|' + TREE;
	
	private static final String[] LIST_ATTR = new String[] {
		"name", "listId=list-ref", "icon", "hidden", "id"
	};
	
	private static final String[] TREE_ATTR = new String[] {
		"branch-class"
	};
	
	private static final String FORM = "form";
	
	private static final String FORM_CHOOSER = "form-chooser";
	
	private static final String VIEW = "view";
	
	private static final String[] VIEW_ATTR = new String[] {
		"name", "template"
	};
	
	private static final String OBJECT_EDITOR = FORM + '|' 
			+ FORM_CHOOSER + '|' + VIEW;
	
	private static final String FORM_OPTION = "form-option";

	
	private static final String[] FORM_ATTR = new String[] {
		"name", "formId=form-ref", "label-property", "hidden"
	};
	
	private static final String ANYTHING = OBJECT_EDITOR + '|' + LIST_EDITOR;
	
	private static final String CUSTOM = "custom";
	
	private static final String[] CUSTOM_ATTR = new String[] {
		"name", "url", "target", "icon"
	};
		
	private XmlEditorRepository editorRepository;
	
	private BeanFactory beanFactory;

	public XmlEditorRepositoryDigester(XmlEditorRepository editorRepository,
			BeanFactory beanFactory) {
		
		this.editorRepository = editorRepository;
		this.beanFactory = beanFactory;
	}
	
	/**
	 * Initializes the repository by digesting the given document.
	 */
	public void digest(Document doc, Resource resource) {
		Element root = doc.getDocumentElement();
		GroupDefinition group = editorRepository.getRootGroupDefinition();
		if (group == null) {
			group = new GroupDefinition(editorRepository);
			group.setName("start"); //REVISIT
			editorRepository.addEditorDefinition(group);
			editorRepository.setRootGroupDefinition(group);
		}
		digestGroupEntries(root, group);
	}
	
	protected void addEditorDefinition(AbstractEditorDefinition editor) {
		if (editor.getId() == null) {
			editor.setId(getUniqueId(editor.getName()));			
		}
		editorRepository.addEditorDefinition(editor);
	}

	protected void digestGroupEntries(Element groupElement, GroupDefinition group) {
		Iterator it = XmlUtils.getChildElements(groupElement).iterator();
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String namespace = ele.getNamespaceURI();
			if (namespace == null || namespace.equals(NAMESPACE)) {
				EditorDefinition ed = digestEditorDefinition(ele);
				group.addEditorDefinition(ed);
			}
		}
	}
	
	/**
	 * Creates an EditorDefinition. 
	 */
	protected EditorDefinition digestEditorDefinition(Element ele) {
		EditorDefinition ed = null;
		if (isListElement(ele) || isTreeElement(ele)) {
			ed = digestListOrTreeDefinition(ele);
		}
		else if (isGroupElement(ele)) {
			ed = digestGroupDefinition(ele);
		}
		else if (isFormElement(ele)) {
			ed = digestFormDefinition(ele, null);
		}
		else if (isCustomElement(ele)) {
			ed = digestCustomDefinition(ele);
		}
		return ed;
	}
	
	/**
	 * Creates a GroupDefinition by digesting the given element. Nested 
	 * EditorDefinitions will be digested by calling 
	 * {@link #digestEditorDefinition(Element) digestEditorDefinition()}.
	 */
	protected GroupDefinition digestGroupDefinition(Element groupElement) {
		GroupDefinition group = new GroupDefinition(editorRepository);
		XmlUtils.populate(group, groupElement, GROUP_ATTR);
		addEditorDefinition(group);
		digestGroupEntries(groupElement, group);
		return group;
	}
	
	protected ListDefinition digestListOrTreeDefinition(Element listElement) {
		ListDefinition listDefinition;
		if (isTreeElement(listElement)) {
			listDefinition = new TreeDefinition(editorRepository);
			XmlUtils.populate(listDefinition, listElement, TREE_ATTR);
		}
		else {
			listDefinition = new ListDefinition(editorRepository);
		}
		initListDefinition(listDefinition, listElement);
		return listDefinition;
	}
	
	/**
	 * Initialized a ListDefinition by digesting the given element.
	 */
	protected void initListDefinition(ListDefinition listDefinition, 
			Element listElement) {
		
		XmlUtils.populate(listDefinition, listElement, LIST_ATTR);
		addEditorDefinition(listDefinition);
		
		EditorDefinition def = null;
		Element e = XmlUtils.getFirstChildByRegex(listElement, ANYTHING);
		if (isFormElement(e) || isFormChooserElement(e) || isViewElement(e)) {
			def = digestObjectEditorDefinition(e, listDefinition);
		}
		else if (isListElement(e) || isTreeElement(e)) {
			def = new IntermediateDefinition(
					listDefinition, digestListOrTreeDefinition(e));
		}
		listDefinition.setDisplayDefinition(def);
	}
	
	
	protected EditorDefinition digestObjectEditorDefinition(
			Element ele, EditorDefinition parentDef) {
		
		if (isFormElement(ele)) {
			return digestFormDefinition(ele, parentDef);
		}
		else if (isFormChooserElement(ele)) {
			return digestFormChooserDefinition(ele, parentDef);
		}
		else if (isViewElement(ele)) {
			return digestViewDefinition(ele, parentDef);
		}
		else {
			throw new RuntimeException("Expected " + OBJECT_EDITOR);
		}
	}
	
	/**
	 * Creates a new DefaultFormDefinition by digesting the given element.
	 */
	protected FormDefinition digestFormDefinition(Element formElement, 
			EditorDefinition parentDef) {
		
		FormDefinition formDefinition = new FormDefinition(editorRepository);
		XmlUtils.populate(formDefinition, formElement, FORM_ATTR, beanFactory);
		formDefinition.setParentEditorDefinition(parentDef);
		addEditorDefinition(formDefinition);
		
		Iterator it = XmlUtils.getChildElementsByRegex(
				formElement, LIST_EDITOR).iterator();
		
		while (it.hasNext()) {
			formDefinition.addChildEditorDefinition(
					digestListOrTreeDefinition((Element) it.next()));
		}
		
		it = XmlUtils.getChildElementsByRegex(
				formElement, OBJECT_EDITOR).iterator();
		
		while (it.hasNext()) {
			EditorDefinition childDef = digestObjectEditorDefinition(
					(Element) it.next(), parentDef);
		
			formDefinition.addChildEditorDefinition(childDef);
		}
		
		return formDefinition;
	}
	
	protected ViewDefinition digestViewDefinition(Element formElement, 
			EditorDefinition parentDef) {
		
		ViewDefinition viewDefinition = new ViewDefinition(editorRepository);
		XmlUtils.populate(viewDefinition, formElement, VIEW_ATTR);
		viewDefinition.setParentEditorDefinition(parentDef);
		addEditorDefinition(viewDefinition);
		return viewDefinition;
	}
	
	protected String getUniqueId(String name) {
		String id = name != null ? name : "editor";
		String uniqueId = id;
		EditorDefinition def = editorRepository.getEditorDefinition(uniqueId);
		int i = 1;
		while (def != null) {
			uniqueId = id + i;
			def = editorRepository.getEditorDefinition(uniqueId);
			i++;
		}
		return uniqueId;
	}

		
	protected FormChooserDefinition digestFormChooserDefinition(
			Element chooserElement, EditorDefinition parentDef) {
		
		FormChooserDefinition formChooserDefinition = 
				new FormChooserDefinition(editorRepository);
		
		XmlUtils.populate(formChooserDefinition, chooserElement, 
				FORM_ATTR, beanFactory);
		
		formChooserDefinition.setParentEditorDefinition(parentDef);
		addEditorDefinition(formChooserDefinition);
		
		Iterator it = DomUtils.getChildElementsByTagName(
				chooserElement, FORM_OPTION).iterator();
		
		while (it.hasNext()) {
			FormDefinition formDefinition = 
					digestFormDefinition((Element) it.next(),
					formChooserDefinition);

			formChooserDefinition.addFormDefinition(formDefinition);
		}
		return formChooserDefinition;
	}
	
	protected EditorDefinition digestCustomDefinition(Element ele) {
		CustomEditorDefinition custom = new CustomEditorDefinition(editorRepository);
		XmlUtils.populate(custom, ele, CUSTOM_ATTR);
		addEditorDefinition(custom);
		return custom;
	}
	
	private static boolean isFormElement(Element ele) {
		return ele != null && DomUtils.nodeNameEquals(ele, FORM);
	}
	
	private static boolean isFormChooserElement(Element ele) {
		return ele != null && DomUtils.nodeNameEquals(ele, FORM_CHOOSER);
	}
	
	private static boolean isViewElement(Element ele) {
		return ele != null && DomUtils.nodeNameEquals(ele, VIEW);
	}
	
	private static boolean isListElement(Element ele) {
		return ele != null && DomUtils.nodeNameEquals(ele, LIST);
	}
	
	private static boolean isTreeElement(Element ele) {
		return ele != null && DomUtils.nodeNameEquals(ele, TREE);
	}
	
	private static boolean isGroupElement(Element ele) {
		return ele != null && DomUtils.nodeNameEquals(ele, GROUP);
	}
	
	private static boolean isCustomElement(Element ele) {
		return ele != null && DomUtils.nodeNameEquals(ele, CUSTOM);
	}
	
}
