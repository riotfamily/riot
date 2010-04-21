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
package org.riotfamily.core.screen.form;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.riotfamily.common.util.ExceptionUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.core.runtime.RiotRuntime;
import org.riotfamily.core.screen.AbstractRiotScreen;
import org.riotfamily.core.screen.GroupScreen;
import org.riotfamily.core.screen.ItemScreen;
import org.riotfamily.core.screen.Notification;
import org.riotfamily.core.screen.RiotScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.ScreenContextHolder;
import org.riotfamily.core.screen.ScreenLink;
import org.riotfamily.core.screen.ScreenUtils;
import org.riotfamily.forms2.Form;
import org.riotfamily.forms2.FormSubmissionHandler;
import org.riotfamily.forms2.SubmitButton;
import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.base.FormState;
import org.riotfamily.forms2.client.Action;
import org.riotfamily.forms2.client.ClientEvent;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

public class FormScreen extends AbstractRiotScreen implements ItemScreen, BeanNameAware, FormSubmissionHandler {

	private static final DefaultTransactionDefinition TX_DEF =
			new DefaultTransactionDefinition(
			TransactionDefinition.PROPAGATION_REQUIRED);
	
	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private RiotRuntime riotRuntime;

	@Autowired
	private MessageSource messageSoruce;
	
	private Form form = new Form();
	
	private Collection<RiotScreen> childScreens;
	
	private String viewName = ResourceUtils.getPath(FormScreen.class, "form.ftl");

	public void setElements(List<Object> elements) {
		addElements(elements);
		form.add(new SubmitButton("Save", this));
	}
	
	@SuppressWarnings("unchecked")
	private void addElements(Collection<Object> elements) {
		for (Object element : elements) {
			if (element instanceof Element) {
				form.add((Element) element);
			}
			else if (element instanceof Collection) {
				addElements((Collection) element);
			}
			else {
				throw new IllegalArgumentException("Expected either an Element or a Collection: " + element);
			}
		}
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public void setChildScreens(Collection<RiotScreen> childScreens) {
		this.childScreens = childScreens;
		if (childScreens != null) {
			for (RiotScreen child : childScreens) {
				child.setParentScreen(this);	
			}
		}
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String renderForm(HttpServletRequest request, ModelMap model) throws IOException {
		ScreenContext context = ScreenContextHolder.get();
		FormState formState = form.createState(context.getObject(), context.getDao().getEntityClass());
		formState.setContextPath(request.getContextPath());
		formState.setResourcePath(riotRuntime.getResourcePath());
		formState.put(request.getSession()); //REVISIT Move to Form.java
		model.put("form", form.render(formState));
		//model.put("button", button.render(formState));
		exposeChildLinks(model);
		return viewName;
	}
	
	private void exposeChildLinks(ModelMap model) {
		ScreenContext context = ScreenContextHolder.get();
		if (context.getObject() != null) {
			if (childScreens != null) {
				List<ScreenLink> childLinks = Generics.newArrayList();
				for (RiotScreen screen : childScreens) {
					childLinks.add(context.createChildContext(screen).getLink());
				}
				model.put("childLinks", childLinks);
			}
		}
		model.put("listStateKey", ScreenUtils.getListScreen(this).getId());
	}
	
	@RequestMapping(method=RequestMethod.GET, headers="X-Requested-With=XMLHttpRequest")
	public @ResponseBody List<Action> handleEvent(HttpSession session, ClientEvent event) {
		return form.dispatchEvent(session, event);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public void handleUpload(HttpSession session, ClientEvent event, Writer out) throws IOException {
		String json = new ObjectMapper().writeValueAsString(form.dispatchEvent(session, event));
		out.write("<html><body><script>parent.riot.form.processActions(");
		out.write(json);
		out.write(");</script></body></html>");
	}

	public String onSubmit(FormState state) {
		String result;
		ScreenContext context = ScreenContextHolder.get();
		TransactionStatus status = transactionManager.getTransaction(TX_DEF);
		try {
			Object backingObject = context.getObject();
			Object entity = form.populate(backingObject, state);
			context.getDao().save(entity, context.getParent());
			result = new Notification(messageSoruce, riotRuntime.getResourcePath())
				.setIcon("save")
				.setMessageKey("label.form.saved")
				.setDefaultMessage("Your changes have been saved.")
				.toScript();
			
			if (context.getObjectId() == null) {
				result += String.format("setObjectId('%s');", context.getDao().getObjectId(entity));
			}
		}
		catch (Throwable t) {
			transactionManager.rollback(status);
			throw ExceptionUtils.wrapThrowable(t);
		}
		transactionManager.commit(status);
		return result;
	}

	@Override
	public Collection<RiotScreen> getChildScreens() {
		return childScreens;
	}
	
	@Override
	public String getTitle(ScreenContext context) {
		if (getParentScreen() instanceof GroupScreen) {
			return super.getTitle(context);
		}
		if (context.getObject() != null) {
			return ScreenUtils.getLabel(context.getObject(), this);
		}
		Locale locale = RequestContextUtils.getLocale(context.getRequest());
		return getMessageSource().getMessage("label.form.new", null, "New", locale);
	}

}
