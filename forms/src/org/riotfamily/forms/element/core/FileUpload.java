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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.thumbnail.Thumbnailer;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.file.FileStore;
import org.riotfamily.forms.ajax.JavaScriptEvent;
import org.riotfamily.forms.ajax.JavaScriptEventAdapter;
import org.riotfamily.forms.bind.Editor;
import org.riotfamily.forms.bind.EditorBinder;
import org.riotfamily.forms.element.ContentElement;
import org.riotfamily.forms.element.DHTMLElement;
import org.riotfamily.forms.element.support.CompositeElement;
import org.riotfamily.forms.element.support.TemplateElement;
import org.riotfamily.forms.error.ErrorUtils;
import org.riotfamily.forms.fileupload.UploadStatus;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.StylesheetResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


/**
 * A widget to upload files.
 */
public class FileUpload extends CompositeElement implements Editor, 
		ResourceElement {

	private static MimetypesFileTypeMap defaultMimetypesMap = 
			new MimetypesFileTypeMap();

	private Collection resources = new LinkedList();

	private UploadElement upload = new UploadElement();

	private PreviewElement preview = new PreviewElement();

	private String filenameProperty;

	private String contentTypeProperty;

	private String sizeProperty;

	private MimetypesFileTypeMap mimetypesMap;
	
	private FileStore fileStore;
	
	private Thumbnailer thumbnailer;
		
	private String uri;
	
	private File file;
	
	private File tempFile;

	
	private String fileName;

	private String contentType;

	private Long size;


	public FileUpload() {
		addComponent(upload);
		addComponent(new RemoveButton());
		addComponent(preview);
		setSurroundBySpan(true);
		resources.add(new StylesheetResource("form/fileupload/progress.css"));
		resources.add(new ScriptResource("form/fileupload/upload.js"));
	}
	
	public FileStore getFileStore() {
		return this.fileStore;
	}

	public void setThumbnailer(Thumbnailer thumbnailer) {
		this.thumbnailer = thumbnailer;
	}

	public void setFileStore(FileStore fileStore) {
		this.fileStore = fileStore;
	}

	public Collection getResources() {
		return resources;
	}

	public void setFilenameProperty(String property) {
		this.filenameProperty = property;
	}

	public void setContentTypeProperty(String property) {
		this.contentTypeProperty = property;
	}

	public void setSizeProperty(String property) {
		this.sizeProperty = property;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public Long getSize() {
		return size;
	}
	
	public String getFormatedSize() {
		if (size != null) {
			return FormatUtils.formatByteSize(size.longValue());
		}
		return null;
	}

	public boolean isPresent() {
		return file != null;
	}
	
	protected File getFile() {
		return this.file;
	}
	
	protected void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setMimetypesMap(MimetypesFileTypeMap mimetypesMap) {
		this.mimetypesMap = mimetypesMap;
	}

	protected String contentType(File file) {
		if (mimetypesMap == null) {
			mimetypesMap = defaultMimetypesMap;
		}
		return mimetypesMap.getContentType(file);
	}
	
	public boolean isWebImage() {
		return contentType != null && (
				contentType.equals("image/gif") || 
				contentType.equals("image/jpeg") ||
				contentType.equals("image/pjpeg") ||
				contentType.equals("image/jpg") ||
				contentType.equals("image/x-png") ||
				contentType.equals("image/png"));
	}
	
	public UploadElement getUpload() {
		return upload;
	}
	

	public void setValue(Object value) {
		log.debug("Value set to: " + value);
		if (value == null) {
			return;
		}
		if (fileStore != null) {
			if (!(value instanceof String)) {
				throw new IllegalArgumentException("Value is not a String: " + value);
			}
			uri = (String) value;
			file = fileStore.retrieve(uri);
		}
		else {
			if (!(value instanceof File)) {
				throw new IllegalArgumentException("Value is not a File: " + value);
			}
			uri = null;
			file = (File) value;
		}
		
		tempFile = null;
		
		EditorBinder editorBinder = getEditorBinding().getEditorBinder();
		if (filenameProperty != null) {
			fileName = (String) editorBinder.getPropertyValue(filenameProperty);
		}
		
		if (contentTypeProperty != null) {
			contentType = (String) editorBinder.getPropertyValue(contentTypeProperty);
		}
		else {
			contentType = contentType(file);
		}
		size = new Long(file.length());
	}

	public Object getValue() {
		EditorBinder editorBinder = getEditorBinding().getEditorBinder();
		if (filenameProperty != null) {
			editorBinder.setPropertyValue(filenameProperty, fileName);
		}
		if (contentTypeProperty != null) {
			editorBinder.setPropertyValue(contentTypeProperty, contentType);
		}
		if (sizeProperty != null) {
			editorBinder.setPropertyValue(sizeProperty, size);
		}
		
		if (fileStore != null) {
			if (tempFile != null) {
				try {
					uri = fileStore.store(tempFile, fileName, uri);
					tempFile = null;
					file = fileStore.retrieve(uri);
				}
				catch (IOException e) {
					throw new RuntimeException("Failed to store file: "
							+ e.getMessage(), e);
				}
			}
			return uri;
		}
		else {
			if (tempFile != null) {
				file = tempFile;
				tempFile = null;
			}
			return file;
		}
	}

	protected final void validate() {
		if (isRequired() && file == null) {
			ErrorUtils.rejectRequired(this);
		}
		if (file != null) {
			validateFile(file);
		}
	}
		
	protected void validateFile(File file) {
	}
	
	/**
	 * Though this is a composite element we want it to be treated as a
	 * single widget.
	 */
	public boolean isCompositeElement() {
		return false;
	}

	public class UploadElement extends TemplateElement
			implements JavaScriptEventAdapter {
			
		private String uploadId;
		
		private UploadStatus status;
		
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
		
		public void processRequestInternal(HttpServletRequest request) {
			log.debug("Processing " + getParamName());
			if (request instanceof MultipartHttpServletRequest) {
				MultipartHttpServletRequest mpRequest = 
						(MultipartHttpServletRequest) request;
			
				MultipartFile multipartFile = mpRequest.getFile(getParamName());
			
				if ((multipartFile != null) && (!multipartFile.isEmpty())) {
					try {
						fileName = multipartFile.getOriginalFilename();
						contentType = multipartFile.getContentType();
						if (tempFile != null) {
							tempFile.delete();
						}
						tempFile = File.createTempFile("000", "."
								+ FormatUtils.getExtension(fileName));
						
						multipartFile.transferTo(tempFile);
						log.debug("stored at: " + tempFile.getAbsolutePath());
						
						file = tempFile;
						
						size = new Long(tempFile.length());
						
						validateFile(file);
						
						log.debug("File uploaded: " + fileName + " (" 
								+ contentType + ")");
						
					}
					catch (IOException e) {
						log.error("error saving uploaded file");
					}
				}
			}
		}
						
		/**
		 * @see org.riotfamily.forms.ajax.JavaScriptEventAdapter#getEventTypes()
		 */
		public int getEventTypes() {
			return 0;
		}
		
		/**
		 * 
		 */
		public void handleJavaScriptEvent(JavaScriptEvent event) {
			status = UploadStatus.getStatus(uploadId);
			if (getFormListener() != null) {
				getFormListener().elementChanged(this);
				if (status != null) {
					log.debug("Progress: " + status.getProgress());
					getFormListener().refresh(this);
				}
				else {
					log.debug("No status.");
					getFormListener().elementChanged(FileUpload.this);
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
			tempFile = null;
			uri = null;
			ErrorUtils.removeErrors(FileUpload.this);
			if (getFormListener() != null) {
				getFormListener().elementChanged(FileUpload.this);			
			}			
		}
		
		public void render(PrintWriter writer) {
			if (!FileUpload.this.isRequired() && isPresent()) {
				super.render(writer);
			}
		}
		
		public int getEventTypes() {
			return JavaScriptEvent.ON_CLICK;
		}
	}
	
	public class PreviewElement extends TemplateElement 
			implements ContentElement, DHTMLElement {

		public PreviewElement() {
			setAttribute("file", FileUpload.this);
		}
		
		public void handleContentRequest(HttpServletRequest request,
				HttpServletResponse response) throws IOException {
			
			if (file != null && file.exists()) {
				response.setDateHeader("Expires", 0);
				response.setContentType(contentType);
				if (thumbnailer != null && thumbnailer.supports(contentType)) {
					thumbnailer.renderThumbnail(file, contentType, 
							response.getOutputStream());
				}
				else {
					response.setContentLength(size.intValue());
					FileCopyUtils.copy(new FileInputStream(file), 
							response.getOutputStream());
				}
			}
			else {
				response.sendError(HttpServletResponse.SC_NO_CONTENT);
			}
		}
		
		public boolean isPreviewAvailable() {
			return isWebImage() || (thumbnailer != null && contentType != null
					&& thumbnailer.supports(contentType)); 
		}
		
		public String getPreviewUrl() {
			return getFormContext().getContentUrl(this);
		}

		public String getInitScript() {
			return "initPreview('" + getId() + "', '" + getPreviewUrl() + "')";
		}
		
		public String getPrecondition() {
			return "initPreview";
		}
	}


}
