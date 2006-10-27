/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
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
