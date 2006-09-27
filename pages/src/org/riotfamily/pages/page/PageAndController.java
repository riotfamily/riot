package org.riotfamily.pages.page;

import org.springframework.web.servlet.mvc.Controller;

/**
 * Holder for a page and it's associated controller.
 */
public class PageAndController {
	
	private Page page;
	
	private Controller controller;

	public PageAndController(Page page, Controller controller) {
		this.page = page;
		this.controller = controller;
	}

	public Page getPage() {
		return this.page;
	}
		
	public Controller getController() {
		return this.controller;
	}
			
}