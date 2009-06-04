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
import org.riotfamily.riot.editor.ObjectEditorDefinition;
import org.riotfamily.riot.editor.TreeDefinition;
import org.riotfamily.riot.editor.ViewDefinition;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
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

	private static final String FORM = "form";

	private static final String FORM_CHOOSER = "form-chooser";

	private static final String FORM_CHOOSER_COMMON = "common";

	private static final String VIEW = "view";
	
	private static final String REF = "ref";
	
	private static final String REF_EDITOR = "editor";

	private static final String[] VIEW_ATTR = new String[] {
		"name", "view-name"
	};

	private static final String CUSTOM = "custom";

	private static final String CUSTOM_REF = "ref";
	
	private static final String[] CUSTOM_ATTR = new String[] {
		"name", "target", "url", "handler", "icon"
	};

	private static final String OBJECT_EDITOR = FORM + '|'
			+ FORM_CHOOSER + '|' + VIEW + '|' + GROUP + '|' + CUSTOM
			+ '|' + REF;

	private static final String FORM_OPTION = "form-option";


	private static final String[] FORM_ATTR = new String[] {
		"name", "formId=form-ref", "label-property", "hidden", "discriminator-value"
	};
	
	private static final String[] FORM_CHOOSER_ATTR = new String[] {
		"name", "label-property", "discriminator-property"
	};

	private static final String ANYTHING = OBJECT_EDITOR + '|' + LIST_EDITOR;


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
			group.setId("start");
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
		Iterator<Element> it = XmlUtils.getChildElements(groupElement).iterator();
		while (it.hasNext()) {
			Element ele = it.next();
			String namespace = ele.getNamespaceURI();
			if (namespace == null || namespace.equals(NAMESPACE)) {
				EditorDefinition ed = digestEditorDefinition(ele);
				group.addChildEditorDefinition(ed);
			}
		}
	}

	/**
	 * Creates a GroupDefinition by digesting the given element. Nested
	 * EditorDefinitions will be digested by calling
	 * {@link #digestEditorDefinition(Element) digestEditorDefinition()}.
	 */
	protected GroupDefinition digestGroupDefinition(Element groupElement,
			EditorDefinition parentDef) {
		
		GroupDefinition group = new GroupDefinition(editorRepository);
		XmlUtils.populate(group, groupElement, GROUP_ATTR);
		group.setParentEditorDefinition(parentDef);
		addEditorDefinition(group);
		digestGroupEntries(groupElement, group);
		return group;
	}

	protected ListDefinition digestListOrTreeDefinition(Element listElement) {
		ListDefinition listDefinition;
		if (hasName(listElement, TREE)) {
			listDefinition = new TreeDefinition(editorRepository);
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

		XmlUtils.populate(listDefinition, listElement, LIST_ATTR, beanFactory);
		addEditorDefinition(listDefinition);

		Element e = XmlUtils.getFirstChildByRegex(listElement, ANYTHING);
		if (e != null) {
			ObjectEditorDefinition def = null;
			if (hasName(e, LIST) || hasName(e, TREE)) {
				def = new IntermediateDefinition(
						listDefinition, digestListOrTreeDefinition(e));
			}
			else {
				def = digestObjectEditorDefinition(e, listDefinition);
				
			}
			listDefinition.setDisplayDefinition(def);
		}
	}


	protected ObjectEditorDefinition digestObjectEditorDefinition(
			Element ele, EditorDefinition parentDef) {

		if (hasName(ele,  REF)) {
			EditorDefinition ed = getRef(ele);
			Assert.isInstanceOf(ObjectEditorDefinition.class, ed);
			ed.setParentEditorDefinition(parentDef);
			return (ObjectEditorDefinition) ed;
		}
		if (hasName(ele, FORM)) {
			return digestFormDefinition(ele, parentDef);
		}
		else if (hasName(ele, FORM_CHOOSER)) {
			return digestFormChooserDefinition(ele, parentDef);
		}
		else if (hasName(ele, VIEW)) {
			return digestViewDefinition(ele, parentDef);
		}
		else if (hasName(ele, GROUP)) {
			return digestGroupDefinition(ele, parentDef);
		}
		else if (hasName(ele, CUSTOM)) {
			return (ObjectEditorDefinition) digestCustomDefinition(ele, parentDef);
		}
		else {
			throw new RuntimeException("Expected " + OBJECT_EDITOR);
		}
	}
	
	/**
	 * Creates an EditorDefinition.
	 */
	protected EditorDefinition digestEditorDefinition(Element ele) {
		EditorDefinition ed = null;
		if (hasName(ele, REF)) {
			ed = getRef(ele);
		}
		else if (hasName(ele, LIST) || hasName(ele, TREE)) {
			ed = digestListOrTreeDefinition(ele);
		}
		else {
			ed = digestObjectEditorDefinition(ele, null);
		}
		return ed;
	}
	
	protected EditorDefinition getRef(Element ele) {
		String editorId = XmlUtils.getAttribute(ele, REF_EDITOR);
		EditorDefinition ed = editorRepository.getEditorDefinition(editorId);
		Assert.notNull(ed, "No such editor: " + editorId);
		if (ed.getParentEditorDefinition() instanceof ObjectEditorDefinition) {
			ObjectEditorDefinition parent = (ObjectEditorDefinition) ed.getParentEditorDefinition();
			parent.getChildEditorDefinitions().remove(ed);
		}
		if (ed instanceof ListDefinition) {
			((ListDefinition) ed).setHidden(false);
		}
		return ed;
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

		digestChildEditors(formElement, formDefinition);

		return formDefinition;
	}

	protected void digestChildEditors(Element ele, ObjectEditorDefinition editorDef) {
		Iterator<Element> it = XmlUtils.getChildElementsByRegex(
				ele, ANYTHING).iterator();

		while (it.hasNext()) {
			EditorDefinition childDef = digestChildEditorDefinition(
					(Element) it.next(), editorDef.getParentEditorDefinition());

			editorDef.addChildEditorDefinition(childDef);
		}
	}

	protected EditorDefinition digestChildEditorDefinition(Element ele,
			EditorDefinition parentDef) {

		if (hasName(ele, LIST) || hasName(ele, TREE)) {
			return digestListOrTreeDefinition(ele);
		}
		return digestObjectEditorDefinition(ele, parentDef);
	}

	protected ViewDefinition digestViewDefinition(Element viewElement,
			EditorDefinition parentDef) {

		ViewDefinition viewDefinition = new ViewDefinition(editorRepository);
		XmlUtils.populate(viewDefinition, viewElement, VIEW_ATTR, beanFactory);
		viewDefinition.setParentEditorDefinition(parentDef);
		addEditorDefinition(viewDefinition);
		
		digestChildEditors(viewElement, viewDefinition);
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
				FORM_CHOOSER_ATTR, beanFactory);

		formChooserDefinition.setParentEditorDefinition(parentDef);
		addEditorDefinition(formChooserDefinition);

		Iterator<Element> it = XmlUtils.getChildElementsByTagName(
				chooserElement, FORM_OPTION).iterator();
	
		while (it.hasNext()) {
			FormDefinition formDefinition =
					digestFormDefinition(it.next(),
					formChooserDefinition);

			formChooserDefinition.addFormDefinition(formDefinition);
		}

		Element commonElement = XmlUtils.getFirstChildByTagName(
				chooserElement, FORM_CHOOSER_COMMON);

		if (commonElement != null) {
			digestChildEditors(commonElement, formChooserDefinition);
		}
		return formChooserDefinition;
	}

	protected EditorDefinition digestCustomDefinition(Element ele,
			EditorDefinition parentDef) {

		CustomEditorDefinition custom;
		String beanName = XmlUtils.getAttribute(ele, CUSTOM_REF);
		if (beanName != null) {
			custom = (CustomEditorDefinition) beanFactory.getBean(
					beanName, CustomEditorDefinition.class);
		}
		else {
			custom = new CustomEditorDefinition();
		}
		custom.setEditorRepository(editorRepository);
		
		XmlUtils.populate(custom, ele, CUSTOM_ATTR, beanFactory);
		custom.setParentEditorDefinition(parentDef);
		addEditorDefinition(custom);
		
		digestChildEditors(ele, custom);
		
		return custom;
	}

	private static boolean hasName(Element ele, String name) {
		return ele != null && DomUtils.nodeNameEquals(ele, name);
	}

}
