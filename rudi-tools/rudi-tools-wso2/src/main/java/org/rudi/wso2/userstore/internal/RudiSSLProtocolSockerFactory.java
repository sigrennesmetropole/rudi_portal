/**
 * RUDI Portail
 */
package org.rudi.wso2.userstore.internal;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ControllerThreadSocketFactory;
import org.apache.commons.httpclient.protocol.ReflectionSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Cette classe est inspirée de {@link SSLProtocolSocketFactory} afin de laisser passer les certificats autosignées
 * 
 * @author FNI18300
 *
 */
public class RudiSSLProtocolSockerFactory implements SecureProtocolSocketFactory {

	/**
	 * The factory singleton.
	 */
	private static final SSLProtocolSocketFactory factory = new SSLProtocolSocketFactory();
	private static String hostNameVerifier = EncodingUtil.ALLOW_ALL;
	private static final Log LOGGER = LogFactory.getLog(RudiSSLProtocolSockerFactory.class);

	// This is a a sorted list, if you insert new elements do it ordered.
	private static final String[] BAD_COUNTRY_2LDS = { "ac", "co", "com", "ed", "edu", "go", "gouv", "gov", "info",
			"lg", "ne", "net", "or", "org" };

	private static final String[] LOCALHOSTS = { "::1", "127.0.0.1", "localhost", "localhost.localdomain" };

	static {
		Arrays.sort(LOCALHOSTS);
		Arrays.sort(BAD_COUNTRY_2LDS);
	}

	/**
	 * Gets an singleton instance of the SSLProtocolSocketFactory.
	 * 
	 * @return a SSLProtocolSocketFactory
	 */
	static SSLProtocolSocketFactory getSocketFactory() {
		return factory;
	}

	/**
	 * Constructor for SSLProtocolSocketFactory.
	 */
	public RudiSSLProtocolSockerFactory() {
		super();
	}

	/**
	 * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int,java.net.InetAddress,int)
	 */
	public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException {
		Socket sslSocket = SSLSocketFactory.getDefault().createSocket(host, port, clientHost, clientPort);
		verifyHostName(host, (SSLSocket) sslSocket, hostNameVerifier);
		return sslSocket;
	}

	/**
	 * Attempts to get a new socket connection to the given host within the given time limit.
	 * <p>
	 * This method employs several techniques to circumvent the limitations of older JREs that do not support connect timeout. When running in JRE 1.4 or
	 * above reflection is used to call Socket#connect(SocketAddress endpoint, int timeout) method. When executing in older JREs a controller thread is
	 * executed. The controller thread attempts to create a new socket within the given limit of time. If socket constructor does not return until the
	 * timeout expires, the controller terminates and throws an {@link ConnectTimeoutException}
	 * </p>
	 * 
	 * @param host         the host name/IP
	 * @param port         the port on the host
	 * @param localAddress the local host name/IP to bind the socket to
	 * @param localPort    the port on the local machine
	 * @param params       {@link HttpConnectionParams Http connection parameters}
	 * 
	 * @return Socket a new socket
	 * 
	 * @throws IOException          if an I/O error occurs while creating the socket
	 * @throws UnknownHostException if the IP address of the host cannot be determined
	 * 
	 * @since 3.0
	 */
	@SuppressWarnings({ "unused", "java:S2095" }) // La connexion doit être fermée par les méthodes appelantes
	public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort,
			final HttpConnectionParams params) throws IOException {
		if (params == null) {
			throw new IllegalArgumentException("Parameters may not be null");
		}
		int timeout = params.getConnectionTimeout();
		if (timeout == 0) {
			Socket sslSocket = SSLSocketFactory.getDefault().createSocket(host, port, localAddress, localPort);
			sslSocket.setSoTimeout(params.getSoTimeout());
			verifyHostName(host, (SSLSocket) sslSocket, hostNameVerifier);
			return sslSocket;
		} else {
			// To be eventually deprecated when migrated to Java 1.4 or above
			Socket sslSocket = ReflectionSocketFactory.createSocket("javax.net.ssl.SSLSocketFactory", host, port,
					localAddress, localPort, timeout);
			if (sslSocket == null) { // NOSONAR : Sonar se trompe car la 2e ligne de la méthode renvoie null
				sslSocket = ControllerThreadSocketFactory.createSocket(this, host, port, localAddress, localPort,
						timeout);
			}
			sslSocket.setSoTimeout(params.getSoTimeout());
			verifyHostName(host, (SSLSocket) sslSocket, hostNameVerifier);
			return sslSocket;
		}
	}

	/**
	 * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int)
	 */
	public Socket createSocket(String host, int port) throws IOException {
		Socket sslSocket = SSLSocketFactory.getDefault().createSocket(host, port);
		verifyHostName(host, (SSLSocket) sslSocket, hostNameVerifier);
		return sslSocket;
	}

	/**
	 * @see SecureProtocolSocketFactory#createSocket(java.net.Socket,java.lang.String,int,boolean)
	 */
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
		Socket sslSocket = ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(socket, host, port,
				autoClose);
		verifyHostName(host, (SSLSocket) sslSocket, hostNameVerifier);
		return sslSocket;
	}

	/**
	 * Verifies that the given hostname in certicifate is the hostname we are trying to connect to http://www.cvedetails.com/cve/CVE-2012-5783/
	 * 
	 * @param host
	 * @param ssl
	 * @throws IOException
	 */

	protected static void verifyHostName(String host, SSLSocket ssl, String hostNameVerifier) throws IOException {
		if (host == null) {
			throw new IllegalArgumentException("host to verify was null");
		}
		// Always ok
		LOGGER.debug("verifyHostName done:" + host);
	}

	static boolean isLocalhost(String host) {
		host = host != null ? host.trim().toLowerCase() : "";
		if (host.startsWith("::1")) {
			int x = host.lastIndexOf('%');
			if (x >= 0) {
				host = host.substring(0, x);
			}
		}
		int x = Arrays.binarySearch(LOCALHOSTS, host);
		return x >= 0;
	}

	static boolean validCountryWildcard(final String[] parts) {
		if (parts.length != 3 || parts[2].length() != 2) {
			return true; // it's not an attempt to wildcard a 2TLD within a country code
		}
		return Arrays.binarySearch(BAD_COUNTRY_2LDS, parts[1]) < 0;
	}

	/**
	 * All instances of SSLProtocolSocketFactory are the same.
	 */
	public boolean equals(Object obj) {
		return ((obj != null) && obj.getClass().equals(getClass()));
	}

	/**
	 * All instances of SSLProtocolSocketFactory have the same hash code.
	 */
	public int hashCode() {
		return getClass().hashCode();
	}

}
