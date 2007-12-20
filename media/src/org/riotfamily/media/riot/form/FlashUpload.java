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
package org.riotfamily.media.riot.form;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.RiotSwf;
import org.riotfamily.media.model.SwfData;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Specialized FileUpload element for flash uploads.
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public class FlashUpload extends FileUpload {

	protected RiotFile createRiotFile(MultipartFile multipartFile) 
			throws IOException {
		
		return new RiotSwf(new SwfData(multipartFile));
	}
	
	protected Element createPreviewElement() {
		return new PreviewElement();
	}

	/*
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
	*/
	
	public class PreviewElement extends TemplateElement
			implements ContentElement {

		public PreviewElement() {
			setAttribute("file", getFile());
		}

		public void handleContentRequest(HttpServletRequest request,
				HttpServletResponse response) throws IOException {

			if (getFile() != null) {
				response.setDateHeader("Expires", 0);
				response.setHeader("Content-Type", getFile().getContentType());
		        response.setHeader("Content-Disposition",
		        		"attachment;filename=" + getFile().getFileName());

				response.setContentLength((int) getFile().getSize());
				
				FileCopyUtils.copy(getFile().getInputStream(), 
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
