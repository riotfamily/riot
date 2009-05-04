package org.riotfamily.website.cache;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.servlet.CacheKeyAugmentor;
import org.riotfamily.cachius.servlet.ServletWriterHandler;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class CacheMacroHelper {

	private CacheService cacheService;
	
	private CacheKeyAugmentor cacheKeyAugmentor;
	
	private HttpServletRequest request;

	private TemplateDirectiveModel blockDirective = new BlockDirective();
	
	public CacheMacroHelper(CacheService cacheService,
			CacheKeyAugmentor cacheKeyAugmentor,
			HttpServletRequest request) {
		
		this.cacheService = cacheService;
		this.cacheKeyAugmentor = cacheKeyAugmentor;
		this.request = request;
	}

	public TemplateDirectiveModel getBlockDirective() {
		return blockDirective;
	}
	
	
	public class BlockDirective implements TemplateDirectiveModel {
		
		@SuppressWarnings("unchecked")
		public void execute(Environment env, Map params, TemplateModel[] loopVars,
				TemplateDirectiveBody body) throws TemplateException, IOException {
			
			String name = getRequiredStringParam(params, "name", env);
			
			boolean cache = getBooleanParam(params, "cache", true);
			String cacheKey = null;
			
			if (cache) {
				cacheKey = getStringParam(params, "cacheKey", null);
			}			
			
			if (cache && cacheKey == null) {
				cacheKey = request.getRequestURL().append('#').append(name).toString();
			}
			
			renderBody(body, env.getOut(), cacheKey, env);
		}
		
		
		private void renderBody(TemplateDirectiveBody body, Writer out, 
				String cacheKey, Environment env) 
				throws TemplateException, IOException {
			
			if (body != null) {
				try {
					BodyCacheHandler handler = new BodyCacheHandler(body, out, cacheKey);
					cacheService.handle(handler);
				}
				catch (TemplateException e) {
					throw e;
				}
				catch (IOException e) {
					throw e;
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		private class BodyCacheHandler extends ServletWriterHandler {

			private TemplateDirectiveBody body;
			
			private String cacheKey;
					
			public BodyCacheHandler(TemplateDirectiveBody body, Writer out, 
					String cacheKey) {
				
				super(request, out, cacheKeyAugmentor);
				this.body = body;
				this.cacheKey = cacheKey;
			}

			@Override
			protected String getCacheKeyInternal() {
				return cacheKey;
			}
			
			protected void render(Writer out) throws Exception {
				body.render(out);
			}
						
		}
	}
		
	// Static utility methods -------------------------------------------------
	
	private static String getRequiredStringParam(Map<String, ?> params, 
			String name, Environment env) throws TemplateException {

		Object value = params.get(name);
		if (value instanceof SimpleScalar) {
			return ((SimpleScalar) value).getAsString();
		}
		throw new TemplateException("Missing parameter: " + name, env);
	}
	
	private static String getStringParam(Map<String, ?> params, String name, 
			String defaultValue) {
		
		Object value = params.get(name);
		if (value instanceof SimpleScalar) {
			return ((SimpleScalar) value).getAsString();
		}
		return defaultValue;
	}
	
	private static boolean getBooleanParam(Map<String, ?> params, String name, 
			boolean defaultValue) throws TemplateModelException {
	
		Object value = params.get(name);
		if (value instanceof TemplateBooleanModel) {
			return ((TemplateBooleanModel) value).getAsBoolean();
		}
		return defaultValue;
	}

}
