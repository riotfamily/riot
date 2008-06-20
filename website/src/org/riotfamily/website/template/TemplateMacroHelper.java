package org.riotfamily.website.template;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.servlet.CacheKeyAugmentor;
import org.riotfamily.cachius.servlet.ServletWriterHandler;
import org.riotfamily.common.io.NullWriter;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.util.StringUtils;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class TemplateMacroHelper {

	private CacheService cacheService;
	
	private CacheKeyAugmentor cacheKeyAugmentor;
	
	private HttpServletRequest request;

	private Map<String, String> blocks = Generics.newHashMap();
	
	private boolean childTemplate;
	
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
	
	public class ExtendDirective implements TemplateDirectiveModel {
		public void execute(Environment env, Map params, TemplateModel[] loopVars,
				TemplateDirectiveBody body) throws TemplateException, IOException {
		
			String file = ((SimpleScalar) params.get("file")).getAsString();
			if (!file.startsWith("/")) {
				String dir = "/";
				String path = env.getTemplate().getName();
				int i = path.lastIndexOf('/');
				if (i != -1 && i < path.length() - 1) {
					dir = path.substring(0, i + 1);
				}
				file = StringUtils.cleanPath(dir + file);
			}
			childTemplate = true;
			body.render(new NullWriter());
			childTemplate = false;
			env.include(file, "UTF-8", true);
		}
	}
	
	public class BlockDirective implements TemplateDirectiveModel {
		public void execute(Environment env, Map params, TemplateModel[] loopVars,
				TemplateDirectiveBody body) throws TemplateException, IOException {
			
			String name = ((SimpleScalar) params.get("name")).getAsString();
			
			String cacheKey = ServletUtils.getPathWithinApplication(request) + "#" + name;
			
			if (childTemplate) {
				blocks.put(name, captureBody(body, cacheKey));
			}
			else {
				String content = blocks.get(name);
				if (content != null) {
					env.getOut().write(content);
				}
				else {
					renderBody(body, env.getOut(), cacheKey);
				}
			}
		}
		
		private String captureBody(TemplateDirectiveBody body, String cacheKey) 
				throws TemplateException, IOException {
			
			StringWriter sw = new StringWriter();
			renderBody(body, sw, cacheKey);
			return sw.toString();
		}
		
		private void renderBody(TemplateDirectiveBody body, Writer out, 
				String cacheKey) throws TemplateException, IOException {
			
			if (body != null) {
				try {
					cacheService.handle(new BodyCacheHandler(body, out, cacheKey));
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
			
			public BodyCacheHandler(TemplateDirectiveBody body, Writer out, String cacheKey) {
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
	
}
