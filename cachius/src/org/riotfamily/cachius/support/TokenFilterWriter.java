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
package org.riotfamily.cachius.support;

import java.io.BufferedWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * A FilterWriter that replaces all occurences of a given token with a 
 * replacement string.
 *
 * @author Felix Gnass
 */
public class TokenFilterWriter extends FilterWriter {

	private final int writeBufferSize = 1024;
	
    private char[] writeBuffer;
    
    private char[] pattern;

    private String replacement;
    
    private char[] buffer;
    
    private int index = 0;
    
        
    public TokenFilterWriter(String token, String replacement, Writer out) {
        this(token, replacement, new BufferedWriter(out));
    }
    
    public TokenFilterWriter(String token, String replacement, 
            BufferedWriter out) {
            
        super(out);
        this.pattern = token.toCharArray();
        this.replacement = replacement;
        this.buffer = new char[pattern.length];
    }
    
    public void write(char[] buf, int offset, int len) throws IOException {
        for (int i = offset; i < len; i++) {
            write(buf[i]);
        }
    }
    
    public synchronized void write(String str, int off, int len) 
    		throws IOException {
    	
	    char cbuf[];
	    if (len <= writeBufferSize) {
			if (writeBuffer == null) {
			    writeBuffer = new char[writeBufferSize];
			}
			cbuf = writeBuffer;
	    } 
	    else {
	    	cbuf = new char[len];
	    }
	    str.getChars(off, (off + len), cbuf, 0);
	    write(cbuf, 0, len);
    }
        
    public void write(int c) throws IOException {
        if (c == pattern[index]) {
            buffer[index] = (char) c;
            index++;
            if (index == pattern.length) {
                out.write(replacement);
                index = 0;
            }
        }
        else {
            if (index > 0) {
                out.write(buffer, 0, index);
                index = 0;
            }
            out.write(c);
        }
    }
    
}
