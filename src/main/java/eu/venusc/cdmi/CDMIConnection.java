package eu.venusc.cdmi;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;


public class CDMIConnection {


	private DefaultHttpClient httpclient;
	private BlobOperations blobProxy;
	private ContainerOperations containerProxy;
	private NonCDMIContainerOperations nonCdmiContainerProxy;
	private NonCDMIBlobOperations nonCdmiBlobProxy;
	private URL endpoint;

	public NonCDMIBlobOperations getNonCdmiBlobProxy() {
		return nonCdmiBlobProxy;
	}

	public void setNonCdmiBlobProxy(NonCDMIBlobOperations ncdmiBlobProxy) {
		this.nonCdmiBlobProxy = ncdmiBlobProxy;
	}

	public NonCDMIContainerOperations getNonCdmiContainerProxy() {
		return nonCdmiContainerProxy;
	}

	public void setNonCdmiContainerProxy(
			NonCDMIContainerOperations nonCdmiContainerProxy) {
		this.nonCdmiContainerProxy = nonCdmiContainerProxy;
	}

	public CDMIConnection(Credentials creds, URL endpoint)
			throws CertificateException, NoSuchAlgorithmException,
			KeyManagementException, IOException, KeyStoreException,
			UnrecoverableKeyException {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		System.setProperty("java.protocol.handler.pkgs",
				"com.sun.net.ssl.internal.www.protocol");
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

		// trust All HttpsCertificates

		TrustManager[] trustManagers = new TrustManager[] { new FakeX509TrustManager() };
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, trustManagers, new SecureRandom());
		SSLSocketFactory sf = new SSLSocketFactory(context);
		
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
				.getSocketFactory()));
		schemeRegistry
				.register(new Scheme("https", 443, sf.getSocketFactory()));

		ThreadSafeClientConnManager conMg = new ThreadSafeClientConnManager(
				schemeRegistry);
		conMg.setMaxTotal(200);
		conMg.setDefaultMaxPerRoute(20);

		HttpHost host = new HttpHost(endpoint.getHost(), 80);
		conMg.setMaxForRoute(new HttpRoute(host), 50);

		httpclient = new DefaultHttpClient(conMg);

		httpclient.getCredentialsProvider().setCredentials(
				new AuthScope(endpoint.getHost(), endpoint.getPort()), creds);

		this.endpoint = endpoint;

		this.blobProxy = new BlobOperations(endpoint, httpclient);
		this.containerProxy = new ContainerOperations(endpoint, httpclient);
		this.nonCdmiContainerProxy = new NonCDMIContainerOperations(endpoint,
				httpclient);
		this.nonCdmiBlobProxy = new NonCDMIBlobOperations(endpoint, httpclient);
	}

	public URL getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(URL endpoint) {
		this.endpoint = endpoint;
		
	}

	public DefaultHttpClient getHttpclient() {
		return httpclient;
	}

	public void setHttpclient(DefaultHttpClient httpclient) {
		this.httpclient = httpclient;
	}

	public BlobOperations getBlobProxy() {
		return blobProxy;
	}

	public void setBlobProxy(BlobOperations blobProxy) {
		this.blobProxy = blobProxy;
	}

	public ContainerOperations getContainerProxy() {
		return containerProxy;
	}

	public void setContainerProxy(ContainerOperations containerProxy) {
		this.containerProxy = containerProxy;
	}

}
