package org.riotfamily.forms.element.core;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.riotfamily.forms.element.DHTMLElement;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.ScriptSequence;
import org.riotfamily.forms.template.TemplateUtils;


/**
 * A WYSIWYG richtext editor based on TinyMCE. 
 */
public class TinyMCE extends Textarea implements ResourceElement, DHTMLElement {

	private List resources;
	
	public TinyMCE() {
		resources = Collections.singletonList(
				new ScriptSequence(new ScriptResource[] {

			new ScriptResource("tiny_mce/tiny_mce_src.js", "tinyMCE"),
			new ScriptResource("tiny_mce/strict_mode_fix.js", "tinyMCE.addControl")
		}));
	}
	
	public Collection getResources() {
		return resources;
	}
	
	public String getLanguage() {
		String lang = getFormContext().getLocale().getLanguage().toLowerCase();
		URL languageScript = getClass().getResource(
				"/org/riotfamily/resources/tiny_mce/langs/"	+ lang + ".js");
		
		if (languageScript == null) {
			lang = "en";
		}
		return lang;
	}
	
	public String getInitScript() {
		return TemplateUtils.getInitScript(this);
	}
	
	public String getPrecondition() {
		return "tinyMCE.addControl";
	}

}
