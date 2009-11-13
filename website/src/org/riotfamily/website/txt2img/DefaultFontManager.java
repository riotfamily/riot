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

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class DefaultFontManager implements FontManager, ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	private Map<Resource, Font> fontsByResource = Generics.newHashMap();
	
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public Font getFont(String name, Locale locale) {
		Resource res = getFontResource(name, locale);
		Font font = fontsByResource.get(res);
		if (font == null) {
			font = loadFont(res);
			fontsByResource.put(res, font);
		}
		return font;
	}
	
	public Resource getFontResource(String name, Locale locale) {
		return resourceLoader.getResource(name);
	}
	
	private Font loadFont(Resource res) {
		try {
			return Font.createFont(getFontFormat(res.getFilename()), res.getInputStream());
		}
		catch (FontFormatException e) {
			throw new BeanCreationException("Invalid font", e);
		}
		catch (IOException e) {
			throw new BeanCreationException("Error loading font", e);
		}
	}
	
	private int getFontFormat(String fileName) {
		String ext = FormatUtils.getExtension(fileName).toLowerCase();
		if (ext.equals("ttf")) {
			return Font.TRUETYPE_FONT;			
		}
		if (ext.equals("pfa") || ext.equals("pfb")) {
			return Font.TYPE1_FONT;
		}
		return -1;
	}

}
