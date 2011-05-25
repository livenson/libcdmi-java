package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;

import eu.venusc.cdmi.CDMIConnection;
import eu.venusc.cdmi.CDMIOperationException;
import eu.venusc.cdmi.Utils;

import static eu.venusc.cdmi.CDMIResponseStatus.*;

public class CDMIClient {

	/* CDMI base container: e.g. http://hostname:port/cdmicontainer */
	static String cdmiBase = "/cdmicontainer/";
	/* Non CDMI base container: e.g. http://hostname:port/vinager */
	static String nonCdmiBase = "/vinager/";

	static String ncdmi_level1 = "/ncdmi_level1/";
	static String ncdmi_level2 = "/ncdmi_level1/ncdmi_level2/";

	static String level1 = "/level1/";
	static String level2 = "/level1/level2/";

	public static void main(String[] args) {

		try {
			URL localFileBackend = new URL("http://localhost:2364");
			
			URL remoteAWSBackend = new URL("http://174.129.16.188:2364");
			
			URL pdcStorage = new URL("http://cdmi.pdc2.pdc.kth.se:2364");

			// define custom parameters
			Map parameters = new HashMap();
			parameters.put("mimetype", "text/plain");

			Credentials creds = new UsernamePasswordCredentials("aaa", "aaa");

			CDMIConnection localFileBackendConn = new CDMIConnection(creds,
					localFileBackend);
			CDMIConnection remoteAWSBackendConn = new CDMIConnection(creds,
					remoteAWSBackend);
			CDMIConnection pdcStorageConn = new CDMIConnection(creds,
					pdcStorage);

			// upload some data for the demo
			prepareData(localFileBackendConn, remoteAWSBackendConn,
				 parameters);

			// CDMI blob operations: Download a cdmi object.
			System.out.println("== Reading in cdmi input files ==");

			HttpResponse response = localFileBackendConn.getBlobProxy().read(
					cdmiBase + "input_1.txt");
			
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != REQUEST_READ)
				System.out.println("Download failed : " + cdmiBase
						+ "input_1.txt" +" response code: "+ responseCode);

			File data1 = Utils.createFile(Utils.getTextContent(response),
					"input_1", ".local");
			System.out.println("File downloaded: " + data1.getAbsolutePath());

			response = remoteAWSBackendConn.getBlobProxy().read(
					cdmiBase + "input_2.txt");
			 responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != REQUEST_READ)
				System.out.println("Download faild : " + cdmiBase 
						+ "input_2.txt");

			File data2 = Utils.createFile(Utils.getTextContent(response),
					"input_2", ".local");
			System.out.println("File downloaded: " + data2.getAbsolutePath());

			/* NonCDMI blob operations: Download a non cdmi object (large binary
			 data file).*/
			System.out.println("== Reading in non cdmi input files ==");

