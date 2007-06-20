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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.editor;

import java.util.HashMap;
import java.util.Iterator;

import org.riotfamily.common.beans.propertyeditors.BooleanEditor;
import org.riotfamily.components.Component;
import org.riotfamily.components.ComponentRepository;
import org.riotfamily.components.property.FileStoreProperyProcessor;
import org.riotfamily.components.property.PropertyEditorProcessor;
import org.riotfamily.components.property.XmlPropertyProcessor;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.core.Checkbox;
import org.riotfamily.forms.element.core.FileUpload;
import org.riotfamily.forms.element.core.ImageUpload;
import org.riotfamily.forms.element.core.NumberField;
import org.riotfamily.forms.element.dom.XmlSequence;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.xml.XmlFormRepository;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ComponentFormRepository extends XmlFormRepository {

	private ComponentRepository componentRepository;

	public ComponentFormRepository(ComponentRepository componentRepository) {
		this.componentRepository = componentRepository;
		componentRepository.setFormRepository(this);
		setDefaultBeanClass(HashMap.class);
	}

	public void registerFormFactory(String id, FormFactory formFactory) {
		super.registerFormFactory(id, formFactory);
		Component component = componentRepository.getComponent(id);
		componentRepository.resetPropertyProcessors(component);
		setupForm(component, formFactory.createForm());
	}

	protected void setupForm(Component component, Form form) {
		Iterator it = form.getRegisteredElements().iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			if (e instanceof FileUpload) {
				FileUpload upload = (FileUpload) e;
				component.addPropertyProcessor(
						new FileStoreProperyProcessor(
						upload.getEditorBinding().getProperty(),
						upload.getFileStore()));

				if (upload.getSizeProperty() != null) {
					component.addPropertyProcessor(
							new PropertyEditorProcessor(
							upload.getSizeProperty(),
							new CustomNumberEditor(Long.class, true)));
				}
				if (upload instanceof ImageUpload) {
					ImageUpload imageUpload = (ImageUpload) upload;
					if (imageUpload.getWidthProperty() != null) {
						component.addPropertyProcessor(
								new PropertyEditorProcessor(
								imageUpload.getWidthProperty(),
								new CustomNumberEditor(Integer.class, true)));
					}
					if (imageUpload.getHeightProperty() != null) {
						component.addPropertyProcessor(
								new PropertyEditorProcessor(
								imageUpload.getHeightProperty(),
								new CustomNumberEditor(Integer.class, true)));
					}
				}
			}
			else if (e instanceof Checkbox) {
				Checkbox cb = (Checkbox) e;
				component.addPropertyProcessor(
						new PropertyEditorProcessor(
						cb.getEditorBinding().getProperty(),
						new BooleanEditor(),
						Boolean.toString(cb.isCheckedByDefault())));
			}
			else if (e instanceof NumberField) {
				NumberField nf = (NumberField) e;
				Class numberClass = nf.getEditorBinding().getPropertyType();
				if (!(Number.class.isAssignableFrom(numberClass))) {
					numberClass = Integer.class;
				}
				component.addPropertyProcessor(
						new PropertyEditorProcessor(
						nf.getEditorBinding().getProperty(),
						new CustomNumberEditor(numberClass, false)));
			}
			else if (e instanceof XmlSequence) {
				XmlSequence xs = (XmlSequence) e;
				component.addPropertyProcessor(new XmlPropertyProcessor(
						xs.getEditorBinding().getProperty()));
			}
		}
	}
}
