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
package org.riotfamily.core.screen.form;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.view.FlashScopeView;
import org.riotfamily.core.dao.InvalidPropertyValueException;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.dao.RiotDaoException;
import org.riotfamily.core.screen.ItemScreen;
import org.riotfamily.core.screen.RiotScreen;
import org.riotfamily.core.screen.ScreenContext;
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
	
	private List<RiotScreen> childScreens;
	
	private List<Screenlet> screenlets;

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

	public void setScreenlets(List<Screenlet> screenlets) {
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
			//REVISIT
			mv.addObject("listStateKey", context.createParentContext().getListStateKey());
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
				.setMessage("Your changes have been saved."));
		
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
		this.parentScreen = parentScreen;
	}
	
	public String getTitle(ScreenContext context) {
		if (context.getObject() != null) {
			return ScreenUtils.getLabel(context.getObject(), this);
		}
		return "New";
	}

	public List<Screenlet> getScreenlets() {
		return screenlets;
	}

}
