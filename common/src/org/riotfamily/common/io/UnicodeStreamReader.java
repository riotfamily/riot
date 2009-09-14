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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;

/**
 * Reader that uses a Byte Order Mark (BOM) to identify the encoding of the
 * underlying stream. If present, the BOM is removed from the stream.
 * 
 * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4508058">JDK Bug 4508058</a>
 * @see <a href="http://www.unicode.org/faq/utf_bom.html#BOM">Byte Order Mark FAQ</a>
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class UnicodeStreamReader extends Reader {

	private static final int MAX_BOM_SIZE = 4;
	
	private static final BOM[] BOMS = new BOM[] {
		new BOM("UTF-32LE", new byte [] { (byte) 0xff, (byte) 0xfe, (byte) 0x00, (byte) 0x00 }),
		new BOM("UTF-32BE", new byte [] { (byte) 0x00, (byte) 0x00, (byte) 0xfe, (byte) 0xff }),
		new BOM("UTF-8", new byte [] { (byte) 0xef, (byte) 0xbb, (byte) 0xbf }),
		new BOM("UTF-16LE", new byte [] { (byte) 0xff, (byte) 0xfe }),
		new BOM("UTF-16BE", new byte [] { (byte) 0xfe, (byte) 0xff }),
	};
	
	private InputStreamReader reader;

	private String defaultEncoding;
  
	/**
	 * Creates a new UnicodeStreamReader. If no BOM is found in the stream
	 * the default system encoding is used.
	 * 
	 * @param in An InputStream
	 * @throws IOException If an I/O error occurs
	 */
	public UnicodeStreamReader(InputStream in) throws IOException {
		this(in, null);
	}
	
	/**
	 * Creates a new UnicodeStreamReader. If no BOM is found in the stream
	 * the given default encoding is used.
	 * 
	 * @param in An InputStream
	 * @param defaultEncoding The encoding to be used if no BOM is found
	 * @throws IOException If an I/O error occurs
	 */
	public UnicodeStreamReader(InputStream in, String defaultEncoding)
			throws IOException {
		
		this.defaultEncoding = defaultEncoding;

		byte buffer[] = new byte[MAX_BOM_SIZE];
		PushbackInputStream pushbackStream = new PushbackInputStream(in, MAX_BOM_SIZE);
		int read = pushbackStream.read(buffer, 0, MAX_BOM_SIZE);

		BOM bom = getByteOrderMark(buffer);
		
		int unread = read - bom.length();
		if (unread > 0) {
			pushbackStream.unread(buffer, bom.length(), unread);
		}

		reader = new InputStreamReader(pushbackStream, bom.encoding);
	}

	public String getEncoding() {
		return reader.getEncoding();
	}

	public void close() throws IOException {
		reader.close();
	}

	public int read(char[] cbuf, int off, int len) throws IOException {
		return reader.read(cbuf, off, len);
	}
	
	private BOM getByteOrderMark(byte[] bytes) {
		for (int i = 0; i < BOMS.length; i++) {
			if (BOMS[i].matches(bytes)) {
				return BOMS[i];
			}
		}
		return new BOM(defaultEncoding, null);
	}
	
	private static class BOM {

		private String encoding;
		
		private byte[] bytes;
		
		public BOM(String encoding, byte[] bytes) {
			this.encoding = encoding;
			this.bytes = bytes;
		}
		
		public int length() {
			return bytes != null ? bytes.length : 0;
		}
		
		public boolean matches(byte[] buffer) {
			for (int i = 0; i < bytes.length; i++) {
				if (bytes[i] != buffer[i]) {
					return false;
				}
			}
			return true;
		}
	}

}
