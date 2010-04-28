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
package org.riotfamily.forms.integration.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormSubmissionHandler;
import org.riotfamily.forms.SubmitButton;
import org.riotfamily.forms.base.Binding;
import org.riotfamily.forms.base.FormState;
import org.riotfamily.forms.client.Action;
import org.riotfamily.forms.client.ClientEvent;
import org.riotfamily.forms.element.Datepicker;
import org.riotfamily.forms.element.FileUpload;
import org.riotfamily.forms.element.ListEditor;
import org.riotfamily.forms.element.NestedForm;
import org.riotfamily.forms.element.RadioButtonGroup;
import org.riotfamily.forms.element.SelectBox;
import org.riotfamily.forms.element.SwitchElement;
import org.riotfamily.forms.element.TextArea;
import org.riotfamily.forms.element.TextField;
import org.riotfamily.forms.element.support.DependentElement;
import org.riotfamily.forms.option.DependentOptionsModel;
import org.riotfamily.forms.option.OptionsModel;
import org.riotfamily.forms.option.StaticOptionsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public abstract class AbstractFormTestController implements FormSubmissionHandler {

	private Logger log = LoggerFactory.getLogger(AbstractFormTestController.class);
	
	private Form form;
	
	private SubmitButton button;
	
	public AbstractFormTestController() {
		form = new Form();
		form.add(new Binding("text", new TextField()));
		form.add(new Binding("date", new Datepicker()));
		form.add(new Binding("list", new ListEditor(new TextField())));

		OptionsModel options = new StaticOptionsModel("foo", "bar");
		
		RadioButtonGroup radioGroup = new RadioButtonGroup();
		radioGroup.setOptionsModel(options);
		form.add(new Binding("radio", radioGroup));
		
		SelectBox selectBox = new SelectBox();
		selectBox.setOptionsModel(options);
		form.add(new Binding("select", selectBox));

		form.add(new DependentElement(new Binding("select2", new SelectBox(new DependentOptionsModel<String>() {
			@Override
			protected Iterable<?> getOptions(String s) {
				return Collections.singletonList(s);
			}
		}))));
		
		form.add(new Binding("file", new FileUpload()));
		//form.add(new Binding("tinymce", new TinyMCE()));

		NestedForm nested = new NestedForm();
		initNestedForm(nested);
		nested.add(new Binding("text", new TextField()));
		form.add(new Binding("nested", nested));
		
		form.add(new SwitchElement("discriminator")
			.addCase("1", new Binding("case1", new TextField()))
			.addCase("2", new Binding("case2", new TextArea()))
			);
		
		button = new SubmitButton("Save", this);
		form.add(button);
	}
	
	protected void initNestedForm(NestedForm nested) {
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public void renderForm(HttpSession session, ModelMap model, Writer out) throws IOException {
		FormState formState = form.createState(getBackingObject(), HashMap.class);
		formState.put(session); //REVISIT Move to Form.java
		out.write("<html><body>");
		//out.write("<script src=\"/resources/jquery/jquery.js\" />");
		out.write(form.render(formState));
		//out.write(button.render(formState));
		out.write("<a href=\"../shutdown\">Shutdown</a>");
		out.write("</body></html>");
		//model.put("form", form.render(formState));
		//model.put("button1", button1.render(formState));
	}
		
	@RequestMapping(method=RequestMethod.GET, headers="X-Requested-With=XMLHttpRequest")
	public @ResponseBody List<Action> handleEvent(HttpSession session, ClientEvent event) throws Exception {
		serializeState(session, event);
		return form.dispatchEvent(session, event);
	}

	private void serializeState(HttpSession session, ClientEvent event)
			throws IOException, ClassNotFoundException {
		
		FormState formState = form.getState(session, event);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new ObjectOutputStream(out).writeObject(formState);
		formState = (FormState) new ObjectInputStream(new ByteArrayInputStream(out.toByteArray())).readObject();
		formState.put(session);
	}
		
	@RequestMapping(method=RequestMethod.POST)
	public void handleUpload(HttpSession session, ClientEvent event, Writer out) throws IOException {
		String json = new ObjectMapper().writeValueAsString(form.dispatchEvent(session, event));
		out.write("<html><body><script>parent.riot.form.processActions(");
		out.write(json);
		out.write(");</script></body></html>");
	}
	
	public String onSubmit(FormState state) {
		form.populate(getBackingObject(), state);
		log.info("New value: {}", getBackingObject());
		return null;
	}

	protected abstract Object getBackingObject();
	
}
