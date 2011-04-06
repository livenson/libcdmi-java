package eu.venusc.cdmi;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class BlobOperations {

	private URL endpoint;
	private DefaultHttpClient httpclient;
	private Map parameters;
	
	/**
	 * 
	 * @param endpoint
	 * @param httpclient
	 */
	public BlobOperations(URL endpoint, DefaultHttpClient httpclient) {
		this.httpclient = httpclient;
		this.endpoint = endpoint;
		this.parameters = parameters;
	}
	
	/**
	 * 
	 * @param remoteContainer
	 * @param remoteFNM
	 * @param value
	 * @param parameters
	 * @return
	 * @throws IOException
	 */
	public HttpResponse create(String remoteContainer, String remoteFNM,
			byte[] value, Map parameters) throws IOException {

		HttpPut httpput = new HttpPut(endpoint + "/" + remoteContainer + "/"
				+ remoteFNM);
		httpput.setHeader("Content-Type", CDMIContentType.CDMI_DATA);
		httpput.setHeader("Accept", CDMIContentType.CDMI_DATA);
		httpput.setHeader("X-CDMI-Specification-Version",
				CDMIContentType.CDMI_SPEC_VERSION);

		BlobCreateRequest createObj = new BlobCreateRequest();

		createObj.mimetype = parameters.get("mimetype") != null ? (String) parameters
				.get("mimetype") : "text/plain";

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
	
	/**
	 * 
	 * @param remoteContainer
	 * @param remoteFNM
	 * @return
	 * @throws IOException
	 */
	public HttpResponse delete(String remoteContainer, String remoteFNM)
			throws IOException {

		HttpDelete httpdelete = new HttpDelete(endpoint + "/" + remoteContainer
				+ "/" + remoteFNM);
		httpdelete.setHeader("Content-Type", CDMIContentType.CDMI_DATA);
		httpdelete.setHeader("Accept", CDMIContentType.CDMI_DATA);
		httpdelete.setHeader("X-CDMI-Specification-Version",
				CDMIContentType.CDMI_SPEC_VERSION);

		return httpclient.execute(httpdelete);

	}
	
	/**
	 * 
	 * @param remoteContainer
	 * @param remoteFNM
	 * @return
	 * @throws IOException
	 */
	public HttpResponse read(String remoteContainer, String remoteFNM)
			throws IOException {

		HttpGet httpget = new HttpGet(endpoint + "/" + remoteContainer + "/"
				+ remoteFNM);

		httpget.setHeader("Accept", CDMIContentType.CDMI_DATA);
		httpget.setHeader("Content-Type", CDMIContentType.CDMI_OBJECT);
		httpget.setHeader("X-CDMI-Specification-Version",
				CDMIContentType.CDMI_SPEC_VERSION);
		return httpclient.execute(httpget);

	}

}
