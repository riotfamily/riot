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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.cachius.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.ItemUpdater;
import org.springframework.web.servlet.View;


/**
 * View implementation that is used as wrapper for views returned by a
 * cacheable controller.
 * 
 * @author Felix Gnass
 */
public class CachingView implements View {

    private Log log = LogFactory.getLog(CachingView.class);
    
    private View view;
    
    private HttpServletResponse response;
    
    private ItemUpdater cacheItemUpdate;
    
    public CachingView(View view, HttpServletResponse response, 
    		ItemUpdater cacheItemUpdate) {
    	
        this.view = view;
        this.response = response;
        this.cacheItemUpdate = cacheItemUpdate;
    }
    
    public String getContentType() {
    	return view.getContentType();
    }
    
    /**
     */
    public void render(Map model, HttpServletRequest request, 
            HttpServletResponse response) throws Exception {
        
        if (view != null) {
            view.render(model, request, this.response);
        }
        else {
            log.debug("No view found!");
        }
        this.response.flushBuffer();
        cacheItemUpdate.updateCacheItem();
    }

}
