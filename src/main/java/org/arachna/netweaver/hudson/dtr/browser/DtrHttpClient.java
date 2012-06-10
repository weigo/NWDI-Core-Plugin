/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * Dtr client using the <code>http</code> protocol.
 * 
 * @author Dirk Weigenand
 */
final class DtrHttpClient {
    /**
     * HTTP client to use for requests.
     */
    private final DefaultHttpClient httpClient = new DefaultHttpClient(new BasicClientConnectionManager());

    /**
     * Context to use for conversations.
     */
    private final HttpContext localContext = new BasicHttpContext();

    /**
     * Create an instance of a <code>DtrHttpClient</code>.
     * 
     * @param dtrUser
     *            user for accessing the DTR.
     * @param password
     *            password to authenticate the user against the DTR's UME.
     */
    public DtrHttpClient(final String dtrUser, final String password) {
        validateArgument(dtrUser, "DTR user");
        validateArgument(password, "password");

        this.httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(dtrUser, password));
    }

    /**
     * Validate the given String argument.
     * 
     * @param arg
     *            argument to validate.
     * @param argumentDescription
     *            description for error message.
     */
    private void validateArgument(final String arg, final String argumentDescription) {
        if (arg == null || arg.trim().length() == 0) {
            throw new IllegalArgumentException(String.format("The argument '%s' must not be null or empty!",
                argumentDescription));
        }
    }

    /**
     * Get the content of the page returned by the given query.
     * 
     * @param queryUrl
     *            url for querying activities for a given compartment.
     * @return the content of the page returned by the given query.
     * @throws IOException
     *             when an error occured reading the response.
     */
    InputStream getContent(final String queryUrl) throws IOException {
        final HttpGet httpget = new HttpGet(queryUrl);
        final HttpResponse response = this.httpClient.execute(httpget, this.localContext);

        return response.getEntity().getContent();
    }

    /**
     * Shut down the underlying {@link DefaultHTTPClient}'s connection manager.
     */
    public void close() {
        this.httpClient.getConnectionManager().shutdown();
    }
}
