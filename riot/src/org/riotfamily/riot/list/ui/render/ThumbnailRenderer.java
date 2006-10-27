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
package org.riotfamily.riot.list.ui.render;

import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThumbnailRenderer extends ObjectRenderer {

	Log log = LogFactory.getLog(ThumbnailRenderer.class);
	
	private String thumbnailControllerPath;
	
	public void setThumbnailControllerPath(String thumbnailControllerPath) {
		this.thumbnailControllerPath = thumbnailControllerPath;
	}
	
	protected void renderValue(RenderContext context, PrintWriter writer, 
			String value) {		
		writer.print("<img src=\"");
		writer.print(context.getContextPath());
		writer.print(context.getRequest().getServletPath());
		writer.print(thumbnailControllerPath);
		writer.print("?sourceFile=");
		writer.print(value);
		writer.print("\" />"); 
		
	}
}
