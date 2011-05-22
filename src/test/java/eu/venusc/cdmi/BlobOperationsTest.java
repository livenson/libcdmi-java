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
import static eu.venusc.cdmi.CDMIResponseStatus.*;

public class BlobOperationsTest extends CDMIConnectionWrapper {

	ContainerOperations cops;
	BlobOperations bops;
	static String containerName;
	static String baseContainer;
	static String objectName;
	static Random random = new Random();
	static File tmpFile;

	public BlobOperationsTest(String name) throws MalformedURLException {
		super(name);
		cops = cdmiConnection.getContainerProxy();
		bops = cdmiConnection.getBlobProxy();
	}

	@Before
	public void setUp() throws IOException {
		baseContainer = "/";
		if (baseContainer.charAt(baseContainer.length() - 1) != '/')
			baseContainer = baseContainer + "/";

		containerName = "libcdmi-java" + random.nextInt();
		tmpFile = Utils.createFile("Place for advertisment.", "libcdmi-java",
				".txt");
		objectName = tmpFile.getName();
	}

	@After
	public void tearDown() throws IOException, CDMIOperationException {
		HttpResponse response = bops.delete(baseContainer + containerName + "/"
				+ objectName);
		int responseCode = response.getStatusLine().getStatusCode();
		// we only accept failures for already deleted objects
		if (responseCode != REQUEST_DELETED
				&& responseCode != REQUEST_NOT_FOUND) {
			fail("Could not clean the object: " + baseContainer + containerName
					+ "/" + objectName);
		}
		/* delete the container */
		response = cops.delete(baseContainer + containerName);
		responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_DELETED)
			fail("Could not clean up the container: " + baseContainer
					+ containerName + "/");
	}

	@Test
	public void testCreate() throws ClientProtocolException, IOException,
			CDMIOperationException {

		/*
		 * 1. Create a container for a blob. 2. Create the blob. 3. Check to see
		 * if the blob is created successfully.
		 */

		HttpResponse response = cops.create(baseContainer + containerName,
				parameters);
		int responseCode = response.getStatusLine().getStatusCode();

		if (responseCode != REQUEST_CREATED)
			fail("Could not create container: " + baseContainer + containerName
					+ "/" + responseCode);

		response = bops.create(
				baseContainer + containerName + "/" + objectName,
				Utils.getBytesFromFile(tmpFile), parameters);
		responseCode = response.getStatusLine().getStatusCode();
		assertEquals("Object could not be created: " + baseContainer
				+ containerName + "/" + objectName, REQUEST_CREATED,
				responseCode);
	}

	@Test
	public void testRead() throws IOException, CDMIOperationException,
			ParseException {
		/*
		 * 1. Create a container to embody a blob. 2. Create the blob. 3. Read
		 * the content of the blob. 4. Compare the local and remote blobs.
		 */
		HttpResponse response = cops.create(baseContainer + containerName,
				parameters);

		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_CREATED)
			fail("Could not create container: " + baseContainer + containerName
					+ "/");

		response = bops.create(
				baseContainer + containerName + "/" + objectName,
				Utils.getBytesFromFile(tmpFile), parameters);

		responseCode = response.getStatusLine().getStatusCode();

		if (responseCode != REQUEST_CREATED)
			fail("Could not create blob object " + baseContainer
					+ containerName + "/" + objectName);

		response = bops.read(baseContainer + containerName + "/" + objectName);
		responseCode = response.getStatusLine().getStatusCode();
		String mimeType = (String) Utils.getElement(response, "mimetype");

		response = bops.read(baseContainer + containerName + "/" + objectName);
		responseCode = response.getStatusLine().getStatusCode();

		if (!mimeType.equals("text/plain")) {
			assertEquals("Local and remote blob objects equal: ", new String(
					Utils.getBytesFromFile(tmpFile)),
					Utils.getObjectContent(response));

		} else {
			assertEquals("Local and remote blob objects are not equal: ", new String(
					Utils.getBytesFromFile(tmpFile)),
					Utils.getTextContent(response));
		}
	}

	@Test
	public void testDelete() throws IOException, CDMIOperationException {
		/*
		 * 1. Create a container for a blob. 2. Create the blob. 3. Delete the
		 * content of the blob. 4. Check the result to see if blob is removed.
		 */
		HttpResponse response = cops.create(baseContainer + containerName,
				parameters);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_CREATED)
			fail("Could not create container: " + baseContainer + containerName
					+ "/");

		response = bops.create(
				baseContainer + containerName + "/" + objectName,
				Utils.getBytesFromFile(tmpFile), parameters);

		responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_CREATED)
			fail("Could not create blob object: " + baseContainer
					+ containerName + "/" + objectName);

		response = bops
				.delete(baseContainer + containerName + "/" + objectName);
		responseCode = response.getStatusLine().getStatusCode();
		assertEquals("Object could not be deleted: ", REQUEST_DELETED, responseCode);
	}
}