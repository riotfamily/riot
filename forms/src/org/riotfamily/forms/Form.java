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
package org.riotfamily.forms;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.request.FormRequest;
import org.riotfamily.forms.request.HttpFormRequest;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.LoadingCodeGenerator;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.StylesheetResource;
import org.riotfamily.forms.support.MapOrBeanWrapper;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;


/**
 * Serverside representation of a HTML form.
 */
public class Form implements BeanEditor {

	private static final String DEFAULT_ID = "form";

	private static final String FORM_ATTR = "form";

	private static final String ELEMENT_CONTAINER_ATTR = "elements";

	private Log log = LogFactory.getLog(Form.class);

	private String id = DEFAULT_ID;

	/** Elements keyed by their id */
	private Map elementMap = new HashMap();

	/** Counter to create unique ids */
	private int idCount;

	/** Set of used parameter names */
	private Set paramNames = new HashSet();

	/** EditorBinder to bind toplevel elements to properties */
	private EditorBinder editorBinder;


	/** Set containing resources required by the form itself (not it's elements) */
	private List globalResources = new ArrayList();

	private FormInitializer initializer;

	private List containers = new ArrayList();

	private Container elements;

	private FormContext formContext;

	private FormErrors errors;

	private Validator validator;

	private FormListener formListener;

	private boolean rendering;

	private boolean includeStylesheet;

	private String template = TemplateUtils.getTemplatePath(this);

	private Map renderModel = new HashMap();

	private String hint;

	public Form() {
		setAttribute(FORM_ATTR, this);
		elements = createContainer(ELEMENT_CONTAINER_ATTR);
	}

	public Form(Class type) {
		this();
		setBeanClass(type);
	}

	/**
	 * @since 6.4
	 */
	public Form(Object object) {
		this();
		editorBinder = new EditorBinder(new MapOrBeanWrapper(object.getClass()));
		editorBinder.setBackingObject(object);
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public Collection getRegisteredElements() {
		return elementMap.values();
	}

	public void setAttribute(String key, Object value) {
		renderModel.put(key,value);
	}

	public Object getAttribute(String key) {
		return renderModel.get(key);
	}

	public void setBeanClass(Class beanClass) {
		Assert.notNull(beanClass, "The beanClass must not be null.");
		editorBinder = new EditorBinder(new MapOrBeanWrapper(beanClass));
	}

	public void setValue(Object backingObject) {
		if (backingObject != null) {
			editorBinder.setBackingObject(backingObject);
		}
	}

	public Object getValue() {
		return editorBinder.getBackingObject();
	}

	public boolean isNew() {
		return !editorBinder.isEditingExistingBean();
	}

	public EditorBinder getEditorBinder() {
		return editorBinder;
	}

	public Editor getEditor(String property) {
		return editorBinder.getEditor(property);
	}

	public void bind(Editor editor, String property) {
		editorBinder.bind(editor, property);
	}

	public void addElement(Element element) {
		elements.addElement(element);
	}

	public String getHint() {
		if (hint == null) {
			hint = MessageUtils.getHint(this, editorBinder.getBeanClass());
		}
		return hint;
	}

	/**
	 * Convinience method to add and bind an element within a single step.
	 */
	public void addElement(Editor element, String property) {
		addElement(element);
		bind(element, property);
	}

	public Container createContainer(String name) {
		Container container = new Container();
		containers.add(container);
		registerElement(container);
		setAttribute(name, container);
		return container;
	}

	public void addResource(FormResource resource) {
		globalResources.add(resource);
	}

	protected List getResources() {
		List resources = new ArrayList(globalResources);
		Iterator it = getRegisteredElements().iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			if (element instanceof ResourceElement) {
				ResourceElement re = (ResourceElement) element;
				FormResource res = re.getResource();
				if (res != null) {
					resources.add(res);
				}
			}
		}
		return resources;
	}

	/**
	 * Creates and sets an id for the given element and puts it into the
	 * internal elementMap.
	 *
	 * @param element the element to register
	 */
	public void registerElement(Element element) {
		String id = createId();
		element.setId(id);
		element.setForm(this);
		if (formContext != null) {
			element.setFormContext(formContext);
		}
		elementMap.put(id, element);
	}

	public void unregisterElement(Element element) {
		elementMap.remove(element.getId());
	}

	public String createId() {
		return "e" + idCount++;
	}

	/**
	 * Returns the previously registered element with the given id.
	 */
	public Element getElementById(String id) {
		return (Element) elementMap.get(id);
	}

	/**
	 * Returns a String that can be used as parameter name for input elements.
	 * Subsequent calls will return different values to ensure uniqueness of
	 * parameter names within a form.
	 */
	public String createUniqueParameterName() {
		return createUniqueParameterName(null);
	}

