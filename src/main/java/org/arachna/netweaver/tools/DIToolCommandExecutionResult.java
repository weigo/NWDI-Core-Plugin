/**
 *
 */
package org.arachna.netweaver.tools;

/**
 * Capture the result of dc tool execution.
 *
 * @author Dirk Weigenand
 */
public final class DIToolCommandExecutionResult {
    /**
     * captured output of dc tool.
     */
    private final String output;

    /**
     * result of dc tool execution.
     */
    private final int exitCode;

    /**
     * Create an instance of <code>DcToolCommandExecutionResult</code> using the
     * given output and exit code.
     *
     * @param output
     *            the output produced by dc tool
     * @param exitCode
     *            the exit code returned by dc tool
     */
    public DIToolCommandExecutionResult(final String output, final int exitCode) {
        this.output = output;
        this.exitCode = exitCode;

    }

    /**
     * Returns the captured output of a dc tool execution.
     *
     * @return captured output of a dc tool execution.
     */
    public String getOutput() {
        return this.output;
    }

    /**
     * Returns the exit code of the dc tool execution.
     *
     * @return exit code of the dc tool execution.
     */
    public int getExitCode() {
        return this.exitCode;
    }

    /**
     * Returns <code>true</code> when the exit code of this dc tool execution
     * indocated success, <code>false</code> otherwise.
     *
     * @return <code>true</code> when the exit code of this dc tool execution
     *         indocated success, <code>false</code> otherwise.
     */
    public boolean isExitCodeOk() {
        return 0 == this.getExitCode();
    }
}
