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

import eu.venusc.cdmi.CDMIConnection;

public class CDMIConnectionTest extends TestCase {

	CDMIConnection cdmiConnection = null;
	URL cdmiServer = null;
	Map parameters = null;
	Credentials creds = null;

	public CDMIConnectionTest(String name) {
		super(name);
		try {
			cdmiServer = new URL("http://localhost:2364");
			parameters = new HashMap();
			parameters.put("mimetype", "text/plain");
			creds = new UsernamePasswordCredentials("aaa", "aaa");
			cdmiConnection = new CDMIConnection(creds, cdmiServer);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	static File createTempFile(String content) throws IOException {

		File tempFile = File.createTempFile("venusc_", ".txt");
		// Write to temporary file
		BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
		out.write(content);
		out.close();
		tempFile.deleteOnExit();
		return tempFile;

	}

}
