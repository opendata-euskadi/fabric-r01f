/*
 * Copyright 1994-2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package r01f.httpclient.jsse.www.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.CacheRequest;
import java.net.CookieHandler;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

import r01f.httpclient.jsse.misc.RegexpPool;
import r01f.httpclient.jsse.net.NetworkClient;
import r01f.httpclient.jsse.net.ProgressSource;
import r01f.httpclient.jsse.security.action.GetPropertyAction;
import r01f.httpclient.jsse.www.HeaderParser;
import r01f.httpclient.jsse.www.MessageHeader;
import r01f.httpclient.jsse.www.MeteredStream;
import r01f.httpclient.jsse.www.ParseUtil;
import r01f.httpclient.jsse.www.protocol.http.HttpURLConnection;
/**
 * @author Herb Jellinek
 * @author Dave Brown
 */
public class HttpClient extends NetworkClient {
    // whether this httpclient comes from the cache
    protected boolean cachedHttpClient = false;

    private boolean inCache;

    protected CookieHandler cookieHandler;

    // Http requests we send
    MessageHeader requests;

    // Http data we send with the headers
    PosterOutputStream poster = null;

    // if we've had one io error
    boolean failedOnce = false;

    /** regexp pool of hosts for which we should connect directly, not Proxy
     *  these are intialized from a property.
     */
    @SuppressWarnings("unused")
	private static RegexpPool nonProxyHostsPool = null;

    /** The string source of nonProxyHostsPool
     */
    @SuppressWarnings("unused")
	private static String nonProxyHostsSource = null;

    /** Response code for CONTINUE */
    private static final int    HTTP_CONTINUE = 100;

    /** Default port number for http daemons. REMIND: make these private */
    static final int    httpPortNumber = 80;

    /** return default port number (subclasses may override) */
    protected int getDefaultPort () { return httpPortNumber; }

    static private int getDefaultPort(String proto) {
        if ("http".equalsIgnoreCase(proto))
            return 80;
        if ("https".equalsIgnoreCase(proto))
            return 443;
        return -1;
    }

    /* The following three data members are left in for binary */
    /* backwards-compatibility.  Unfortunately, HotJava sets them directly */
    /* when it wants to change the settings.  The new design has us not */
    /* cache these, so this is unnecessary, but eliminating the data members */
    /* would break HJB 1.1 under JDK 1.2. */
    /* */
    /* These data members are not used, and their values are meaningless. */
    /* REMIND:  Take them out for JDK 2.0! */
    /**
     * @deprecated
     */
    //    public static String proxyHost = null;
    /**
     * @deprecated
     */
    //    public static int proxyPort = 80;

    /* instance-specific proxy fields override the static fields if set.
     * Used by FTP.  These are set to the true proxy host/port if
     * usingProxy is true.
     */
    //    private String instProxy = null;
    //    private int instProxyPort = -1;

    /* All proxying (generic as well as instance-specific) may be
     * disabled through use of this flag
     */
    protected boolean proxyDisabled;

    // are we using proxy in this instance?
    public boolean usingProxy = false;
    // target host, port for the URL
    protected String host;
    protected int port;

    /* where we cache currently open, persistent connections */
    protected static KeepAliveCache kac = new KeepAliveCache();

    private static boolean keepAliveProp = true;

    // retryPostProp is true by default so as to preserve behavior
    // from previous releases.
    private static boolean retryPostProp = true;

    volatile boolean keepingAlive = false;     /* this is a keep-alive connection */
    int keepAliveConnections = -1;    /* number of keep-alives left */

    /**Idle timeout value, in milliseconds. Zero means infinity,
     * iff keepingAlive=true.
     * Unfortunately, we can't always believe this one.  If I'm connected
     * through a Netscape proxy to a server that sent me a keep-alive
     * time of 15 sec, the proxy unilaterally terminates my connection
     * after 5 sec.  So we have to hard code our effective timeout to
     * 4 sec for the case where we're using a proxy. *SIGH*
     */
    int keepAliveTimeout = 0;

