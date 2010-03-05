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

import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.RiotSwf;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Specialized FileUpload element for flash uploads.
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public class FlashUpload extends FileUpload {

	private static final FormResource PREVIEW_RESOURCE = new ScriptResource(
			"swfobject/swfobject.js", "deconcept.SWFObject", 
			Resources.SCRIPTACULOUS_EFFECTS);
	
	private int[] widths;

	private int[] heights;

	private int minWidth;

	private int maxWidth;

	private int minHeight;

	private int maxHeight;

	@Override
	protected RiotFile createRiotFile(MultipartFile multipartFile) 
			throws IOException {
		
		return new RiotSwf(multipartFile);
	}
	
	@Override
	protected Element createPreviewElement() {
		return new PreviewElement();
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

	@Override
	protected void validateFile(RiotFile file) {
		RiotSwf swf = (RiotSwf) file;
		if (!swf.isValid()) {
			ErrorUtils.reject(this, "flash.invalidFormat");
		}
		
		int swfHeight = swf.getHeight();
		int swfWidth = swf.getWidth();

		if (widths != null) {
			boolean match = false;
			for (int i = 0; i < widths.length; i++) {
				if (swfWidth == widths[i]) {
					match = true;
					break;
				}
			}
			if (!match) {
				ErrorUtils.reject(this, "flash.size.mismatch");
				return;
			}
		}
		else if (swfWidth < minWidth || (maxWidth > 0 && swfWidth > maxWidth)) {
			ErrorUtils.reject(this, "flash.size.mismatch");
			return;
		}

		if (heights != null) {
			boolean match = false;
			for (int i = 0; i < heights.length; i++) {
				if (swfHeight == heights[i]) {
					match = true;
					break;
				}
			}
			if (!match) {
				ErrorUtils.reject(this, "flash.size.mismatch");
			}
		}
		else if (swfHeight < minHeight || (maxHeight > 0 && swfHeight > maxHeight)) {
			ErrorUtils.reject(this, "flash.size.mismatch");
		}
	}
	
	public class PreviewElement extends TemplateElement
			implements ContentElement, ResourceElement, DHTMLElement {

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
				
				FileCopyUtils.copy(getPreviewFile().getInputStream(), 
						response.getOutputStream());
			}
			else {
				response.sendError(HttpServletResponse.SC_NO_CONTENT);
			}
		}
		
		@Override
		protected void renderTemplate(PrintWriter writer) {
			if (getSwf() != null) {
				double w = getSwf().getWidth();
				double h = getSwf().getHeight();
				if (w > 0 && h > 0) {
					double scaleX = 150d / w; 
					double scaleY = 150d / h;
					double scale = Math.min(Math.min(scaleX, scaleY), 1);
					setAttribute("previewWidth", (int) (w * scale));
					setAttribute("previewHeight", (int) (h * scale));
				}
			}
			super.renderTemplate(writer);
		}
		
		public RiotSwf getSwf() {
			return (RiotSwf) FlashUpload.this.getPreviewFile();
		}

		public String getDownloadUrl() {
			return getFormContext().getContentUrl(this);
		}
		
		public String getInitScript() {
			return getSwf() != null ? TemplateUtils.getInitScript(this) : null;
		}

	}

}
