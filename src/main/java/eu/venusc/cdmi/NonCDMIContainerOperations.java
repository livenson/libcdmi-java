package eu.venusc.cdmi;

import java.io.IOException;
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
			IOException, CDMIOperationException {
		HttpPut httpput = new HttpPut(endpoint+ "/"+ Utils.urlBuilder(remoteContainer) + "/");
		return httpclient.execute(httpput);
	}

	public HttpResponse read(String remoteContainer, List<String> fields)
			throws ClientProtocolException, IOException {
		String path = endpoint+ "/"+ Utils.urlBuilder(remoteContainer) + "/?";

		for (String f : fields) {
			path = path + f;
		}
		HttpGet httpget = new HttpGet(path);
		return httpclient.execute(httpget);
	}

	public HttpResponse delete(String remoteContainer)
			throws ClientProtocolException, IOException, CDMIOperationException {

		HttpDelete httpdelete = new HttpDelete(endpoint+ "/"+ Utils.urlBuilder(remoteContainer)+ "/");
		return httpclient.execute(httpdelete);
	}

	public String[] getChildren(String remoteContainer)
			throws ClientProtocolException, IOException,
			CDMIOperationException, ParseException {

		List<String> fields = new ArrayList<String>();
		fields.add("children");
		HttpResponse response = read(remoteContainer, fields);
		// TODO: better conversion to String[]?
		List<Object> elements = Utils
				.getElementCollection(response, "children");
		return (String[]) elements.toArray(new String[elements.size()]);
	}

}
