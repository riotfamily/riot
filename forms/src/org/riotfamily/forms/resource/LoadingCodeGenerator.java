package org.riotfamily.forms.resource;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class LoadingCodeGenerator implements ResourceVisitor {

	private LinkedHashSet<ScriptResource> scripts = new LinkedHashSet<ScriptResource>();
	
	private LinkedHashSet<StylesheetResource> stylesheets = new LinkedHashSet<StylesheetResource>();

	private LoadingCodeGenerator() {
	}
	
	public static void renderLoadingCode(Collection<FormResource> resources, 
			PrintWriter writer) {
		
		new LoadingCodeGenerator().render(resources, writer);
	}
	
	private void loadResources(Collection<FormResource> resources) {
		if (resources == null) {
			return;
		}
		for (FormResource resource : resources) {
			if (resource != null) {
				resource.accept(this);
			}
		}
	}
	
	public void visitScript(ScriptResource script) {
		if (!scripts.contains(script)) {
			loadResources(script.getDependencies());
			scripts.add(script);
		}
	}

	public void visitStyleSheet(StylesheetResource stylesheet) {
		if (!stylesheets.contains(stylesheet)) {
			stylesheets.add(stylesheet);
		}
	}

	private void render(Collection<FormResource> resources, PrintWriter writer) {
		loadResources(resources);
		for (StylesheetResource stylesheet : stylesheets) {
			writer.print("Resources.loadStyleSheet('");
			writer.print(stylesheet.getUrl());
			writer.print("');");
		}
		
		if (!scripts.isEmpty()) {
			writer.print("Resources.loadScriptSequence([");
			Iterator<ScriptResource> it = scripts.iterator();
			while (it.hasNext()) {
				ScriptResource script = it.next();
				writer.print("{src:'");
				writer.print(script.getUrl());
				writer.print('\'');
				if (script.getTest() != null) {
					writer.print(", test:'");
					writer.print(script.getTest());
					writer.print('\'');
				}
				writer.print("}");
				if (it.hasNext()) {
					writer.print(',');
				}
			}
			writer.print("]);");
		}
	}
	
}
