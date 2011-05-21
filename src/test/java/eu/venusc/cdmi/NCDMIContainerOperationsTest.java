package eu.venusc.cdmi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NCDMIContainerOperationsTest extends CDMIConnectionWrapper implements
CDMIResponseStatus {

	
	NCDMIContainerOperations cops = null;
	static String containerName = null;
	static String baseContainer = null;
	static Random random = new Random();

	public NCDMIContainerOperationsTest(String name) throws MalformedURLException {
		super(name);
		cops = cdmiConnection.getNcdmiContainerProxy();
	}

	
	@Before
	public void setUp() throws Exception {
		baseContainer = "/";
		if (baseContainer.charAt(baseContainer.length()-1)!='/')
			baseContainer = baseContainer + "/";
		
		containerName = "noncdmi-container" + random.nextInt();
	}
	@After
	public void tearDown() throws IOException, ParseException,
			CDMIOperationException {

		String[] children = cops.getChildren(baseContainer);

		for (int i = 0; i < children.length; i++) {
			String url = baseContainer + children[i];
			HttpResponse response = cops.delete(url);
			int responseCode = response.getStatusLine().getStatusCode();

			if (responseCode == REQUEST_NOT_FOUND
					|| responseCode == REQUEST_DELETED)
				continue;
			else
				fail("Container " + url + " could not be cleaned up." + responseCode);

		}
	}

	@Test
	public void testCreate() throws ClientProtocolException, IOException,
			CDMIOperationException {
		HttpResponse response = cops.create(baseContainer + containerName,
				parameters);
		int responseCode = response.getStatusLine().getStatusCode();
		assertEquals("Creating container " + baseContainer + containerName
				+ "/", REQUEST_CREATED, responseCode);
	}

	@Test
	public void testGetChildren() throws ClientProtocolException, IOException,
			CDMIOperationException, ParseException {

		Set<String> newContainers = new HashSet<String>();

		// create containers
		for (int i = 0; i < 3; i++) {
			String container_name = containerName + "_" + i;
			HttpResponse response = cops.create(baseContainer + container_name, parameters);
			int responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != REQUEST_CREATED)
				fail("Could not create  the container: " + baseContainer + container_name);
			newContainers.add(container_name + "/");
		}

		String[] children = cops.getChildren(baseContainer);

		Set<String> childSet = new HashSet<String>();
		for (int i = 0; i < children.length; i++) {
			childSet.add(children[i]);
		}
		
		assertTrue("Checking created container children.", childSet.containsAll(newContainers));
		childSet.clear();

	}

	@Test
	public void testDelete() throws ClientProtocolException, IOException,
			CDMIOperationException {

		HttpResponse response = null;
		int responseCode = 0;

		// Create a container
		response = cops.create(baseContainer + containerName, parameters);
		responseCode = response.getStatusLine().getStatusCode();

		if (responseCode != REQUEST_CREATED)
			fail("Could not create the container: " + baseContainer
					+ containerName);
		// delete the container
		response = cops.delete(baseContainer + containerName);
		responseCode = response.getStatusLine().getStatusCode();
		assertEquals("Container deleted: " + baseContainer + containerName,
				REQUEST_DELETED, responseCode);
	}
}
