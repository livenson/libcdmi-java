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

public class ContainerOperationsTest extends CDMIConnectionWrapper implements
		CDMIResponseStatus {

	ContainerOperations cops = null;
	static String containerName = null;
	static String baseContainer = null;
	static Random random = new Random();

	public ContainerOperationsTest(String name) throws MalformedURLException {
		super(name);				
		cops = cdmiConnection.getContainerProxy();
	}

	@Before
	public void setUp() throws IOException {		
		baseContainer = "/";
		containerName = "libcdmi-java" + random.nextInt();
	}

	@After
	public void tearDown() throws IOException, ParseException,
			CDMIOperationException {

		String[] children = cops.getChildren(baseContainer);

		for (int i = 0; i < children.length; i++) {
			HttpResponse response = cops.delete(baseContainer + "/"
					+ children[i]);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != REQUEST_DELETED)
				fail("Container " + baseContainer + " could not be cleaned up.");
		}

	}


	@Test
	public void testCreate() throws ClientProtocolException, IOException,
			CDMIOperationException {

		HttpResponse response = cops.create(containerName +"/", parameters);
		int responseCode = response.getStatusLine().getStatusCode();
		assertEquals("Creating container " + containerName+"/", responseCode,
				REQUEST_CREATED);

	}

	@Test
	public void testGetChildren() throws ClientProtocolException, IOException,
			CDMIOperationException, ParseException {

		Set<String> set = new HashSet<String>();

		// create containers
		for (int i = 0; i < 3; i++) {

			HttpResponse response = cops.create(containerName + i + "/",
					parameters);
			int responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != REQUEST_CREATED)
				fail("Could not create  the container: " + containerName + "/");
			set.add(containerName + i + "/");
		}

		String[] children = cops.getChildren(baseContainer);

		Set<String> childSet = new HashSet<String>();
		for (int i = 0; i < children.length; i++) {
			childSet.add(children[i]);
		}

		assertEquals("Getting the container children: ", set, childSet);
		childSet.clear();

	}


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
