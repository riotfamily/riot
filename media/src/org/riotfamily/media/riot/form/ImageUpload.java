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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.media.riot.form;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.image.ImageCropper;
import org.riotfamily.common.io.IOUtils;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.forms.AbstractElement;
import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.StylesheetResource;
import org.riotfamily.media.model.CroppedRiotImage;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.RiotImage;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Specialized FileUpload element for image uploads.
 */
public class ImageUpload extends FileUpload {

	@SuppressWarnings("unused")
	private RiotLog log = RiotLog.get(this);	
	
	public enum Alpha {
		ALLOWED, REQUIRED, FORBIDDEN
		
	}
	private static final FormResource PREVIEW_RESOURCE = new ScriptResource(
			"riot-js/image-cropper.js", "Cropper",
			Resources.SCRIPTACULOUS_SLIDER,
			new StylesheetResource("style/cropper.css"));

	private int[] widths;

	private int[] heights;

	private int minWidth;

	private int maxWidth;

	private int minHeight;

	private int maxHeight;
	
	private int previewWidth = 263;
	
	private int previewHeight = 100;

	private String validFormats = "GIF,JPEG,PNG";
	
	private Alpha alpha;

	private ImageCropper cropper;

	private boolean crop = true;

	public ImageUpload(ImageCropper cropper) {
		this.cropper = cropper;	
	}

	protected Element createPreviewElement() {
		return new PreviewElement();
	}

	public void setCrop(boolean crop) {
		this.crop = crop;
	}

	public void setWidths(int[] widths) {
		this.widths = widths;
		if (widths != null) {
			int min = Integer.MAX_VALUE;
			int max = 0;
			for (int i = 0; i < widths.length; i++) {
				min = Math.min(min, widths[i]);
				max = Math.max(max, widths[i]);
			}
			setMinWidth(min);
			setMaxWidth(max);
		}
	}

	public void setWidth(int width) {
		setWidths(new int[] { width });
	}

	public void setHeights(int[] heights) {
		this.heights = heights;
		if (heights != null) {
			int min = Integer.MAX_VALUE;
			int max = 0;
			for (int i = 0; i < heights.length; i++) {
				min = Math.min(min, heights[i]);
				max = Math.max(max, heights[i]);
			}
			setMinHeight(min);
			setMaxHeight(max);
		}
	}

	public void setHeight(int height) {
		setHeights(new int[] {height});
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}
	
	public void setPreviewWidth(int previewWidth) {
		this.previewWidth = previewWidth;
	}

	public void setPreviewHeight(int previewHeight) {
		this.previewHeight = previewHeight;
	}

	public void setValidFormats(String validFormats) {
		this.validFormats = validFormats;
	}
	
	public void setAlpha(String alpha) {
		this.alpha = alpha != null ? Alpha.valueOf(alpha.toUpperCase()) : null;
	}

	protected RiotFile createRiotFile(MultipartFile multipartFile) throws IOException {
		return new RiotImage(multipartFile);
	}
	
	protected void validateFile(RiotFile file) {
		RiotImage image = (RiotImage) file;
		if (validFormats != null) {
			if (validFormats.indexOf(image.getFormat()) == -1) {
				ErrorUtils.reject(this, "image.invalidFormat",
				new Object[] { validFormats, image.getFormat() });
			}
		}
		if (alpha != null) {
			if (alpha == Alpha.REQUIRED && !image.isAlpha()) {
				ErrorUtils.reject(this, "image.alphaRequired");
			}
			else if (alpha == Alpha.FORBIDDEN && image.isAlpha()) {
				ErrorUtils.reject(this, "image.alphaForbidden");
			}
		}
		int imageHeight = image.getHeight();
		int imageWidth = image.getWidth();

		if (widths != null) {
			boolean match = false;
			for (int i = 0; i < widths.length; i++) {
				if (imageWidth == widths[i]) {
					match = true;
					break;
				}
			}
			if (!match) {
				ErrorUtils.reject(this, "image.size.mismatch");
				return;
			}
		}
		else if (imageWidth < minWidth || (maxWidth > 0 && imageWidth > maxWidth)) {
			ErrorUtils.reject(this, "image.size.mismatch");
			return;
		}

		if (heights != null) {
			boolean match = false;
			for (int i = 0; i < heights.length; i++) {
				if (imageHeight == heights[i]) {
					match = true;
					break;
				}
			}
			if (!match) {
				ErrorUtils.reject(this, "image.size.mismatch");
			}
		}
		else if (imageHeight < minHeight || (maxHeight > 0 && imageHeight > maxHeight)) {
			ErrorUtils.reject(this, "image.size.mismatch");
		}
	}
	
