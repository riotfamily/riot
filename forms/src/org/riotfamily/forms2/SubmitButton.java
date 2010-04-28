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
package org.riotfamily.forms2;

import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.base.ElementState;
import org.riotfamily.forms2.base.UserInterface;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;

public class SubmitButton extends Element {
	
	private transient String label;
	
	private transient FormSubmissionHandler submissionHandler;

	public SubmitButton(String label, FormSubmissionHandler submissionHandler) {
		this.label = label;
		this.submissionHandler = submissionHandler;
		//form.addStatusListener(this);
	}
			
	public class State extends ElementState {
		
		@Override
		public void setValue(Object value) {
		}
		
		@Override
		protected void renderElement(Html html) {
			html.button("click").messageText(label);
		}
		
		public void click(UserInterface ui, String value) {
			ui.eval(submissionHandler.onSubmit(getFormState()));
		}
		
		@Override
		public void populate(Value value) {
		}
	}

}
