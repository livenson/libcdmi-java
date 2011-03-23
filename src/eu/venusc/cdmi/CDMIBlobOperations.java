package eu.venusc.cdmi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.sun.swing.internal.plaf.metal.resources.metal;

public class CDMIBlobOperations {
	
	Credentials creds = null; 
	URL endpoint = null;
	
	public CDMIBlobOperations(Credentials creds, URL endpoint) {
		this.creds = creds;
		this.endpoint = endpoint;
	}

	public int create(URI local, String remote, Map parameters) throws Exception {

		DefaultHttpClient httpclient = new DefaultHttpClient();

		httpclient.getCredentialsProvider().setCredentials(new AuthScope(endpoint.getHost(),endpoint.getPort()), creds);
		
		HttpResponse response = null;
		HttpPut httpput = new HttpPut(endpoint+remote);

		httpput.setHeader("Content-Type", CDMIContentType.CDMI_DATA);
		httpput.setHeader("Accept", CDMIContentType.CDMI_DATA);
		httpput.setHeader("X-CDMI-Specification-Version", CDMIContentType.CDMI_SPEC_VERSION);
		
		BlobCreateRequest create = new BlobCreateRequest();
		create.mimetype = parameters.get("mimetype") != null ? (String) parameters.get("mimetype") : "text/plain";
		create.metadata= parameters.get("metadata") != null ? (MetadataField) parameters.get("metadata"): null;
		create.domainURI= parameters.get("domainURI") != null ? (String) parameters.get("domainURI"): null;
		create.value = parameters.get("value") != null ? (String)parameters.get("value"): Utils.getContents(new File(local.getPath()));
		create.copy= parameters.get("copy") != null ? (String) parameters.get("copy"): null;
		create.deserialize= parameters.get("deserialize") != null ? (String) parameters.get("deserialize"): null;
		create.serialize= parameters.get("serialize") != null ? (String) parameters.get("serialize"): null;
		create.move = parameters.get("move") != null ? (String) parameters.get("move"): null;
		create.objectID =parameters.get("objectID") != null ? (String) parameters.get("objectID"): null;
		create.objectURI = parameters.get("objectURI") != null ? (String) parameters.get("objectURI"): null;
		create.reference = parameters.get("reference") != null ? (String) parameters.get("reference"): null;

		Gson gson = new Gson(); 

		StringEntity entity = new StringEntity(gson.toJson(create));
		httpput.setEntity(entity);
		response = httpclient.execute(httpput);

		int responseCode = response.getStatusLine().getStatusCode();

		switch (responseCode) {

		case 201:
			/* New data object was created */
			break;

		case 400:
			throw new CDMIOperationException(
					"Invalid parameter of field names in the request: " +endpoint+remote,
					responseCode);
		case 401:
			throw new CDMIOperationException(
					"Incorrect or missing authentication credentials: " +endpoint+ remote,
					responseCode);
		case 403:
			throw new CDMIOperationException(
					"Client lacks the proper authorization to perform this request: "+endpoint+remote,
					responseCode);

		case 409:
			throw new CDMIOperationException(
					"The operation conflicts with a non-CDMI access protocol lock, or could cause a state transition error on the server or he data object cannot be deleted."
					+endpoint+remote,responseCode);
		}
		return responseCode;

	}

	public int create(String localFNM, String remoteFNM, Map parameters)
			throws Exception {
		return create(new URI(localFNM), remoteFNM, parameters);
	}

