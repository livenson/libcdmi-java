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
import static eu.venusc.cdmi.CDMIContentType.*;

public class BlobOperations{

	private URL endpoint;
	private DefaultHttpClient httpclient;

	public BlobOperations(URL endpoint, DefaultHttpClient httpclient) {
		this.httpclient = httpclient;
		this.endpoint = endpoint;
	}
	
	public HttpResponse create(String remoteFNM, byte[] value, Map <String, Object> parameters)
			throws IOException {

		HttpPut httpput = new HttpPut(endpoint +  remoteFNM);
		httpput.setHeader("Content-Type", CDMI_DATA);
		httpput.setHeader("Accept", CDMI_DATA);
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
		System.out.println(endpoint +  remoteFNM);
		return httpclient.execute(httpput);
	}
	
	
	public HttpResponse delete(String remoteFNM) throws IOException {

		HttpDelete httpdelete = new HttpDelete(endpoint + remoteFNM);
		httpdelete.setHeader("Content-Type", CDMI_DATA);
		httpdelete.setHeader("Accept", CDMI_DATA);
		httpdelete.setHeader("X-CDMI-Specification-Version",
				CDMI_SPEC_VERSION);

		return httpclient.execute(httpdelete);
	}
	
	
	public HttpResponse read(String remoteFNM) throws IOException {

		HttpGet httpget = new HttpGet(endpoint + remoteFNM);
		httpget.setHeader("Accept", CDMI_DATA);
		httpget.setHeader("Content-Type", CDMI_OBJECT);
		httpget.setHeader("X-CDMI-Specification-Version",
				CDMI_SPEC_VERSION);
		System.out.println(endpoint +remoteFNM);
		return httpclient.execute(httpget);
	}

}
