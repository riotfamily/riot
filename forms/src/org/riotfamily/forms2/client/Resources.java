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
package org.riotfamily.forms2.client;


public final class Resources {

	private Resources() {
	}
	
	public static final ScriptResource JQUERY_UI = 
			new ScriptResource("jquery/ui/jquery-ui.js", "jQuery.ui",
			new StylesheetResource("jquery/ui/jquery-ui.css"));
	
	public static final ScriptResource RIOT_FORMS = 
			new ScriptResource("forms/form.js", "riot.form", JQUERY_UI, 
			new StylesheetResource("forms/form.css"));
	
}
