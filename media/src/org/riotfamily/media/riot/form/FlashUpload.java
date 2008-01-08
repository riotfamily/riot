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
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
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

	private static final FormResource PREVIEW_RESOURCE = new ScriptResource(
			"swfobject/1.5/swfobject.js", "deconcept.SWFObject", 
			Resources.SCRIPTACULOUS_EFFECTS);
	
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
			implements ContentElement, ResourceElement, DHTMLElement {

		public PreviewElement() {
		}

		public FormResource getResource() {
			return PREVIEW_RESOURCE;
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
		
		protected void renderTemplate(PrintWriter writer) {
			if (getSwf() != null) {
				double w = getSwf().getWidth();
				double h = getSwf().getHeight();
				if (w > 0 && h > 0) {
					double scaleX = 150d / w; 
					double scaleY = 150d / h;
					double scale = Math.min(Math.min(scaleX, scaleY), 1);
					setAttribute("previewWidth", new Integer((int) (w * scale)));
					setAttribute("previewHeight", new Integer((int) (h * scale)));
				}
			}
			super.renderTemplate(writer);
		}
		
		public RiotSwf getSwf() {
			return (RiotSwf) FlashUpload.this.getFile();
		}

		public String getDownloadUrl() {
			return getFormContext().getContentUrl(this);
		}
		
		public String getInitScript() {
			return getSwf() != null ? TemplateUtils.getInitScript(this) : null;
		}

	}

}
