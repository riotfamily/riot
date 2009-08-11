package org.riotfamily.website.txt2img;

import javax.servlet.http.HttpServletRequest;

public class Txt2ImgMacroHelper {

	private ButtonService buttonService;
	
	private HttpServletRequest request;
	
	
	public Txt2ImgMacroHelper(ButtonService buttonService,
			HttpServletRequest request) {
		
		this.buttonService = buttonService;
		this.request = request;
	}

	public String getButtonStyle(String id, String label) throws Exception {
		return buttonService.getInlineStyle(id, label, request);
	}
}
