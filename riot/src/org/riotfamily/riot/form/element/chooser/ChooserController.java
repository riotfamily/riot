package org.riotfamily.riot.form.element.chooser;

import org.riotfamily.riot.editor.EditorRepository;

public interface ChooserController {

	public String getUrl(String targetEditorId);
	
	public EditorRepository getEditorRepository();

}