	public int update(String localFNM, String remoteFNM, Map parameters)
			throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(endpoint.getHost(),endpoint.getPort()), creds);

		HttpResponse response = null;
		HttpPut httpput = new HttpPut(endpoint+remoteFNM);

		httpput.setHeader("Content-Type", CDMIContentType.CDMI_DATA);
		httpput.setHeader("Accept", CDMIContentType.CDMI_DATA);
		httpput.setHeader("X-CDMI-Specification-Version", CDMIContentType.CDMI_SPEC_VERSION);
		
		BlobUpdateRequest update = new BlobUpdateRequest();
		update.mimetype = parameters.get("mimetype") != null ? (String) parameters.get("mimetype") : "text/plain";
		update.metadata= parameters.get("metadata") != null ? (MetadataField) parameters.get("metadata"): null;
		update.domainURI= parameters.get("domainURI") != null ? (String) parameters.get("domainURI"): null;
		update.value = Utils.getContents(new File(localFNM));
		Gson gson = new Gson(); 
		
		StringEntity entity = new StringEntity(gson.toJson(update));
		httpput.setEntity(entity);

		response = httpclient.execute(httpput);

		int responseCode = response.getStatusLine().getStatusCode();

		switch (responseCode) {

		case 200:
			/* New metadata and/or content accepted */
			break;
		case 302:
			throw new CDMIOperationException(
					"The URI is a reference to another URI: " +endpoint+remoteFNM,
					responseCode);
		case 400:
			throw new CDMIOperationException(
					"Invalid parameter of field names in the request: "
					+endpoint+remoteFNM, responseCode);
		case 401:
			throw new CDMIOperationException(
					"Incorrect or missing authentication credentials: "
					+endpoint+ remoteFNM, responseCode);
		case 403:
			throw new CDMIOperationException(
					"Client lacks the proper authorization to perform this request: "
					+endpoint+ remoteFNM, responseCode);
		case 404:
			throw new CDMIOperationException(
					"An update was attempted on an object that does not exist: "
					+endpoint+ remoteFNM, responseCode);
		case 409:
			throw new CDMIOperationException(
					"The operation conflicts with a non-CDMI access protocol lock, or could cause a state transition error on the server or he data object cannot be deleted."
					+endpoint+ remoteFNM, responseCode);
		}

		return responseCode;
	}

	public void delete(String remoteFNM) throws Exception {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(endpoint.getHost(),endpoint.getPort()), creds);

		HttpDelete httpdelete = new HttpDelete(endpoint+remoteFNM);
		httpdelete.setHeader("Content-Type", CDMIContentType.CDMI_DATA);
		httpdelete.setHeader("Accept", CDMIContentType.CDMI_DATA);
		httpdelete.setHeader("X-CDMI-Specification-Version", CDMIContentType.CDMI_SPEC_VERSION);

		HttpResponse response = httpclient.execute(httpdelete);

		int responseCode = response.getStatusLine().getStatusCode();

		switch (responseCode) {

		case 200:
			break;
		case 400:
			throw new CDMIOperationException(
					"Invalid parameter of field names in the request: "
							+ remoteFNM, responseCode);
		case 401:
			throw new CDMIOperationException(
					"Incorrect or missing authentication credentials: "
							+ remoteFNM, responseCode);
		case 403:
			throw new CDMIOperationException(
					"Client lacks the proper authorization to perform this request: "
					+endpoint+ remoteFNM, responseCode);
		case 404:
			throw new CDMIOperationException(
					"The resource specified was not found: "+ endpoint +remoteFNM,
					responseCode);
		case 409:
			throw new CDMIOperationException(
					"The operation conflicts with a non-CDMI access protocol lock, or could cause a state transition error on the server or he data object cannot be deleted: "
					+endpoint+ remoteFNM, responseCode);
		}
	}

	public File readFile(String remoteFNM) throws Exception {

		File file = null;
		BufferedWriter bw = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(endpoint.getHost(),endpoint.getPort()), creds);

		HttpGet httpget = new HttpGet(endpoint+remoteFNM);

		httpget.setHeader("Accept", CDMIContentType.CDMI_DATA);
		httpget.setHeader("Content-Type", CDMIContentType.CDMI_OBJECT);
		httpget.setHeader("X-CDMI-Specification-Version", CDMIContentType.CDMI_SPEC_VERSION);

		HttpResponse response = httpclient.execute(httpget);

		int responseCode = response.getStatusLine().getStatusCode();

		switch (responseCode) {

		case 200:
			/* Valid response is enclosed */
			break;
		case 302:
			throw new CDMIOperationException(
					"The URI is a reference to another URI: " + endpoint + remoteFNM,
					responseCode);
		case 400:
			throw new CDMIOperationException(
					"Invalid parameter of field names in the request: "
							+ endpoint + remoteFNM, responseCode);
		case 401:
			throw new CDMIOperationException(
					"Incorrect or missing authentication credentials: "
					+ endpoint + remoteFNM, responseCode);
		case 403:
			throw new CDMIOperationException(
					"Client lacks the proper authorization to perform this request: "
					+ endpoint + remoteFNM, responseCode);
		case 404:
			throw new CDMIOperationException(
					"An update was attempted on an object that does not exist: "
					+ endpoint + remoteFNM, responseCode);
		case 406:
			throw new CDMIOperationException(
					"The server is unable to provide the object in the content-type specified in the Accept header: "
					+ endpoint + remoteFNM, responseCode);
		}

		InputStream respStream = response.getEntity().getContent();
		Gson gson = new Gson();
		BlobReadResponse responseBody = gson
				.fromJson(Utils.convertStreamToString(respStream),
						BlobReadResponse.class);


		if (responseBody.mimetype.equals("text/plain")) {
			URL url = new URL(endpoint+remoteFNM);

			file = new File(System.getProperty("user.home") + url.getFile());
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(responseBody.value);
			bw.close();
		}
		return file;
	}

	public String[] getChildren(String remoteContainer) throws Exception {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(endpoint.getHost(),endpoint.getPort()), creds);

		HttpResponse response = null;
		HttpGet httpget = new HttpGet(endpoint+remoteContainer);
		httpget.setHeader("Content-Type", CDMIContentType.CDMI_CONTAINER);
		httpget.setHeader("Accept", CDMIContentType.CDMI_OBJECT);
		httpget.setHeader("X-CDMI-Specification-Version", CDMIContentType.CDMI_SPEC_VERSION);
		response = httpclient.execute(httpget);

		int responseCode = response.getStatusLine().getStatusCode();

		switch (responseCode) {

		case 200:
			/* Metadata for the container Object provided in the Message Body */
			break;
		case 302:
			throw new CDMIOperationException(
					"The URI is a reference to another URI: " + endpoint+remoteContainer,
					responseCode);
		case 400:
			throw new CDMIOperationException(
					"Invalid parameter of field names in the request	: "
					+endpoint+ remoteContainer, responseCode);
		case 401:
			throw new CDMIOperationException(
					"Incorrect or missing authentication credentials: "
					+endpoint+  remoteContainer, responseCode);
		case 403:
			throw new CDMIOperationException(
					"Client lacks the proper authorization to perform this request: "
					+endpoint+  remoteContainer, responseCode);
		case 404:
			throw new CDMIOperationException(
					"A container was not found at the specified URI: "
					+endpoint+ remoteContainer, responseCode);
		case 406:
			throw new CDMIOperationException(
					"The server is unable to provide the object in the content-type specified in the Accept header: "
					+endpoint+ remoteContainer, responseCode);
		}

		InputStream respStream = response.getEntity().getContent();
		Gson gson = new Gson();

		ContainerReadRequest responseBody = gson.fromJson(
				Utils.convertStreamToString(respStream),
				ContainerReadRequest.class);

		return responseBody.children;
	}

}


