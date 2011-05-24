package eu.venusc.cdmi;

import java.net.URL;

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

	public void setNonCdmiContainerProxy(NonCDMIContainerOperations nonCdmiContainerProxy) {
		this.nonCdmiContainerProxy = nonCdmiContainerProxy;
	}

	public CDMIConnection(Credentials creds, URL endpoint) {

		/** Create and manage pool of connections. */
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
				.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory
				.getSocketFactory()));

		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
				schemeRegistry);

		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(20);
		HttpHost host = new HttpHost(endpoint.getHost(), 80);
		cm.setMaxForRoute(new HttpRoute(host), 50);
		httpclient = new DefaultHttpClient(cm);
		httpclient.getCredentialsProvider().setCredentials(
				new AuthScope(endpoint.getHost(), endpoint.getPort()), creds);
		this.endpoint = endpoint;
		this.blobProxy = new BlobOperations(endpoint, httpclient);
		this.containerProxy = new ContainerOperations(endpoint, httpclient);
		this.nonCdmiContainerProxy = new NonCDMIContainerOperations(endpoint, httpclient);
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
