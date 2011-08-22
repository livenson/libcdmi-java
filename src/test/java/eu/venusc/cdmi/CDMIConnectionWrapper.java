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

import junit.framework.TestCase;


public abstract class CDMIConnectionWrapper extends TestCase {
    CDMIConnection cdmiConnection;
    /* CDMI Server url */
    URL cdmiServer;
    Map<String, Object> parameters;
    
    public CDMIConnectionWrapper(String name) throws KeyManagementException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        super(name);
        this.createConnection();
    }

    protected void createConnection() throws KeyManagementException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        cdmiServer = new URL("http://localhost:2364/");
        parameters = new HashMap<String, Object>();
        //credentials = new UsernamePasswordCredentials("christian", "venusc");
        cdmiConnection = new CDMIConnection("aaa", "aaa", cdmiServer);
    }


}
