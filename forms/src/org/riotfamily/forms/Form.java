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
package org.riotfamily.forms;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.DocumentWriter;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.TagWriter;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.ClickEvent;
import org.riotfamily.forms.event.ClickListener;
import org.riotfamily.forms.event.EventPropagation;
import org.riotfamily.forms.event.JavaScriptEventAdapter;
import org.riotfamily.forms.request.FormRequest;
import org.riotfamily.forms.request.HttpFormRequest;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.LoadingCodeGenerator;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.ui.Dimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;


/**
 * Serverside representation of a HTML form.
 */
public class Form implements BeanEditor {

	private static final String DEFAULT_ID = "f0";

	private static final String FORM_ATTR = "form";

	private static final String ELEMENT_CONTAINER_ATTR = "elements";
	
	private static final String BUTTON_CONTAINER_ATTR = "buttons";

	private Logger log = LoggerFactory.getLogger(Form.class);

	private String id = DEFAULT_ID;

	/** Elements keyed by their ID */
	private Map<String, Element> elementMap = new HashMap<String, Element>();

	/** Counter to create unique IDs */
	private int idCount;

	/** Set of used parameter names */
	private Set<String> paramNames = Generics.newHashSet();

	/** EditorBinder to bind top-level elements to properties */
	private EditorBinder editorBinder;

	/** Set containing resources required by the form itself (not it's elements) */
	private List<FormResource> globalResources = Generics.newArrayList();

	private FormInitializer initializer;

	private List<Container> containers = Generics.newArrayList();

	private Container elements;
	
	private Container buttons;

	private FormContext formContext;

	private FormErrors errors;

	private Validator validator;

	private FormListener formListener;

	private boolean rendering;

	private String clickedButton;
	
	private String template = TemplateUtils.getTemplatePath(this);

	private Map<String, Object> renderModel = Generics.newHashMap();

	private String hint;

	public Form() {
		setAttribute(FORM_ATTR, this);
		elements = createContainer(ELEMENT_CONTAINER_ATTR);
		buttons = createContainer(BUTTON_CONTAINER_ATTR);
	}

	public Form(Class<?> type) {
		this();
		setBeanClass(type);
	}

	/**
	 * @since 6.4
	 */
	@SuppressWarnings("unchecked")
	public Form(Object object) {
		this();
		Assert.notNull(object);
		if (object instanceof Map) {
			editorBinder = new MapEditorBinder((Map) object);
		}
		else {
			editorBinder = new BeanEditorBinder(object);
		}
	}
	
