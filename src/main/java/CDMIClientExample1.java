import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;

import eu.venusc.cdmi.CDMIConnection;
import eu.venusc.cdmi.CDMIOperationException;

public class CDMIClientExample1 {

	public static void main(String[] args) {

		try {
			URL localFileBackend = new URL("http://localhost:8080/");

			// define custom parameters
			Map parameters = new HashMap();
			parameters.put("mimetype", "text/plain");

			Credentials creds = new UsernamePasswordCredentials("aaa", "aaa");

			CDMIConnection localFileBackendConn = new CDMIConnection(creds,
					localFileBackend);

			localFileBackendConn.createBlob(createTempFile(
					"local file from the laptop").toURI(), "input_1.txt",
					parameters);
			
			File data1 = localFileBackendConn.readBlob("input_1.txt",
					"input_1");
			System.out.println("File downloaded: " + data1.getAbsolutePath());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CDMIOperationException e) {
			System.err.println("CDMI protocol exception. Response code: "
					+ e.getResponseCode());
			e.printStackTrace();
		}
	}

	static File createTempFile(String content) throws IOException {
		File tempFile = File.createTempFile("venusc_", "demo");
		// Write to temporary file
		BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
		out.write(content);
		out.close();
		// Delete temp file when program exits
		tempFile.deleteOnExit();
		return tempFile;
	}

}
