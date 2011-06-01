package eu.venusc.cdmi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static eu.venusc.cdmi.CDMIResponseStatus.*;

public class ContainerOperationsTest extends CDMIConnectionWrapper {

	ContainerOperations cops;
	static String containerName;
	static String baseContainer;
	static Random random = new Random();

	public ContainerOperationsTest(String name) throws KeyManagementException,
			UnrecoverableKeyException, CertificateException,
			NoSuchAlgorithmException, KeyStoreException, IOException {
		super(name);
		cops = cdmiConnection.getContainerProxy();
	}

	@Before
	public void setUp() throws IOException {

		baseContainer = "/";
		if (baseContainer.charAt(baseContainer.length() - 1) != '/')
			baseContainer = baseContainer + "/";

		containerName = "libcdmi-java" + random.nextInt();
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
				fail("Container " + url + " could not be cleaned up."
						+ responseCode);

		}

	}

	@Test
	public void testCreate() throws ClientProtocolException, IOException,
			CDMIOperationException {

		HttpResponse response = cops.create(baseContainer + containerName,
				parameters);
		int responseCode = response.getStatusLine().getStatusCode();
		assertEquals("Creating container failed:" + baseContainer
				+ containerName + "/", REQUEST_CREATED, responseCode);

	}

	@Test
	public void testGetChildren() throws ClientProtocolException, IOException,
			CDMIOperationException, ParseException {

		Set<String> set = new HashSet<String>();

		// create containers
		for (int i = 0; i < 3; i++) {

			HttpResponse response = cops.create(baseContainer + containerName
					+ i, parameters);
			int responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != REQUEST_CREATED)
				fail("Could not create the container: " + baseContainer
						+ containerName + "/");
			set.add(containerName + i + "/");
		}

		String[] children = cops.getChildren(baseContainer);

		Set<String> childSet = new HashSet<String>();
		for (int i = 0; i < children.length; i++) {
			childSet.add(children[i]);
		}

		assertEquals("Getting the container children failed: ", set, childSet);
		childSet.clear();

	}

	@Test
	public void testDelete() throws ClientProtocolException, IOException,
			CDMIOperationException {
		// Create a container
		HttpResponse response = cops.create(baseContainer + containerName,
				parameters);
		int responseCode = response.getStatusLine().getStatusCode();

		if (responseCode != REQUEST_CREATED)
			fail("Could not create the container: " + baseContainer
					+ containerName);
		// delete the container
		response = cops.delete(baseContainer + containerName);
		responseCode = response.getStatusLine().getStatusCode();
		assertEquals("Container could not be deleted: " + baseContainer
				+ containerName, REQUEST_DELETED, responseCode);
	}
}