			response = localFileBackendConn.getNonCdmiBlobProxy().read(
					nonCdmiBase + "noncdmi_input_1.dat");
			responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != REQUEST_READ)
				System.out.println("Download faild : " + cdmiBase + "/"
						+ "noncdmi_input_1");

			/* TODO: change the file format to a runtime mimetype for non text
			 data */
			
			File data3 = Utils.createFile(new String(Utils.extractContents(response)),
					"noncdmi_input_1", ".local");
			System.out.println("File downloaded: " + data3.getAbsolutePath());

			
			response = remoteAWSBackendConn.getBlobProxy().read(
					nonCdmiBase  + "noncdmi_input_2.dat");
			responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != REQUEST_READ)
				System.out.println("Download faild : " + nonCdmiBase  +
						 "noncdmi_input_2");

			File data4 = Utils.createFile(new String(Utils.extractContents(response)),
					"noncdmi_input_2", ".local");
			System.out.println("File downloaded: " + data4.getAbsolutePath());

			// create CDMI directory structure

			System.out
					.println("\n== Creating cdmi folder structure on output storage ==");

			response = pdcStorageConn.getContainerProxy().create(
					level1, parameters);
			responseCode = response.getStatusLine().getStatusCode();

			response = pdcStorageConn.getContainerProxy().create(
					level2, parameters);
			responseCode = response.getStatusLine().getStatusCode();

			System.out.println("\n== Uploading files to output storage ==");

			response = pdcStorageConn.getBlobProxy().create(
					level2  + data1.getName(),
					Utils.getBytesFromFile(data1), parameters);
			responseCode = response.getStatusLine().getStatusCode();

			response = pdcStorageConn.getBlobProxy().create(
					level2 + data2.getName(),
					Utils.getBytesFromFile(data2), parameters);
			responseCode = response.getStatusLine().getStatusCode();

			System.out
					.println("\n== Creating non-cdmi folder structure on output storage ==");

			response = pdcStorageConn.getNonCdmiContainerProxy().create(
					level1, parameters);
			responseCode = response.getStatusLine().getStatusCode();

			pdcStorageConn.getContainerProxy().create(level2,
					parameters);

			response = pdcStorageConn.getNonCdmiContainerProxy().create(
					level1, parameters);
			responseCode = response.getStatusLine().getStatusCode();

			System.out
					.println("\n== Uploading non-cdmi files to output storage ==");

			byte[] value = Utils.getBytesFromFile(data3);
			response = pdcStorageConn.getNonCdmiBlobProxy().create(
					ncdmi_level2 + data3.getName(), value, parameters);
			responseCode = response.getStatusLine().getStatusCode();

			value = Utils.getBytesFromFile(data3);
			response = pdcStorageConn.getNonCdmiBlobProxy().create(
					ncdmi_level2  + data4.getName(), value, parameters);
			responseCode = response.getStatusLine().getStatusCode();

			// see what's in the cdmi folders

			System.out.println("== contents of:  " + level1 + " ==");

			for (String s : pdcStorageConn.getContainerProxy()
					.getChildren(level1)) {
				System.out.println(s);
			}
			System.out.println("==============");

			System.out.println("\n== contents of: " + level2 + " ==");
			for (String s : pdcStorageConn.getContainerProxy()
					.getChildren(level1)) {
				System.out.println(s);
			}
			System.out.println("==============");

			// see what's in the non cdmi folders

			System.out.println("== contents of: " + level1 + " ==");

			for (String s : pdcStorageConn.getNonCdmiContainerProxy()
					.getChildren(level1)) {
				System.out.println(s);
			}
			System.out.println("==============");

			System.out.println("\n== contents of: " + level2 + " ==");
			for (String s : pdcStorageConn.getNonCdmiContainerProxy()
					.getChildren(level2)) {
				System.out.println(s);
			}
			System.out.println("==============");
			cleanUp(localFileBackendConn);
			cleanUp(remoteAWSBackendConn);
			
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
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}


	static void prepareData(CDMIConnection localFileBackendConn,
			CDMIConnection remoteAWSBackendConn, Map parameters)
			throws IOException, CDMIOperationException {
		
		System.out.println("Creating directories.. ");
		
		HttpResponse response = localFileBackendConn.getContainerProxy().create(cdmiBase, parameters);
		int responseCode = response.getStatusLine().getStatusCode();
		System.out.println(cdmiBase+ " created: "+ responseCode);
		
		response =  localFileBackendConn.getNonCdmiContainerProxy().create(nonCdmiBase, parameters);
		responseCode = response.getStatusLine().getStatusCode();
		System.out.println(nonCdmiBase+ " created: "+ responseCode);

		response =  remoteAWSBackendConn.getContainerProxy().create(cdmiBase, parameters);
		responseCode = response.getStatusLine().getStatusCode();
		System.out.println(cdmiBase+ " created: "+ responseCode);

		response =  remoteAWSBackendConn.getNonCdmiContainerProxy().create(nonCdmiBase, parameters);
		responseCode = response.getStatusLine().getStatusCode();
		System.out.println(nonCdmiBase+ " created: "+ responseCode);

		
		System.out.println("Uploading sample cdmi data");
		File tempFile = Utils.createFile("local file from the laptop", "venus_c", "demo");

		String blobPath = cdmiBase + "input_1.txt";
		byte[] value = Utils.getBytesFromFile(tempFile);
		response = localFileBackendConn.getBlobProxy().create(blobPath, value, parameters);
		responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println(blobPath+ " created: "+ responseCode);

		
		
		tempFile = Utils.createFile("remote file on AWS", "venus_c", "demo");
		blobPath = cdmiBase + "input_2.txt";
		response = remoteAWSBackendConn.getBlobProxy().create(blobPath, value, parameters);
		responseCode = response.getStatusLine().getStatusCode();
		System.out.println(blobPath+ " created: "+ responseCode);
		
		System.out.println("Uploading sample non cdmi data");
		
		tempFile = Utils.createFile("local non cdmi data from the laptop", "venus_c", "demo");
		blobPath =  nonCdmiBase + "noncdmi_input_1.dat";
		value = Utils.getBytesFromFile(tempFile);
		response = localFileBackendConn.getNonCdmiBlobProxy().create(blobPath, value,
				parameters);
		responseCode = response.getStatusLine().getStatusCode();
		System.out.println(blobPath+ " created: "+ responseCode);
		
		tempFile = Utils.createFile("remote non cdmi data on AWS", "venus_c", "demo");
		blobPath = nonCdmiBase + "noncdmi_input_2.dat";
		response = remoteAWSBackendConn.getNonCdmiBlobProxy().create(blobPath, value,
				parameters);
		responseCode = response.getStatusLine().getStatusCode();
		System.out.println(blobPath+ " created: "+ responseCode);

		
	}

	static void cleanUp(CDMIConnection conn)
			throws ClientProtocolException, IOException, CDMIOperationException {
		System.out.println("Cleanup " + conn.getEndpoint());

		// delete cdmi
		HttpResponse response = conn.getBlobProxy().delete(level2 + "/input_1.local");
		int responseCode = response.getStatusLine().getStatusCode();
		
		response = conn.getBlobProxy().delete(level2 + "/input_2.local");
		responseCode = response.getStatusLine().getStatusCode();
		
		response = conn.getContainerProxy().delete(level2);
		responseCode = response.getStatusLine().getStatusCode();
		
		response = conn.getContainerProxy().delete(level1);
		responseCode = response.getStatusLine().getStatusCode();
		
		// delete non cdmi
		response = conn.getBlobProxy().delete(ncdmi_level2 + "/input_1.local");
		responseCode = response.getStatusLine().getStatusCode();
		
		response = conn.getBlobProxy().delete(ncdmi_level2 + "/input_2.local");
		responseCode = response.getStatusLine().getStatusCode();
		
		response = conn.getContainerProxy().delete(ncdmi_level2);
		responseCode = response.getStatusLine().getStatusCode();
		
		response =  conn.getContainerProxy().delete(ncdmi_level1);
		responseCode = response.getStatusLine().getStatusCode();

		// delete cached files
		new File("input_1.local").delete();
		new File("input_2.local").delete();
		new File("noncdmi_input_1.local").delete();
		new File("noncdmi_input_2.local").delete();

	}

}