    /** whether the response is to be cached */
    private CacheRequest cacheRequest = null;

    /** Url being fetched. */
    protected URL       url;

    /* if set, the client will be reused and must not be put in cache */
    public boolean reuse = false;

    /**
     * A NOP method kept for backwards binary compatibility
     * @deprecated -- system properties are no longer cached.
     */
    @Deprecated
    public static synchronized void resetProperties() {
    }

    int getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    static {
        String keepAlive = java.security.AccessController.doPrivileged(
            new GetPropertyAction("http.keepAlive"));

        String retryPost = java.security.AccessController.doPrivileged(
            new GetPropertyAction("sun.net.http.retryPost"));

        if (keepAlive != null) {
            keepAliveProp = Boolean.valueOf(keepAlive).booleanValue();
        } else {
            keepAliveProp = true;
        }

        if (retryPost != null) {
            retryPostProp = Boolean.valueOf(retryPost).booleanValue();
        } else
            retryPostProp = true;

    }

    /**
     * @return true iff http keep alive is set (i.e. enabled).  Defaults
     *          to true if the system property http.keepAlive isn't set.
     */
    public boolean getHttpKeepAliveSet() {
        return keepAliveProp;
    }


    protected HttpClient() {
    }

    @SuppressWarnings("unused")
	private HttpClient(URL url)
    throws IOException {
        this(url, (String)null, -1, false);
    }

    protected HttpClient(URL url,
                         boolean proxyDisabled) throws IOException {
        this(url, null, -1, proxyDisabled);
    }

    /* This package-only CTOR should only be used for FTP piggy-backed on HTTP
     * HTTP URL's that use this won't take advantage of keep-alive.
     * Additionally, this constructor may be used as a last resort when the
     * first HttpClient gotten through New() failed (probably b/c of a
     * Keep-Alive mismatch).
     *
     * XXX That documentation is wrong ... it's not package-private any more
     */
    public HttpClient(URL url, String proxyHost, int proxyPort)
    throws IOException {
        this(url, proxyHost, proxyPort, false);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected HttpClient(URL url, Proxy p, int to) throws IOException {
        proxy = (p == null) ? Proxy.NO_PROXY : p;
        this.host = url.getHost();
        this.url = url;
        port = url.getPort();
        if (port == -1) {
            port = getDefaultPort();
        }
        setConnectTimeout(to);

        // get the cookieHandler if there is any
        cookieHandler = (CookieHandler)java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction() {
                public Object run() {
                    return CookieHandler.getDefault();
                }
            });

        openServer();
    }

    static protected Proxy newHttpProxy(String proxyHost, int proxyPort,
                                      String proto) {
        if (proxyHost == null || proto == null)
            return Proxy.NO_PROXY;
        int pport = proxyPort < 0 ? getDefaultPort(proto) : proxyPort;
        InetSocketAddress saddr = InetSocketAddress.createUnresolved(proxyHost, pport);
        return new Proxy(Proxy.Type.HTTP, saddr);
    }

    /*
     * This constructor gives "ultimate" flexibility, including the ability
     * to bypass implicit proxying.  Sometimes we need to be using tunneling
     * (transport or network level) instead of proxying (application level),
     * for example when we don't want the application level data to become
     * visible to third parties.
     *
     * @param url               the URL to which we're connecting
     * @param proxy             proxy to use for this URL (e.g. forwarding)
     * @param proxyPort         proxy port to use for this URL
     * @param proxyDisabled     true to disable default proxying
     */
    private HttpClient(URL url, String proxyHost, int proxyPort,
                       boolean proxyDisabled)
        throws IOException {
        this(url, proxyDisabled ? Proxy.NO_PROXY :
             newHttpProxy(proxyHost, proxyPort, "http"), -1);
    }

    public HttpClient(URL url, String proxyHost, int proxyPort,
                       boolean proxyDisabled, int to)
        throws IOException {
        this(url, proxyDisabled ? Proxy.NO_PROXY :
             newHttpProxy(proxyHost, proxyPort, "http"), to);
    }

