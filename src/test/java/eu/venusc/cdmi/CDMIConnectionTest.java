package eu.venusc.cdmi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;

public class CDMIConnectionTest extends TestCase {

	CDMIConnection cdmiConnection = null;
	/* CDMI Server url*/
	URL cdmiServer = null;
	Map parameters = null;
	/* Username, Password*/
	Credentials creds = null;

	public CDMIConnectionTest(String name) {
		super(name);
		try {
			
			cdmiServer = new URL("http://localhost:2364");
			parameters = new HashMap();
			/* File format*/
			parameters.put("mimetype", "text/plain");
			creds = new UsernamePasswordCredentials("aaa", "aaa");
			cdmiConnection = new CDMIConnection(creds, cdmiServer);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

}