	protected void cropImage(int width, int height, int x, int y,
			int scaledWidth) throws IOException {

		RiotImage original = (RiotImage) getPreviewFile();
		setNewFile(new CroppedRiotImage(original, cropper, 
				width, height, x, y, scaledWidth));
	}

	protected void undoCrop() {
		CroppedRiotImage croppedImage = (CroppedRiotImage) getPreviewFile();
		setNewFile(croppedImage.getOriginal());
	}
	
	public class PreviewElement extends AbstractElement
			implements ContentElement, DHTMLElement, ResourceElement {

		public PreviewElement() {
			setWrap(false);
		}
		
		public FormResource getResource() {
			return PREVIEW_RESOURCE;
		}

		protected void renderInternal(PrintWriter writer) {
			int w = crop && maxWidth > 0 ? maxWidth : previewWidth;
			int h = (maxHeight > 0 ? maxHeight : previewHeight) + 50;
			new TagWriter(writer).start("div")
					.attribute("id", getId())
					.attribute("style", "width:" + w + "px;height:" + h + "px")
					.end();
		}

		private int getIntParameter(HttpServletRequest request, String name) {
			return ServletRequestUtils.getIntParameter(request, name, 0);
		}

		public void handleContentRequest(HttpServletRequest request,
				HttpServletResponse response) throws IOException {

			RiotFile file = getPreviewFile();
			if (file != null) {
				if ("crop".equals(request.getParameter("action"))) {
					cropImage(
							getIntParameter(request, "width"),
							getIntParameter(request, "height"),
							getIntParameter(request, "x"),
							getIntParameter(request, "y"),
							getIntParameter(request, "scaledWidth"));

					response.getWriter().print(getCroppedImageUrl());
				}
				else if ("undo".equals(request.getParameter("action"))) {
					undoCrop();
				}
				else {
					ServletUtils.setNoCacheHeaders(response);
					try {
						response.setHeader("Content-Type", file.getContentType());
						response.setContentLength((int) file.getSize());
						IOUtils.serve(file.getInputStream(), response.getOutputStream());
					}
					catch (FileNotFoundException e) {
						log.error(e.getMessage());
					}
				}
			}
			else {
				response.sendError(HttpServletResponse.SC_NO_CONTENT);
			}
		}
	
		public String getImageUrl() {
			if (getPreviewFile() != null) {
				return getFormContext().getContentUrl(this)
						+ "&time=" + System.currentTimeMillis();
			}
			return null;
		}

		public String getCropUrl() {
			if (isEnabled() && cropper != null && crop) {
				return getFormContext().getContentUrl(this) + "&action=crop";
			}
			return null;
		}

		public String getUndoUrl() {
			if (cropper != null && crop) {
				return getFormContext().getContentUrl(this) + "&action=undo";
			}
			return null;
		}

		public String getCroppedImageUrl() {
			return getFormContext().getContentUrl(this)
					+ "&cropped=true&time=" + System.currentTimeMillis();
		}
		
		public String getParentId() {
			return ImageUpload.this.getId();
		}

		public String getInitScript() {
			return TemplateUtils.getInitScript(this);
		}

		public int getMinWidth() {
			return minWidth;
		}

		public int getMaxWidth() {
			return maxWidth;
		}

		public int getMinHeight() {
			return minHeight;
		}

		public int getMaxHeight() {
			return maxHeight;
		}

		public int[] getWidths() {
			return widths;
		}

		public int[] getHeights() {
			return heights;
		}
		
		public int getPreviewWidth() {
			return previewWidth;
		}
		
		public int getPreviewHeight() {
			return previewHeight;
		}

	}
	
}