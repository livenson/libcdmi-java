package eu.venusc.cdmi;

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

	public HttpResponse delete(String remoteFNM) throws IOException, URISyntaxException {		
		HttpDelete httpdelete = new HttpDelete(Utils.getURI(endpoint, remoteFNM));
		return httpclient.execute(httpdelete);
	}

	public HttpResponse read(String remoteFNM) throws IOException, URISyntaxException {		
		HttpGet httpget = new HttpGet(Utils.getURI(endpoint, remoteFNM));		
		return httpclient.execute(httpget);		
	}
}
