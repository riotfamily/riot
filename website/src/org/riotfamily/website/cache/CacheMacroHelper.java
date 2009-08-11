/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.website.cache;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheHandler;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.CachiusContext;
import org.riotfamily.cachius.servlet.CacheKeyAugmentor;
import org.riotfamily.cachius.servlet.ServletWriterHandler;
import org.riotfamily.common.util.FormatUtils;

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
	
	public void tag(String name) {
		CachiusContext.tag(name);
	}

	public void preventCaching() {
		CachiusContext.preventCaching();
	}
	
	public class BlockDirective implements TemplateDirectiveModel {
		
		@SuppressWarnings("unchecked")
		public void execute(Environment env, Map params, TemplateModel[] loopVars,
				TemplateDirectiveBody body) throws TemplateException, IOException {
			
			boolean bypass = getBooleanParam(params, "bypass", false);
			String cacheKey = null;
			long ttl = CacheHandler.CACHE_ETERNALLY;
			
			if (!bypass) {
				cacheKey = getStringParam(params, "key", null);
				if (cacheKey == null) {
					cacheKey = request.getRequestURL()
							.append('#')
							.append(getRequiredStringParam(params, "key", env))
							.toString();
				}
				String s = getStringParam(params, "ttl", null);
				if (s != null) {
					ttl = FormatUtils.parseMillis(s);
				}
			}			
			renderBody(body, env.getOut(), cacheKey, ttl, env);
		}
		
		
		private void renderBody(TemplateDirectiveBody body, Writer out, 
				String cacheKey, long ttl, Environment env) 
				throws TemplateException, IOException {
			
			if (body != null) {
				try {
					BodyCacheHandler handler = new BodyCacheHandler(body, out, cacheKey, ttl);
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
			
			private long ttl;
			
			public BodyCacheHandler(TemplateDirectiveBody body, Writer out, 
					String cacheKey, long ttl) {
				
				super(request, out, cacheKeyAugmentor);
				this.body = body;
				this.cacheKey = cacheKey;
				this.ttl = ttl;
			}

			@Override
			public long getTimeToLive() {
				return ttl;
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
