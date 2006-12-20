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
package org.riotfamily.forms.element.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

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
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.ScriptSequence;
import org.riotfamily.forms.support.TemplateUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestUtils;

/**
 * Specialized FileUpload element for image uploads.
 */
public class ImageUpload extends FileUpload {

	private int width;
	
	private int height;
	
	private int minWidth;
	
	private int maxWidth;
	
	private int minHeight;
	
	private int maxHeight;
	
	private String validFormats = "GIF,JPEG,PNG";

	private String widthProperty;
	
	private String heightProperty;
	
	private ImageInfo info;
	
	private ImageCropper cropper;
	
	private File croppedFile;
	
	public ImageUpload() {
		addResource(new ScriptSequence(new ScriptResource[] {
			Resources.PROTOTYPE, 
			Resources.SCRIPTACULOUS_SLIDER,
			new ScriptResource("riot-js/image-cropper.js", "Cropper")	
		}));
	}

	protected Element createPreviewElement() {
		return new PreviewElement();
	}
	
	public void setCropper(ImageCropper cropper) {
		this.cropper = cropper;
	}

	public void setWidth(int width) {
		this.width = width;
		setMinWidth(width);
		setMaxWidth(width);
	}
	
	public void setHeight(int height) {
		this.height = height;
		setMinHeight(height);
		setMaxHeight(height);
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
		cropper.cropImage(getFile(), croppedFile, width, height, x, y, 
				scaledWidth);
		
		setFile(croppedFile);
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
			
			if (width > 0) {
				if (imageWidth != width) {
					ErrorUtils.reject(this, "image.width.mismatch", new Integer(width));
				}
			}
			else {
				if (imageWidth < minWidth) {
					ErrorUtils.reject(this, "image.width.tooSmall", new Integer(minWidth));
				}
				if (maxWidth > 0 && imageWidth > maxWidth) {
					ErrorUtils.reject(this, "image.width.tooLarge", new Integer(maxWidth));
				}
			}
			
			if (height > 0) {
				if (imageHeight != height) {
					ErrorUtils.reject(this, "image.height.mismatch", new Integer(height));
				}
			}
			else {
				if (imageHeight < minHeight) {
					ErrorUtils.reject(this, "image.height.tooSmall", new Integer(minHeight));
				}
				if (maxHeight > 0 && imageHeight > maxHeight) {
					ErrorUtils.reject(this, "image.height.tooLarge", new Integer(maxHeight));
				}
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
			int w = maxWidth > 0 ? maxWidth : 150;
			int h = (maxHeight > 0 ? maxHeight : 100) + 50;
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
				else {
					ServletUtils.setNoCacheHeaders(response);
					response.setHeader("Content-Type", getContentType());
					response.setContentLength(getSize().intValue());
					FileCopyUtils.copy(new FileInputStream(getFile()), 
							response.getOutputStream());
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
			if (cropper == null) {
				return null;
			}
			return getFormContext().getContentUrl(this) + "&action=crop";
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

	}

}