	public void setEditorBinder(EditorBinder editorBinder) {
		this.editorBinder = editorBinder.replace(this.editorBinder);
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

	public Collection<Element> getRegisteredElements() {
		return elementMap.values();
	}

	public void setAttribute(String key, Object value) {
		renderModel.put(key,value);
	}

	@SuppressWarnings("unchecked")
	public<T> T getAttribute(String key) {
		return (T) renderModel.get(key);
	}

	@SuppressWarnings("unchecked")
	public void setBeanClass(Class<?> beanClass) {
		Assert.notNull(beanClass, "The beanClass must not be null.");
		if (Map.class.isAssignableFrom(beanClass)) {
			editorBinder = new MapEditorBinder((Class<? extends Map<Object,Object>>) beanClass);
		}
		else {
			editorBinder = new BeanEditorBinder(beanClass);
		}
	}

	public void setBackingObject(Object value) {
		editorBinder.setBackingObject(value);
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
	
	public List<Element> getElements() {
		return this.elements.getComponents();
	}
	
	public List<Element> getButtons() {
		return this.buttons.getComponents();
	}

	public String getHint() {
		if (hint == null) {
			hint = MessageUtils.getHint(this, editorBinder.getBeanClass());
		}
		return hint;
	}

	/**
	 * Convenience method to add and bind an element in a single step.
	 */
	public void addElement(Editor element, String property) {
		bind(element, property);
		addElement(element);
	}
	
	public void addButton(Button button) {
		button.addClickListener(new ClickListener() {
			public void clicked(ClickEvent event) {
				clickedButton = event.getSource().getParamName();
			}
		});
		buttons.addElement(button);	
	}
	
	public void addButton(String name) {
		Button button = new Button();
		button.setSubmit(true);
		button.setParamName(name);
		button.setLabelKey("label.form.button." + name);
		addButton(button);
	}
	
	public String getClickedButton() {
		return clickedButton;
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

	protected List<FormResource> getResources() {
		List<FormResource> resources = Generics.newArrayList(globalResources);
		for (Element element : getRegisteredElements()) {
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
		return FormatUtils.toCssClass(this.id) + "e" + idCount++;
	}

	/**
	 * Returns the previously registered element with the given id.
	 */
	public Element getElementById(String id) {
		return elementMap.get(id);
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
		//TODO Assure uniqueness of synthetic names
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
		doc.start("script").body();
		writer.write("if (!(window.riot && riot.Resources)) document.write('" 
				+ "<script src=\"" + formContext.getContextPath()
				+ formContext.getResourcePath() + "riot/resources.js"
				+ "\"></scr'+'ipt>');\n");
		doc.end();
		doc.start("script").body();
		writer.write("riot.Resources.setBasePath('" + formContext.getContextPath() 
				+ formContext.getResourcePath()	+ "');\n");
		
		LoadingCodeGenerator.renderLoadingCode(getResources(), writer);
		doc.end();

		formContext.getTemplateRenderer().render(
				template, renderModel, writer);

		writer.print("<script>");
		ArrayList<EventPropagation> propagations = new ArrayList<EventPropagation>();
		for (Element element : getRegisteredElements()) { 
			if (element instanceof JavaScriptEventAdapter) {
				JavaScriptEventAdapter adapter = (JavaScriptEventAdapter) element;
				EventPropagation.addPropagations(adapter, propagations);
			}
		}
		
		if (!propagations.isEmpty()) {
			writer.print("riot.Resources.waitFor('propagate', function() {");
			for (EventPropagation p : propagations) { 
				writer.print("propagate('");
				writer.print(p.getTriggerId());
				writer.print("', '");
				writer.print(p.getType());
				writer.print("', '");
				writer.print(p.getSourceId());
				writer.print("');\n");
			}
			writer.print("});");
		}
		writer.print("</script>");
		
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
				script.start("script");
				script.attribute("language", "JavaScript");
				script.attribute("type", "text/javascript");
				script.body().print("//").cData().println();
				if (dhtml instanceof ResourceElement) {
					ResourceElement resEle = (ResourceElement) dhtml;
					FormResource res = resEle.getResource();
					if (res != null) {
						script.print("riot.Resources.execWhenLoaded(['");
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
		addResource(new ScriptResource("form/ajax.js", "propagate", 
				Resources.RIOT_EFFECTS));
		
		if (initializer != null) {
			initializer.initForm(this);
		}
		editorBinder.initEditors();
	}

	public void processRequest(HttpServletRequest request) {
		processRequest(new HttpFormRequest(request));
	}

	public void processRequest(FormRequest request) {
		clickedButton = null;
		errors.removeAllErrors();
		Iterator<Container> it = containers.iterator();
		while (it.hasNext()) {
			Container container = it.next();
			if (container.isEnabled()) {
				container.processRequest(request);
			}
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
		errors.removeAllErrors();
		Element element = getElementById(elementId);
		if (element.isEnabled()) {
			element.processRequest(request);
		}
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
		
		errors = new FormErrors(this);
		editorBinder.registerPropertyEditors(formContext.getPropertyEditorRegistrars());
		renderModel.put("messageResolver", formContext.getMessageResolver());
		elements.setComponentPadding(formContext.getSizing().getLabelSize());
		
		Iterator<Element> it = getRegisteredElements().iterator();
		while (it.hasNext()) {
			Element element = it.next();
			element.setFormContext(formContext);
		}
	}
	
	public Dimension getDimension() {
		return elements.getDimension();
	}
	
	public String getAction() {
		return formContext.getFormUrl();
	}

	public FormErrors getErrors() {
		return errors;
	}

	public boolean hasErrors() {
		return errors.hasErrors();
	}
	
}