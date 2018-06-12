/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import hudson.util.IOUtils;

/**
 * DTR client using the <code>http</code> protocol.
 * 
 * @author Dirk Weigenand
 */
final class DtrHttpClient {
    /**
     * Logger.
     */
    private final Logger logger = Logger.getLogger(DtrHttpClient.class.getName());

    /**
     * HTTP client to use for requests.
     */
    private final CloseableHttpClient httpClient;

    /**
     * Context to use for conversations.
     */
    private final HttpContext localContext = new BasicHttpContext();

    private HttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();

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
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(dtrUser, password));
        httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultCredentialsProvider(credsProvider).build();
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
            throw new IllegalArgumentException(String.format("The argument '%s' must not be null or empty!", argumentDescription));
        }
    }

    /**
     * Get the content of the page returned by the given query.
     * 
     * @param queryUrl
     *            URL for querying activities for a given compartment.
     * @return the content of the page returned by the given query.
     * @throws IOException
     *             when an error occurred reading the response.
     */
    InputStream getContent(final String queryUrl) throws IOException {
        logger.fine(queryUrl);
        CloseableHttpResponse response = null;
        ByteArrayOutputStream content = new ByteArrayOutputStream();

        try {
            final HttpGet httpget = new HttpGet(queryUrl);
            response = httpClient.execute(httpget, localContext);
            IOUtils.copy(response.getEntity().getContent(), content);
        }
        catch (UnsupportedOperationException e) {
            logger.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
        finally {
            if (response != null) {
                response.close();
            }
        }

        return new ByteArrayInputStream(content.toByteArray());
    }

    /**
     * Shut down the underlying {@link DefaultHTTPClient}'s connection manager.
     */
    public void close() {
        connectionManager.shutdown();
    }
}
