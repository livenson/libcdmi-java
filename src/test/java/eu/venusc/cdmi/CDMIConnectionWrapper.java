package eu.venusc.cdmi;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;

public abstract class CDMIConnectionWrapper extends TestCase {
    CDMIConnection cdmiConnection;
    /* CDMI Server url */
    URL cdmiServer;
    Map<String, Object> parameters;
    Credentials credentials;

    public CDMIConnectionWrapper(String name) throws Exception {
        super(name);
        this.createConnection();
    }

    protected void createConnection() throws Exception {
        cdmiServer = new URL("http://localhost:2364/");
        parameters = new HashMap<String, Object>();
        credentials = new UsernamePasswordCredentials("christian", "venusc");
        cdmiConnection = new CDMIConnection(credentials, cdmiServer);
    }

}
