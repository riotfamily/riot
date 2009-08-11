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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class RuntimeCommand {

	private String[] commandLine;

	private Process process;

	private OutputStream stdErr = new ByteArrayOutputStream();

	private OutputStream stdOut = new ByteArrayOutputStream();

	private OutputStream stdIn;
	
	private StreamCopyThread stdErrCopyThread;
	
	private StreamCopyThread stdOutCopyThread;

	private Integer exitCode;

	public RuntimeCommand(String... commandLine) {
		this.commandLine = commandLine;
	}

	public RuntimeCommand setStdOutStream(OutputStream stdOut) {
		if (stdOut == null) {
			stdOut = new NullOutputStream();
		}
		this.stdOut = stdOut;
		return this;
	}

	public RuntimeCommand setStdErrStream(OutputStream stdErr) {
		if (stdErr == null) {
			stdErr = new NullOutputStream();
		}
		this.stdErr = stdErr;
		return this;
	}

	public RuntimeCommand exec() throws IOException {
		process = Runtime.getRuntime().exec(commandLine);
		
		stdErrCopyThread = new StreamCopyThread(process.getErrorStream(), stdErr);
		stdErrCopyThread.start();
		
		stdOutCopyThread = new StreamCopyThread(process.getInputStream(), stdOut);
		stdOutCopyThread.start();
		
		stdIn = process.getOutputStream();
		return this;
	}

	public OutputStream getStdIn() {
		Assert.notNull(stdIn, "exec() must be called first");
		return stdIn;
	}

	public int getExitCode() {
		Assert.notNull(process, "exec() must be called first");
		if (exitCode == null) {
			try {
				IOUtils.closeStream(stdIn);
				stdErrCopyThread.join();
				stdOutCopyThread.join();
				exitCode = new Integer(process.waitFor());
			}
			catch (InterruptedException e) {
				return -1;
			}
		}
		return exitCode.intValue();
	}

	public String getOutput() {
		getExitCode();
		return stdOut.toString();
	}

	public String getErrors() {
		getExitCode();
		return stdErr.toString();
	}

	public String getResult() throws IOException {
		if (getExitCode() == 0) {
			return stdOut.toString();
		}
		else {
			throw new IOException(
					StringUtils.arrayToDelimitedString(commandLine, " ") 
					+ "\n" + stdErr.toString());
		}
	}

}
