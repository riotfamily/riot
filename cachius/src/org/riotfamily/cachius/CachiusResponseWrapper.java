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
package org.riotfamily.cachius;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.riotfamily.cachius.support.MultiplexPrintWriter;
import org.riotfamily.cachius.support.MultiplexServletOutputStream;


/**
 * A HttpServletResponseWrapper that captures the response and updates
 * the accociated CacheItem in case no error occures during request processing.
 *
 * @author Felix Gnass
 */
public class CachiusResponseWrapper extends HttpServletResponseWrapper {

    private ItemUpdater cacheItemUpdate;
    
    private ServletOutputStream outputStream;
    
    private PrintWriter writer;
        
 
    public CachiusResponseWrapper(HttpServletResponse response, 
            ItemUpdater cacheItemUpdate) {
        
        super(response);
        this.cacheItemUpdate = cacheItemUpdate;
    }
	    
    /**
     * Set the HTTP status code
     *
     * @param sc The status
     */
    public void setStatus(int sc) {
        super.setStatus(sc);
        if (sc != 0 && sc != HttpServletResponse.SC_OK) {
        	cacheItemUpdate.discard();
        }
    }
       
    
    public void setContentType(String contentType) {
        super.setContentType(contentType);
        cacheItemUpdate.setContentType(contentType);
    }
        
    
    /**
     * Get an OutputStream
     *
     * @throws IllegalStateException If getWriter() has been called before
     * @throws IOException
     */
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException();
        }
        if (outputStream == null) {
        	OutputStream captureStream = cacheItemUpdate.getOutputStream();
        	if (captureStream != null) {
	            outputStream = new MultiplexServletOutputStream(
	            		captureStream, super.getOutputStream());
        	}
        	else {
        		// Fail gracefully - continue without caching
        		outputStream = super.getOutputStream();
        	}
        }
        return outputStream;
    }

    /**
     * Get a PrintWriter
     *
     * @throws IllegalStateExcepion If getOutputStream() has been called before
     * @throws IOException
     */
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            if (outputStream != null) {
                throw new IllegalStateException();
            }
            Writer captureWriter = cacheItemUpdate.getWriter();
            if (captureWriter != null) {
	            writer = new MultiplexPrintWriter(
	            		captureWriter, 
	            		super.getWriter());
            }
            else {
            	// Fail gracefully - continue without caching
            	writer = super.getWriter(); 
            }
        }
        return writer;
    }
    
    public void flushBuffer() throws IOException {
    	if (writer != null) {
    		writer.flush();
    	}
    	else if (outputStream != null) {
    		outputStream.flush();
    	}
    }
    
}
