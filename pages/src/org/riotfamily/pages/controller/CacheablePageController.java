package org.riotfamily.pages.controller;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.riotfamily.website.controller.CacheableViewController;
import org.springframework.ui.Model;

public class CacheablePageController extends CacheableViewController {

	@Override
	protected void populateModel(Model model, HttpServletRequest request) {
		populateModel(model, PageResolver.getResolvedPage(request));
	}
	
	protected void populateModel(Model model, Page page) {
	}

}
