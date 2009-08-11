package org.riotfamily.website.txt2img;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.view.MacroHelperFactory;

public class Txt2ImgMacroHelperFactory implements MacroHelperFactory {

	private ButtonService buttonService;
	
	public Txt2ImgMacroHelperFactory(ButtonService buttonService) {
		this.buttonService = buttonService;
	}

	public Object createMacroHelper(HttpServletRequest request,
			HttpServletResponse response, Map<String, ?> model) {

		return new Txt2ImgMacroHelper(buttonService, request);
	}

}
