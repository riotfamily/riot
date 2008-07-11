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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.ui;

import java.io.PrintWriter;
import java.util.Locale;

import org.riotfamily.common.web.ui.ObjectRenderer;
import org.riotfamily.common.web.ui.RenderContext;
import org.riotfamily.riot.runtime.RiotRuntime;
import org.riotfamily.riot.runtime.RiotRuntimeAware;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class CountryFlagRenderer implements ObjectRenderer, RiotRuntimeAware {

	private RiotRuntime runtime;

	private String internationalFlag = "int";
	
	public void setRiotRuntime(RiotRuntime runtime) {
		this.runtime = runtime;
	}
	
	public void setInternationalFlag(String internationalFlag) {
		this.internationalFlag = internationalFlag;
	}

	protected boolean flagExists(String flag) {
		return getClass().getResourceAsStream(
				"/org/riotfamily/riot/runtime/resources/style/icons/flags/"
				+ flag.toLowerCase() + ".gif") != null;
		
	}
	
	protected void renderFlag(String flag, String title, 
			RenderContext context, PrintWriter writer) {
		
		writer.print("<img class=\"flag\" src=\"");
		writer.print(context.getContextPath());
		writer.print(runtime.getResourcePath());
		writer.print("style/icons/flags/");
		writer.print(flag.toLowerCase());
		writer.print(".gif\"");
		if (title != null) {
			writer.print("title=\"");
			writer.print(title);
			writer.print('"');
		}
		writer.print(" />");
	}
	
	public void render(Object obj, RenderContext context, PrintWriter writer) {
		if (obj != null) {
			String flag = null;
			String title = null;
			if (obj instanceof Locale) {
				Locale locale = (Locale) obj;
				flag = locale.getCountry();
				title = locale.getDisplayName();
			}
			else {
				flag = obj.toString();
			}
			if (!flagExists(flag)) {
				flag = internationalFlag;
			}
			renderFlag(flag, title, context, writer);
		}
	}

}
