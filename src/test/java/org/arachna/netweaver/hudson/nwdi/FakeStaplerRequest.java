/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * A fake {@link StaplerRequest} for testing purposes.
 * 
 * @author Dirk Weigenand
 */
final class FakeStaplerRequest implements StaplerRequest {
    /**
     * Mapping of parameter names to respective values.
     */
    private final Map<String, String> parameterMapping = new HashMap<String, String>();

    /**
     * Create an instance with the given parameter names and values.
     * 
     * The given parameter names and values must have the same length.
     * 
     * @param keys
     *            parameter names
     * @param values
     *            parameter values
     */
    FakeStaplerRequest(final String[] keys, final String[] values) {
        for (int i = 0; i < keys.length; i++) {
            this.parameterMapping.put(keys[i], values[i]);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getAuthType() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Cookie[] getCookies() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public long getDateHeader(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getHeader(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Enumeration getHeaders(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Enumeration getHeaderNames() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public int getIntHeader(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getMethod() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getPathInfo() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getContextPath() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getQueryString() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getRemoteUser() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getRequestURI() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getServletPath() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public HttpSession getSession(boolean create) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public HttpSession getSession() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Object getAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Enumeration getAttributeNames() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public int getContentLength() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getContentType() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getParameter(final String name) {
        return this.parameterMapping.get(name);
    }

    /**
     * {@inheritDoc}
     */
    public Enumeration getParameterNames() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String[] getParameterValues(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Map getParameterMap() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getProtocol() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getScheme() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getServerName() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public int getServerPort() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getRemoteAddr() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getRemoteHost() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void setAttribute(String name, Object o) {
        throw new UnsupportedOperationException();

    }

    /**
     * {@inheritDoc}
     */
    public void removeAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Locale getLocale() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Enumeration getLocales() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSecure() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getRealPath(String path) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public int getRemotePort() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getLocalName() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getLocalAddr() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public int getLocalPort() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Stapler getStapler() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getRestOfPath() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getOriginalRestOfPath() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public RequestDispatcher getView(Object it, String viewName) throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public RequestDispatcher getView(Class clazz, String viewName) throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getRootPath() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getReferer() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public List<Ancestor> getAncestors() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Ancestor findAncestor(Class type) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T findAncestorObject(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Ancestor findAncestor(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasParameter(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getOriginalRequestURI() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean checkIfModified(long timestampOfResource, StaplerResponse rsp) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean checkIfModified(Date timestampOfResource, StaplerResponse rsp) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean checkIfModified(Calendar timestampOfResource, StaplerResponse rsp) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean checkIfModified(long timestampOfResource, StaplerResponse rsp, long expiration) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void bindParameters(Object bean) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void bindParameters(Object bean, String prefix) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> bindParametersToList(Class<T> type, String prefix) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T bindParameters(Class<T> type, String prefix) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T bindParameters(Class<T> type, String prefix, int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T bindJSON(Class<T> type, JSONObject src) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T bindJSON(Type genericType, Class<T> erasure, Object json) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void bindJSON(Object bean, JSONObject src) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> bindJSONToList(Class<T> type, Object src) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public JSONObject getSubmittedForm() throws ServletException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public FileItem getFileItem(String name) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isJavaScriptProxyCall() {
        throw new UnsupportedOperationException();
    }
}
