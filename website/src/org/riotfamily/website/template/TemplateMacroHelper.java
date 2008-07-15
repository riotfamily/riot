package org.riotfamily.website.template;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheItem;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.TaggingContext;
import org.riotfamily.cachius.servlet.CacheKeyAugmentor;
import org.riotfamily.cachius.servlet.ServletWriterHandler;
import org.riotfamily.common.io.NullWriter;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.util.ServletUtils;
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

	private Map<String, Block> blocks = Generics.newHashMap();
	
	private boolean capture;
	
	private TemplateDirectiveModel extendDirective = new ExtendDirective();
	
	private TemplateDirectiveModel blockDirective = new BlockDirective();
	
	public TemplateMacroHelper(CacheService cacheService,
			CacheKeyAugmentor cacheKeyAugmentor,
			HttpServletRequest request) {
		
		this.cacheService = cacheService;
		this.cacheKeyAugmentor = cacheKeyAugmentor;
		this.request = request;
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
	
	private static String getRequiredStringParam(Map<String, ?> params, String name, Environment env) 
			throws TemplateException {
		
		Object value = params.get(name);
		if (value instanceof SimpleScalar) {
			return ((SimpleScalar) value).getAsString();
		}
		throw new TemplateException("Missing parameter: " + name, env);
	}
	
	private static String getStringParam(Map<String, ?> params, String name, String defaultValue) {
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
			if (body != null) {
				capture = true;
				body.render(new NullWriter());
				capture = false;
			}
			env.include(file, "UTF-8", true);
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
						ServletUtils.getPathWithinApplication(request) + "#" + name);
			}
			
			Block block = blocks.get(name);
			if (capture) {
				if (block == null) {
					capture = false;
					block = captureBody(body, cacheKey, env);
					capture = true;
					blocks.put(name, block);
				}
			}
			else {
				if (block != null) {
					block.propagateTagsAndFiles();
					env.getOut().write(block.getContent());
				}
				else {
					renderBody(body, env.getOut(), cacheKey, env);
				}
			}
		}
		
		private Block captureBody(TemplateDirectiveBody body, String cacheKey, 
				Environment env) throws TemplateException, IOException {
			
			StringWriter sw = new StringWriter();
			CacheItem cacheItem = renderBody(body, sw, cacheKey, env);
			return new Block(sw.toString(), cacheItem);
		}
		
		private CacheItem renderBody(TemplateDirectiveBody body, Writer out, 
				String cacheKey, Environment env) 
				throws TemplateException, IOException {
			
			if (body != null) {
				try {
					BodyCacheHandler handler = new BodyCacheHandler(body, out, cacheKey, env);
					cacheService.handle(handler);
					return handler.getCacheItem();
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
			
			private Environment env;
			
			private CacheItem cacheItem;
			
			public BodyCacheHandler(TemplateDirectiveBody body, Writer out, 
					String cacheKey, Environment env) {
				
				super(request, out, cacheKeyAugmentor);
				this.body = body;
				this.cacheKey = cacheKey;
				this.env = env;
			}

			@Override
			protected String getCacheKeyInternal() {
				return cacheKey;
			}
			
			protected void render(Writer out) throws Exception {
				env.getConfiguration().getTemplate(env.getTemplate().getName());
				body.render(out);
			}
			
			@Override
			protected void postProcess(CacheItem cacheItem) throws Exception {
				// Remember the CacheItem so that we can access it later 
				this.cacheItem = cacheItem;
			}
			
			@Override
			protected void writeCacheItemInternal(CacheItem cacheItem) throws IOException {
				this.cacheItem = cacheItem;
				super.writeCacheItemInternal(cacheItem);
			}
			
			public CacheItem getCacheItem() {
				return cacheItem;
			}
		}
	}
	
	private static class Block {
		
		private String content;
		
		private Set<String> tags;
		
		private Set<File> involvedFiles;

		public Block(String content, CacheItem cacheItem) {
			this.content = content;
			if (cacheItem != null) {
				tags = cacheItem.getTags();
				involvedFiles = cacheItem.getInvolvedFiles();
			}
		}
		
		public String getContent() {
			return content;
		}

		public void propagateTagsAndFiles() {
			TaggingContext ctx = TaggingContext.getContext();
			if (ctx != null) {
				ctx.addTags(tags);
				ctx.addInvolvedFiles(involvedFiles);
			}
		}
		
	}
	
}
