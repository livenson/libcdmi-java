package eu.venusc.cdmi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
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

	public int create(String remoteContainer, Map parameters) throws ClientProtocolException,
			IOException, CDMIOperationException {

		HttpResponse response;
		HttpPut httpput = new HttpPut(endpoint + remoteContainer);

		httpput.setHeader("Content-Type", CDMIContentType.CDMI_CONTAINER);
		httpput.setHeader("Accept", CDMIContentType.CDMI_CONTAINER);
		httpput.setHeader("X-CDMI-Specification-Version",
				CDMIContentType.CDMI_SPEC_VERSION);
		
		ContainerCreateRequest create = new ContainerCreateRequest();
		
		// TODO: Metadata field should not be exposed to a client
		create.metadata = (MetadataField) parameters.get("metadata");		

		Gson gson = new Gson();
		StringEntity entity = new StringEntity(gson.toJson(create));
		httpput.setEntity(entity);
		response = httpclient.execute(httpput);

		int responseCode = response.getStatusLine().getStatusCode();
		CDMIErrorHandling.checkResponseCode("default", responseCode);
		
		HttpEntity ent = response.getEntity();
		EntityUtils.consume(ent);
		return responseCode;
	}
	
	public void delete(String remoteContainer) throws ClientProtocolException, IOException, CDMIOperationException{

		HttpDelete httpdelete = new HttpDelete(endpoint + remoteContainer);
		httpdelete.setHeader("Content-Type", CDMIContentType.CDMI_CONTAINER);
		httpdelete.setHeader("Accept", CDMIContentType.CDMI_CONTAINER);
		httpdelete.setHeader("X-CDMI-Specification-Version",
				CDMIContentType.CDMI_SPEC_VERSION);

		HttpResponse response = httpclient.execute(httpdelete);

		int responseCode = response.getStatusLine().getStatusCode();
		CDMIErrorHandling.checkResponseCode("default", responseCode);
		
		HttpEntity entity = response.getEntity();
		EntityUtils.consume(entity);
	}

	public String[] getChildren(String remoteContainer)
			throws ClientProtocolException, IOException, CDMIOperationException {

		HttpResponse response;
		HttpGet httpget = new HttpGet(endpoint + remoteContainer);
		httpget.setHeader("Content-Type", CDMIContentType.CDMI_CONTAINER);
		httpget.setHeader("Accept", CDMIContentType.CDMI_CONTAINER);
		httpget.setHeader("X-CDMI-Specification-Version",
				CDMIContentType.CDMI_SPEC_VERSION);
		response = httpclient.execute(httpget);

		int responseCode = response.getStatusLine().getStatusCode();

		CDMIErrorHandling.checkResponseCode("default", responseCode);

		InputStream respStream = response.getEntity().getContent();
		Gson gson = new Gson();
		ContainerReadRequest responseBody = gson.fromJson(Utils
				.convertStreamToString(respStream), ContainerReadRequest.class);

		HttpEntity ent = response.getEntity();
		EntityUtils.consume(ent);
		return responseBody.children;
	}

}
