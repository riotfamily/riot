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
import org.riotfamily.forms2.base.FormState;
import org.riotfamily.forms2.base.TypedState;
import org.riotfamily.forms2.base.UserInterface;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;

public class SubmitButton extends Element {
	
	private String label;
	
	private FormSubmissionHandler submissionHandler;

	public SubmitButton(String label, FormSubmissionHandler submissionHandler) {
		this.label = label;
		this.submissionHandler = submissionHandler;
		//form.addStatusListener(this);
	}
	
	public String render(FormState formState) {
		Html html = formState.newHtml();
		formState.getElementState(this).render(html, this);
		return html.toString();
	}
			
	public static class State<T extends SubmitButton> extends TypedState<T> {
		
		@Override
		public void renderInternal(Html html, T button) {
			html.button("click").messageText(button.label);
		}
		
		public void click(UserInterface ui, T button, String value) {
			button.submissionHandler.onSubmit(getFormState());
		}
		
		@Override
		public void populateInternal(Value value, T button) {
		}
	}

}
