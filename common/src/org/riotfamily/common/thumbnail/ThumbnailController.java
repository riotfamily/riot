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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.thumbnail;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.file.FileStore;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ThumbnailController implements Controller {
	
	private Thumbnailer thumbnailer;
	
	private FileStore fileStore;
	
	public void setFileStore(FileStore fileStore) {
		this.fileStore = fileStore;
	}
	
	public void setThumbnailer(Thumbnailer thumbnailer) {
		this.thumbnailer = thumbnailer;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		String fileName = ServletRequestUtils.getStringParameter(request, "sourceFile");
		File file = fileStore.retrieve(fileName);
		if (file != null && file.exists()) {
			thumbnailer.renderThumbnail(file, null, response.getOutputStream());
		}
		return null;
	}	
	
}
