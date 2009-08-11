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
package org.riotfamily.website.template;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.riotfamily.common.io.NullWriter;
import org.riotfamily.common.util.Generics;
import org.springframework.util.StringUtils;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class TemplateMacroHelper {
	
	private List<TemplateDefinition> definitions = Generics.newArrayList();
	
	private TemplateDefinition currentTemplate;
	
	private Map<String, String> blocks = Generics.newHashMap();
	
	private boolean nestedBlock = false;
	
	private TemplateDirectiveModel rootDirective = new RootDirective();
	
	private TemplateDirectiveModel extendDirective = new ExtendDirective();
	
	private TemplateDirectiveModel blockDirective = new BlockDirective();
	
	public TemplateMacroHelper() {
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
			
			TemplateDefinition root = new TemplateDefinition(body, env);			
			if (definitions.isEmpty()) {
				// Not extended, root template directly requested
				currentTemplate = root;
				body.render(env.getOut());
			}
			else {
				definitions.add(root);
			}
		}
	}
	
	
	public class ExtendDirective implements TemplateDirectiveModel {
		
		@SuppressWarnings("unchecked")
		public void execute(Environment env, Map params, TemplateModel[] loopVars,
				TemplateDirectiveBody body) throws TemplateException, IOException {
		
			String file = getRequiredStringParam(params, "file", env);
			file = resolveTemplate(env, file);
			
			boolean deepest = definitions.isEmpty();
			definitions.add(new TemplateDefinition(body, env));
			
			env.include(file, "UTF-8", true);
			
			if (deepest) {
				// Template was not extended
				Writer out = new NullWriter();
				// Go from deepest up to root template
				Iterator<TemplateDefinition> it = definitions.iterator();
				while (it.hasNext()) {
					currentTemplate = it.next();
					it.remove();
					if (!it.hasNext()) {
						// Reached the root template, write to output
						out = env.getOut();
					}
					currentTemplate.render(out);
				}
			}
		}

		private String resolveTemplate(Environment env, String file) {
			if (!file.startsWith("/")) {
				String dir = "/";
				String path = env.getTemplate().getName();
				int i = path.lastIndexOf('/');
				if (i != -1 && i < path.length() - 1) {
					dir = path.substring(0, i + 1);
				}
				file = StringUtils.cleanPath(dir + file);
			}
			return file;
		}
	}
	
	public class BlockDirective implements TemplateDirectiveModel {
		
		@SuppressWarnings("unchecked")
		public void execute(Environment env, Map params, TemplateModel[] loopVars,
				TemplateDirectiveBody body) throws TemplateException, IOException {
			
			String name = getRequiredStringParam(params, "name", env);
			
			String block = blocks.get(name);
			if (nestedBlock || definitions.isEmpty()) {
				//Render
				if (block != null) {
					env.getOut().write(block);
				}
				else {
					renderBody(body, env.getOut(), env);
				}
			}
			else {				
				if (block == null) {
					//Capture, Block has not been processed by a deeper template 
					boolean nested = nestedBlock;
					nestedBlock = true;
					block = captureBody(body, env);
					nestedBlock = nested;
					blocks.put(name, block);
				}
			}
		}
		
		private String captureBody(TemplateDirectiveBody body,  
				Environment env) throws TemplateException, IOException {
			
			StringWriter sw = new StringWriter();
			renderBody(body, sw, env);
			return sw.toString();
		}
		
		private void renderBody(TemplateDirectiveBody body, Writer out, 
				Environment env) throws TemplateException, IOException {
			
			if (body != null) {
				try {
					currentTemplate.requestTemplate();
					body.render(out);
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
	
	// Static utility methods -------------------------------------------------
	
	private static String getRequiredStringParam(Map<String, ?> params, 
			String name, Environment env) throws TemplateException {

		Object value = params.get(name);
		if (value instanceof SimpleScalar) {
			return ((SimpleScalar) value).getAsString();
		}
		throw new TemplateException("Missing parameter: " + name, env);
	}

}
