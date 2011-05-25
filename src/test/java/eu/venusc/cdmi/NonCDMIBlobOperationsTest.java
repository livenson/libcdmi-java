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

public class NonCDMIBlobOperationsTest extends CDMIConnectionWrapper {

	NonCDMIContainerOperations cops;
	NonCDMIBlobOperations bops;
	static String containerName;
	static String baseContainer;
	static String objectName;
	static Random random = new Random();
	static File tmpFile;

	public NonCDMIBlobOperationsTest(String name) throws MalformedURLException {
		super(name);
		cops = cdmiConnection.getNonCdmiContainerProxy();
		bops = cdmiConnection.getNonCdmiBlobProxy();
	}

	@Before
	public void setUp() throws IOException {
		baseContainer = "/";
		if (baseContainer.charAt(baseContainer.length() - 1) != '/')
			baseContainer = baseContainer + "/";

		containerName = "noncdmi-container" + random.nextInt();

		if (parameters.get("mimetype").equals("text/plain"))
			tmpFile = Utils.createFile("Place for advertisment.", "venus_c",
					".txt");
		else
			tmpFile = Utils.createZip("venus_c");
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
					+ containerName + "/" + objectName + " "+ responseCode);

		response = bops.read(baseContainer + containerName + "/" + objectName);
		responseCode = response.getStatusLine().getStatusCode();
		String mimeType = response.getFirstHeader("Content-Type").getValue();

			assertEquals("Local and remote blob objects are not equal: ",
					new String(Utils.getBytesFromFile(tmpFile)), new String(
							Utils.extractContents(response)));
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
				baseContainer + containerName + "/" + objectName,
				Utils.getBytesFromFile(tmpFile), parameters);

		responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_CREATED)
			fail("Could not create blob object: " + baseContainer
					+ containerName + "/" + objectName+ " "+ responseCode);

		response = bops
				.delete(baseContainer + containerName + "/" + objectName);
		responseCode = response.getStatusLine().getStatusCode();
		assertEquals("Object could not be deleted: ", REQUEST_DELETED,
				responseCode);
	}
}
