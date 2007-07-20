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
 *   Carsten Woelk [cwoelk at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.FlashInfo;
import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.EditorBinder;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.springframework.util.FileCopyUtils;

/**
 * Specialized FileUpload element for flash uploads.
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public class FlashUpload extends FileUpload {

	public static final String FLASH_CONTENT_TYPE = "application/x-shockwave-flash";

	private String widthProperty;

	private String heightProperty;

	private String versionProperty;

	private boolean isValidSwf = false;

	private int width;

	private int height;

	private int version;


	public FlashUpload() {
		super();
	}

	protected void destroy() {
		super.destroy();
	}


	public boolean isPreviewAvailable() {
		return true;
	}

	protected Element createPreviewElement() {
		return new PreviewElement();
	}


	public String getContentType() {
		return FLASH_CONTENT_TYPE;
	}


	public String getWidthProperty() {
		return this.widthProperty;
	}

	public void setWidthProperty(String widthProperty) {
		this.widthProperty = widthProperty;
	}

	public String getHeightProperty() {
		return this.heightProperty;
	}

	public void setHeightProperty(String heightProperty) {
		this.heightProperty = heightProperty;
	}

	public String getVersionProperty() {
		return versionProperty;
	}

	public void setVersionProperty(String versionProperty) {
		this.versionProperty = versionProperty;
	}


	public boolean isValidSwf() {
		return this.isValidSwf;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getVersion() {
		return this.version;
	}


	protected void validateFile(File file) {
		try {
			parseFlashMovie(file);
			if (!isValidSwf) {
				ErrorUtils.reject(this, "flash.invalidFormat");
			}
		}
		catch (IOException e) {
			ErrorUtils.reject(this, "unexpected");
		}
	}

	public void setValue(Object value) {
		super.setValue(value);
		if (value == null) {
			return;
		}

		try {
			parseFlashMovie(getFile());
			EditorBinder editorBinder = getEditorBinding().getEditorBinder();
			if (widthProperty != null) {
				width = getIntegerProperty(editorBinder, widthProperty);
			}
			if (heightProperty != null) {
				height = getIntegerProperty(editorBinder, heightProperty);
			}
			if (versionProperty != null) {
				version = getIntegerProperty(editorBinder, versionProperty);;
			}
		}
		catch (IOException e) {
			isValidSwf = false;
		}
	}

	private static int getIntegerProperty(EditorBinder editorBinder, String propertyName) {
		int result = 0;
		Object value = editorBinder.getPropertyValue(propertyName);
		if (value != null && value instanceof Integer) {
			result = ((Integer) value).intValue();
		}
		return result;
	}

	public Object getValue() {
		EditorBinder editorBinder = getEditorBinding().getEditorBinder();
		setIntegerProperty(editorBinder, widthProperty, width);
		setIntegerProperty(editorBinder, heightProperty, height);
		setIntegerProperty(editorBinder, versionProperty, version);
		return super.getValue();
	}

	private static void setIntegerProperty(EditorBinder editorBinder, String propertyName, int value) {
		if (value != 0) {
			if (propertyName != null) {
				editorBinder.setPropertyValue(propertyName,	new Integer(value));
			}
		}
	}

	protected void parseFlashMovie(File file) throws IOException {
		FlashInfo flashInfo = new FlashInfo(file);
		isValidSwf = flashInfo.isValid();
		width = flashInfo.getWidth();
		height = flashInfo.getHeight();
		version = flashInfo.getVersion();
	}

	public class PreviewElement extends TemplateElement
			implements ContentElement {

		public PreviewElement() {
			setAttribute("flash", FlashUpload.this);
		}

		public void handleContentRequest(HttpServletRequest request,
				HttpServletResponse response) throws IOException {

			File file = getFile();
			if (file != null && file.exists()) {
				response.setDateHeader("Expires", 0);
				response.setHeader("Content-Type", getContentType());

				response.setContentLength(getSize().intValue());
				FileCopyUtils.copy(new FileInputStream(file),
						response.getOutputStream());
			}
			else {
				response.sendError(HttpServletResponse.SC_NO_CONTENT);
			}
		}

		public String getDownloadUrl() {
			return getFormContext().getContentUrl(this);
		}

	}

}
