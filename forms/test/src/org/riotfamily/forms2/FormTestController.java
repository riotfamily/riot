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
package org.riotfamily.forms2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.riotfamily.forms2.base.Binding;
import org.riotfamily.forms2.base.FormState;
import org.riotfamily.forms2.client.Action;
import org.riotfamily.forms2.client.ClientEvent;
import org.riotfamily.forms2.element.FileUpload;
import org.riotfamily.forms2.element.ListEditor;
import org.riotfamily.forms2.element.RadioButtonGroup;
import org.riotfamily.forms2.element.TextField;
import org.riotfamily.forms2.option.StaticOptionsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/form")
public class FormTestController implements FormSubmissionHandler {

	private Logger log = LoggerFactory.getLogger(FormTestController.class);
	
	private Form form;
	
	private SubmitButton button;
	
	@Resource
	private Map<String, Object> backingObject;
	
	public FormTestController() {
		form = new Form();
		form.add(new Binding("text", new TextField()));
		form.add(new Binding("list", new ListEditor(new TextField())));

		RadioButtonGroup radioGroup = new RadioButtonGroup();
		radioGroup.setOptionsModel(new StaticOptionsModel("foo", "bar"));
		form.add(new Binding("radio", radioGroup));
		
		form.add(new Binding("file", new FileUpload()));
		
		button = new SubmitButton("Save", this);
		form.add(button);
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public void renderForm(HttpSession session, ModelMap model, Writer out) throws IOException {
		FormState formState = form.createState(backingObject, HashMap.class);
		formState.put(session); //REVISIT Move to Form.java
		out.write("<html><body>");
		//out.write("<script src=\"/resources/jquery/jquery.js\" />");
		out.write(form.render(formState));
		//out.write(button.render(formState));
		out.write("<a href=\"shutdown\">Shutdown</a>");
		out.write("</body></html>");
		//model.put("form", form.render(formState));
		//model.put("button1", button1.render(formState));
	}
		
	@RequestMapping(method=RequestMethod.GET, headers="X-Requested-With=XMLHttpRequest")
	public @ResponseBody List<Action> handleEvent(HttpSession session, ClientEvent event) throws Exception {
		
		FormState formState = form.getState(session, event);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new ObjectOutputStream(out).writeObject(formState);
		formState = (FormState) new ObjectInputStream(new ByteArrayInputStream(out.toByteArray())).readObject();
		formState.put(session);
		
		return form.dispatchEvent(session, event);
	}
		
	@RequestMapping(method=RequestMethod.POST)
	public void handleUpload(HttpSession session, ClientEvent event, Writer out) throws IOException {
		String json = new ObjectMapper().writeValueAsString(form.dispatchEvent(session, event));
		out.write("<html><body><script>parent.riot.form.processActions(");
		out.write(json);
		out.write(");</script></body></html>");
	}
	
	public void onSubmit(FormState state) {
		form.populate(backingObject, state);
		log.info("New value: {}", backingObject);
	}
	
}
