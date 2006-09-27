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
