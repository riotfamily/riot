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
package org.riotfamily.forms.config;

import org.riotfamily.common.beans.namespace.ConstructorArgDecorator;
import org.riotfamily.common.beans.namespace.GenericNamespaceHandlerSupport;
import org.riotfamily.common.beans.namespace.ListDecorator;
import org.riotfamily.common.beans.namespace.PropertyDecorator;
import org.riotfamily.forms.base.Binding;
import org.riotfamily.forms.element.CheckBoxGroup;
import org.riotfamily.forms.element.Datepicker;
import org.riotfamily.forms.element.FileUpload;
import org.riotfamily.forms.element.ListEditor;
import org.riotfamily.forms.element.NestedForm;
import org.riotfamily.forms.element.PasswordField;
import org.riotfamily.forms.element.RadioButtonGroup;
import org.riotfamily.forms.element.SelectBox;
import org.riotfamily.forms.element.SwitchElement;
import org.riotfamily.forms.element.TextArea;
import org.riotfamily.forms.element.TextField;
import org.riotfamily.forms.element.TinyMCE;
import org.riotfamily.forms.element.support.DependentElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Node;

/**
 * NamespaceHandler that handles the <code>form</code> namespace as
 * defined in <code>form.xsd</code> which can be found in the same package.
 */
public class FormNamespaceHandler extends GenericNamespaceHandlerSupport {
	
	public void init() {
		registerBeanDefinitionDecoratorForAttribute("bind", new BindingDecorator());
		register("textfield", TextField.class);
		register("password", PasswordField.class);
		register("textarea", TextArea.class);
		register("tinymce", TinyMCE.class);
		register("datepicker", Datepicker.class);
		register("file", FileUpload.class);
		//register("checkbox", Checkbox.class);
		
		
		register("list", ListEditor.class).setDecorator(new PropertyDecorator("itemEditor"));
		register("nested", NestedForm.class).setDecorator(new ListDecorator("childElements"));
		register("dependent", DependentElement.class).setDecorator(new ConstructorArgDecorator());
		register("switch", SwitchElement.class); //TODO Parse nested case elements
		
		register("select", SelectBox.class).setDecorator(new PropertyDecorator("optionsModel"));
		register("radio-group", RadioButtonGroup.class).setDecorator(new PropertyDecorator("optionsModel"));
		register("checkbox-group", CheckBoxGroup.class).setDecorator(new PropertyDecorator("optionsModel"));
	}

	private static class BindingDecorator implements BeanDefinitionDecorator {

		public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext) {
			BeanDefinition binding = new RootBeanDefinition(Binding.class);
			MutablePropertyValues pv = binding.getPropertyValues();
			pv.add("target", node.getNodeValue());
			pv.add("element", definition);
			return new BeanDefinitionHolder(binding, "");
		}

	}
}
