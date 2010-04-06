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

import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.base.ElementState;
import org.riotfamily.forms2.base.UserInterface;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;

public class PasswordField extends Element {

	public class State extends ElementState {

		private String password;
		
		private String confirmation;
		
		private boolean alreadySet;
		
		private boolean setNew;
		
		private String strength;
		
		@Override
		protected void onInit(Value value) {
			this.alreadySet = value.get() != null;
		}
		
		@Override
		public void populate(Value value) {
			if (setNew) {
				value.set(password);
			}
		}
		
		public void updatePassword(UserInterface ui, String value) {
			password = value;
			//calculate strength
			//buildStrength(ui.replace(this, ".strength"));
		}
		
		public void updateConfirmation(UserInterface ui, String value) {
			confirmation = value;
		}
		
		public void foo(UserInterface ui, String value) {
			
		}
		
		public void buildStrength(Html html) {
			html.div("strength")
				.div(strength)
				.messageText("strength." + strength);
		}
		
		@Override
		protected void renderElement(Html html) {
			if (setNew) { 
				if (alreadySet) {
					//html.button("Keep old password");
				}
				html.label("Enter new password");
				html.input("password", password);
				
				buildStrength(html);
				html.label("Re-type password");
				html.input("password", confirmation).addClass("confirm");
			}
			else {
				//html.button("Change password");
			}
		}
	}
	
}
