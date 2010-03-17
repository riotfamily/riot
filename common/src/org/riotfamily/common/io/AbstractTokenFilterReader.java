/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Abstract base class for FilterReaders that replace <code>${placehoder}</code> 
 * tokens.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class AbstractTokenFilterReader extends FilterReader {

	private String replacement = null;
	
	private int replaceIndex = -1;
		
	public AbstractTokenFilterReader(Reader in) {
		super(in);
	}
	
	@Override
	public int read() throws IOException {
	
		if (replaceIndex != -1) {
			// Fill in replacement ...
            int i = replacement.charAt(replaceIndex++);
            if (replaceIndex == replacement.length()) {
                replaceIndex = -1;
            }
            return i;
        }
		
		int i = super.read();
		if (i != '$') {
			// Normal character - no further processing is required
			return i;
		}
				
		// Read ahead to check if next character is '{'
		int c = super.read();
		if (c != '{') {
			// Just a single '$' so we set the replacement to the second char
			replacement = Character.toString((char) c);
			replaceIndex = 0;
			return '$';
		}

		// We encountered a '${' sequence, so we'll read on until '}' or EOF
		StringBuffer buffer = new StringBuffer();
		boolean endReached = false;
		while (!endReached) {
			c = super.read();
			endReached = c == -1 || c == '}'; 
			if (!endReached) {
				buffer.append((char) c);
			}
		}
		
		if (c == -1) {
			throw new IOException("EOF encountered but '}' was expected.");
		}
		
		String key = buffer.toString();
		replacement = getReplacement(key);
		if (replacement != null && replacement.length() > 0) {
			replaceIndex = 0;
		}
		return read();
	}
	
	/**
     * @see Reader#read(char[], int, int)
     */
    @Override
	public final int read(char[] cbuf, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            final int ch = read();
            if (ch == -1) {
            	return i == 0 ? -1 : i;
            }
            cbuf[off + i] = (char) ch;
        }
        return len;
    }
    
    protected abstract String getReplacement(String key);

}
