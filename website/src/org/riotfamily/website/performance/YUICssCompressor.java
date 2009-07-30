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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.performance;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.springframework.util.FileCopyUtils;

import com.yahoo.platform.yui.compressor.CssCompressor;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class YUICssCompressor implements Compressor {

	private int linebreak = -1;

	private boolean enabled = true;

	
	/**
	 * Enables the Compressor. Per default the compressor is enabled. 
	 * @param enabled true to enabled, false to disable this compressor
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Sets the column number after which a line break should be inserted.
	 * Default is <code>-1</code>, which means that no breaks will be added.
	 */
	public void setLinebreak(int linebreak) {
		this.linebreak = linebreak;
	}
	
	/**
	 * Reads a cascading style sheet (CSS) from the given Reader and writes
	 * the compressed version to the specified Writer. 
	 */
	public void compress(Reader in, Writer out) throws IOException {
		if (enabled) {
			CssCompressor compressor = new CssCompressor(in);
			compressor.compress(out, linebreak);
		}
		else {
			FileCopyUtils.copy(in, out);
		}
	}
}
