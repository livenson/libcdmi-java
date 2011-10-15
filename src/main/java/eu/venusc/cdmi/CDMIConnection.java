package eu.venusc.cdmi;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

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

    public CDMIConnection(Credentials credentials, URL endpoint)
            throws CertificateException, NoSuchAlgorithmException,
            KeyManagementException, IOException, KeyStoreException,
            UnrecoverableKeyException {

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        // TODO: load server credentials
        trustStore.load(null, null);

        SSLSocketFactory blindTrustFactory = new CustomSSLSocketFactory(
                trustStore);
        blindTrustFactory
                .setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        // XXX: very wierd - using deprecated constructor for Scheme it works. A
        // suggested option
        // - with factory and default port interchanged - fails. 10min debugging
        // didn't result in
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), endpoint.getPort()));
        schemeRegistry.register(new Scheme("https", blindTrustFactory, endpoint.getPort()));

        ThreadSafeClientConnManager conMg = new ThreadSafeClientConnManager(
                schemeRegistry);
        conMg.setMaxTotal(200);
        conMg.setDefaultMaxPerRoute(10);

        HttpHost host = new HttpHost(endpoint.getHost(), 80);
        conMg.setMaxForRoute(new HttpRoute(host), 20);

        httpclient = new DefaultHttpClient(conMg);

        httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(endpoint.getHost(), endpoint.getPort()),
                credentials);

        this.endpoint = endpoint;

        this.blobProxy = new BlobOperations(endpoint, httpclient);
        this.containerProxy = new ContainerOperations(endpoint, httpclient);
        this.nonCdmiContainerProxy = new NonCDMIContainerOperations(endpoint,
                httpclient);
        this.nonCdmiBlobProxy = new NonCDMIBlobOperations(endpoint, httpclient);
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

    public URL getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(URL endpoint) {
        this.endpoint = endpoint;
    }
}
