/**
 *
 */
package org.arachna.netweaver.dctool.commands;

import java.io.EOFException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * base class for readers of dctool output.
 *
 * @author Dirk Weigenand
 */
public abstract class AbstractDcToolOutputReader {
    /**
     * line number reader for reading the DCTool output.
     */
    private final LineNumberReader reader;

    /**
     * Create an instance of an <code>DcToolOutputReader</code> using the given
     * {@link Reader} containing the output of the DC tool.
     *
     * @param dcToolOutput
     *            reader for output of DC tool.
     */
    AbstractDcToolOutputReader(final Reader dcToolOutput) {
        this.reader = new LineNumberReader(dcToolOutput);
    }

    /**
     * Reads a line trying to match the given pattern.
     *
     * @param pattern
     *            the pattern to use in matching the line
     * @return If the pattern matches the first group will be extracted and
     *         returned, the empty string otherwise;
     * @throws IOException
     *             when an exception occured during reading the next line
     */
    protected String readLine(final Pattern pattern) throws IOException {
        String match = "";
        final String line = reader.readLine();

        if (line != null) {
            final Matcher matcher = pattern.matcher(line);

            if (matcher.matches()) {
                match = matcher.group(1);
            }
        }
        else {
            throw new EOFException();
        }

        return match.trim();
    }

    /**
     * read a line using the internal line number reader.
     *
     * @return the line just read
     * @throws IOException
     *             if an I/O error occurs.
     */
    protected final String readLine() throws IOException {
        return this.reader.readLine();
    }

    /**
     * Close the internal reader.
     *
     * @throws IOException
     *             if an I/O error occurs.
     */
    protected final void close() throws IOException {
        this.reader.close();
    }

    public abstract void read() throws IOException;
}
