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
import java.io.PrintWriter;

import org.riotfamily.forms.CompositeElement;
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
import org.riotfamily.forms.ui.Dimension;
import org.springframework.web.multipart.MultipartFile;

public abstract class AbstractFileUpload extends CompositeElement implements Editor,
		ResourceElement {
	
	protected static FormResource RESOURCE = new ScriptResource(
			"form/inline-upload.js", null);
	
	public AbstractFileUpload() {
		addComponent(new UploadElement());
		addComponent(new RemoveButton());
		addComponent(createPreviewElement());
	}
	
	public FormResource getResource() {
		return RESOURCE;
	}
	
	/**
	 * Though this is a composite element we want it to be treated as a
	 * single widget.
	 */
	@Override
	public boolean isCompositeElement() {
		return false;
	}
	
	@Override
	protected final void processRequestInternal(FormRequest request) {
		validate();
	}
	
	protected void validate() {
	}
	
	protected void uploadCompleted() {
	}
	
	protected abstract Element createPreviewElement();
	
	protected abstract void onUpload(MultipartFile multipartFile) 
			throws IOException;
	
	protected abstract void onRemove();
	
	protected abstract boolean isFilePresent();
	
	
	public class UploadElement extends TemplateElement
			implements JavaScriptEventAdapter {
		
		private String uploadId;
		
		private UploadStatus status;
		
		private volatile boolean processing;
		
		private volatile boolean completed;
		
		public UploadElement() {
			setWrap(false);
			this.uploadId = UploadStatus.createUploadId();
		}
		
		@Override
		public String getEventTriggerId() {		
			return getId();
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
		
		@Override
		public void processRequestInternal(FormRequest request) {
			log.debug("Processing " + getParamName());
			MultipartFile multipartFile = request.getFile(getParamName());
			if ((multipartFile != null) && (!multipartFile.isEmpty())) {
				try {
					processing = true;
					onUpload(multipartFile);
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
						ErrorUtils.reject(AbstractFileUpload.this, "error.uploadFailed");
						getFormListener().elementChanged(AbstractFileUpload.this);
					}
					else if (completed) {
						status.clear();
						status = null;
						completed = false;
						getFormListener().elementChanged(AbstractFileUpload.this);
						uploadCompleted();
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
		
		@Override
		public Dimension getDimension() {
			return getFormContext().getSizing().getTextfieldSize();
		}
		
	}
	
	private class RemoveButton extends Button {

		private RemoveButton() {
			setStyleClass("remove-file");
		}

		@Override
		public String getLabel() {
			return "Remove";
		}

		@Override
		protected void onClick() {
			onRemove();
			ErrorUtils.removeErrors(AbstractFileUpload.this);
			if (getFormListener() != null) {
				getFormListener().elementChanged(AbstractFileUpload.this);
			}
		}

		@Override
		public void renderInternal(PrintWriter writer) {
			if (!AbstractFileUpload.this.isRequired() && isFilePresent()) {
				super.renderInternal(writer);
			}
		}

		@Override
		public int getEventTypes() {
			return JavaScriptEvent.ON_CLICK;
		}
	}

}
