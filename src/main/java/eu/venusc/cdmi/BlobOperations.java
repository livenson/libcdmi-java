package eu.venusc.cdmi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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

public class BlobOperations {

	private URL endpoint;
	private DefaultHttpClient httpclient;

	public BlobOperations(URL endpoint, DefaultHttpClient httpclient) {
		this.httpclient = httpclient;
		this.endpoint = endpoint;
	}

	public int create(URI local, String remote, Map parameters)
			throws IOException, CDMIOperationException {
		HttpResponse response;
		HttpPut httpput = new HttpPut(endpoint + remote);

		httpput.setHeader("Content-Type", CDMIContentType.CDMI_DATA);
		httpput.setHeader("Accept", CDMIContentType.CDMI_DATA);
		httpput.setHeader("X-CDMI-Specification-Version",
				CDMIContentType.CDMI_SPEC_VERSION);
		
		BlobCreateRequest create = new BlobCreateRequest();
		create.value = Utils.getContents(new File(local.getPath()));
		create.mimetype = parameters.get("mimetype") != null ? (String) parameters
				.get("mimetype")
				: "text/plain";
		// TODO: remove MetadataField from request DTOs. Client should not know about these objects!
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

	public void delete(String remoteFNM) throws ClientProtocolException, IOException, CDMIOperationException{

		HttpDelete httpdelete = new HttpDelete(endpoint + remoteFNM);
		httpdelete.setHeader("Content-Type", CDMIContentType.CDMI_DATA);
		httpdelete.setHeader("Accept", CDMIContentType.CDMI_DATA);
		httpdelete.setHeader("X-CDMI-Specification-Version",
				CDMIContentType.CDMI_SPEC_VERSION);

		HttpResponse response = httpclient.execute(httpdelete);

		int responseCode = response.getStatusLine().getStatusCode();
		CDMIErrorHandling.checkResponseCode("default", responseCode);
		
		HttpEntity entity = response.getEntity();
		EntityUtils.consume(entity);
	}

	public File read(String remoteFNM, String localFNM) throws ClientProtocolException, IOException, CDMIOperationException {

		File file = null;
		BufferedWriter bw = null;

		HttpGet httpget = new HttpGet(endpoint + remoteFNM);

		httpget.setHeader("Accept", CDMIContentType.CDMI_DATA);
		httpget.setHeader("Content-Type", CDMIContentType.CDMI_OBJECT);
		httpget.setHeader("X-CDMI-Specification-Version",
				CDMIContentType.CDMI_SPEC_VERSION);

		HttpResponse response = httpclient.execute(httpget);

		int responseCode = response.getStatusLine().getStatusCode();
		CDMIErrorHandling.checkResponseCode("default", responseCode);

		InputStream respStream = response.getEntity().getContent();
		Gson gson = new Gson();
		BlobReadResponse responseBody = gson.fromJson(Utils
				.convertStreamToString(respStream), BlobReadResponse.class);

		HttpEntity ent = response.getEntity();
		EntityUtils.consume(ent);

		if (responseBody.mimetype.equals("text/plain")) {
			file = new File(localFNM);
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(responseBody.value);
			bw.close();
		} else
			throw new IOException(responseBody.mimetype + " is not handled");
		return file;
	}

}
