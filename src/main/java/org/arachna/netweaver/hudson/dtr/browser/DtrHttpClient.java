/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
    private final DefaultHttpClient httpClient = new DefaultHttpClient();

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

        this.httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(dtrUser, password));
    }

    /**
     * Validate the given String argument.
     * 
     * @param arg
     *            argument to validate.
     * @param argumentDescription
     *            description for error message.
     * @throws IllegalArgumentException
     *             when the given argument is null or empty.
     */
    private void validateArgument(final String arg, final String argumentDescription) throws IllegalArgumentException {
        if (arg == null || arg.trim().length() == 0) {
            throw new IllegalArgumentException(String.format("The argument '%s' must not be null or empty!", argumentDescription));
        }
    }

    /**
     * @param queryUrl
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     * @throws IllegalStateException
     */
    InputStream getContent(final String queryUrl) throws IOException, ClientProtocolException, IllegalStateException {
        final HttpGet httpget = new HttpGet(queryUrl);
        final HttpResponse response = this.httpClient.execute(httpget, this.localContext);

        return response.getEntity().getContent();
    }
}
