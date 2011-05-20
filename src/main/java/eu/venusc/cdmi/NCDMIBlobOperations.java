package eu.venusc.cdmi;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class NCDMIBlobOperations implements CDMIContentType {

	private URL endpoint;
	private DefaultHttpClient httpclient;

	public NCDMIBlobOperations(URL endpoint, DefaultHttpClient httpclient) {
		this.httpclient = httpclient;
		this.endpoint = endpoint;
	}

	public HttpResponse create(String remoteFNM, byte[] value,
			Map<String, Object> parameters) throws IOException {

		HttpPut httpput = new HttpPut(endpoint + remoteFNM);

		String contentType = parameters.get("mimetype") != null ? (String) parameters
				.get("mimetype") : "text/plain";

		ByteArrayEntity entity = new ByteArrayEntity(value);
		entity.setContentType(contentType);
		httpput.setEntity(entity);
		return httpclient.execute(httpput);
	}

	public HttpResponse delete(String remoteFNM) throws IOException {

		HttpDelete httpdelete = new HttpDelete(endpoint + remoteFNM);
		// httpdelete.setHeader("X-CDMI-Specification-Version",
		// CDMI_SPEC_VERSION);

		return httpclient.execute(httpdelete);
	}

	public HttpResponse read(String remoteFNM) throws IOException {

		HttpGet httpget = new HttpGet(endpoint + remoteFNM);
		
		return httpclient.execute(httpget);
	}

}