	/**
	 * Like {@link #createUniqueParameterName()}this method returns a String
	 * that can be used as parameter name. Since most modern browsers keep track
	 * of previously entered values (with the same parameter name) a desired
	 * name can be passed to this method. Typically an element will use the name
	 * of the property it is bound to as name. As this name might already be
	 * taken by another element (especially when lists of nested forms are used)
	 * this method will append an integer value to the given name if necessary.
	 */
	public String createUniqueParameterName(String desiredName) {
		if (desiredName == null) {
			desiredName = "p" + paramNames.size();
		}
		String name = desiredName;
		if (name.equalsIgnoreCase("target")) {
			// Otherwise changing the target of the form would not work
			name = "_target";
		}
		//TODO Aussure uniqueness of syntetic names
		if (paramNames.contains(name)) {
			name = desiredName + paramNames.size();
		}
		paramNames.add(name);
		return name;
	}

	public void render(PrintWriter writer) {
		rendering = true;

		formContext.setWriter(writer);
		DocumentWriter doc = new DocumentWriter(writer);

		doc.start(Html.SCRIPT);
		doc.attribute(Html.SCRIPT_SRC, formContext.getContextPath()
				+ formContext.getResourcePath() + "riot-js/resources.js");

		doc.attribute(Html.SCRIPT_TYPE, "text/javascript");
		doc.attribute(Html.SCRIPT_LANGUAGE, "JavaScript");
		doc.end();

		doc.start(Html.SCRIPT);
		doc.attribute(Html.SCRIPT_SRC, formContext.getContextPath()
				+ formContext.getResourcePath() + "form/hint.js");

		doc.attribute(Html.SCRIPT_TYPE, "text/javascript");
		doc.attribute(Html.SCRIPT_LANGUAGE, "JavaScript");
		doc.end();

		doc.start(Html.SCRIPT).body();
		LoadingCodeGenerator.renderLoadingCode(getResources(), writer);
		doc.end();

		formContext.getTemplateRenderer().render(
				template, renderModel, writer);

		rendering = false;
	}

	public boolean isRendering() {
		return rendering;
	}

	public void elementRendered(Element element) {
		log.debug("Element rendered: " + element.getId());
		if (getFormListener() != null) {
			getFormListener().elementRendered(element);
		}
		if (rendering && element instanceof DHTMLElement) {
			DHTMLElement dhtml = (DHTMLElement) element;
			PrintWriter writer = formContext.getWriter();
			TagWriter script = new TagWriter(writer);
			String initScript = dhtml.getInitScript();
			if (initScript != null) {
				script.start(Html.SCRIPT);
				script.attribute(Html.SCRIPT_LANGUAGE, "JavaScript");
				script.attribute(Html.SCRIPT_TYPE, "text/javascript");
				script.body().print("//").cData().println();
				if (dhtml instanceof ResourceElement) {
					ResourceElement resEle = (ResourceElement) dhtml;
					FormResource res = resEle.getResource();
					if (res != null) {
						script.print("Resources.execWhenLoaded(['");
						script.print(res.getUrl());
						script.print("'], function() {");
						script.print(initScript);
						script.print("})");
					}
					else {
						script.print(initScript);
					}
				}
				else {
					script.print(initScript);
				}
				script.print("//").end();
			}
		}
	}

	public void setInitializer(FormInitializer initializer) {
		this.initializer = initializer;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public void init() {
		if (initializer != null) {
			initializer.initForm(this);
		}
		editorBinder.initEditors();
		if (includeStylesheet) {
			addResource(new StylesheetResource("form/form.css"));
		}
	}

	public void processRequest(HttpServletRequest request) {
		processRequest(new HttpFormRequest(request));
	}

	public void processRequest(FormRequest request) {
		errors = new FormErrors(this);
		Iterator it = containers.iterator();
		while (it.hasNext()) {
			Container container = (Container) it.next();
			container.processRequest(request);
		}
		if (validator != null) {
			validator.validate(populateBackingObject(), errors);
		}
	}
	
	public void processExclusiveRequest(String elementId,
			HttpServletRequest request) {
		
		processExclusiveRequest(elementId, new HttpFormRequest(request));
	}
	
	public void processExclusiveRequest(String elementId, FormRequest request) {
		errors = new FormErrors(this);
		Element element = getElementById(elementId);
		element.processRequest(request);
	}

	public void setFormListener(FormListener formListener) {
		this.formListener = formListener;
	}

	public FormListener getFormListener() {
		return formListener;
	}

	public void requestFocus(Element element) {
		if (formListener != null) {
			formListener.elementFocused(element);
		}
	}

	public Object getBackingObject() {
		return editorBinder.getBackingObject();
	}

	public Object populateBackingObject() {
		return editorBinder.populateBackingObject();
	}

	public FormContext getFormContext() {
		return this.formContext;
	}

	public void setFormContext(FormContext formContext) {
		this.formContext = formContext;
		this.errors = new FormErrors(this);
		Iterator it = getRegisteredElements().iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			element.setFormContext(formContext);
		}
	}

	public FormErrors getErrors() {
		return errors;
	}

	public boolean hasErrors() {
		return errors.hasErrors();
	}
}