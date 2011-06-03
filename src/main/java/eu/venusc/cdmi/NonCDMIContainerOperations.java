package eu.venusc.cdmi;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.json.simple.parser.ParseException;

public class NonCDMIContainerOperations {

	private URL endpoint;
	private HttpClient httpclient;

	public NonCDMIContainerOperations(URL endpoint, HttpClient httpclient) {
		this.httpclient = httpclient;
		this.endpoint = endpoint;
	}

	public HttpResponse create(String remoteContainer,
			Map<String, Object> parameters) throws ClientProtocolException,
			IOException, CDMIOperationException, URISyntaxException {
		HttpPut httpput = new HttpPut(Utils.getURI(endpoint, remoteContainer, true));
		return httpclient.execute(httpput);
	}

	public HttpResponse read(String remoteContainer, List<String> fields)
			throws ClientProtocolException, IOException, URISyntaxException {
		String path = remoteContainer + "/?";

		for (String f : fields) {
			path = path + f;
		}
		HttpGet httpget = new HttpGet(Utils.getURI(endpoint, path, true));
		return httpclient.execute(httpget);
	}

	public HttpResponse delete(String remoteContainer)
			throws ClientProtocolException, IOException, CDMIOperationException, URISyntaxException {

		HttpDelete httpdelete = new HttpDelete(Utils.getURI(endpoint, remoteContainer, true));
		return httpclient.execute(httpdelete);
	}

	public String[] getChildren(String remoteContainer)
			throws ClientProtocolException, IOException,
			CDMIOperationException, ParseException, URISyntaxException {

		List<String> fields = new ArrayList<String>();
		fields.add("children");
		HttpResponse response = read(remoteContainer, fields);
		// TODO: better conversion to String[]?
		List<Object> elements = Utils
				.getElementCollection(response, "children");
		return (String[]) elements.toArray(new String[elements.size()]);
	}

}
