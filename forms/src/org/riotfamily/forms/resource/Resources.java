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
			new ScriptResource("riot-js/window/dialog.js", "riot.window.Dialog", 
			PROTOTYPE);
	
	public static final ScriptResource RIOT_UTIL = 
			new ScriptResource("riot-js/util.js", "RElement", PROTOTYPE);
	
	public static final ScriptResource RIOT_EFFECTS = 
			new ScriptResource("riot-js/effects.js", "Effect.Remove", 
			SCRIPTACULOUS_EFFECTS);
}
