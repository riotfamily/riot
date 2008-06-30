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
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.txt2img;

import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;

public class Txt2ImgMacroHelper {

	private Map<String, ButtonStyle> buttons;
	
	private File baseDir;
	
	private HttpServletRequest request;
	
	
	public Txt2ImgMacroHelper(File baseDir, Map<String, ButtonStyle> buttons,
			HttpServletRequest request) {
		
		this.baseDir = baseDir;
		this.buttons = buttons;
		this.request = request;
	}

	public String getButtonStyle(String id, String label) throws Exception {
		ButtonStyle button = buttons.get(id);
		Assert.notNull(button, "No such button: " + id);
		File dir = new File(baseDir, id);
		dir.mkdirs();
		return button.getInlineStyle(dir, label, request);
	}
}
