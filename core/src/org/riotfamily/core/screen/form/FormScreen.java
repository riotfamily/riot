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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mvc.view.FlashScopeView;
import org.riotfamily.core.dao.InvalidPropertyValueException;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.dao.RiotDaoException;
import org.riotfamily.core.screen.ItemScreen;
import org.riotfamily.core.screen.RiotScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.ScreenLink;
import org.riotfamily.core.screen.ScreenUtils;
import org.riotfamily.core.screen.Screenlet;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.AjaxFormController;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.forms.factory.FormRepository;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

public class FormScreen extends AjaxFormController
		implements ItemScreen, BeanNameAware {

	private static final DefaultTransactionDefinition TX_DEF =
			new DefaultTransactionDefinition(
			TransactionDefinition.PROPAGATION_REQUIRED);
	
	private PlatformTransactionManager transactionManager;

	private FormRepository formRepository;
	
	private String viewName = ResourceUtils.getPath(
			FormScreen.class, "form.ftl");

	private String id;
	
	private String formId;

	private String icon;
	
	private RiotScreen parentScreen;
	
	private Collection<RiotScreen> childScreens;
	
	private Collection<Screenlet> screenlets;

	public FormScreen(FormContextFactory formContextFactory,
			FormRepository formRepository,
			PlatformTransactionManager transactionManager) {
		
		super(formContextFactory);
		this.formRepository = formRepository;
		this.transactionManager = transactionManager;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getFormId() {
		if (formId == null) {
			return getId();
		}
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public void setChildScreens(Collection<RiotScreen> childScreens) {
		this.childScreens = childScreens;
		if (childScreens != null) {
			for (RiotScreen child : childScreens) {
				child.setParentScreen(this);	
			}
		}
	}

	public void setScreenlets(Collection<Screenlet> screenlets) {
		this.screenlets = screenlets;
	}
	
	public void setBeanName(String beanName) {
		if (id == null) {
			id = beanName;
		}
	}
	
	/**
	 * Returns the name of the attribute under which the {@link Form} is
	 * stored in the HTTP session. This implementation returns the
	 * requestURI with the controller's class name as prefix. 
	 */
	@Override
	protected String getSessionAttribute(HttpServletRequest request) {
		return getClass().getName() + ":" + request.getRequestURI();
	}
		
	@Override
	protected Form createForm(HttpServletRequest request) {
		Form form = formRepository.createForm(getFormId());
		form.addButton("save");
		return form;
	}

	@Override
	protected Object getFormBackingObject(HttpServletRequest request) {
		return ScreenContext.get(request).getObject();
	}
	
	@Override
	protected ModelAndView showForm(Form form, HttpServletRequest request,
			HttpServletResponse response) {

		StringWriter sw = new StringWriter();
		renderForm(form, new PrintWriter(sw));
		ModelAndView mv = new ModelAndView(viewName);
		mv.addObject("form", sw.toString());
		
		ScreenContext context = ScreenContext.get(request);
		if (context.getObject() != null) {
			mv.addObject("listStateKey", context.createParentContext().getListStateKey());
			if (childScreens != null) {
				List<ScreenLink> childLinks = Generics.newArrayList();
				for (RiotScreen screen : childScreens) {
					childLinks.add(context.createChildContext(screen).getLink());
				}
				mv.addObject("childLinks", childLinks);
			}
		}
		return mv;
	}

	@Override
	public final ModelAndView handleFormSubmission(Form form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ScreenContext context = ScreenContext.get(request);
		try {
			return handleFormSubmissionInternal(form, request, response, context);
		}
		catch (InvalidPropertyValueException e) {
			form.getErrors().rejectValue(e.getField(), e.getCode(),
					e.getArguments(), e.getMessage());

			return showForm(form, request, response);
		}
		catch (RiotDaoException e) {
			form.getErrors().reject(e.getCode(), e.getArguments(), e.getMessage());
			return showForm(form, request, response);
		}
	}

	protected ModelAndView handleFormSubmissionInternal(Form form, 
			HttpServletRequest request, HttpServletResponse response,
			ScreenContext context) throws Exception {
		
		boolean save = form.isNew();
		saveOrUpdate(form, context);
		removeFormFromSession(request);
		return afterSaveOrUpdate(form, request, context, save);
	}
		
	protected void saveOrUpdate(Form form, ScreenContext context) 
			throws Exception {
		
		TransactionStatus status = transactionManager.getTransaction(TX_DEF);
		RiotDao dao = context.getDao();
		try {
			if (form.isNew()) {
				log.debug("Saving entity ...");
				Object parent = context.getParent();
				Object bean = form.populateBackingObject();				
				dao.save(bean, parent);
				context.setObject(bean);
			}
			else {
				log.debug("Updating entity ...");
				Object bean = form.populateBackingObject();
				dao.update(bean);
			}
		}
		catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		}
		transactionManager.commit(status);
	}

	private boolean hasChildScreens() {
		return getChildScreens() != null && !getChildScreens().isEmpty();
	}
	
	protected ModelAndView afterSaveOrUpdate(
			Form form, HttpServletRequest request,
			ScreenContext context, boolean save) {
		
		ModelAndView mv;
		String focus = request.getParameter("focus");
		if (focus != null || (save && hasChildScreens())) {
			mv = reloadForm(form, context, focus);
		}
		else {
			mv = showParentList(context);
		}
		
		mv.addObject("notification", new FormNotification(form)
				.setIcon("save")
				.setMessageKey("label.form.saved")
				.setDefaultMessage("Your changes have been saved."));
		
		return mv;
	}

	protected ModelAndView showParentList(ScreenContext context) {
		String listUrl = context.createParentContext().getUrl();
		return new ModelAndView(new FlashScopeView(listUrl, true));
	}

	protected ModelAndView reloadForm(Form form, ScreenContext context, String focus) {
		return new ModelAndView(new FlashScopeView(context.getUrl(), true))
				.addObject("focus", focus);
	}

	// -----------------------------------------------------------------------
	// Implementation of the RiotScreen interface
	// -----------------------------------------------------------------------
	
	public Collection<RiotScreen> getChildScreens() {
		return childScreens;
	}

	public String getIcon() {
		return icon;
	}

	public String getId() {
		return id;
	}

	public RiotScreen getParentScreen() {
		return parentScreen;
	}

	public void setParentScreen(RiotScreen parentScreen) {
		if (this.parentScreen == null) {
			this.parentScreen = parentScreen;
		}
	}
	
	public String getTitle(ScreenContext context) {
		if (context.getObject() != null) {
			return ScreenUtils.getLabel(context.getObject(), this);
		}
		Locale locale = RequestContextUtils.getLocale(context.getRequest());
		return getMessageSource().getMessage("label.form.new", null, "New", locale);
	}

	public Collection<Screenlet> getScreenlets() {
		return screenlets;
	}

}
