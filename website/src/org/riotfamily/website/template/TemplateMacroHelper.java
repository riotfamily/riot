package org.riotfamily.website.template;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheItem;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.TaggingContext;
import org.riotfamily.cachius.servlet.CacheKeyAugmentor;
import org.riotfamily.cachius.servlet.ServletWriterHandler;
import org.riotfamily.common.io.NullWriter;
import org.riotfamily.common.util.Generics;
import org.springframework.util.StringUtils;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class TemplateMacroHelper {

	private CacheService cacheService;
	
	private CacheKeyAugmentor cacheKeyAugmentor;
	
	private HttpServletRequest request;

	private List<TemplateDefinition> definitions = Generics.newArrayList();
	
	private TemplateDefinition currentTemplate;
	
	private Map<String, Block> blocks = Generics.newHashMap();
	
	private boolean nestedBlock = false;
	
	private TemplateDirectiveModel rootDirective = new RootDirective();
	
	private TemplateDirectiveModel extendDirective = new ExtendDirective();
	
	private TemplateDirectiveModel blockDirective = new BlockDirective();
	
	public TemplateMacroHelper(CacheService cacheService,
			CacheKeyAugmentor cacheKeyAugmentor,
			HttpServletRequest request) {
		
		this.cacheService = cacheService;
		this.cacheKeyAugmentor = cacheKeyAugmentor;
		this.request = request;
	}

	public TemplateDirectiveModel getRootDirective() {
		return rootDirective;
	}
	
	public TemplateDirectiveModel getExtendDirective() {
		return extendDirective;
	}
	
	public TemplateDirectiveModel getBlockDirective() {
		return blockDirective;
	}
	
	public boolean blockExists(String name) {
		return blocks.get(name) != null;
	}
	
	
	
	public class RootDirective implements TemplateDirectiveModel {
	
		@SuppressWarnings("unchecked")
		public void execute(Environment env, Map params, TemplateModel[] loopVars,
				TemplateDirectiveBody body) throws TemplateException, IOException {
			
			definitions.add(new TemplateDefinition(body, env));
		}
	}
	
	
	public class ExtendDirective implements TemplateDirectiveModel {
		
		@SuppressWarnings("unchecked")
		public void execute(Environment env, Map params, TemplateModel[] loopVars,
				TemplateDirectiveBody body) throws TemplateException, IOException {
		
			String file = getRequiredStringParam(params, "file", env);
			if (!file.startsWith("/")) {
				String dir = "/";
				String path = env.getTemplate().getName();
				int i = path.lastIndexOf('/');
				if (i != -1 && i < path.length() - 1) {
					dir = path.substring(0, i + 1);
				}
				file = StringUtils.cleanPath(dir + file);
			}
			
			boolean deepest = definitions.isEmpty();
			definitions.add(new TemplateDefinition(body, env));
			
			env.include(file, "UTF-8", true);
			
			if (deepest) {
				Writer out = new NullWriter();
				Iterator<TemplateDefinition> it = definitions.iterator();
				while (it.hasNext()) {
					currentTemplate = it.next();
					it.remove();
					if (!it.hasNext()) {
						out = env.getOut();
					}
					currentTemplate.render(out);
				}
			}
		}
	}
	
	public class BlockDirective implements TemplateDirectiveModel {
		
		@SuppressWarnings("unchecked")
		public void execute(Environment env, Map params, TemplateModel[] loopVars,
				TemplateDirectiveBody body) throws TemplateException, IOException {
			
			String name = getRequiredStringParam(params, "name", env);
			String cacheKey = null;
			
			boolean cache = getBooleanParam(params, "cache", true);
			if (cache) {
				cacheKey = getStringParam(params, "cacheKey",
						request.getRequestURL().append('#').append(name).toString());
			}
			
			Block block = blocks.get(name);
			if (nestedBlock || definitions.isEmpty()) {
				//Render
				if (block != null) {
					block.render(env.getOut());
				}
				else {
					renderBody(body, env.getOut(), cacheKey, env);
				}
			}
			else {
				//Capture
				if (block == null) {
					boolean nested = nestedBlock;
					nestedBlock = true;
					block = captureBody(body, cacheKey, env);
					nestedBlock = nested;
					blocks.put(name, block);
				}
			}
		}
		
		private Block captureBody(TemplateDirectiveBody body, String cacheKey, 
				Environment env) throws TemplateException, IOException {
			
			StringWriter sw = new StringWriter();
			TaggingContext ctx = renderBody(body, sw, cacheKey, env);
			return new Block(sw.toString(), ctx);
		}
		
		private TaggingContext renderBody(TemplateDirectiveBody body, Writer out, 
				String cacheKey, Environment env) 
				throws TemplateException, IOException {
			
			if (body != null) {
				try {
					BodyCacheHandler handler = new BodyCacheHandler(body, out, cacheKey);
					cacheService.handle(handler);
					return handler.getTaggingContext();
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
			return null;
		}
		
		private class BodyCacheHandler extends ServletWriterHandler {

			private TemplateDirectiveBody body;
			
			private String cacheKey;
			
			private TaggingContext taggingContext;
			
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
				currentTemplate.requestTemplate();
				body.render(out);
			}
			
			@Override
			protected void postProcess(CacheItem cacheItem) throws Exception {
				taggingContext = TaggingContext.getContext();
			}
			
			public TaggingContext getTaggingContext() {
				return taggingContext;
			}
			
		}
	}
		
	private static class TemplateDefinition {
		
		private TemplateDirectiveBody body;
		
		private Environment env;
		
		private String templateName;

		public TemplateDefinition(TemplateDirectiveBody body,
				Environment env) {
			
			this.body = body;
			this.env = env;
			this.templateName = env.getTemplate().getName();
		}
		
		public void render(Writer out) throws TemplateException, IOException {
			body.render(out);
		}
		
		public void requestTemplate() throws IOException {
			env.getConfiguration().getTemplate(templateName);
		}
	}
	
	private static class Block {
		
		private String content;
		
		private TaggingContext context;

		public Block(String content, TaggingContext context) {
			this.content = content;
			this.context = context;
		}

		public void render(Writer out) throws IOException {
			TaggingContext.inheritFrom(context);
			out.write(content);
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
