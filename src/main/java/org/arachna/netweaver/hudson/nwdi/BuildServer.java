/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Abstraction for the NWDI server.
 * 
 * @author Dirk Weigenand
 */
final class BuildServer {
    /**
     * the default port number the NWDI listens on.
     */
    private static final int NWDI_DEFAULT_PORT = 53000;
    /**
     * the NWDI server name.
     */
    private final String server;

    /**
     * the port the NWDI server listens on.
     */
    private int port = NWDI_DEFAULT_PORT;

    /**
     * Create an instance of a <code>BuildServer</code> using the given URL.
     * 
     * @param url
     *            the url of the NWDI as read from a <code>.confdef</code> configuration file.
     */
    BuildServer(final String url) {
        try {
            final URL buildServerUrl = new URL(url);
            this.server = buildServerUrl.getHost();
            this.port = buildServerUrl.getPort();
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * @return the server
     */
    String getServer() {
        return server;
    }

    /**
     * @return the port
     */
    int getPort() {
        return port;
    }
}
