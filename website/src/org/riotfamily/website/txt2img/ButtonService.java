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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheHandler;
import org.riotfamily.cachius.CacheItem;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.common.image.ImageUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.common.web.filter.ResourceStamper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ButtonService implements ApplicationContextAware {

	private CacheService cacheService;
	
	private Map<String, ButtonRenderer> buttons;
	
	private ResourceStamper resourceStamper;
	
	private boolean reloadable = false;
	
	public ButtonService(CacheService cacheService, ResourceStamper resourceStamper) {
		this.cacheService = cacheService;
		this.resourceStamper = resourceStamper;
	}

	public boolean isReloadable() {
		return reloadable;
	}

	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	public void setApplicationContext(ApplicationContext ctx) {
		buttons = SpringUtils.beansOfType(ctx, ButtonRenderer.class);
	}
	
	public String getInlineStyle(String style, String label, 
			HttpServletRequest request) 
			throws Exception {
		
		ButtonRenderer renderer = buttons.get(style);
		String imageUri = getImageUri(style, label, request);
		InlineStyleHandler handler = new InlineStyleHandler(label, renderer, imageUri);
		cacheService.handle(handler);
		return handler.getInlineStyle();
	}
	
	public void serveImage(String style, String label, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		ButtonRenderer renderer = buttons.get(style);
		String imageUri = getImageUri(style, label, request);
		ImageHandler handler = new ImageHandler(label, renderer, imageUri, response);
		cacheService.handle(handler);
	}
	
	public void writeRules(PrintWriter out) {
		for (ButtonRenderer renderer : buttons.values()) {
			out.write(renderer.getRules());
		}
	}

	public long getLastModified() {
		long mtime = 0;
		for (ButtonRenderer renderer : buttons.values()) {
			mtime = Math.max(mtime, renderer.getLastModified());
		}
		return mtime;
	}
	
	private String getImageUri(String style, String label, HttpServletRequest request) {
		String encodedLabel = FormatUtils.uriEscape(label);
		return String.format("%s/riot-utils/imagebtn/%s/%s.png", 
				request.getContextPath(), style, encodedLabel);
	}
	
	private abstract class AbstractButtonHandler implements CacheHandler {

		private String label;
		
		private ButtonRenderer renderer;

		private String imageUri;
		
		public AbstractButtonHandler(String label, ButtonRenderer renderer,
				String imageUri) {
			
			this.label = label;
			this.renderer = renderer;
			this.imageUri = imageUri;
		}

		public String getCacheKey() {
			return imageUri;
		}

		public long getLastModified() throws Exception {
			return renderer.getLastModified();
		}

		public long getTimeToLive() {
			return reloadable ? 0 : CACHE_ETERNALLY;
		}

		public void handleUncached() throws Exception {
			throw new IllegalStateException();
		}

		public boolean updateCacheItem(CacheItem cacheItem) throws Exception {
			BufferedImage image = renderer.generate(label);
			ImageUtils.write(image, ImageUtils.FORMAT_PNG, cacheItem.getOutputStream());
			Map<String, String> properties = Generics.newHashMap();
			properties.put("inlineStyle", getInlineStyle(image));
			cacheItem.setContentType("image/png");
			cacheItem.setSetContentLength(true);
			cacheItem.setProperties(properties);
			return true;
		}
		
		private String getInlineStyle(BufferedImage image) {
			String url = resourceStamper.stamp(imageUri);
			return "width:" + image.getWidth() + "px;" 
					+ "background-image:url(" + url + ")";
		}

	}
	
	private class InlineStyleHandler extends AbstractButtonHandler {
		
		private String inlineStyle;

		
		public InlineStyleHandler(String label, ButtonRenderer renderer,
				String imageUri) {
			
			super(label, renderer, imageUri);
		}

		public void writeCacheItem(CacheItem cacheItem) throws IOException {
			inlineStyle = cacheItem.getProperties().get("inlineStyle");
		}
		
		public String getInlineStyle() {
			return inlineStyle;
		}
		
	}
	
	private class ImageHandler extends AbstractButtonHandler {

		private HttpServletResponse response;
		
		public ImageHandler(String label, ButtonRenderer renderer,
				String imageUri, HttpServletResponse response) {
			
			super(label, renderer, imageUri);
			this.response = response;
		}

		public void writeCacheItem(CacheItem cacheItem) throws IOException {
			cacheItem.writeTo(response);
		}
	}
}