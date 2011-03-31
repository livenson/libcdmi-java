package eu.venusc.cdmi;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;

public class CDMIConnection {

	private DefaultHttpClient httpclient;

	BlobOperations blobProxy;
	ContainerOperations containerProxy;

	public CDMIConnection(Credentials creds, URL endpoint) {
		httpclient = new DefaultHttpClient();
		httpclient.getCredentialsProvider().setCredentials(
				new AuthScope(endpoint.getHost(), endpoint.getPort()), creds);
		
		this.blobProxy = new BlobOperations(endpoint, httpclient);
		this.containerProxy = new ContainerOperations(endpoint, httpclient);
	}

	public int createBlob(URI localFNM, String remoteFNM, Map parameters)
			throws IOException, CDMIOperationException {
		return blobProxy.create(localFNM, remoteFNM, parameters);
	}

	public int createBlob(String localFNM, String remoteFNM, Map parameters)
			throws IOException, CDMIOperationException, URISyntaxException {
		return blobProxy.create(new URI(localFNM), remoteFNM, parameters);
	}

	public int updateBlob(String localFNM, String remoteFNM, Map parameters)
			throws IOException, CDMIOperationException, URISyntaxException {
		// TODO: treat update and create differently
		return blobProxy.create(new URI(localFNM), remoteFNM, parameters);
	}

	public void delete(String remoteFNM) throws ClientProtocolException,
			IOException, CDMIOperationException {
		blobProxy.delete(remoteFNM);
	}

	public File readBlob(String remoteFNM, String localFNM)
			throws ClientProtocolException, IOException, CDMIOperationException {
		return blobProxy.read(remoteFNM, localFNM);
	}

	public String[] getChildren(String remoteContainer)
			throws ClientProtocolException, IOException, CDMIOperationException {
		return containerProxy.getChildren(remoteContainer);
	}

	public int createContainer(String remoteContainer, Map parameters)
			throws ClientProtocolException, IOException, CDMIOperationException {
		return containerProxy.create(remoteContainer, parameters);
	}

	public void deleteContainer(String remoteContainer)
			throws ClientProtocolException, IOException, CDMIOperationException {
		containerProxy.delete(remoteContainer);
	}
}
