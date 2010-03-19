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
package org.riotfamily.forms2.element;

import org.riotfamily.common.web.mvc.multipart.ProgressMonitor;
import org.riotfamily.common.web.mvc.multipart.UploadProgress;
import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.base.ElementState;
import org.riotfamily.forms2.base.TypedState;
import org.riotfamily.forms2.base.UserInterface;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;
import org.springframework.web.multipart.MultipartFile;

public class FileUpload extends Element {

	@Override
	protected ElementState createState(Value value) {
		return new State();
	}
	
	protected static class State extends TypedState<FileUpload> {

		private String uploadId = ProgressMonitor.nextUploadId();
		
		private String contentType;
		
		private String originalFilename;
		
		@Override
		protected void renderInternal(Html html, FileUpload element) {
			html.div("progress");
			html.elem("iframe").cssClass("upload").attr("name", getId() + "_target");
			renderForm(html);
		}
		
		private Html renderForm() {
			Html html = newHtml();
			renderForm(html);
			return html;
		}
		
		private void renderForm(Html html) {
			html.multipartForm("?uploadId=" + uploadId)
				.attr("target", getId() + "_target")
				.propagate("submit", "updateProgress", String.format("'%s'", uploadId))
				.hiddenInput("formId", getFormState().getId()).up()
				.hiddenInput("stateId", getId()).up()
				.hiddenInput("handler", "onFinish").up()
				.input("file", "file", null).up()
				.submit("Upload");
		}
		
		/**
		 * Updates the progress bar and schedules itself for re-invocation 
		 * every 500 milliseconds.
		 * <p>
		 * If meanwhile a new <code>uploadId</code> has been assigned, i.e.
		 * {@link #onFinish} has been invoked, the method does nothing.
		 */
		public void updateProgress(UserInterface ui, FileUpload element, String uploadId) {
			if (uploadId.equals(this.uploadId)) {
				Html html = newHtml();
				UploadProgress progress = ProgressMonitor.getProgress(uploadId);
				if (progress != null) {
					html.div("track").div("bar").style("width: %s%%", progress.getProgress());
				}
				else {
					html.messageText("Waiting for data");
				}
				ui.update(this, ".progress", html);
				ui.schedule(this, "updateProgress", uploadId, 500);
			}
		}
		
		/**
		 * Handler method that is invoked when the upload has finished.
		 * It assigns a new <code>uploadId</code> and re-renders the form.
		 */
		public void onFinish(UserInterface ui, FileUpload element, MultipartFile file) {
			contentType = file.getContentType();
			originalFilename = file.getOriginalFilename();
			uploadId = ProgressMonitor.nextUploadId();
			ui.update(this, ".progress", null);
			ui.replace(this, "form", renderForm());
		}
		
		@Override
		protected void populateInternal(Value value, FileUpload element) {
		}
		
	}

}
