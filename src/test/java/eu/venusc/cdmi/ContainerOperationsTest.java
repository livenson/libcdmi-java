package eu.venusc.cdmi;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ContainerOperationsTest extends CDMIConnectionTest implements
		CDMIResponseStatus {

	ContainerOperations cops = null;
	static String containerName = null;
	static String baseContainer = null;
	static Random random = null;

	public ContainerOperationsTest(String name) {
		super(name);
		cops = cdmiConnection.getContainerProxy();
	}

	@Before
	public void setUp() {
		random = new Random();
		baseContainer = "/";
		containerName = "libcdmi-java" + random.nextInt();
	}

	@After
	public void tearDown() throws IOException, ParseException,
			CDMIOperationException {
		Object[] children;
		children = (Object[]) cops.getChildren(baseContainer);

		for (int i = 0; i < children.length; i++) {

			HttpResponse response = cops.delete((String) children[i]);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != REQUEST_DELETED)
				fail("Container " + containerName + " could not be cleanup");
		}

		containerName = null;
		random = null;

	}

	/**
	 * Test method for deleting a container.
	 * {@link eu.venusc.cdmi.ContainerOperations#create(java.lang.String, java.util.Map)}.
	 * 
	 * @throws CDMIOperationException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */

	@Test
	public void testCreate() throws ClientProtocolException, IOException,
			CDMIOperationException {

		HttpResponse response = null;
		int responseCode = 0;
		response = cops.create(containerName + "/", parameters);
		responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_CREATED)
			fail("Could not create  the container: " + containerName + "/");

	}

	/**
	 * Test method for getting the children of a container.
	 * {@link eu.venusc.cdmi.ContainerOperations#getChildren(java.lang.String)}.
	 * 
	 * @throws CDMIOperationException
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws ParseException
	 * 
	 * @throws ParseException
	 */

	@Test
	public void testGetChildren() throws ClientProtocolException, IOException,
			CDMIOperationException, ParseException {

		HttpResponse response = null;
		int responseCode = 0;
		Object[] children = null;

		Set<String> set = new HashSet<String>();
		// create some containers
		response = cops.create(containerName + 0 + "/", parameters);
		responseCode = response.getStatusLine().getStatusCode();

		if (responseCode != REQUEST_CREATED)
			fail("Could not create  the container: " + containerName + "/");

		set.add(containerName + 0 + "/");

		response = cops.create(containerName + 1, parameters);
		responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_CREATED)
			fail("Could not create  the container: " + containerName + "/");

		set.add(containerName + 1 + "/");

		response = cops.create(containerName + 2, parameters);
		responseCode = response.getStatusLine().getStatusCode();

		if (responseCode != REQUEST_CREATED)
			fail("Could not create  the container: " + containerName + "/");

		set.add(containerName + 2 + "/");

		Set<String> expected = new TreeSet<String>(set);

		children = (Object[]) cops.getChildren(baseContainer);
		Set<Object> childSet = new HashSet<Object>();
		for (int i = 0; i < children.length; i++) {
			childSet.add(children[i]);
		}
		Set<Object> actual = new TreeSet<Object>(childSet);
		assertEquals("Getting the container children: ", expected, actual);
		expected.clear();
		actual.clear();

	}

	/**
	 * Test method deletes a container.
	 * {@link eu.venusc.cdmi.ContainerOperations#delete(java.lang.String)}.
	 * 
	 * @throws CDMIOperationException
	 * @throws IOException
	 * @throws ClientProtocolException
	 * 
	 * */

	@Test
	public void testDelete() throws ClientProtocolException, IOException,
			CDMIOperationException {

		HttpResponse response = null;
		int responseCode = 0;

		// Create a container
		response = cops.create(containerName + "/", parameters);
		responseCode = response.getStatusLine().getStatusCode();

		if (responseCode != REQUEST_CREATED)
			fail("Could not create the container: " + containerName + "/");
		// delete the container
		response = cops.delete(containerName + "/");
		responseCode = response.getStatusLine().getStatusCode();
		assertEquals("Container deleted: ", REQUEST_DELETED, responseCode);
	}
}
