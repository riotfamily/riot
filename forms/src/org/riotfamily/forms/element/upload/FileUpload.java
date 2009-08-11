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
package org.riotfamily.forms.element.upload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUpload extends AbstractFileUpload {

	private byte[] data;
	
	private byte[] uploadedData;
	
	
	@Override
	protected Element createPreviewElement() {
		return new PreviewElement();
	}

	protected byte[] getPreviewData() {
		if (uploadedData != null) {
			return uploadedData;
		}
		return data;
	}
	
	@Override
	protected boolean isFilePresent() {
		return data != null || uploadedData != null;
	}

	@Override
	protected void onRemove() {
		uploadedData = null;
		data = null;
	}

	@Override
	protected void onUpload(MultipartFile multipartFile) throws IOException {
		uploadedData = multipartFile.getBytes();
	}

	public Object getValue() {
		if (uploadedData != null) {
			data = uploadedData;
		}
		return data;
	}

	public void setValue(Object value) {
		data = (byte[]) value;
	}
	
	@Override
	protected void validate() {
		ErrorUtils.removeErrors(this);
		if (isRequired() && !isFilePresent()) {
			ErrorUtils.rejectRequired(this);
		}
	}
	
	public class PreviewElement extends TemplateElement
			implements ContentElement {
		
		public boolean isFilePresent() {
			return FileUpload.this.isFilePresent();
		}
		
		public void handleContentRequest(HttpServletRequest request,
				HttpServletResponse response) throws IOException {
		
			byte[] data = getPreviewData(); 
			if (data!= null) {
				response.setDateHeader("Expires", 0);
				response.setHeader("Content-Type", "application/x-download");
				FileCopyUtils.copy(data, response.getOutputStream());
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
