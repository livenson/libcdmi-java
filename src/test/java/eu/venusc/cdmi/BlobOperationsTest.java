package eu.venusc.cdmi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BlobOperationsTest extends CDMIConnectionWrapper implements
		CDMIResponseStatus {

	ContainerOperations cops = null;
	BlobOperations bops = null;
	static String containerName = null;
	static String baseContainer = null;
	static String objectName = null;
	static Random random = new Random();
	static File tmpFile = null;

	public BlobOperationsTest(String name) throws MalformedURLException {
		super(name);
		cops = cdmiConnection.getContainerProxy();
		bops = cdmiConnection.getBlobProxy();
	}

	@Before
	public void setUp() throws IOException {		
		baseContainer = "/";
		containerName = "libcdmi-java" + random.nextInt();
		tmpFile = Utils.createFile("put your data here", "venus_c", ".txt");
		objectName = tmpFile.getName();		
	}

	@After
	public void tearDown() throws IOException, CDMIOperationException {

		HttpResponse response = null;
		int responseCode = 0;

		response = bops.delete(containerName + "/" + objectName);
		responseCode = response.getStatusLine().getStatusCode();
		// we only accept failures for already deleted objects
		if (responseCode != REQUEST_DELETED && responseCode != REQUEST_NOT_FOUND) {		
			fail("Could not clean the object: " + containerName + "/" + objectName);
		}
		/* delete the container */
		response = cops.delete(containerName + "/");
		responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_DELETED)
			fail("Could not clean the container: " + containerName + "/");
	}


	@Test
	public void testCreate() throws ClientProtocolException, IOException,
			CDMIOperationException {
		HttpResponse response = null;
		int responseCode = 0;
		/*
		 * 1. Create a container for a blob.
		 * 2. Create the blob.
		 * 3. Check to see if the blob is created successfully.
		 */

		response = cops.create(containerName, parameters);
		responseCode = response.getStatusLine().getStatusCode();

		if (responseCode != REQUEST_CREATED)
			fail("Could not create container: " + responseCode);

		response = bops.create(containerName + "/" + objectName, Utils
				.getBytesFromFile(tmpFile), parameters);
		responseCode = response.getStatusLine().getStatusCode();
		assertEquals("Object created: ", REQUEST_CREATED, responseCode);

	}

	
	@Test
	public void testRead() throws IOException, CDMIOperationException,
			ParseException {		
		HttpResponse response = null;
		int responseCode = -1;

		/*
		 * 1. Create a container to embody a blob. 
		 * 2. Create the blob. 
		 * 3. Read the content of the blob. 
		 * 4. Compare the local and remote blobs.
		 */
		response = cops.create(containerName + "/", parameters);
		
		responseCode = response.getStatusLine().getStatusCode();	
		if (responseCode != REQUEST_CREATED)
			fail("Could not create container: " + containerName + "/");

		response = bops.create(containerName + "/" + objectName, Utils
				.getBytesFromFile(tmpFile), parameters);

		responseCode = response.getStatusLine().getStatusCode();
		
		if (responseCode != REQUEST_CREATED)
			fail("Could not create blob object " + containerName + "/"
					+ objectName);

		response = bops.read(containerName + "/" + objectName);
		responseCode = response.getStatusLine().getStatusCode();
		String mimeType = (String) Utils.getElement(response, "mimetype");

		response = bops.read(containerName + "/" + objectName);
		responseCode = response.getStatusLine().getStatusCode();

		if (!mimeType.equals("text/plain")) {
			assertEquals("Local and remote blob objects equal: ", new String(
					Utils.getBytesFromFile(tmpFile)), Utils
					.getObjectContent(response));

		} else {
			assertEquals("Local and remote blob objects equal: ", new String(
					Utils.getBytesFromFile(tmpFile)), Utils
					.getTextContent(response));
		}

	}

	
	@Test
	public void testDelete() throws IOException, CDMIOperationException {
		HttpResponse response = null;
		int responseCode = 0;

		/*
		 * 1. Create a container for a blob.
		 * 2. Create the blob. 
		 * 3. Delete the content of the blob.
		 * 4. Check the result to see if blob is removed.
		 */
		response = cops.create(containerName + "/", parameters);
		responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_CREATED)
			fail("Could not create container: " + containerName + "/");

		response = bops.create(containerName + "/" + objectName, Utils
				.getBytesFromFile(tmpFile), parameters);

		responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_CREATED)
			fail("Could not create blob object " + containerName + "/"
					+ objectName);

		response = bops.delete(containerName + "/" + objectName);
		responseCode = response.getStatusLine().getStatusCode();
		assertEquals("Object deleted: ", REQUEST_DELETED, responseCode);

	}
}
