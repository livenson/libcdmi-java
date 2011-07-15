package eu.venusc.cdmi;

import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_CREATED;
import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_DELETED;
import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_NOT_FOUND;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BlobOperationsTest extends CDMIConnectionWrapper {

    ContainerOperations cops;
    BlobOperations bops;
    static String containerName;
    static String baseContainer;
    static String textObjectName;
    static String binaryObjectName;
    static Random random = new Random();
    static File tmpTextFile;
    static File tmpBinaryFile;

    public BlobOperationsTest(String name) throws KeyManagementException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
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

        tmpTextFile = Utils.createTemporaryFile("Place for advertisment.",
                "libcdmi-java", ".txt");
        tmpBinaryFile = Utils.createZip("libcdmi-java");

        textObjectName = tmpTextFile.getName() + "  aa";
        binaryObjectName = tmpBinaryFile.getName();
    }

    @After
    public void tearDown() throws IOException, CDMIOperationException, URISyntaxException {
        for (String objectName : new String[] { textObjectName,
                binaryObjectName }) {
            HttpResponse response = bops.delete(baseContainer + containerName
                    + "/" + objectName);
            int responseCode = response.getStatusLine().getStatusCode();
            // we only accept failures for already deleted objects
            if (responseCode != REQUEST_DELETED
                    && responseCode != REQUEST_NOT_FOUND) {
                fail("Could not clean the object: " + baseContainer
                        + containerName + "/" + objectName + " " + responseCode);
            }
        }

        // delete the container
        HttpResponse response = cops.delete(baseContainer + containerName);
        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != REQUEST_DELETED)
            fail("Could not clean up the container: " + baseContainer
                    + containerName + "/" + " " + responseCode);
    }

    @Test
    public void testCreateText() throws ClientProtocolException, IOException,
            CDMIOperationException, URISyntaxException {
        this.parameters.put("mimetype", "text/plain");
        HttpResponse response = cops.create(baseContainer + containerName,
                parameters);
        int responseCode = response.getStatusLine().getStatusCode();

        if (responseCode != REQUEST_CREATED)
            fail("Could not create container: " + baseContainer + containerName
                    + "/" + " " + responseCode);

        response = bops.create(baseContainer + containerName + "/"
                + textObjectName, Utils.getBytesFromFile(tmpTextFile),
                parameters);
        responseCode = response.getStatusLine().getStatusCode();
        assertEquals("Object could not be created: " + baseContainer
                + containerName + "/" + textObjectName, REQUEST_CREATED,
                responseCode);
    }

    @Test
    public void testCreateBinary() throws ClientProtocolException, IOException,
            CDMIOperationException, URISyntaxException {
        this.parameters.put("mimetype", "application/x-zip-compressed");
        HttpResponse response = cops.create(baseContainer + containerName,
                parameters);
        int responseCode = response.getStatusLine().getStatusCode();

        if (responseCode != REQUEST_CREATED)
            fail("Could not create container: " + baseContainer + containerName
                    + "/" + " " + responseCode);

        response = bops.create(baseContainer + containerName + "/"
                + binaryObjectName, Utils.getBytesFromFile(tmpBinaryFile),
                parameters);
        responseCode = response.getStatusLine().getStatusCode();
        assertEquals("Object could not be created: " + baseContainer
                + containerName + "/" + binaryObjectName, REQUEST_CREATED,
                responseCode);
    }

    @Test
    public void testReadText() throws IOException, CDMIOperationException,
            ParseException, URISyntaxException {

        HttpResponse response = cops.create(baseContainer + containerName,
                parameters);

        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != REQUEST_CREATED)
            fail("Could not create container: " + baseContainer + containerName
                    + "/" + " " + responseCode);

        response = bops.create(baseContainer + containerName + "/"
                + textObjectName, Utils.getBytesFromFile(tmpTextFile),
                parameters);

        responseCode = response.getStatusLine().getStatusCode();

        if (responseCode != REQUEST_CREATED)
            fail("Could not create blob object " + baseContainer
                    + containerName + "/" + textObjectName + " " + responseCode);

        response = bops.read(baseContainer + containerName + "/"
                + textObjectName);
        responseCode = response.getStatusLine().getStatusCode();
        String mimeType = (String) Utils.getElement(response, "mimetype");

        response = bops.read(baseContainer + containerName + "/"
                + textObjectName);
        responseCode = response.getStatusLine().getStatusCode();

        if (!mimeType.equals("text/plain")) {
            assertEquals("Local and remote blob objects are not equal: ",
                    new String(Utils.getBytesFromFile(tmpTextFile)),
                    Utils.getObjectContent(response));

        } else {
            assertEquals("Local and remote blob objects are not equal: ",
                    new String(Utils.getBytesFromFile(tmpTextFile)),
                    Utils.getTextContent(response));
        }
    }

    @Test
    public void testReadBinary() throws IOException, CDMIOperationException,
            ParseException, URISyntaxException {

        this.parameters.put("mimetype", "application/x-zip-compressed");
        HttpResponse response = cops.create(baseContainer + containerName,
                parameters);
        int responseCode = response.getStatusLine().getStatusCode();

        if (responseCode != REQUEST_CREATED)
            fail("Could not create container: " + baseContainer + containerName
                    + "/" + " " + responseCode);

        response = bops.create(baseContainer + containerName + "/"
                + binaryObjectName, Utils.getBytesFromFile(tmpBinaryFile),
                parameters);

        responseCode = response.getStatusLine().getStatusCode();

        if (responseCode != REQUEST_CREATED)
            fail("Could not create blob object " + baseContainer
                    + containerName + "/" + binaryObjectName + " "
                    + responseCode);

        response = bops.read(baseContainer + containerName + "/"
                + binaryObjectName);
        responseCode = response.getStatusLine().getStatusCode();
        String mimeType = (String) Utils.getElement(response, "mimetype");

        response = bops.read(baseContainer + containerName + "/"
                + binaryObjectName);
        responseCode = response.getStatusLine().getStatusCode();

        if (!mimeType.equals("text/plain")) {
            assertEquals("Local and remote blob objects are not equal: ",
                    new String(Utils.getBytesFromFile(tmpBinaryFile)),
                    Utils.getObjectContent(response));

        } else {
            assertEquals("Local and remote blob objects are not equal: ",
                    new String(Utils.getBytesFromFile(tmpBinaryFile)),
                    Utils.getTextContent(response));
        }
    }

    @Test
    public void testDelete() throws IOException, CDMIOperationException, URISyntaxException {

        HttpResponse response = cops.create(baseContainer + containerName,
                parameters);
        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != REQUEST_CREATED)
            fail("Could not create container: " + baseContainer + containerName
                    + "/" + " " + responseCode);

        response = bops.create(baseContainer + containerName + "/"
                + textObjectName, Utils.getBytesFromFile(tmpTextFile),
                parameters);

        responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != REQUEST_CREATED)
            fail("Could not create blob object: " + baseContainer
                    + containerName + "/" + textObjectName + " " + responseCode);

        response = bops.delete(baseContainer + containerName + "/"
                + textObjectName);
        responseCode = response.getStatusLine().getStatusCode();
        assertEquals("Object could not be deleted: ", REQUEST_DELETED,
                responseCode);
    }

}
