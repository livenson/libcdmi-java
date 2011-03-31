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

public class CDMIClientExample2 {

	public static void main(String[] args) {

		try {
			URL localFileBackend = new URL("http://localhost:8080/");
			URL remoteAWSBackend = new URL(
					"http://ec2-184-72-152-135.compute-1.amazonaws.com:8080/");
			URL remoteAzureBackend = new URL(
					"http://ec2-204-236-221-158.compute-1.amazonaws.com:8080/");
			URL remoteCDMIServe = new URL("http://localhost:2364/");

			// define custom parameters
			Map parameters = new HashMap();
			parameters.put("mimetype", "text/plain");

			Credentials creds = new UsernamePasswordCredentials("aaa", "aaa");

			CDMIConnection localFileBackendConn = new CDMIConnection(creds,
					localFileBackend);
			CDMIConnection remoteAWSBackendConn = new CDMIConnection(creds,
					remoteAWSBackend);
			CDMIConnection remoteCDMIServeConn = new CDMIConnection(creds,
					remoteCDMIServe);
			CDMIConnection remoteAzureBackendConn = new CDMIConnection(creds,
					remoteAzureBackend);

			// upload some data for the demo
			prepareData(localFileBackendConn, remoteAWSBackendConn,
					remoteCDMIServeConn, parameters);

			// perform basic operations on blob
			System.out.println("== Reading in input files ==");

			File data1 = localFileBackendConn
					.readBlob("input_1.txt", "input_1");
			System.out.println("File downloaded: " + data1.getAbsolutePath());
			File data2 = remoteAWSBackendConn
					.readBlob("input_2.txt", "input_2");
			System.out.println("File downloaded: " + data2.getAbsolutePath());
			File data3 = remoteCDMIServeConn.readBlob("input_3.txt", "input_3");
			System.out.println("File downloaded: " + data3.getAbsolutePath());

			// create directory structure
			System.out
					.println("\n== Creating folder structure on output storage ==");
			remoteAzureBackendConn.createContainer("/experiment1", parameters);
			remoteAzureBackendConn.createContainer("/experiment1/run1",
					parameters);

			System.out.println("\n== Uploading files to output storage ==");
			remoteAzureBackendConn.createBlob(data1.toURI(),
					"/experiment1/run1/input_1", parameters);
			remoteAzureBackendConn.createBlob(data2.toURI(),
					"/experiment1/run1/input_2", parameters);
			remoteAzureBackendConn.createBlob(data3.toURI(),
					"/experiment1/run1/input_3", parameters);

			// see what's in the folder
			String p = "/experiment1";
			System.out.println("== " + p + " ==");

			for (String s : remoteAzureBackendConn.getChildren(p)) {
				System.out.println(s);
			}
			System.out.println("==============");

			p = "/experiment1/run1/";
			System.out.println("\n== " + p + " ==");
			for (String s : remoteAzureBackendConn.getChildren(p)) {
				System.out.println(s);
			}
			System.out.println("==============");

			// cleanUp(remoteAzureBackendConn);

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

	static void prepareData(CDMIConnection localFileBackendConn,
			CDMIConnection remoteAWSBackendConn,
			CDMIConnection remoteCDMIServeConn, Map parameters)
			throws IOException, CDMIOperationException {
		System.out.println("Uploading sample data");
		localFileBackendConn.createBlob(createTempFile(
				"local file from the laptop").toURI(), "input_1.txt",
				parameters);

		remoteAWSBackendConn.createBlob(createTempFile("remote file on AWS")
				.toURI(), "input_2.txt", parameters);

		remoteCDMIServeConn.createBlob(createTempFile(
				"remote file exported through CDMI-serve").toURI(),
				"input_3.txt", parameters);

	}

	static void cleanUp(CDMIConnection remoteAzureBackendConn)
			throws ClientProtocolException, IOException, CDMIOperationException {
		System.out.println("Cleanup...");
		remoteAzureBackendConn.delete("/experiment1/run1/input_1");
		remoteAzureBackendConn.delete("/experiment1/run1/input_2");
		remoteAzureBackendConn.delete("/experiment1/run1/input_3");

		remoteAzureBackendConn.deleteContainer("/experiment1/run1");
		remoteAzureBackendConn.deleteContainer("/experiment1/");

		// delete cached files
		new File("input_1").delete();
		new File("input_2").delete();
		new File("input_3").delete();
	}
}
