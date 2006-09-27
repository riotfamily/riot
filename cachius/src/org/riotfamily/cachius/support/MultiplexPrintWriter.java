package org.riotfamily.cachius.support;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * PrintWriter that additionally sends its data to second PrintWriter.
 *
 * @author Felix Gnass
 */
public class MultiplexPrintWriter extends PrintWriter {
    
    private PrintWriter printWriter = null;

    /**
     * Construct a new MultiplexPrintWriter
     *
     * @param writer The underlying writer
     * @param printWriter An existing PrintWriter
     */
    public MultiplexPrintWriter(Writer writer, PrintWriter printWriter) {
        super(writer);
        this.printWriter = printWriter;
    }

    /**
     * Write a single character.
     */
    public void write(int ch) {
        super.write(ch);
        printWriter.write(ch);
    }
    
    /**
     * Write a portion of an array of characters.
     */
    public void write(char[] charArray, int offset, int count) {
        super.write(charArray, offset, count);
        printWriter.write(charArray, offset, count);
    }

    /**
     * Write a portion of a string.
     */
    public void write(String str, int offset, int count) {
        super.write(str, offset, count);
        printWriter.write(str, offset, count);
    }
    
    /**
     * Flush the writers.
     */
    public void flush() {
        super.flush();
        printWriter.flush();
    }
    
    /**
     * Close the writers.
     */
    public void close() {
        super.close();
        printWriter.close();
    }
    
}
