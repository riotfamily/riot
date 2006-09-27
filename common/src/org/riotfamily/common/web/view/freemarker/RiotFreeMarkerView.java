package org.riotfamily.common.web.view.freemarker;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.view.ViewContext;
import org.springframework.beans.BeansException;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

/**
 * Differences to Spring's FreeMarkerView:
 * <ul>
 * <li>Model attributes may override attributes from the request or the session</li>
 * <li>The HttpServletRequest is exposed under the key "request"</li>
 * <li>The template URL is exposed under the key "template"</li>
 * <li>The model is only exposed to the request if freeMarkerServletMode is enabled</li>
 * </ul> 
 */
public class RiotFreeMarkerView extends FreeMarkerView {	
	
	public static final String REQUEST_ATTRIBUTE = "request";
	
	public static final String TEMPLATE_ATTRIBUTE = "template";
		
	public static final String MODEL_ATTRIBUTE = 
			RiotFreeMarkerView.class.getName() + ".model";
	
	private boolean allowModelOverride;
	
	private boolean freeMarkerServletMode;
	
	public void setAllowModelOverride(boolean allowModelOverride) {
		this.allowModelOverride = allowModelOverride;
	}
	
	public void setFreeMarkerServletMode(boolean freeMarkerServletMode) {
		this.freeMarkerServletMode = freeMarkerServletMode;
	}
	
	protected void initApplicationContext() throws BeansException {
		super.initApplicationContext();
		/*
		Configuration cfg = getConfiguration();
		TemplateLoader originalLoader = cfg.getTemplateLoader();
		TemplateLoader macroLoader = new ClassTemplateLoader(getClass(), 
				"/org/riotfamily/pages/runtime/macros");
		
		cfg.setTemplateLoader(new MultiTemplateLoader(
				new TemplateLoader[] { macroLoader, originalLoader }));
		
		cfg.addAutoImport("riot", "/riot.ftl");
		*/
	}

	public void render(Map model, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
	
		if (allowModelOverride) {
			Map emptyModel = new HashMap();
			emptyModel.put(MODEL_ATTRIBUTE, model);
			model = emptyModel;
		}
		super.render(model, request, response);
	}
	
	private void unwrapModel(Map model) {
		Map originalModel = (Map) model.remove(MODEL_ATTRIBUTE);
		if (originalModel != null) {
			model.putAll(originalModel);
		}
	}
	
	protected void renderMergedTemplateModel(final Map model, 
			final HttpServletRequest request, 
			final HttpServletResponse response) 
			throws Exception {

		unwrapModel(model);
		ViewContext.execute(request, response, new ViewContext.Callback() {
			public void doInContext() throws Exception {
				doRender(model, request, response);
			}
		});
	}
	
	protected void doRender(Map model, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		model.put(REQUEST_ATTRIBUTE, request);
		model.put(TEMPLATE_ATTRIBUTE, getUrl());
		
		if (freeMarkerServletMode) {
			super.doRender(model, request, response);
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Rendering FreeMarker template [" + getUrl() 
						+ "] in FreeMarkerView '" + getBeanName() + "'");
			}
			Locale locale = RequestContextUtils.getLocale(request);
			processTemplate(getTemplate(locale), model, response);
		}
	}

}
