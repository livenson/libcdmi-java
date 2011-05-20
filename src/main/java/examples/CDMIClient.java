package examples;

import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_CREATED;
import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_DELETED;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;

import eu.venusc.cdmi.BlobOperations;
import eu.venusc.cdmi.CDMIConnection;
import eu.venusc.cdmi.ContainerOperations;
import eu.venusc.cdmi.NonCDMIBlobOperations;
import eu.venusc.cdmi.NonCDMIContainerOperations;
import eu.venusc.cdmi.Utils;

public class CDMIClient {

	static CDMIConnection cdmiConnection;
	/* CDMI Server url */
	static URL cdmiServer;
	static Map<String, Object> parameters;
	/* Username, Password */
	static Credentials creds;
	/* CDMI container operations */
	static ContainerOperations cops = null;
	/* CDMI Blob operations*/
	static BlobOperations bops = null;
	/* Non-CDMI container operations */
	static NonCDMIContainerOperations ncops = null;
	/* Non-CDMI blob operations */
	static NonCDMIBlobOperations nbops = null;
	static String baseContainer = "/MyContainer";
	static File tmpFile = null;

	public CDMIClient() throws MalformedURLException {
		// create a connection.
		this.createConnection();

		// get a cdmi container operations proxy.
		cops = cdmiConnection.getContainerProxy();

		// get a cdmi data object operations proxy.
		bops = cdmiConnection.getBlobProxy();
		
	}

	public static void main(String[] args) {
		testCDMIContainerCreate(baseContainer+ "/vinager");
		testGetChildren(baseContainer+ "/vinager");
		
	}

	static void testCDMIContainerCreate(String containerName) {

		try {
			HttpResponse response = cops.create(baseContainer + containerName,
					parameters);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != REQUEST_CREATED)
				System.err.println("Container " + baseContainer + "vinager"
						+ " couldn't be created");
			else
				System.out.println("Container " + baseContainer + "vinager"
						+ " created");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}

	}

	static void testDeleteContainer(String containerName) {
		try {
			HttpResponse response = cops.delete(baseContainer + containerName);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != REQUEST_DELETED)
				System.err.println("Container couldn't be deleted: "
						+ baseContainer + containerName);
			else
				System.out.println("Container deleted: " + baseContainer
						+ containerName);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}

	static void testGetChildren(String containerName) {
		try {
			String[] children = cops.getChildren(baseContainer);

			System.out.println("The children of container " + containerName
					+ ":");
			for (int i = 0; i < children.length; i++) {
				System.out.println(children[i]);
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}

	static void testCreateCDMIBlob(String blobPath) {
		try {
			
			HttpResponse response = bops.create(blobPath, Utils.getBytesFromFile(tmpFile), parameters);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode!= REQUEST_CREATED)
				System.err.println("Object could not be created: " + blobPath);
			else 
				System.out.println("Object created: " + blobPath);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);

		}
	}
	
	static void testReadCDMIBlob(String blobPath){
		
		try {
			HttpResponse response = bops.read(blobPath);
			int responseCode = response.getStatusLine().getStatusCode();
			String mimeType = (String) Utils.getElement(response, "mimetype");

			response = bops.read(blobPath);
			responseCode = response.getStatusLine().getStatusCode();

			if (!mimeType.equals("text/plain")) {
				if(Utils.getObjectContent(response).equals(new String(Utils.getBytesFromFile(tmpFile))))
				System.out.println("Local and remote blob objects are equal "+ " "+ tmpFile.getName()+ " "+blobPath );
				else System.out.println("Local and remote blob objects are not equal "+ " "+ tmpFile.getName()+ " "+blobPath );
			} else {
				
				 if (Utils.getTextContent(response).equals(new String(Utils.getBytesFromFile(tmpFile))))
					 System.out.println("Local and remote blob objects are equal "+ " "+ tmpFile.getName()+ " "+blobPath );
				 else System.out.println("Local and remote blob objects are not equal "+ " "+ tmpFile.getName()+ " "+blobPath );

			}

			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}


	static void testDeleteBlob(String blobPath){
		try {
			HttpResponse response = bops.delete(blobPath);
			int responseCode = response.getStatusLine().getStatusCode();
			if ( responseCode!=REQUEST_DELETED)
				System.out.println("Object deleted: "+blobPath);
			else System.out.println("Object couldn't be deleted: "+blobPath);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}

	static void createNonCDMIContainer(String containerName){

		try {
			HttpResponse response = ncops.create(baseContainer + containerName,
					parameters);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != REQUEST_CREATED)
				System.err.println("Container " + baseContainer + "vinager"
						+ " couldn't be created");
			else
				System.out.println("Container " + baseContainer + "vinager"
						+ " created");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}

	}


	static void testReadNonCDMIBlob(String blobPath){
		
		try {
			HttpResponse response = nbops.read(blobPath);
			int responseCode = response.getStatusLine().getStatusCode();
			String mimeType = (String) Utils.getElement(response, "mimetype");

			response = nbops.read(blobPath);
			responseCode = response.getStatusLine().getStatusCode();

			if (!mimeType.equals("text/plain")) {
				if(Utils.getObjectContent(response).equals(new String(Utils.getBytesFromFile(tmpFile))))
				System.out.println("Local and remote blob objects are equal "+ " "+ tmpFile.getName()+ " "+blobPath );
				else System.out.println("Local and remote blob objects are not equal "+ " "+ tmpFile.getName()+ " "+blobPath );
			} else {
				
				 if (Utils.getTextContent(response).equals(new String(Utils.getBytesFromFile(tmpFile))))
					 System.out.println("Local and remote blob objects are equal "+ " "+ tmpFile.getName()+ " "+blobPath );
				 else System.out.println("Local and remote blob objects are not equal "+ " "+ tmpFile.getName()+ " "+blobPath );

			}

			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
	
	static void createConnection() throws MalformedURLException {
		cdmiServer = new URL("http://localhost:2364");
		parameters = new HashMap<String, Object>();
		/* File format */
		parameters.put("mimetype", "text/plain");
		creds = new UsernamePasswordCredentials("aaa", "aaa");
		cdmiConnection = new CDMIConnection(creds, cdmiServer);
	}

}
