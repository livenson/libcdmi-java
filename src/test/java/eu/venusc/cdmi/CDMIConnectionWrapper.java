package eu.venusc.cdmi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;

public abstract class CDMIConnectionWrapper extends TestCase {
	CDMIConnection cdmiConnection;
	/* CDMI Server url*/
	URL cdmiServer;
	Map <String, Object> parameters;
	/* Username, Password*/
	Credentials creds;	
	
	public CDMIConnectionWrapper(String name) throws MalformedURLException {
		super(name);
		this.createConnection();
	}
	
	protected void createConnection() throws MalformedURLException {		
		cdmiServer = new URL("http://174.129.16.188:2364/");
		parameters = new HashMap <String, Object>();
		/* File format*/
		parameters.put("mimetype", "text/plain");
		creds = new UsernamePasswordCredentials("aaa", "aaa");
		cdmiConnection = new CDMIConnection(creds, cdmiServer);		
	}	
	
	
}
