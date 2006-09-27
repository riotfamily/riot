package org.riotfamily.forms.resource;

public final class Resources {

	private Resources() {
	}
	
	public static final ScriptResource PROTOTYPE = 
			new ScriptResource("prototype/prototype.js", "Prototype");
	
	public static final ScriptResource SCRIPTACULOUS_EFFECTS = 
			new ScriptResource("scriptaculous/effects.js", "Effect");
	
	public static final ScriptResource SCRIPTACULOUS_DRAG_DROP = 
			new ScriptResource("scriptaculous/dragdrop.js", "Droppables");
	
	public static final ScriptSequence SCRIPTACULOUS_DRAG_DROP_SEQ =
			new ScriptSequence(new ScriptResource[] {
				PROTOTYPE, SCRIPTACULOUS_EFFECTS, SCRIPTACULOUS_DRAG_DROP 
			});
	
	public static final ScriptResource RIOT_WINDOW_CALLBACK = 
			new ScriptResource("riot-js/window-callback.js", "WindowCallback");
				
	public static final ScriptResource RIOT_NUMBER_INPUT = 
			new ScriptResource("riot-js/number-input.js");
	
	public static final ScriptResource RIOT_IMAGE_CHECKBOX = 
		new ScriptResource("riot-js/image-checkbox.js");

}
