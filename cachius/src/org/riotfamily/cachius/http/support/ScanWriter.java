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
package org.riotfamily.cachius.http.support;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Writer that scans the output for expressions. 
 */
public class ScanWriter extends Writer {

	private int index;
	
	private char[] startPattern;
	
	private char endChar;
	
	private int p;
	
	private int maxBlockSize = 512;
	
	private Block block;
	
	private LinkedList<Block> blocks = new LinkedList<Block>();
	
	private Writer out;

	public ScanWriter(Writer out, String startMarker, char endChar) {
		this.out = out;
		this.startPattern = startMarker.toCharArray();
		this.endChar = endChar;
	}

	public Collection<Block> getBlocks() {
		return blocks;
	}
	
	public boolean foundBlocks() {
		return blocks.size() > 0;
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		int end = off + len;
		for (int i = off; i < end; i++) {
			char c = cbuf[i];
			if (block != null) {
				if (c == endChar) {
					block.end(index);
					blocks.add(block);
		    		block = null;
				}
				else if (block.length() > maxBlockSize) {
					block = null;
				}
				else {
					block.append(c);
				}
			}
			else {
				if (c == startPattern[p]) {
					p++;
					if (p == startPattern.length) {
						block = new Block(index + 1 - startPattern.length);
						p = 0;
					}
				}
				else {
					p = 0;
				}
			}
			index++;
		}
		out.write(cbuf, off, len);
	}

	public static class Block {
    	
    	private int start;
    	
    	private int end;
    	
    	private StringBuilder sb = new StringBuilder();
    	
    	private Block(int start) {
    		this.start = start;
    	}
    	
    	private void append(char c) {
    		sb.append(c);
    	}
    	
    	private void end(int end) {
    		this.end = end;
    	}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}
		
		private int length() {
			return sb.length();
		}
    	
    	public String getValue() {
    		return sb.toString();
    	}
    }

}
