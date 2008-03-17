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
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.forms.CompositeElement;
import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.event.JavaScriptEventAdapter;
import org.riotfamily.forms.fileupload.UploadStatus;
import org.riotfamily.forms.request.FormRequest;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.StylesheetResource;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.service.ProcessingService;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;


/**
 * A widget to upload files.
 */
public class FileUpload extends CompositeElement implements Editor,
		ResourceElement {

	protected static FormResource RESOURCE = new ScriptResource(
			"inline-upload.js", null, 
			new StylesheetResource("style/riot-media.css"));

	private ProcessingService processingService;
	
	private String processor;
	
	private RiotFile file;
	
	private RiotFile rejectedFile;

	private boolean fileUploaded;
	
	public FileUpload(ProcessingService processingService) {
		this.processingService = processingService;
		addComponent(new UploadElement());
		addComponent(new RemoveButton());
		addComponent(createPreviewElement());
		setSurroundByDiv(true);
	}
	
	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public FormResource getResource() {
		return RESOURCE;
	}
	
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
		if (fileUploaded && processor != null) {
			processingService.process(file.getFileData(), processor);
		}
		return file;
	}

	protected RiotFile getFile() {
		return file;
	}
	
	protected RiotFile getRejectedFile() {
		return rejectedFile;
	}
	
	protected RiotFile getPreviewFile() {
		return rejectedFile != null ? rejectedFile : file;
	}
	
	/**
	 * Though this is a composite element we want it to be treated as a
	 * single widget.
	 */
	public boolean isCompositeElement() {
		return false;
	}
	
	protected RiotFile createRiotFile(MultipartFile multipartFile) 
			throws IOException {
		
		return new RiotFile(multipartFile);
	}
	
	protected void processRequestInternal(FormRequest request) {
		validate(getPreviewFile());
	}
	
	private void validate(RiotFile file) {
		ErrorUtils.removeErrors(this);
		if (file != null) {
			validateFile(file);
		}
		else if (isRequired()) {
			ErrorUtils.rejectRequired(this);
		}
	}
	
	protected void validateFile(RiotFile file) {
	}
	
	protected void setNewFile(RiotFile file) {
		validate(file);
		if (!ErrorUtils.hasErrors(this)) {
			this.file = file;
			this.rejectedFile = null;
			this.fileUploaded = true;
		}
		else {
			this.rejectedFile = file;
			this.file = null;
		}
	}
	
	public boolean isFileUploaded() {
		return this.fileUploaded;
	}
	
	public class UploadElement extends TemplateElement
			implements JavaScriptEventAdapter {

		private String uploadId;

		private UploadStatus status;
		
		private volatile boolean processing;
		
		private volatile boolean completed;

		public UploadElement() {
			this.uploadId = UploadStatus.createUploadId();
		}

		public String getUploadId() {
			return uploadId;
		}

		public String getUploadUrl() {
			return getFormContext().getUploadUrl(uploadId);
		}

		public UploadStatus getStatus() {
			return status;
		}
		
		public boolean isProcessing() {
			return processing;
		}

		public void processRequestInternal(FormRequest request) {
			log.debug("Processing " + getParamName());
			MultipartFile multipartFile = request.getFile(getParamName());
			if ((multipartFile != null) && (!multipartFile.isEmpty())) {
				try {
					processing = true;
					setNewFile(createRiotFile(multipartFile));
				}
				catch (IOException e) {
					log.error("error saving uploaded file", e);
				}
				processing = false;
				completed = true;
			}
		}

		public int getEventTypes() {
			return JavaScriptEvent.NONE;
		}

		public void handleJavaScriptEvent(JavaScriptEvent event) {
			status = UploadStatus.getStatus(uploadId);
			if (getFormListener() != null) {
				if (status != null) {
					if (status.isError()) {
						ErrorUtils.reject(FileUpload.this, "error.uploadFailed");
						getFormListener().elementChanged(FileUpload.this);
					}
					else if (completed) {
						status.clear();
						status = null;
						completed = false;
						getFormListener().elementChanged(FileUpload.this);
					}
					else {
						log.debug("Progress: " + status.getProgress());
						getFormListener().elementChanged(this);
						getFormListener().refresh(this);
					}
				}
				else {
					getFormListener().refresh(this);
				}
			}
		}

	}

	private class RemoveButton extends Button {

		private RemoveButton() {
			setCssClass("remove-file");
		}

		public String getLabel() {
			return "Remove";
		}

		protected void onClick() {
			file = null;
			ErrorUtils.removeErrors(FileUpload.this);
			if (getFormListener() != null) {
				getFormListener().elementChanged(FileUpload.this);
			}
		}

		public void render(PrintWriter writer) {
			if (!FileUpload.this.isRequired() && file != null) {
				super.render(writer);
			}
		}

		public int getEventTypes() {
			return JavaScriptEvent.ON_CLICK;
		}
	}

	public class PreviewElement extends TemplateElement
			implements ContentElement {

		public RiotFile getFile() {
			return file;
		}
		
		public void handleContentRequest(HttpServletRequest request,
				HttpServletResponse response) throws IOException {

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
