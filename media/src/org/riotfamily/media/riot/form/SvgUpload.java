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

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.io.IOUtils;
import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.RiotSvg;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Specialized FileUpload element for svg.
 */
public class SvgUpload extends FileUpload {
	
	private int previewWidth = 263;
	
	private int previewHeight = 100;
	
	private int[] widths;

	private int[] heights;

	private int minWidth;

	private int maxWidth;

	private int minHeight;

	private int maxHeight;
	
	public void setPreviewWidth(int previewWidth) {
		this.previewWidth = previewWidth;
	}
	
	public void setPreviewHeight(int previewHeight) {
		this.previewHeight = previewHeight;
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
	protected RiotFile createRiotFile(MultipartFile multipartFile, String bucket) 
			throws IOException {
		
		RiotSvg file = new RiotSvg(bucket);
		file.setMultipartFile(multipartFile);
		return file;
	}
	
	@Override
	protected Element createPreviewElement() {
		return new PreviewElement();
	}
	
	@Override
	protected void validateFile(RiotFile file) {
		RiotSvg svg = (RiotSvg) file;
		if (!svg.isValid()) {
			ErrorUtils.reject(this, "svg.invalidFormat");
		}
		
		int svgHeight = svg.getHeight();
		int svgWidth = svg.getWidth();

		if (widths != null) {
			boolean match = false;
			for (int i = 0; i < widths.length; i++) {
				if (svgWidth == widths[i]) {
					match = true;
					break;
				}
			}
			if (!match) {
				ErrorUtils.reject(this, "image.size.mismatch");
				return;
			}
		}
		else if (svgWidth < minWidth || (maxWidth > 0 && svgWidth > maxWidth)) {
			ErrorUtils.reject(this, "image.size.mismatch");
			return;
		}

		if (heights != null) {
			boolean match = false;
			for (int i = 0; i < heights.length; i++) {
				if (svgHeight == heights[i]) {
					match = true;
					break;
				}
			}
			if (!match) {
				ErrorUtils.reject(this, "image.size.mismatch");
			}
		}
		else if (svgHeight < minHeight || (maxHeight > 0 && svgHeight > maxHeight)) {
			ErrorUtils.reject(this, "image.size.mismatch");
		}
	}
	
	public class PreviewElement extends TemplateElement
			implements ContentElement {

		public PreviewElement() {
		}

		public void handleContentRequest(HttpServletRequest request,
				HttpServletResponse response) throws IOException {

			RiotFile file = getPreviewFile();
			
			if (file != null) {
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
		
		public int getPreviewWidth() {
			return maxWidth > 0 ? maxWidth : previewWidth;
		}
		
		public int getPreviewHeight() {
			return maxHeight > 0 ? maxHeight : previewHeight;
		}
		
		public String getSizeLabel() {
			StringBuilder sb = new StringBuilder();
			if (widths != null && widths.length > 0) {
				append(sb, widths);
			}
			else {
				append(sb, minWidth, maxWidth);
			}
			sb.append(" &times; ");
			if (heights != null && heights.length > 0) {
				append(sb, heights);
			}
			else {
				append(sb, minHeight, maxHeight);
			}
			return sb.toString();
		}
		
		public RiotSvg getSvg() {
			return (RiotSvg) SvgUpload.this.getPreviewFile();
		}
		
		private void append(StringBuilder sb, int[] arr) {
			for (int i = 0; i < arr.length; i++) {
				if (i > 0) {
					sb.append(" / ");
				}
				sb.append(arr[i]).append(" px");
			}
		}
		
		private void append(StringBuilder sb, int min, int max) {
			if (min > 0) {
				sb.append(min);
			}
			else {
				sb.append("1");
			}
			sb.append("-");
			if (max > 0) {
				sb.append(max);
			}
			else {
				sb.append("&infin;");
			}
			sb.append(" px");
		}
		

	}

}
