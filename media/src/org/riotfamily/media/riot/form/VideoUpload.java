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
package org.riotfamily.media.riot.form;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.io.IOUtils;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * Specialized FileUpload element for flash uploads.
 * @since 7.0
 */
public class VideoUpload extends FileUpload {

	private static final FormResource PREVIEW_RESOURCE = new ScriptResource(
			"swfobject/swfobject.js", "deconcept.SWFObject", 
			Resources.SCRIPTACULOUS_EFFECTS);
	
	
	protected RiotFile createRiotFile(MultipartFile multipartFile) 
			throws IOException {
		
		return new RiotVideo(multipartFile);
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

			if (getPreviewFile() != null) {
				response.setDateHeader("Expires", 0);
				response.setHeader("Content-Type", getPreviewFile().getContentType());
		        response.setHeader("Content-Disposition",
		        		"attachment;filename=" + getPreviewFile().getFileName());

				response.setContentLength((int) getPreviewFile().getSize());
				IOUtils.serve(getPreviewFile().getInputStream(), 
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
			return (RiotVideo) VideoUpload.this.getPreviewFile();
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
