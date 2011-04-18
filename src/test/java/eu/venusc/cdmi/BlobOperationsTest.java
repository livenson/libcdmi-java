package eu.venusc.cdmi;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.print.attribute.ResolutionSyntax;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BlobOperationsTest extends CDMIConnectionTest implements CDMIResponseStatus {

	ContainerOperations cops = null;
	BlobOperations bops = null;
	static String containerName = null;
	static String baseContainer = null;
	static String objectName = null;
	static Random random = null;
	static File tmpFile = null;

	public BlobOperationsTest(String name) {
		super(name);
		cops = cdmiConnection.getContainerProxy();
		bops = cdmiConnection.getBlobProxy();

	}

	@Before
	public void setUp() {
		random = new Random();
		baseContainer = "/";
		try {
			containerName = "libcdmi-java" + random.nextInt();
			tmpFile = Utils.createFile("put your data here", "venus_c", ".txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		objectName = tmpFile.getName();
	}

	@After
	public void tearDown() {

		HttpResponse response = null;
		int responseCode = 0;
		try {

			response = bops.delete(containerName + "/" + objectName);
			responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != REQUEST_DELETED) {
				/* when container is removed */
				if (responseCode == REQUEST_NOT_FOUND)
					;
				else
					fail("Could not clean the object: " + containerName + "/"
							+ objectName);
			}

			// delete the container
			response = cops.delete(containerName + "/");
			responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != REQUEST_DELETED)
				fail("Could not clean the container: " + containerName + "/");

		} catch (CDMIOperationException e) {
			System.err.println(e.getMessage());
		} catch (ClientProtocolException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		containerName = null;
		objectName = null;
		random = null;

	}

	/**
	 * Test method for creating a blob object:response code 201 in case of
	 * success.
	 * {@link eu.venusc.cdmi.BlobOperations#create(java.lang.String, java.lang.String, byte[], java.util.Map)}
	 * .
	 * 
	 * @throws CDMIOperationException
	 */
	@Test
	public void testCreate() {
		HttpResponse response = null;
		int responseCode = 0;
		try {
			/*
			 * 1. Create a container to embody a bolb 2. Create the blob 3.
			 * Check to see if the blob is created successfully.
			 */

			response = cops.create(containerName, parameters);
			responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != REQUEST_CREATED)
				fail("Could not create container: " + responseCode);

			response = bops.create(containerName + "/" + objectName, Utils
					.getBytesFromFile(tmpFile), parameters);
			responseCode = response.getStatusLine().getStatusCode();
			assertEquals("Object created: ", REQUEST_CREATED, responseCode);
		} catch (CDMIOperationException e) {
			System.err.println(e.getMessage());

		} catch (ClientProtocolException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

	/**
	 * Test method for reading a blob object.
	 * {@link eu.venusc.cdmi.BlobOperations#read(java.lang.String, java.lang.String)}
	 * .
	 * 
	 * @throws ParseException
	 * @throws IOException
	 */
	@Test
	public void testRead() {
		HttpResponse response = null;
		int responseCode = -1;
		try {

			/*
			 * 1. Create a container to embody a bolb 2. Create the blob 3. Read
			 * the content of the bolb 4. Compare the local and remote blobs
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
				assertEquals("Local and remote blob objects equal: ",
						new String(Utils.getBytesFromFile(tmpFile)), Utils
								.getObjectContent(response));

			} else {
				assertEquals("Local and remote blob objects equal: ",
						new String(Utils.getBytesFromFile(tmpFile)), Utils
								.getTextContent(response));
			}

		} catch (ClientProtocolException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CDMIOperationException e) {
			System.err.println(e.getMessage() + "  ++ " + responseCode);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for deleting a blob object: response code 204 in case of
	 * success.
	 * {@link eu.venusc.cdmi.BlobOperations#delete(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testDelete() {
		HttpResponse response = null;
		int responseCode = 0;
		try {
			/*
			 * 1. Create a container to embody a bolb 2. Create the blob 3.
			 * Delete the content of the bolb 4. Check the result to see if bolb
			 * is removed
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
			
		} catch (ClientProtocolException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (CDMIOperationException e) {
			System.err.println(e.getMessage());
		}
	}
}
