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
package org.riotfamily.forms.resource;


public final class Resources {

	private Resources() {
	}
	
	public static final ScriptResource PROTOTYPE = 
			new ScriptResource("prototype/prototype.js", "Prototype");
	
	public static final ScriptResource SCRIPTACULOUS_EFFECTS = 
			new ScriptResource("scriptaculous/effects.js", "Effect", PROTOTYPE);
	
	public static final ScriptResource SCRIPTACULOUS_DRAG_DROP = 
			new ScriptResource("scriptaculous/dragdrop.js", "Droppables",
			SCRIPTACULOUS_EFFECTS);
	
	public static final ScriptResource SCRIPTACULOUS_CONTROLS = 
			new ScriptResource("scriptaculous/controls.js", "Autocompleter", 
			SCRIPTACULOUS_EFFECTS);
	
	public static final ScriptResource SCRIPTACULOUS_SLIDER = 
			new ScriptResource("scriptaculous/slider.js", "Control.Slider", 
			PROTOTYPE);
		
	public static final ScriptResource RIOT_DIALOG = 
			new ScriptResource("riot/window/dialog.js", "riot.window.Dialog", 
			PROTOTYPE);
	
	public static final ScriptResource RIOT_UTIL = 
			new ScriptResource("riot/util.js", "RElement", PROTOTYPE);
	
	public static final ScriptResource RIOT_EFFECTS = 
			new ScriptResource("riot/effects.js", "Effect.Remove", 
			SCRIPTACULOUS_EFFECTS);
}
