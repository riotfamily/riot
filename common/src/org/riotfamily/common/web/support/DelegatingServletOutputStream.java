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
package org.riotfamily.common.web.support;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * ServletOutputStream that delegates all methods to a regular 
 * {@link OutputStream}. 
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DelegatingServletOutputStream extends ServletOutputStream {

    private final OutputStream targetStream;

    /**
     * Create a new DelegatingServletOutputStream.
     * @param targetStream the target OutputStream
     */
    public DelegatingServletOutputStream(OutputStream targetStream) {
            this.targetStream = targetStream;
    }

    public void write(int b) throws IOException {
            this.targetStream.write(b);
    }

    public void flush() throws IOException {
            super.flush();
            this.targetStream.flush();
    }

    public void close() throws IOException {
            super.close();
            this.targetStream.close();
    }

}
