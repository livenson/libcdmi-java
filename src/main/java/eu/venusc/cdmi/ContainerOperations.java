package eu.venusc.cdmi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class ContainerOperations {
	
	private URL endpoint;
	private DefaultHttpClient httpclient;

	public ContainerOperations(URL endpoint, DefaultHttpClient httpclient) {
		this.httpclient = httpclient;
		this.endpoint = endpoint;
	}
	
	public String[] getChildren(String remoteContainer)
			throws ClientProtocolException, IOException, CDMIOperationException{

		HttpResponse response;
		HttpGet httpget = new HttpGet(endpoint + remoteContainer);
		httpget.setHeader("Content-Type", CDMIContentType.CDMI_CONTAINER);
		httpget.setHeader("Accept", CDMIContentType.CDMI_OBJECT);
		httpget.setHeader("X-CDMI-Specification-Version",
				CDMIContentType.CDMI_SPEC_VERSION);
		response = httpclient.execute(httpget);

		int responseCode = response.getStatusLine().getStatusCode();
		
		CDMIErrorHandling.checkResponseCode("default", responseCode);
		
		InputStream respStream = response.getEntity().getContent();
		Gson gson = new Gson();

		ContainerReadRequest responseBody = gson.fromJson(
				Utils.convertStreamToString(respStream),
				ContainerReadRequest.class);
		
		HttpEntity ent = response.getEntity();
        EntityUtils.consume(ent);
		return responseBody.children;
	}

}
