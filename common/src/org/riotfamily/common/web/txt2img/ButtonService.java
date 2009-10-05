/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.web.txt2img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheHandler;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.persistence.DiskStore;
import org.riotfamily.common.io.IOUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.ImageUtils;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.common.web.performance.ResourceStamper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.support.RequestContextUtils;

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
		buttons = ctx.getBeansOfType(ButtonRenderer.class);
	}
	
	public String getInlineStyle(String style, String label, 
			HttpServletRequest request) throws Exception {
		
		ButtonRenderer renderer = buttons.get(style);
		String imageUri = getImageUri(style, label, request);
		Locale locale = RequestContextUtils.getLocale(request);
		InlineStyleHandler handler = new InlineStyleHandler(label, locale, renderer, imageUri);
		cacheService.handle(handler);
		return handler.getInlineStyle();
	}
	
	public void serveImage(String style, String label, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		ButtonRenderer renderer = buttons.get(style);
		String imageUri = getImageUri(style, label, request);
		Locale locale = RequestContextUtils.getLocale(request);
		ImageHandler handler = new ImageHandler(label, locale, renderer, imageUri, response);
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
		String locale = RequestContextUtils.getLocale(request).toString();
		return String.format("%s/riot-utils/imagebtn/%s.png?label=%s&locale=%s", 
				request.getContextPath(), style, encodedLabel, locale);
	}
	
	
	private static class Button implements Serializable {
		
		private File file;
		
		private String inlineStyle;

		public Button(File file, String inlineStyle) {
			this.file = file;
			this.inlineStyle = inlineStyle;
		}

		public String getInlineStyle() {
			return inlineStyle;
		}

		public File getFile() {
			return file;
		}
		
	}
	
	private abstract class AbstractButtonHandler implements CacheHandler {

		private String label;
		
		private Locale locale;
		
		private ButtonRenderer renderer;

		private String imageUri;
		
		public AbstractButtonHandler(String label, Locale locale,
				ButtonRenderer renderer, String imageUri) {
			
			this.label = label;
			this.locale = locale;
			this.renderer = renderer;
			this.imageUri = imageUri;
		}

		public String getCacheRegion() {
			return ButtonService.class.getName();
		}
		
		public String getCacheKey() {
			return imageUri;
		}
		
		public long getLastModified() {
			return renderer.getLastModified();
		}
		
		public Serializable capture(DiskStore diskStore) throws Exception {
			File file = diskStore.getFile();
			BufferedImage image = generateImage();
			writeImage(image, new FileOutputStream(file));
			return new Button(file, getInlineStyle(image));
		}
		
		public void delete(Serializable data) throws Exception {
			Button button = (Button) data;
			button.getFile().delete();
		}
		
		protected BufferedImage generateImage() throws Exception {
			return renderer.generate(label, locale);
		}
		
		protected void writeImage(BufferedImage image, OutputStream out) throws IOException {
			ImageUtils.write(image, ImageUtils.FORMAT_PNG, out);
		}
		
		protected String getInlineStyle(BufferedImage image) {
			String url = resourceStamper.stamp(imageUri);
			return "width:" + image.getWidth() + "px;" 
					+ "background-image:url(" + url + ")";
		}

	}
	
	private class InlineStyleHandler extends AbstractButtonHandler {
		
		private String inlineStyle;
		
		public InlineStyleHandler(String label, Locale locale,
				ButtonRenderer renderer, String imageUri) {
			
			super(label, locale, renderer, imageUri);
		}

		public void handleUncached() throws Exception {
			inlineStyle = getInlineStyle(generateImage());
		}
		
		public void serve(Serializable data) throws IOException {
			Button button = (Button) data;
			inlineStyle = button.getInlineStyle();
		}
		
		public String getInlineStyle() {
			return inlineStyle;
		}
		
	}
	
	private class ImageHandler extends AbstractButtonHandler {

		private HttpServletResponse response;
		
		public ImageHandler(String label, Locale locale, ButtonRenderer renderer,
				String imageUri, HttpServletResponse response) {
			
			super(label, locale, renderer, imageUri);
			this.response = response;
		}

		public void handleUncached() throws Exception {
			response.setContentType("image/png");
			writeImage(generateImage(), response.getOutputStream());
		}
				
		public void serve(Serializable data) throws IOException {
			Button button = (Button) data;
			response.setContentType("image/png");
			IOUtils.serve(button.getFile(), response.getOutputStream());
		}
	}
}
