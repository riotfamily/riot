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
package org.riotfamily.riot.form.element;

import java.io.PrintWriter;

import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.beans.ProtectedBeanWrapper;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.select.AbstractChooser;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.form.ui.FormUtils;
import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.ui.render.CellRenderer;
import org.riotfamily.riot.list.ui.render.RenderContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class ObjectChooser extends AbstractChooser 
		implements BeanFactoryAware {

	private String targetEditorId;
		
	private String rootEditorId;
	
	private String rootProperty;
	
	private String rootIdAttribute;
	
	private String[] display;
	
	private BeanFactory beanFactory;
	
	private EditorRepository editorRepository;
	
	private ListDefinition rootListDefinition;
	
	private EditorDefinition targetEditorDefinition;
	
	private ListConfig targetListConfig;
		
	/**
	 * Sets the id of an editor that will be used to list the target objects.
	 * If the specified editor is nested within another list, Riot will display
	 * the root list and let the user descend down to the target list.
	 */
	public void setTargetEditorId(String targetEditorId) {
		this.targetEditorId = targetEditorId;
	}

	/**
	 * Sets the id of the list that that is initially displayed. When you 
	 * specify a nested {@link #setTargetEditorId(String) target editor}, the
	 * choosing process will start with the root list.
	 * <p>
	 * This property allows you to select another list as starting point. 
	 * Since any non-root list will require a parent id, you have to specify a 
	 * {@link #setRootProperty(String) root property} or a 
	 * {@link #setRootIdAttribute(String) root id attribute}.
	 */
	public void setRootEditorId(String rootEditorId) {
		this.rootEditorId = rootEditorId;
	}
	
	/**
	 * Sets the name of a property on the <b>parent</b> object that refers to 
	 * the parent object of the {@link #setRootEditorId(String) root list}.
	 * <p>
	 * <b>Example:</b> Lets say we had three classes (Foo, Bar, Baz) and the Riot navigation 
	 * path looked like this: 
	 * <pre> 
	 * Foo: foo1 | Bar: bar1 | Baz: new
	 * </pre>
	 * The Baz class has a property "otherBaz" and we want to configure a 
	 * chooser that lets the user select another Baz object with the same 
	 * parent (<code>foo1</code>). The configuration would look like this: 
	 * <pre>
	 * &lt;riot:chooser bind="otherBaz" rootEditorId="bars" rootProperty="foo" /&gt;
	 * </pre>
	 * This instructs Riot to invoke the <code>getFoo()</code> method on the
	 * <code>bar1</code> object and use the returned value (<code>foo1</code>)
	 * as parent for the "bars" list.
	 */
	public void setRootProperty(String rootProperty) {
		this.rootProperty = rootProperty;
	}
	
	/**
	 * Sets the name of a {@link Form#getAttribute(String) form attribute} that
	 * contains the String which should be used as parentId for the root list. 
	 */
	public void setRootIdAttribute(String rootIdAttribute) {
		this.rootIdAttribute = rootIdAttribute;
	}
	
	public void setDisplay(String display) {
		this.display = StringUtils.commaDelimitedListToStringArray(display);
	}
	
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	protected void afterFormSet() {
		if (editorRepository == null) {
			Assert.isInstanceOf(ListableBeanFactory.class, beanFactory,
					"Not a ListableBeanFactory");
			
			ListableBeanFactory lbf = (ListableBeanFactory) beanFactory;
			editorRepository = (EditorRepository) 
					BeanFactoryUtils.beanOfTypeIncludingAncestors(
					lbf, EditorRepository.class);
			
			Assert.notNull(editorRepository, 
					"No EditorRepository found in BeanFactory");
		}
		
		log.debug("Looking up editor: " + targetEditorId);
		targetEditorDefinition = editorRepository.getEditorDefinition(
				targetEditorId);
		
		Assert.notNull(targetEditorDefinition, "No such EditorDefinition: "
					+ targetEditorId);

		targetListConfig = EditorDefinitionUtils.getListDefinition(
				targetEditorDefinition).getListConfig();
		
		if (rootEditorId != null) {
			rootListDefinition = editorRepository.getListDefinition(rootEditorId);
			Assert.notNull(rootListDefinition, "No such ListDefinition: " + rootEditorId);
		}
		else {
			rootListDefinition = EditorDefinitionUtils.getRootListDefinition(
					targetEditorDefinition);
		}
	}
	
	public RiotDao getRiotDao() {
		return EditorDefinitionUtils.getListDefinition(targetEditorDefinition)
				.getListConfig().getDao();
	}
	
	protected Object loadBean(String objectId) {
		return EditorDefinitionUtils.loadBean(targetEditorDefinition, objectId);
	}

	protected void renderLabel(Object object, PrintWriter writer) {
		if (object == null) {
			return;
		}
		if (display == null) {
			new TagWriter(writer).start(Html.SPAN)
					.body(targetEditorDefinition.getLabel(object))
					.end();
			
			return;
		}
		
		DocumentWriter doc = new DocumentWriter(writer);
		doc.start(Html.SPAN).attribute(Html.COMMON_CLASS, 
				"chosen " + targetListConfig.getId());

		LabelRenderContext context = new LabelRenderContext();
		CellRenderer defaultRenderer = editorRepository.getListRepository()
				.getDefaultCellRenderer();
		
		ProtectedBeanWrapper wrapper = new ProtectedBeanWrapper(object);
		for (int i = 0; i < display.length; i++) {
			ColumnConfig column = targetListConfig.getColumnConfig(display[i]);
			CellRenderer renderer = null;
			if (column != null) {
				renderer = column.getRenderer();
			}
			if (renderer == null) {
				renderer = defaultRenderer;
			}
			Object value = wrapper.getPropertyValue(display[i]);
			doc.start(Html.SPAN).attribute(Html.COMMON_CLASS, 
					FormatUtils.toCssClass(display[i])).body();
			
			renderer.render(display[i], value, context, writer);
			doc.end();
		}
		
		doc.end();
	}
	
	
	protected String getChooserUrl() {
		String rootId = null;
		if (rootProperty != null) {
			Object parent = FormUtils.loadParent(getForm());
			Object root = PropertyUtils.getProperty(parent, rootProperty);
			rootId = EditorDefinitionUtils.getObjectId(rootListDefinition, root);
		}
		else if (rootIdAttribute != null) {
			rootId = (String) getForm().getAttribute(rootIdAttribute);
		}
		else if (rootEditorId != null) {
			rootId = FormUtils.getParentId(getForm());
		}
		return rootListDefinition.getEditorUrl(null, rootId) 
				+ "?choose=" + targetEditorDefinition.getId();
	}
	
	private class LabelRenderContext implements RenderContext {

		public String getListId() {
			return targetListConfig.getId();
		}
		
		public Class getBeanClass() {
			return targetEditorDefinition.getBeanClass();
		}
		
		public String getContextPath() {
			return getFormContext().getContextPath();
		}
		
		public MessageResolver getMessageResolver() {
			return getFormContext().getMessageResolver();
		}
		
	}
	
}
