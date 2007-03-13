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
package org.riotfamily.forms.element.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.devlib.schmidt.imageinfo.ImageInfo;
import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.bind.EditorBinder;
import org.riotfamily.forms.element.ContentElement;
import org.riotfamily.forms.element.DHTMLElement;
import org.riotfamily.forms.element.support.AbstractElement;
import org.riotfamily.forms.element.support.image.ImageCropper;
import org.riotfamily.forms.error.ErrorUtils;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.support.TemplateUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestUtils;

/**
 * Specialized FileUpload element for image uploads.
 */
public class ImageUpload extends FileUpload {

	protected static final FormResource RESOURCE = new ScriptResource(
			"riot-js/image-cropper.js", "Cropper",
			new FormResource[] { 
					FileUpload.RESOURCE, Resources.SCRIPTACULOUS_SLIDER
			});
	
	private int[] widths;
	
	private int[] heights;
	
	private int minWidth;
	
	private int maxWidth;
	
	private int minHeight;
	
	private int maxHeight;
	
	private String validFormats = "GIF,JPEG,PNG";

	private String widthProperty;
	
	private String heightProperty;
	
	private ImageInfo info;
	
	private ImageCropper cropper;
	
	private boolean crop = true;
	
	private File originalFile;
	
	private File croppedFile;
	
	public ImageUpload() {
	}

	public FormResource getResource() {
		return RESOURCE;
	}
	
	protected Element createPreviewElement() {
		return new PreviewElement();
	}
	
	public void setCropper(ImageCropper cropper) {
		this.cropper = cropper;
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

	public void setValidFormats(String validFormats) {
		this.validFormats = validFormats;
	}

	public String getHeightProperty() {
		return this.heightProperty;
	}

	public void setHeightProperty(String heightProperty) {
		this.heightProperty = heightProperty;
	}

	public String getWidthProperty() {
		return this.widthProperty;
	}

	public void setWidthProperty(String widthProperty) {
		this.widthProperty = widthProperty;
	}

	public boolean isPreviewAvailable() {
		return true; 
	}
	
	protected void cropImage(int width, int height, int x, int y, 
			int scaledWidth) throws IOException {
		
		if (croppedFile == null) {
			croppedFile = File.createTempFile("000", ".tmp");
		}
		if (originalFile == null) {
			originalFile = getFile();
		}
		cropper.cropImage(originalFile, croppedFile, width, height, x, y, 
				scaledWidth);
		
		setFile(croppedFile);
	}
	
	protected void afterFileUploaded() {
		originalFile = getFile();
	}
	
	protected void undoCrop() {
		setFile(originalFile);
	}
	
	protected void destroy() {
		super.destroy();
		if (croppedFile != null && !croppedFile.equals(getReturnedFile())) {
			croppedFile.delete();
		}
	}
	
	protected void validateFile(File file) {
		try {
			info = new ImageInfo();
			info.setInput(new FileInputStream(file));
			info.check();
			log.debug(info.getFormatName() + " Size: " 
					+ info.getWidth() + "x" + info.getHeight());
			
			if (validFormats != null) {
				if (validFormats.indexOf(info.getFormatName()) == -1) {
					ErrorUtils.reject(this, "image.invalidFormat", 
					new Object[] {  validFormats, info.getFormatName() });
				}
			}
			int imageHeight = info.getHeight();
			int imageWidth = info.getWidth();
			
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
		catch (IOException e) {
		}
	}

	public Object getValue() {
		if (info != null) {
			EditorBinder editorBinder = getEditorBinding().getEditorBinder();
			if (widthProperty != null) {
				editorBinder.setPropertyValue(widthProperty, 
						new Integer(info.getWidth()));
			}
			if (heightProperty != null) {
				editorBinder.setPropertyValue(heightProperty, 
						new Integer(info.getHeight()));
			}
		}
		return super.getValue();
	}
	
	public class PreviewElement extends AbstractElement 
			implements ContentElement, DHTMLElement {


		protected void renderInternal(PrintWriter writer) {
			int w = crop && maxWidth > 0 ? maxWidth : 263;
			int h = (crop && maxHeight > 0 ? maxHeight : 150) + 50;
			new TagWriter(writer).start(Html.DIV)
					.attribute(Html.COMMON_ID, getId())
					.attribute(Html.COMMON_STYLE, 
					"width:" + w + "px;height:" + h + "px").end();
		}
		
		private int getIntParameter(HttpServletRequest request, String name) {
			return ServletRequestUtils.getIntParameter(request, name, 0);
		}
		
		public void handleContentRequest(HttpServletRequest request,
				HttpServletResponse response) throws IOException {
			
			if (isPresent()) {
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
					response.setHeader("Content-Type", getContentType());
					response.setContentLength(getSize().intValue());
					
					try {
						//TODO Check if file exists
						FileCopyUtils.copy(new FileInputStream(getFile()), 
								response.getOutputStream());
					}
					catch (IOException e) {
						// Ignore exceptions caused by client abortion:
						if (!SocketException.class.isInstance(e.getCause())) {
							throw e;
						}
					}
				}
			}
			else {
				response.sendError(HttpServletResponse.SC_NO_CONTENT);
			}
		}
		
		public String getImageUrl() {
			if (isPresent()) {
				return getFormContext().getContentUrl(this) 
						+ "&time=" + System.currentTimeMillis();
			}
			return null;
		}
		
		public String getCropUrl() {
			if (cropper != null && crop) {
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
		
		public String getInitScript() {
			return TemplateUtils.getInitScript(this);
		}

		public String getPrecondition() {
			return "Cropper";
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

	}

}
