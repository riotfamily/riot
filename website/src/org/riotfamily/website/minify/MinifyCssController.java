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
package org.riotfamily.website.minify;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class MinifyCssController extends AbstractMinifyController {

	protected String getContentType() {
		return "text/css";
	}
	
	@Override
	protected void capture(String path, ByteArrayOutputStream buffer,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String media = null;
		int i = path.lastIndexOf('@');
		if (i != -1) {
			media = path.substring(i + 1);
			path = path.substring(0, i);
		}
		if (media != null) {
			PrintWriter out = new PrintWriter(buffer);
			out.write("@media ");
			out.write(media);
			out.write(" {\n");
			out.flush();
			super.capture(path, buffer, request, response);
			out.write("}\n");
			out.flush();
		}
		else {
			super.capture(path, buffer, request, response);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String buildParam(Collection<?> sheets) {
		StringBuilder param = new StringBuilder();
		Iterator<?> it = sheets.iterator();
		while (it.hasNext()) {
			Object sheet = it.next();
			if (sheet instanceof Map) {
				Map<String, String> map = (Map<String, String>) sheet;
				param.append(map.get("href"));
				String media = map.get("media");
				if (media != null) {
					param.append('@').append(media);	
				}
			}
			else {
				param.append(sheet);
			}
			if (it.hasNext()) {
				param.append(',');
			}
		}
		return param.toString();
	}
	
}
