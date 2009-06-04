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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.forms.element.upload.AbstractFileUpload;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.service.UnknownFormatException;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;


/**
 * A widget to upload files.
 */
public class FileUpload extends AbstractFileUpload {

	private RiotFile file;
	
	private RiotFile uploadedFile;
	
	protected Element createPreviewElement() {
		return new PreviewElement();
	}
		
	public void setValue(Object value) {
		log.debug("Value set to: " + value);
		if (value == null) {
			return;
		}
		if (value instanceof RiotFile) {
			file = (RiotFile) value;	
		}
		else {
			throw new IllegalArgumentException("Value is no RiotFile: " + value);
		}
	}

	public Object getValue() {
		if (uploadedFile != null) {
			file = uploadedFile;
		}
		return file;
	}
	
	protected RiotFile getUploadedFile() {
		return uploadedFile;
	}
	
	protected RiotFile getPreviewFile() {
		return uploadedFile != null ? uploadedFile : file;
	}
	
	@Override
	protected boolean isFilePresent() {
		return getPreviewFile() != null;
	}
	
	protected RiotFile createRiotFile(MultipartFile multipartFile) 
			throws IOException {
		
		return new RiotFile(multipartFile);
	}
	
	@Override
	protected void validate() {
		ErrorUtils.removeErrors(this);
		if (uploadedFile != null) {
			validateFile(uploadedFile);
		}
		else if (file == null && isRequired()) {
			ErrorUtils.rejectRequired(this);
		}
	}
	
	protected void validateFile(RiotFile file) {
	}
	
	@Override
	protected void onUpload(MultipartFile multipartFile) throws IOException {
		try {
			setNewFile(createRiotFile(multipartFile));
		}
		catch (UnknownFormatException e) {
			ErrorUtils.reject(this, "unknownFileFormat");
		}
	}
	
	protected final void setNewFile(RiotFile file) {
		uploadedFile = file;
		validate();
	}
	
	@Override
	protected void onRemove() {
		file = null;
		uploadedFile = null;
	}
		

	public class PreviewElement extends TemplateElement
			implements ContentElement {

		public RiotFile getFile() {
			return getPreviewFile();
		}
		
		public void handleContentRequest(HttpServletRequest request,
				HttpServletResponse response) throws IOException {

			RiotFile file = getPreviewFile();
			if (file != null) {
				response.setDateHeader("Expires", 0);
				response.setHeader("Content-Type", "application/x-download");
		        response.setHeader("Content-Disposition",
		        		"attachment;filename=" + file.getFileName());

				response.setContentLength((int) file.getSize());
				
				FileCopyUtils.copy(file.getInputStream(), 
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
