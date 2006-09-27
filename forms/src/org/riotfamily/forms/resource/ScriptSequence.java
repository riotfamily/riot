package org.riotfamily.forms.resource;

import java.io.PrintWriter;
import java.util.Collection;

public class ScriptSequence implements FormResource {

	private ScriptResource[] scripts;
	

	public ScriptSequence(ScriptResource[] scripts) {
		this.scripts = scripts;
	}

	public void renderLoadingCode(PrintWriter writer, Collection loadedResources) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < scripts.length; i++) {
			ScriptResource res = scripts[i];
			if (!loadedResources.contains(res)) {
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append("{src:'").append(res.getSrc()).append('\'');
				if (res.getTest() != null) {
					sb.append(", test:'").append(res.getTest()).append('\'');
				}
				sb.append("}");
				loadedResources.add(res);
			}
		}
		if (sb.length() > 0) {
			writer.print("Resources.loadScriptSequence([");
			writer.println(sb);
			writer.print("]);");
		}
	}
	
	public String getTest() {
		if (scripts.length > 0) {
			return scripts[scripts.length - 1].getTest();
		}
		return null;
	}

}
