package eu.venusc.cdmi;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ContainerOperationsTest extends CDMIConnectionTest {

	ContainerOperations cops = null;
	static String containerName = null;
	static String baseContainer = null;
	static Random random = null;

	public ContainerOperationsTest(String name) {
		super(name);
		cops = cdmiConnection.getContainerProxy();
	}

	@Before
	public void setUp() throws Exception {
		random = new Random();
		baseContainer = "/";
		containerName = "libcdmi-java" + random.nextInt();

	}

	@After
	public void tearDown() throws Exception {

		Object[] children = (Object[]) cops.getChildren(baseContainer);

		for (int i = 0; i < children.length; i++) {

			HttpResponse response = cops.delete((String) children[i]);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != 204) {
				fail("Container " + containerName + " could not be cleanup");
				throw new Exception("Container " + containerName
						+ " could not be cleanup");
			}

		}
		containerName = null;
		random = null;

	}

	/**
	 * Test method for deleting a container : response code 201 in case of
	 * success
	 * {@link eu.venusc.cdmi.ContainerOperations#create(java.lang.String, java.util.Map)}
	 * .
	 */

	@Test
	public void testCreate() {

		HttpResponse response = null;
		int responseCode = 0;
		try {
			response = cops.create(containerName +"/", parameters);
			responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != 201)
				fail("Could not create  the container: " + containerName + "/");

		} catch (CDMIOperationException e) {
			System.err.println(e.getMessage());
		} catch (ClientProtocolException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

	/**
	 * Test method for getting the children of a container
	 * {@link eu.venusc.cdmi.ContainerOperations#getChildren(java.lang.String)}.
	 * 
	 * @throws ParseException
	 */

	@Test
	public void testGetChildren() {

		HttpResponse response = null;
		int responseCode = 0;
		Object[] children = null;

		Set set = new HashSet();

		try {
			// create some containers
			response = cops.create(containerName + 0 +"/" , parameters);
			responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != 201)
				fail("Could not create  the container: " + containerName + "/");

			set.add(containerName + 0 + "/");

			response = cops.create(containerName + 1, parameters);
			responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != 201)
				fail("Could not create  the container: " + containerName + "/");

			set.add(containerName + 1 + "/");

			response = cops.create(containerName + 2, parameters);
			responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != 201)
				fail("Could not create  the container: " + containerName + "/");

			set.add(containerName + 2 + "/");

			Set expected = new TreeSet(set);

			children = (Object[]) cops.getChildren(baseContainer);
			Set childSet = new HashSet();
			for (int i = 0; i < children.length; i++) {
				childSet.add(children[i]);
			}
			Set actual = new TreeSet(childSet);
			Iterator i;
			i = expected.iterator();

			Iterator it;
			it = actual.iterator();

			assertEquals("Getting the container children: ", expected, actual);
			expected.clear();
			actual.clear();
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		} catch (CDMIOperationException e) {
			System.err.println(e.getMessage());
		} catch (ClientProtocolException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

	/**
	 * Test method for deleting a container : response code 204 in case of
	 * success
	 * {@link eu.venusc.cdmi.ContainerOperations#delete(java.lang.String)}.
	 * 
	 * */

	@Test
	public void testDelete() {

		HttpResponse response = null;
		int responseCode = 0;
		try {
			// Create a container
			response = cops.create(containerName + "/", parameters);
			responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != 201)
				fail("Could not create the container: " + containerName + "/");
			// delete the container
			response = cops.delete(containerName + "/");
			responseCode = response.getStatusLine().getStatusCode();
			assertEquals("Container deleted: ", 204, responseCode);

		} catch (CDMIOperationException e) {
			System.err.println(e.getMessage());
		} catch (ClientProtocolException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
