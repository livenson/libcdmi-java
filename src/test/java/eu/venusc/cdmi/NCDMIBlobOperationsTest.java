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

public class NCDMIBlobOperationsTest extends CDMIConnectionWrapper implements
		CDMIResponseStatus {

	NCDMIContainerOperations cops = null;
	NCDMIBlobOperations bops = null;
	static String containerName = null;
	static String baseContainer = null;
	static String objectName = null;
	static Random random = new Random();
	static File tmpFile = null;

	public NCDMIBlobOperationsTest(String name) throws MalformedURLException {
		super(name);
		cops = cdmiConnection.getNcdmiContainerProxy();
		bops = cdmiConnection.getNcdmiBlobProxy();
	}

	@Before
	public void setUp() throws IOException {
		baseContainer = "/";
		if (baseContainer.charAt(baseContainer.length() - 1) != '/')
			baseContainer = baseContainer + "/";

		containerName = "noncdmi-container" + random.nextInt();
		tmpFile = Utils.createFile("put your data here", "venus_c", ".txt");
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
		response = cops.delete(baseContainer + containerName);
		responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_DELETED)
			fail("Could not clean up the container: " + baseContainer
					+ containerName + "/");
	}

	@Test
	public void testCreate() throws ClientProtocolException, IOException,
			CDMIOperationException {

		HttpResponse response = cops.create(baseContainer + containerName,
				parameters);
		int responseCode = response.getStatusLine().getStatusCode();

		if (responseCode != REQUEST_CREATED)
			fail("Could not create container: " + baseContainer + containerName
					+ "/" + responseCode);

		response = bops.create(
				baseContainer + containerName + "/" + objectName, Utils
						.getBytesFromFile(tmpFile), parameters);
		responseCode = response.getStatusLine().getStatusCode();
		assertEquals("Object could not be created: " + baseContainer
				+ containerName + "/" + objectName, REQUEST_CREATED,
				responseCode);

	}

	@Test
	public void testRead() throws IOException, CDMIOperationException,
			ParseException {
		HttpResponse response = cops.create(baseContainer + containerName,
				parameters);

		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_CREATED)
			fail("Could not create container: " + baseContainer + containerName
					+ "/");
		response = bops.create(
				baseContainer + containerName + "/" + objectName, Utils
						.getBytesFromFile(tmpFile), parameters);

		responseCode = response.getStatusLine().getStatusCode();

		if (responseCode != REQUEST_CREATED)
			fail("Could not create blob object " + baseContainer
					+ containerName + "/" + objectName);

		response = bops.read(baseContainer + containerName + "/" + objectName);
		responseCode = response.getStatusLine().getStatusCode();
		String mimeType = response.getFirstHeader("Content-Type").getValue();
		
		if (mimeType.equals("text/plain")) {
			assertEquals("Local and remote blob objects equal: ", new String(
					Utils.getBytesFromFile(tmpFile)), new String(Utils
					.extractContents(response)));

		} else {
			assertEquals("Local and remote blob objects equal: ", new String(
					Utils.getBytesFromFile(tmpFile)), Utils
					.extractContents(response));
		}
	}

	@Test
	public void testDelete() throws IOException, CDMIOperationException {
		HttpResponse response = cops.create(baseContainer + containerName,
				parameters);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_CREATED)
			fail("Could not create container: " + baseContainer + containerName
					+ "/");

		response = bops.create(
				baseContainer + containerName + "/" + objectName, Utils
						.getBytesFromFile(tmpFile), parameters);

		responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_CREATED)
			fail("Could not create blob object: " + baseContainer
					+ containerName + "/" + objectName);

		response = bops
				.delete(baseContainer + containerName + "/" + objectName);
		responseCode = response.getStatusLine().getStatusCode();
		assertEquals("Object deleted: ", REQUEST_DELETED, responseCode);
	}
}
