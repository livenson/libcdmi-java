package eu.venusc.cdmi;

import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_OK;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.json.simple.parser.ParseException;

public class NonCDMIBlobOperations{

    private URL endpoint;
    private HttpClient httpclient;

    public NonCDMIBlobOperations(URL endpoint, HttpClient httpclient) {
        this.httpclient = httpclient;
        this.endpoint = endpoint;
    }

    public HttpResponse create(String remoteFNM, byte[] value,
            Map<String, Object> parameters) throws IOException, URISyntaxException {
        HttpPut httpput = new HttpPut(Utils.getURI(endpoint, remoteFNM));

        String contentType = parameters.get("mimetype") != null ? (String) parameters
                .get("mimetype") : "text/plain";

        ByteArrayEntity entity = new ByteArrayEntity(value);
        entity.setContentType(contentType);
        httpput.setEntity(entity);
        return httpclient.execute(httpput);
    }

    public HttpResponse create(String remoteFNM, File file,
            Map<String, Object> parameters) throws IOException, URISyntaxException {
        HttpPut httpput = new HttpPut(Utils.getURI(endpoint, remoteFNM));

        String contentType = parameters.get("mimetype") != null ? (String) parameters
                .get("mimetype") : "text/plain";
        FileEntity contents = new FileEntity(file, contentType);
        httpput.setEntity(contents);
        return httpclient.execute(httpput);
    }


    public HttpResponse delete(String remoteFNM) throws IOException, URISyntaxException {
        HttpDelete httpdelete = new HttpDelete(Utils.getURI(endpoint, remoteFNM));
        return httpclient.execute(httpdelete);
    }

    public HttpResponse read(String remoteFNM) throws IOException, URISyntaxException {
        HttpGet httpget = new HttpGet(Utils.getURI(endpoint, remoteFNM));
        return httpclient.execute(httpget);
    }

    /**
     * Read non-CDMI blob and save the contents to a file.
     *
     * @param remoteFileName
     *            The remote file path
     * @param localFileName
     *            Absolute path of the local file
     * @return Returns an integer containing the HTTP response code
     */
    public int readToFile (String remoteFileName, String localFileName)
            throws IOException, URISyntaxException, CDMIOperationException, ParseException {
        HttpResponse response = read(remoteFileName);

        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != REQUEST_OK)
            throw new CDMIOperationException("Download failed : "
                    + remoteFileName, responseCode);


        FileOutputStream outputFile = new FileOutputStream(localFileName);
        Utils.copyStreamData(response.getEntity().getContent(), outputFile);
        //outputFile.write(Utils.extractContents(response));
        outputFile.close();

        return responseCode;
    }
}
