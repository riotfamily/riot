package org.riotfamily.cachius.http.content;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.riotfamily.cachius.http.support.ScanWriter;

public class Directives {

	public static final Directives DEFAULTS = new Directives(
			new SessionIdDirective(),
			new IncludeDirective());
	
	private List<Directive> directives = new ArrayList<Directive>();
	
	public Directives(Directive... directives) {
		for (Directive d : directives) {
			this.directives.add(d);
		}
	}

	public ContentFragment parse(String s) {
		for (Directive directive : directives) {
			ContentFragment fragment = directive.parse(s);
			if (fragment != null) {
				return fragment;
			}
		}
		return null;
	}

	public ScanWriter createWriter(Writer out) {
		return new ScanWriter(out, "(@riot.", ')');
	}

}
