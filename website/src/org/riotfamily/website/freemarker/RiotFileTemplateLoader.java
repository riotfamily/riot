package org.riotfamily.website.freemarker;

import java.io.File;
import java.io.IOException;

import org.riotfamily.cachius.CachiusContext;

import freemarker.cache.FileTemplateLoader;

/**
 * TemplateLoader that invokes {@link CachiusContext#addInvolvedFile(File)}
 * to track files involved in the generation of cached content.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class RiotFileTemplateLoader extends FileTemplateLoader {

	public RiotFileTemplateLoader(File baseDir) throws IOException {
		super(baseDir);
	}

	@Override
	public Object findTemplateSource(String name) throws IOException {
		File file = (File) super.findTemplateSource(name);
		if (file != null) {
			CachiusContext.addFile(file);
		}
		return file;
	}
}
