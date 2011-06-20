package eu.venusc.cdmi;

import static eu.venusc.cdmi.CDMIContentType.CDMI_OBJECT;
import static eu.venusc.cdmi.CDMIContentType.CDMI_SPEC_VERSION;
import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_READ;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.simple.parser.ParseException;

public class BlobOperations{

	private URL endpoint;
	private HttpClient httpclient;

	public BlobOperations(URL endpoint, HttpClient httpclient) {
		this.httpclient = httpclient;
		this.endpoint = endpoint;
	}
	
	public HttpResponse create(String remoteFNM, byte[] value, Map <String, Object> parameters)
			throws IOException, URISyntaxException {

		HttpPut httpput = new HttpPut(Utils.getURI(endpoint, remoteFNM));
		httpput.setHeader("Content-Type", CDMI_OBJECT);
		httpput.setHeader("Accept", CDMI_OBJECT);
		httpput.setHeader("X-CDMI-Specification-Version",
				CDMI_SPEC_VERSION);

		BlobCreateRequest createObj = new BlobCreateRequest();

		createObj.mimetype = parameters.get("mimetype") != null ? (String) parameters
				.get("mimetype")
				: "text/plain";

		if (!createObj.mimetype.equals("text/plain")) {
			Base64 encoder = new Base64();
			createObj.value = encoder.encodeToString(value);
		} else {
			createObj.value = new String(value);
		}

		createObj.metadata = (MetadataField) parameters.get("metadata");

		StringWriter out = new StringWriter();
		createObj.writeJSONString(out);
		StringEntity entity = new StringEntity(out.toString());
		httpput.setEntity(entity);

		return httpclient.execute(httpput);
	}
	
	
	public HttpResponse delete(String remoteFNM) throws IOException, URISyntaxException {

		HttpDelete httpdelete = new HttpDelete(Utils.getURI(endpoint, remoteFNM));
		httpdelete.setHeader("Content-Type", CDMI_OBJECT);
		httpdelete.setHeader("Accept", CDMI_OBJECT);
		httpdelete.setHeader("X-CDMI-Specification-Version",
				CDMI_SPEC_VERSION);
		return httpclient.execute(httpdelete);
	}
	
	
	public HttpResponse read(String remoteFNM) throws IOException, URISyntaxException {

		HttpGet httpget = new HttpGet(Utils.getURI(endpoint, remoteFNM));
		httpget.setHeader("Accept", CDMI_OBJECT);
		httpget.setHeader("Content-Type", CDMI_OBJECT);
		httpget.setHeader("X-CDMI-Specification-Version",
				CDMI_SPEC_VERSION);

		return httpclient.execute(httpget);
	}
	
	/**
	 * Read CDMI blob and save the contents to a file.
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
		if (responseCode != REQUEST_READ)
			throw new CDMIOperationException("Download failed : "
					+ remoteFileName, responseCode);
		
		String mimeType = (String) Utils.getElement(response, "mimetype");	
		FileOutputStream outputFile = new FileOutputStream(localFileName);
		if (mimeType.equals("text/plain")) {			
			outputFile.write(Utils.getObjectContent(response).getBytes());			
		} else {
			outputFile.write(Utils.getTextContent(response).getBytes());			
		}
		outputFile.close();

		return responseCode;
	}

}
