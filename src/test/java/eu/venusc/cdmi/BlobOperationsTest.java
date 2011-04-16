package eu.venusc.cdmi;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BlobOperationsTest extends CDMIConnectionTest {

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
	public void setUp() throws Exception {
		random = new Random();
		baseContainer = "/";
		containerName =  "libcdmi-java"+ random.nextInt();
		tmpFile = createTempFile("put your data here");
		objectName = tmpFile.getName();
	}

	@After
	public void tearDown() throws Exception {

		HttpResponse response = null;
		int responseCode = 0;
		try {
			
			response = bops.delete(containerName + "/" + objectName);
			responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != 204){
				/*when container is removed*/
				if (responseCode== 404)
					;
				else fail("Could not clean the object: " + containerName + "/"+objectName);
			}
				
			
			// delete the container
			response = cops.delete(containerName + "/");
			responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != 204)
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

			response = cops.create(containerName, parameters);
			responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != 201)
				fail("Could not create container: " + responseCode);

			response = bops.create(containerName + "/" + objectName , Utils.getBytesFromFile(tmpFile), parameters);
			responseCode = response.getStatusLine().getStatusCode();
			assertEquals("Object created: ", 201, responseCode);
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
	 */
	@Test
	public void testRead() {

		HttpResponse response = null;
		int responseCode = 0;
		try {
			response = cops.create(containerName + "/", parameters);
			responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != 201)
				fail("Could not create container: " + containerName + "/");

			response = bops.create(containerName + "/"+ objectName, Utils
					.getBytesFromFile(tmpFile), parameters);

			responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != 201)
				fail("Could not create blob object " + containerName + "/"
						+ objectName);

			response = bops.read(containerName + "/"+ objectName);
			responseCode = response.getStatusLine().getStatusCode();
			String mimeType = (String) Utils.getElement(response, "mimetype");
			if (mimeType != "text/plain") {
				assertEquals("Local and remote blob objects equal: ",
						new String(Utils.getBytesFromFile(tmpFile)), Utils
								.getObjectContent(response));

			} else {
				assertEquals("Local and remote blob objects equal: ",
						new String(Utils.getBytesFromFile(tmpFile)), Utils
								.getObjectContent(response));
			}

		} catch (ClientProtocolException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (CDMIOperationException e) {
			System.err.println(e.getMessage());
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
			response = cops.create(containerName + "/", parameters);

			responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != 201)
				fail("Could not create container: " + containerName + "/");

			response = bops.create(containerName + "/"+ objectName, Utils
					.getBytesFromFile(tmpFile), parameters);

			responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != 201)
				fail("Could not create blob object " + containerName + "/"
						+ objectName);

			response = bops.delete(containerName + "/"+ objectName);
	
			responseCode = response.getStatusLine().getStatusCode();
			assertEquals("Object deleted: ", 204, responseCode);
		} catch (ClientProtocolException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (CDMIOperationException e) {
			System.err.println(e.getMessage());
		}}
		

	}


