package org.riotfamily.website.performance;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.riotfamily.common.util.RiotLog;
import org.springframework.util.FileCopyUtils;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class YUIJavaScriptCompressor implements Compressor {

	private RiotLog log = RiotLog.get(YUIJavaScriptCompressor.class);
	
	private ErrorReporter defaultErrorReporter = new RiotLogErrorReporter();
	
	private int linebreak = -1;
	
	private boolean munge = true;
	
	private boolean warn = false;
	
	private boolean preserveAllSemiColons;
	
	private boolean mergeStringLiterals;
	
	private boolean failsafe = false;
	
	private boolean enabled = true;


	/**
	 * Enables the Compressor. Per default the compressor is enabled. 
	 * @param enabled true to enabled, false to disable this compressor
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Sets the column number after which a line break should be inserted.
	 * Default is <code>-1</code>, which means that no breaks will be added.
	 */
	public void setLinebreak(int linebreak) {
		this.linebreak = linebreak;
	}

	/**
	 * Sets whether the code should be obfuscated. If set to <code>false</code>
	 * the script will only be minified. Default is <code>true</code>.
	 */
	public void setMunge(boolean munge) {
		this.munge = munge;
	}

	/**
	 * Sets whether possible errors in the code should be displayed.
	 * Default is <code>false</code>.
	 */
	public void setWarn(boolean warn) {
		this.warn = warn;
	}

	/**
	 * Sets whether a semicolons should be preserved.
	 * Default is <code>false</code>.
	 */
	public void setPreserveAllSemiColons(boolean preserveAllSemiColons) {
		this.preserveAllSemiColons = preserveAllSemiColons;
	}

	/**
	 * Sets whether concatenated string literals should be merged.
	 * Default is <code>true</code>.
	 */
	public void setMergeStringLiterals(boolean mergeStringLiterals) {
		this.mergeStringLiterals = mergeStringLiterals;
	}

	/**
	 * Sets the {@link ErrorReporter} used by {@link #compress(Reader, Writer)}.
	 */
	public void setDefaultErrorReporter(ErrorReporter defaultErrorReporter) {
		this.defaultErrorReporter = defaultErrorReporter;
	}

	/**
	 * Sets whether the uncompressed script should be served in case a syntax 
	 * error is encountered. Default is <code>false</code>.
	 */
	public void setFailsafe(boolean failsafe) {
		this.failsafe = failsafe;
	}
	
	/**
	 * Reads JavaScript from the the given Reader and writes the compressed
	 * code to the specified Writer. Errors are reported to the 
	 * {@link #setDefaultErrorReporter(ErrorReporter) default ErrorReorter}. 
	 */
	public void compress(Reader in, Writer out) throws IOException {
		compress(in, out, defaultErrorReporter);
	}
	
	/**
	 * Reads JavaScript from the the given Reader and writes the compressed
	 * code to the specified Writer. The given fileName will be used in 
	 * error messages.  
	 */
	public void compress(Reader in, Writer out, String fileName) throws IOException {
		RiotLogErrorReporter errorReporter = new RiotLogErrorReporter();
		errorReporter.setSourceName(fileName);
		compress(in, out, errorReporter);
	}
	
	/**
	 * Reads JavaScript from the the given Reader and writes the compressed
	 * code to the specified Writer. Errors are reported to the given 
	 * ErrorReorter. In {@link #setFailsafe(boolean) failsafe mode} the script
	 * is first read into a buffer so that it can be written as-is in case 
	 * of an error. The actual compression is performed in 
	 * {@link #compressInternal(Reader, Writer, ErrorReporter)}.
	 */
	public void compress(Reader in, Writer out, ErrorReporter errorReporter) 
			throws IOException {
	
		if (enabled) {
		
			if (failsafe) {
				StringWriter buffer = new StringWriter();
				FileCopyUtils.copy(in, buffer);
				in = new StringReader(buffer.toString());
				try {
					compressInternal(in, out, errorReporter);
				}
				catch (EvaluatorException e) {
					log.warn("JavaScript compression failed, serving uncompressed script.");
					out.write(buffer.toString());
				}
			}
			else {
				compressInternal(in, out, errorReporter);
			}
		
		}
		else {
			FileCopyUtils.copy(in, out);
		}
		
	}
	
	/**
	 * Reads JavaScript from the the given Reader and writes the compressed
	 * code to the specified Writer. Errors are reported to the given 
	 * ErrorReorter.
	 */
	protected void compressInternal(Reader in, Writer out, 
			ErrorReporter errorReporter) 
			throws EvaluatorException, IOException {
		
		JavaScriptCompressor compressor = new JavaScriptCompressor(in, errorReporter);
		compressor.compress(out, linebreak, munge, warn, 
				preserveAllSemiColons, !mergeStringLiterals);
	}
	
	private class RiotLogErrorReporter implements ErrorReporter {

		private String defaultSourceName = "[unknown]";
		
		public void setSourceName(String sourceName) {
			this.defaultSourceName = sourceName;
		}
		
		public void warning(String message, String sourceName,
	            int line, String lineSource, int lineOffset) {
			
	       	log.warn(formatMessage(message, sourceName, line, lineSource, lineOffset));
	    }

	    public void error(String message, String sourceName,
	            int line, String lineSource, int lineOffset) {
	    	
	       	log.error(formatMessage(message, sourceName, line, lineSource, lineOffset));
	    }
	    
	    protected String formatMessage(String message, String sourceName, int line, String lineSource, int lineOffset) {
	    	StringBuffer sb = new StringBuffer();
	    	sb.append(message);
	    	if (sourceName == null) {
	    		sourceName = defaultSourceName;
	    	}
	    	if (sourceName != null) {
	    		sb.append(" in ").append(sourceName);
	    	}
	    	if (lineOffset > 0) {
	    		sb.append(" (line ")
	    				.append(line)
	    				.append(", column ")
	    				.append(lineOffset)
	    				.append(")");
	    	}
	    	return sb.toString();	
	    }

	    public EvaluatorException runtimeError(String message, String sourceName,
	            int line, String lineSource, int lineOffset) {
	        
	    	error(message, sourceName, line, lineSource, lineOffset);
	        return new EvaluatorException(message);
	    }
	}

}
