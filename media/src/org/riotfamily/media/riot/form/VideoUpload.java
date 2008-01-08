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
import org.riotfamily.media.model.RiotVideo;
import org.riotfamily.media.model.VideoData;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Specialized FileUpload element for flash uploads.
 * @since 7.0
 */
public class VideoUpload extends FileUpload {

	private static final FormResource PREVIEW_RESOURCE = new ScriptResource(
			"swfobject/1.5/swfobject.js", "deconcept.SWFObject", 
			Resources.SCRIPTACULOUS_EFFECTS);
	
	protected RiotFile createRiotFile(MultipartFile multipartFile) 
			throws IOException {
		
		return new RiotVideo(new VideoData(multipartFile));
	}
	
	protected Element createPreviewElement() {
		return new PreviewElement();
	}

	public class PreviewElement extends TemplateElement
			implements ContentElement, ResourceElement, DHTMLElement {

		private int previewWidth;
		
		private int previewHeight;
		
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
			if (getVideo() != null) {
				double w = getVideo().getWidth();
				double h = getVideo().getHeight();
				if (w > 0 && h > 0) {
					double scaleX = 150d / w; 
					double scaleY = 150d / h;
					double scale = Math.min(Math.min(scaleX, scaleY), 1);
					previewWidth = (int) (w * scale);
					previewHeight = (int) (h * scale);
				}
			}
			super.renderTemplate(writer);
		}
		
		public int getPreviewWidth() {
			return this.previewWidth;
		}

		public int getPreviewHeight() {
			return this.previewHeight;
		}

		public RiotVideo getVideo() {
			return (RiotVideo) VideoUpload.this.getFile();
		}

		public String getPlayerUrl() {
			return getFormContext().getContextPath() 
					+ getFormContext().getResourcePath() + "FLVPlayer.swf";
		}
		
		public String getDownloadUrl() {
			return getFormContext().getContentUrl(this);
		}
		
		public String getInitScript() {
			return getVideo() != null ? TemplateUtils.getInitScript(this) : null;
		}

	}

}
