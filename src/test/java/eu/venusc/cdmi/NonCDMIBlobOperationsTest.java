package eu.venusc.cdmi;

import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_CREATED;
import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_DELETED;
import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_NOT_FOUND;
import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_OK;

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

public class NonCDMIBlobOperationsTest extends CDMIConnectionWrapper {

    NonCDMIContainerOperations cops;
    NonCDMIBlobOperations bops;
    static String containerName;
    static String baseContainer;
    static String objectName;
    static String textObjectName;
    static String binaryObjectName;

    static Random random = new Random();
    static File tmpTextFile;
    static File tmpBinaryFile;

    public NonCDMIBlobOperationsTest(String name) throws Exception{
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

        tmpTextFile = Utils.createTemporaryFile("Place for advertisment.", "venus_C",
        ".txt");

        tmpBinaryFile = Utils.createZip("venus_c");
        textObjectName = tmpTextFile.getName();
        binaryObjectName = tmpBinaryFile.getName();

    }

    @After
    public void tearDown() throws IOException, CDMIOperationException, URISyntaxException {

        for (String objectName: new String[] {textObjectName, binaryObjectName}) {
        HttpResponse response = bops.delete(baseContainer + containerName + "/"
                + objectName);
        int responseCode = response.getStatusLine().getStatusCode();
        // we only accept failures for already deleted objects
        if (responseCode != REQUEST_DELETED
                && responseCode != REQUEST_NOT_FOUND) {
            fail("Could not clean the object: " + baseContainer + containerName
                    + "/" + objectName+" "+responseCode);
        }
        }
        HttpResponse response = cops.delete(baseContainer + containerName);
        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != REQUEST_DELETED)
            fail("Could not clean up the container: " + baseContainer
                    + containerName + "/"+" "+responseCode);
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
                    + "/" + responseCode);

        response = bops.create(
                baseContainer + containerName + "/" + textObjectName,
                Utils.getBytesFromFile(tmpTextFile), parameters);
        responseCode = response.getStatusLine().getStatusCode();
        assertEquals("Object could not be created: " + baseContainer
                + containerName + "/" + objectName, REQUEST_CREATED,
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
                    + "/" + responseCode);

        response = bops.create(
                baseContainer + containerName + "/" + binaryObjectName,
                Utils.getBytesFromFile(tmpBinaryFile), parameters);
        responseCode = response.getStatusLine().getStatusCode();
        assertEquals("Object could not be created: " + baseContainer
                + containerName + "/" + objectName, REQUEST_CREATED,
                responseCode);

        // trying streaming
        response = bops.create(
                baseContainer + containerName + "/" + binaryObjectName,
                tmpBinaryFile, parameters);
        responseCode = response.getStatusLine().getStatusCode();
        assertEquals("Object could not be created with a streaming create function: " + baseContainer
                + containerName + "/" + binaryObjectName, REQUEST_OK,
                responseCode);
    }

    @Test
    public void testReadText() throws IOException, CDMIOperationException,
            ParseException, URISyntaxException {
        this.parameters.put("mimetype", "text/plain");

        HttpResponse response = cops.create(baseContainer + containerName,
                parameters);

        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != REQUEST_CREATED)
            fail("Could not create container: " + baseContainer + containerName
                    + "/");
        response = bops.create(
                baseContainer + containerName + "/" + textObjectName,
                Utils.getBytesFromFile(tmpTextFile), parameters);

        responseCode = response.getStatusLine().getStatusCode();

        if (responseCode != REQUEST_CREATED)
            fail("Could not create blob object " + baseContainer
                    + containerName + "/" + textObjectName + " "+ responseCode);

        response = bops.read(baseContainer + containerName + "/" + textObjectName);
        responseCode = response.getStatusLine().getStatusCode();
        String mimeType = response.getFirstHeader("Content-Type").getValue();
        assertEquals("Wrong mimetype", "text/plain", mimeType);
        assertEquals("Local and remote blob objects are not equal: ",
                    new String(Utils.getBytesFromFile(tmpTextFile)), new String(
                            Utils.extractContents(response)));
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
                    + "/");
        response = bops.create(
                baseContainer + containerName + "/" + binaryObjectName,
                Utils.getBytesFromFile(tmpBinaryFile), parameters);

        responseCode = response.getStatusLine().getStatusCode();

        if (responseCode != REQUEST_CREATED)
            fail("Could not create blob object " + baseContainer
                    + containerName + "/" + binaryObjectName + " "+ responseCode);

        response = bops.read(baseContainer + containerName + "/" + binaryObjectName);
        responseCode = response.getStatusLine().getStatusCode();
        String mimeType = response.getFirstHeader("Content-Type").getValue();
        assertEquals("Wrong mimetype", "application/x-zip-compressed", mimeType);
        assertEquals("Local and remote blob objects are not equal: ",
                    new String(Utils.getBytesFromFile(tmpBinaryFile)), new String(
                            Utils.extractContents(response)));
    }

    @Test
    public void testReadFile() throws IOException, CDMIOperationException,
            ParseException, URISyntaxException {
        this.parameters.put("mimetype", "application/x-zip-compressed");

        HttpResponse response = cops.create(baseContainer + containerName,
                parameters);

        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != REQUEST_CREATED)
            fail("Could not create container: " + baseContainer + containerName
                    + "/");
        response = bops.create(baseContainer + containerName + "/" + binaryObjectName,
                tmpBinaryFile, parameters);

        responseCode = response.getStatusLine().getStatusCode();

        if (responseCode != REQUEST_CREATED)
            fail("Could not create blob object " + baseContainer
                    + containerName + "/" + binaryObjectName + " "+ responseCode);

        String fnm = "tmp_out_file";
        File f = new File(fnm);
        int res = bops.readToFile(baseContainer + containerName + "/" + binaryObjectName, fnm);
        f.delete();
        assertEquals("Failed operation of reading-in a file.", 200, res);

    }

    @Test
    public void testDelete() throws IOException, CDMIOperationException, URISyntaxException {
        HttpResponse response = cops.create(baseContainer + containerName,
                parameters);
        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != REQUEST_CREATED)
            fail("Could not create container: " + baseContainer + containerName
                    + "/");

        response = bops.create(
                baseContainer + containerName + "/" + textObjectName,
                Utils.getBytesFromFile(tmpTextFile), parameters);

        responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != REQUEST_CREATED)
            fail("Could not create blob object: " + baseContainer
                    + containerName + "/" + textObjectName+ " "+ responseCode);

        response = bops
                .delete(baseContainer + containerName + "/" + textObjectName);
        responseCode = response.getStatusLine().getStatusCode();
        assertEquals("Object could not be deleted: ", REQUEST_DELETED,
                responseCode);
    }
}
