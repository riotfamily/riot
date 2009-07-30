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
package org.riotfamily.core.ui;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;

import org.riotfamily.common.ui.DateRenderer;
import org.riotfamily.common.ui.ObjectRenderer;
import org.riotfamily.common.ui.RenderContext;
import org.riotfamily.common.ui.StringRenderer;

/**
 * Default ObjectRenderer with special handling for Boolean, Date, and 
 * Locale values.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class DefaultObjectRenderer implements ObjectRenderer {

	private CssClassRenderer booleanRenderer;
	
	private DateRenderer dateRenderer;
	
	private LocaleRenderer localeRenderer;
	
	private StringRenderer defaultRenderer;

	public DefaultObjectRenderer(String resourcePath) {
		booleanRenderer = new CssClassRenderer();
		dateRenderer = new DateRenderer();
		localeRenderer = new LocaleRenderer(resourcePath);
		defaultRenderer = new StringRenderer();
	}
	
	public void render(Object obj, RenderContext context, PrintWriter writer) {
		if (obj == null) {
			return;
		}
		if (obj instanceof Boolean) {
			booleanRenderer.render(obj, context, writer);
		}
		else if (obj instanceof Date) {
			dateRenderer.render(obj, context, writer);
		}
		else if (obj instanceof Locale) {
			localeRenderer.render(obj, context, writer);
		}
		else {
			defaultRenderer.render(obj, context, writer);
		}
	}

}
