package eu.venusc.cdmi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
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
	/* Username, Password */
	Credentials credentials;

	public CDMIConnectionWrapper(String name) throws KeyManagementException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
		super(name);
		this.createConnection();
	}

	protected void createConnection() throws KeyManagementException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
		cdmiServer = new URL("https://localhost:8080");
		parameters = new HashMap<String, Object>();
		/*
		 * File format: http://silk.nih.gov/public/zzyzzap.@www.silk.types.html
		 */
		credentials = new UsernamePasswordCredentials("aaa", "aaa");
		cdmiConnection = new CDMIConnection(credentials, cdmiServer);
	}

}