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
package org.riotfamily.cachius.support;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * ServletOutputStream that sends the data written to it to two
 * different streams.
 *
 * @author Felix Gnass
 */
public class MultiplexServletOutputStream extends ServletOutputStream {
    
    private OutputStream out1 = null;
    
    private OutputStream out2 = null;

    private boolean clientAbort = false;
    
    /**
     * Construct a new MultiplexServletOutputStream
     *
     * @param out1 The first output stream
     * @param out2 The second output stream
     */
    public MultiplexServletOutputStream(OutputStream out1, 
            ServletOutputStream out2) {
                
        this.out1 = out1;
        this.out2 = out2;
    }

    /**
     * Write to the output streams
     *
     * @param value The value to write
     * @throws IOException
     */
    public void write(int value) throws IOException {
        out1.write(value);
        try {
        	if (!clientAbort) {
        		out2.write(value);
        	}
        }
        catch (IOException e) {
        	clientAbort = true;
        }
    }

    /**
     * Write to the output streams
     *
     * @param value The value to write
     * @throws IOException
     */
    public void write(byte[] value) throws IOException {
        out1.write(value);
        try {
        	if (!clientAbort) {
        		out2.write(value);
        	}
        }
        catch (IOException e) {
        	clientAbort = true;
        }
    }

    /**
     * Write to the output streams
     *
     * @param b The data to write
     * @param off The offset before starting to write
     * @param len The lenght to write
     * @throws IOException
     */
    public void write(byte[] b, int off, int len) throws IOException {
        out1.write(b, off, len);
        try {
        	if (!clientAbort) {
        		out2.write(b, off, len);
        	}
        }
        catch (IOException e) {
        	clientAbort = true;
        }
    }
    
    /**
     * Flush the streams
     */
    public void flush() throws IOException {
        out1.flush();
        try {
            if (!clientAbort) {
            	out2.flush();
            }
        }
        catch (IOException e) {
        	clientAbort = true;
        }
    }
    
    /**
     * Close the streams
     */
    public void close() throws IOException {
        out1.close();
        out2.close();
    }
    
}
