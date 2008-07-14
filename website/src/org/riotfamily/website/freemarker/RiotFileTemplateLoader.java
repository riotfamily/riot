/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.freemarker;

import java.io.File;
import java.io.IOException;

import org.riotfamily.cachius.TaggingContext;

import freemarker.cache.FileTemplateLoader;

/**
 * TemplateLoader that invokes {@link TaggingContext#addInvolvedFile(File)}
 * to track files involved in the generation of cached content.
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
			TaggingContext.addFile(file);
		}
		return file;
	}
}