    /* This class has no public constructor for HTTP.  This method is used to
     * get an HttpClient to the specifed URL.  If there's currently an
     * active HttpClient to that server/port, you'll get that one.
     */
    public static HttpClient New(URL url)
    throws IOException {
        return HttpClient.New(url, Proxy.NO_PROXY, -1, true);
    }

    public static HttpClient New(URL url, boolean useCache)
        throws IOException {
        return HttpClient.New(url, Proxy.NO_PROXY, -1, useCache);
    }

    public static HttpClient New(URL url, Proxy p, int to, boolean useCache)
        throws IOException {
        if (p == null) {
            p = Proxy.NO_PROXY;
        }
        HttpClient ret = null;
        /* see if one's already around */
        if (useCache) {
            ret = (HttpClient) kac.get(url, null);
            if (ret != null) {
                if ((ret.proxy != null && ret.proxy.equals(p)) ||
                    (ret.proxy == null && p == null)) {
                    synchronized (ret) {
                        ret.cachedHttpClient = true;
                        assert ret.inCache;
                        ret.inCache = false;
                    }
                } else {
                    // We cannot return this connection to the cache as it's
                    // KeepAliveTimeout will get reset. We simply close the connection.
                    // This should be fine as it is very rare that a connection
                    // to the same host will not use the same proxy.
                    ret.inCache = false;
                    ret.closeServer();
                    ret = null;
                }
            }
        }
        if (ret == null) {
            ret = new HttpClient(url, p, to);
        } else {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                if (ret.proxy == Proxy.NO_PROXY || ret.proxy == null) {
                    security.checkConnect(InetAddress.getByName(url.getHost()).getHostAddress(),
                         url.getPort());
                } else {
                    security.checkConnect(url.getHost(), url.getPort());
                }
            }
            ret.url = url;
        }
        return ret;
    }

    public static HttpClient New(URL url, Proxy p, int to) throws IOException {
        return New(url, p, to, true);
    }

    public static HttpClient New(URL url, String proxyHost, int proxyPort,
                                 boolean useCache)
        throws IOException {
        return New(url, newHttpProxy(proxyHost, proxyPort, "http"), -1, useCache);
    }

    public static HttpClient New(URL url, String proxyHost, int proxyPort,
                                 boolean useCache, int to)
        throws IOException {
        return New(url, newHttpProxy(proxyHost, proxyPort, "http"), to, useCache);
    }

    /* return it to the cache as still usable, if:
     * 1) It's keeping alive, AND
     * 2) It still has some connections left, AND
     * 3) It hasn't had a error (PrintStream.checkError())
     * 4) It hasn't timed out
     *
     * If this client is not keepingAlive, it should have been
     * removed from the cache in the parseHeaders() method.
     */

    public void finished() {
        if (reuse) /* will be reused */
            return;
        keepAliveConnections--;
        poster = null;
        if (keepAliveConnections > 0 && isKeepingAlive() &&
               !(serverOutput.checkError())) {
            /* This connection is keepingAlive && still valid.
             * Return it to the cache.
             */
            putInKeepAliveCache();
        } else {
            closeServer();
        }
    }

    protected synchronized void putInKeepAliveCache() {
        if (inCache) {
            assert false : "Duplicate put to keep alive cache";
            return;
        }
        inCache = true;
        kac.put(url, null, this);
    }

    protected boolean isInKeepAliveCache() {
        return inCache;
    }

    /*
     * Close an idle connection to this URL (if it exists in the
     * cache).
     */
    public void closeIdleConnection() {
        HttpClient http = (HttpClient) kac.get(url, null);
        if (http != null) {
            http.closeServer();
        }
    }

    /* We're very particular here about what our InputStream to the server
     * looks like for reasons that are apparent if you can decipher the
     * method parseHTTP().  That's why this method is overidden from the
     * superclass.
     */
    public void openServer(String server, int port) throws IOException {
        serverSocket = doConnect(server, port);
        try {
            serverOutput = new PrintStream(
                new BufferedOutputStream(serverSocket.getOutputStream()),
                                         false, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new InternalError(encoding+" encoding not found");
        }
        serverSocket.setTcpNoDelay(true);
    }

    /*
     * Returns true if the http request should be tunneled through proxy.
     * An example where this is the case is Https.
     */
    public boolean needsTunneling() {
        return false;
    }

    /*
     * Returns true if this httpclient is from cache
     */
    public boolean isCachedConnection() {
        return cachedHttpClient;
    }

    /*
     * Finish any work left after the socket connection is
     * established.  In the normal http case, it's a NO-OP. Subclass
     * may need to override this. An example is Https, where for
     * direct connection to the origin server, ssl handshake needs to
     * be done; for proxy tunneling, the socket needs to be converted
     * into an SSL socket before ssl handshake can take place.
     */
    public void afterConnect() throws IOException, UnknownHostException {
        // NO-OP. Needs to be overwritten by HttpsClient
    }

    /*
     * call openServer in a privileged block
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized void privilegedOpenServer(final InetSocketAddress server)
         throws IOException
    {
        try {
            java.security.AccessController.doPrivileged(
                new java.security.PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    openServer(server.getHostName(), server.getPort());
                    return null;
                }
            });
        } catch (java.security.PrivilegedActionException pae) {
            throw (IOException) pae.getException();
        }
    }

    /*
     * call super.openServer
     */
    private void superOpenServer(final String proxyHost,
                                 final int proxyPort)
        throws IOException, UnknownHostException
    {
        super.openServer(proxyHost, proxyPort);
    }

    /*
     * call super.openServer in a privileged block
     */
    @SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private synchronized void privilegedSuperOpenServer(final String proxyHost,
                                                        final int proxyPort)
        throws IOException
    {
        try {
            java.security.AccessController.doPrivileged(
                new java.security.PrivilegedExceptionAction() {
                public Object run() throws IOException
                {
                    superOpenServer(proxyHost, proxyPort);
                    return null;
                }
            });
        } catch (java.security.PrivilegedActionException pae) {
            throw (IOException) pae.getException();
        }
    }

    /*
     */
    @SuppressWarnings("unused")
	protected synchronized void openServer() throws IOException {

        SecurityManager security = System.getSecurityManager();

        if (keepingAlive) { // already opened
            if (security != null) {
                security.checkConnect(host, port);
            }

            return;
        }

        String urlHost = url.getHost().toLowerCase();

        if (url.getProtocol().equals("http") ||
            url.getProtocol().equals("https") ) {

            if ((proxy != null) && (proxy.type() == Proxy.Type.HTTP)) {
                r01f.httpclient.jsse.www.URLConnection.setProxiedHost(host);
                if (security != null) {
                    security.checkConnect(host, port);
                }
                privilegedOpenServer((InetSocketAddress) proxy.address());
                usingProxy = true;
                return;
            } else {
                // make direct connection
                if (security != null) {
                    // redundant?
                    security.checkConnect(host, port);
                }
                openServer(host, port);
                usingProxy = false;
                return;
            }

        } else {
            /* we're opening some other kind of url, most likely an
             * ftp url.
             */
            if ((proxy != null) && (proxy.type() == Proxy.Type.HTTP)) {
                r01f.httpclient.jsse.www.URLConnection.setProxiedHost(host);
                if (security != null) {
                    security.checkConnect(host, port);
                }
                privilegedOpenServer((InetSocketAddress) proxy.address());
                usingProxy = true;
                return;
            } else {
                // make direct connection
                if (security != null) {
                    // redundant?
                    security.checkConnect(host, port);
                }
                super.openServer(host, port);
                usingProxy = false;
                return;
            }
        }
    }

    public String getURLFile() throws IOException {

        String fileName = url.getFile();
        if ((fileName == null) || (fileName.length() == 0))
            fileName = "/";

        /**
         * proxyDisabled is set by subclass HttpsClient!
         */
        if (usingProxy && !proxyDisabled) {
            // Do not use URLStreamHandler.toExternalForm as the fragment
            // should not be part of the RequestURI. It should be an
            // absolute URI which does not have a fragment part.
            StringBuffer result = new StringBuffer(128);
            result.append(url.getProtocol());
            result.append(":");
            if (url.getAuthority() != null && url.getAuthority().length() > 0) {
                result.append("//");
                result.append(url.getAuthority());
            }
            if (url.getPath() != null) {
                result.append(url.getPath());
            }
            if (url.getQuery() != null) {
                result.append('?');
                result.append(url.getQuery());
            }

            fileName =  result.toString();
        }
        if (fileName.indexOf('\n') == -1)
            return fileName;
        else
            throw new java.net.MalformedURLException("Illegal character in URL");
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void writeRequests(MessageHeader head) {
        requests = head;
        requests.print(serverOutput);
        serverOutput.flush();
    }

    public void writeRequests(MessageHeader head,
                              PosterOutputStream pos) throws IOException {
        requests = head;
        requests.print(serverOutput);
        poster = pos;
        if (poster != null)
            poster.writeTo(serverOutput);
        serverOutput.flush();
    }

    /** Parse the first line of the HTTP request.  It usually looks
        something like: "HTTP/1.0 <number> comment\r\n". */

    public boolean parseHTTP(MessageHeader responses, ProgressSource pi, HttpURLConnection httpuc)
    throws IOException {
        /* If "HTTP/*" is found in the beginning, return true.  Let
         * HttpURLConnection parse the mime header itself.
         *
         * If this isn't valid HTTP, then we don't try to parse a header
         * out of the beginning of the response into the responses,
         * and instead just queue up the output stream to it's very beginning.
         * This seems most reasonable, and is what the NN browser does.
         */

        try {
            serverInput = serverSocket.getInputStream();
            serverInput = new BufferedInputStream(serverInput);
            return (parseHTTPHeader(responses, pi, httpuc));
        } catch (SocketTimeoutException stex) {
            // We don't want to retry the request when the app. sets a timeout
            closeServer();
            throw stex;
        } catch (IOException e) {
            closeServer();
            cachedHttpClient = false;
            if (!failedOnce && requests != null) {
                if (httpuc.getRequestMethod().equals("POST") && !retryPostProp) {
                    // do not retry the request
                }  else {
                    // try once more
                    failedOnce = true;
                    openServer();
                    if (needsTunneling()) {
                        httpuc.doTunneling();
                    }
                    afterConnect();
                    writeRequests(requests, poster);
                    return parseHTTP(responses, pi, httpuc);
                }
            }
            throw e;
        }

    }

    public int setTimeout (int timeout) throws SocketException {
        int old = serverSocket.getSoTimeout ();
        serverSocket.setSoTimeout (timeout);
        return old;
    }

    @SuppressWarnings("unchecked")
	private boolean parseHTTPHeader(MessageHeader responses, ProgressSource pi, HttpURLConnection httpuc)
    throws IOException {
        /* If "HTTP/*" is found in the beginning, return true.  Let
         * HttpURLConnection parse the mime header itself.
         *
         * If this isn't valid HTTP, then we don't try to parse a header
         * out of the beginning of the response into the responses,
         * and instead just queue up the output stream to it's very beginning.
         * This seems most reasonable, and is what the NN browser does.
         */

        keepAliveConnections = -1;
        keepAliveTimeout = 0;

        boolean ret = false;
        byte[] b = new byte[8];

        try {
            int nread = 0;
            serverInput.mark(10);
            while (nread < 8) {
                int r = serverInput.read(b, nread, 8 - nread);
                if (r < 0) {
                    break;
                }
                nread += r;
            }
            String keep=null;
            ret = b[0] == 'H' && b[1] == 'T'
                    && b[2] == 'T' && b[3] == 'P' && b[4] == '/' &&
                b[5] == '1' && b[6] == '.';
            serverInput.reset();
            if (ret) { // is valid HTTP - response started w/ "HTTP/1."
                responses.parseHeader(serverInput);

                // we've finished parsing http headers
                // check if there are any applicable cookies to set (in cache)
                if (cookieHandler != null) {
                    URI uri = ParseUtil.toURI(url);
                    // NOTE: That cast from Map shouldn't be necessary but
                    // a bug in javac is triggered under certain circumstances
                    // So we do put the cast in as a workaround until
                    // it is resolved.
                    if (uri != null)
                        cookieHandler.put(uri, (Map<java.lang.String,java.util.List<java.lang.String>>)responses.getHeaders());
                }

                /* decide if we're keeping alive:
                 * This is a bit tricky.  There's a spec, but most current
                 * servers (10/1/96) that support this differ in dialects.
                 * If the server/client misunderstand each other, the
                 * protocol should fall back onto HTTP/1.0, no keep-alive.
                 */
                if (usingProxy) { // not likely a proxy will return this
                    keep = responses.findValue("Proxy-Connection");
                }
                if (keep == null) {
                    keep = responses.findValue("Connection");
                }
                if (keep != null && keep.toLowerCase().equals("keep-alive")) {
                    /* some servers, notably Apache1.1, send something like:
                     * "Keep-Alive: timeout=15, max=1" which we should respect.
                     */
                    HeaderParser p = new HeaderParser(
                            responses.findValue("Keep-Alive"));
                    if (p != null) {
                        /* default should be larger in case of proxy */
                        keepAliveConnections = p.findInt("max", usingProxy?50:5);
                        keepAliveTimeout = p.findInt("timeout", usingProxy?60:5);
                    }
                } else if (b[7] != '0') {
                    /*
                     * We're talking 1.1 or later. Keep persistent until
                     * the server says to close.
                     */
                    if (keep != null) {
                        /*
                         * The only Connection token we understand is close.
                         * Paranoia: if there is any Connection header then
                         * treat as non-persistent.
                         */
                        keepAliveConnections = 1;
                    } else {
                        keepAliveConnections = 5;
                    }
                }
            } else if (nread != 8) {
                if (!failedOnce && requests != null) {
                    if (httpuc.getRequestMethod().equals("POST") && !retryPostProp) {
                        // do not retry the request
                    } else {
                        failedOnce = true;
                        closeServer();
                        cachedHttpClient = false;
                        openServer();
                        if (needsTunneling()) {
                            httpuc.doTunneling();
                        }
                        afterConnect();
                        writeRequests(requests, poster);
                        return parseHTTP(responses, pi, httpuc);
                    }
                }
                throw new SocketException("Unexpected end of file from server");
            } else {
                // we can't vouche for what this is....
                responses.set("Content-type", "unknown/unknown");
            }
        } catch (IOException e) {
            throw e;
        }

        int code = -1;
        try {
            String resp;
            resp = responses.getValue(0);
            /* should have no leading/trailing LWS
             * expedite the typical case by assuming it has
             * form "HTTP/1.x <WS> 2XX <mumble>"
             */
            int ind;
            ind = resp.indexOf(' ');
            while (resp.charAt(ind) == ' ')
                ind++;
            code = Integer.parseInt(resp.substring(ind, ind + 3));
        } catch (Exception e) {}

        if (code == HTTP_CONTINUE) {
            responses.reset();
            return parseHTTPHeader(responses, pi, httpuc);
        }

        int cl = -1;

        /*
         * Set things up to parse the entity body of the reply.
         * We should be smarter about avoid pointless work when
         * the HTTP method and response code indicate there will be
         * no entity body to parse.
         */
        String te = null;
        try {
            te = responses.findValue("Transfer-Encoding");
        } catch (Exception e) {}
        if (te != null && te.equalsIgnoreCase("chunked")) {
            serverInput = new ChunkedInputStream(serverInput, this, responses);

            /*
             * If keep alive not specified then close after the stream
             * has completed.
             */
            if (keepAliveConnections <= 1) {
                keepAliveConnections = 1;
                keepingAlive = false;
            } else {
                keepingAlive = true;
            }
            failedOnce = false;
        } else {

            /*
             * If it's a keep alive connection then we will keep
             * (alive if :-
             * 1. content-length is specified, or
             * 2. "Not-Modified" or "No-Content" responses - RFC 2616 states that
             *    204 or 304 response must not include a message body.
             */
            try {
                cl = Integer.parseInt(responses.findValue("content-length"));
            } catch (Exception e) {}

            String requestLine = requests.getKey(0);

            if ((requestLine != null &&
                 (requestLine.startsWith("HEAD"))) ||
                code == HttpURLConnection.HTTP_NOT_MODIFIED ||
                code == HttpURLConnection.HTTP_NO_CONTENT) {
                cl = 0;
            }

            if (keepAliveConnections > 1 &&
                (cl >= 0 ||
                 code == HttpURLConnection.HTTP_NOT_MODIFIED ||
                 code == HttpURLConnection.HTTP_NO_CONTENT)) {
                keepingAlive = true;
                failedOnce = false;
            } else if (keepingAlive) {
                /* Previously we were keeping alive, and now we're not.  Remove
                 * this from the cache (but only here, once) - otherwise we get
                 * multiple removes and the cache count gets messed up.
                 */
                keepingAlive=false;
            }
        }

        /* wrap a KeepAliveStream/MeteredStream around it if appropriate */

        if (cl > 0) {
            // In this case, content length is well known, so it is okay
            // to wrap the input stream with KeepAliveStream/MeteredStream.

            if (pi != null) {
                // Progress monitor is enabled
                pi.setContentType(responses.findValue("content-type"));
            }

            if (isKeepingAlive())   {
                // Wrap KeepAliveStream if keep alive is enabled.
                serverInput = new KeepAliveStream(serverInput, pi, cl, this);
                failedOnce = false;
            }
            else        {
                serverInput = new MeteredStream(serverInput, pi, cl);
            }
        }
        else if (cl == -1)  {
            // In this case, content length is unknown - the input
            // stream would simply be a regular InputStream or
            // ChunkedInputStream.

            if (pi != null) {
                // Progress monitoring is enabled.

                pi.setContentType(responses.findValue("content-type"));

                // Wrap MeteredStream for tracking indeterministic
                // progress, even if the input stream is ChunkedInputStream.
                serverInput = new MeteredStream(serverInput, pi, cl);
            }
            else    {
                // Progress monitoring is disabled, and there is no
                // need to wrap an unknown length input stream.

                // ** This is an no-op **
            }
        }
        else    {
            if (pi != null)
                pi.finishTracking();
        }

        return ret;
    }

    public synchronized InputStream getInputStream() {
        return serverInput;
    }

    public OutputStream getOutputStream() {
        return serverOutput;
    }

    public String toString() {
        return getClass().getName()+"("+url+")";
    }

    public final boolean isKeepingAlive() {
        return getHttpKeepAliveSet() && keepingAlive;
    }

    public void setCacheRequest(CacheRequest cacheRequest) {
        this.cacheRequest = cacheRequest;
    }

    CacheRequest getCacheRequest() {
        return cacheRequest;
    }

    protected void finalize() throws Throwable {
        // This should do nothing.  The stream finalizer will
        // close the fd.
    }

    public void setDoNotRetry(boolean value) {
        // failedOnce is used to determine if a request should be retried.
        failedOnce = value;
    }


    /* Use only on connections in error. */
    public void closeServer() {
        try {
            keepingAlive = false;
            serverSocket.close();
        } catch (Exception e) {}
    }

    /**
     * @return the proxy host being used for this client, or null
     *          if we're not going through a proxy
     */
    public String getProxyHostUsed() {
        if (!usingProxy) {
            return null;
        } else {
            return ((InetSocketAddress)proxy.address()).getHostName();
        }
    }

    /**
     * @return the proxy port being used for this client.  Meaningless
     *          if getProxyHostUsed() gives null.
     */
    public int getProxyPortUsed() {
        if (usingProxy)
            return ((InetSocketAddress)proxy.address()).getPort();
        return -1;
    }
}
