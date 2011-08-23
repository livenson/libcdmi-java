package eu.venusc.cdmi;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;

import junit.framework.TestCase;


public abstract class CDMIConnectionWrapper extends TestCase {
    CDMIConnection cdmiConnection;
    /* CDMI Server url */
    URL cdmiServer;
    Map<String, Object> parameters;
    Credentials credentials;
    public CDMIConnectionWrapper(String name) throws KeyManagementException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        super(name);
        this.createConnection();
    }

    protected void createConnection() throws KeyManagementException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        cdmiServer = new URL("http://cdmi.pdc2.pdc.kth.se:2364");
        parameters = new HashMap<String, Object>();
        credentials = new UsernamePasswordCredentials("christian", "venusc");
        cdmiConnection = new CDMIConnection(credentials, cdmiServer);
    }


}
