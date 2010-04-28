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
package org.riotfamily.forms.element;

import org.riotfamily.common.web.mvc.multipart.ProgressMonitor;
import org.riotfamily.common.web.mvc.multipart.UploadProgress;
import org.riotfamily.forms.base.Element;
import org.riotfamily.forms.base.UserInterface;
import org.riotfamily.forms.base.Element.State;
import org.riotfamily.forms.client.Html;
import org.springframework.web.multipart.MultipartFile;

public class FileUpload extends Element {
	
	public class State extends Element.State {

		private String uploadId = ProgressMonitor.nextUploadId();

		private String contentType;
		
		private String originalFilename;
		
		@Override
		public void setValue(Object value) {
			// TODO Auto-generated method stub
		}
		
		@Override
		protected void renderElement(Html html) {
			html.div("progress")
				.div("ui-progressbar ui-widget ui-widget-content ui-corner-all")
				.div("ui-progressbar-value ui-widget-header ui-corner-left").style("width:0")
				.up(2).div("status");

			html.elem("iframe").cssClass("upload").attr("name", "%s_target", id());
			renderForm(html);
		}
		
		private Html renderForm() {
			Html html = newHtml();
			renderForm(html);
			return html;
		}
		
		private void renderForm(Html html) {
			html.multipartForm("?uploadId=" + uploadId)
				.attr("target", "%s_target", id())
				.propagate("submit", "updateProgress", String.format("'%s'", uploadId))
				.hiddenInput("formId", getFormState().id()).up()
				.hiddenInput("stateId", id()).up()
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
		public void updateProgress(UserInterface ui, String uploadId) {
			if (uploadId.equals(this.uploadId)) {
				Html status = newHtml();
				UploadProgress progress = ProgressMonitor.getProgress(uploadId);
				if (progress != null) {
					status.text(progress.getDataTransfered());
					ui.invoke(this, ".ui-progressbar-value", "css", "width", progress.getPercentage() + "%");
				}
				else {
					status.messageText("Waiting for data");
				}
				ui.update(this, ".status", status);
				ui.schedule(this, "updateProgress", uploadId, 500);
			}
		}
		
		/**
		 * Handler method that is invoked when the upload has finished.
		 * It assigns a new <code>uploadId</code> and re-renders the form.
		 */
		public void onFinish(UserInterface ui, MultipartFile file) {
			contentType = file.getContentType();
			originalFilename = file.getOriginalFilename();
			uploadId = ProgressMonitor.nextUploadId();
			ui.replace(this, "form", renderForm());
		}
		
	}

}
