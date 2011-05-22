package eu.venusc.cdmi;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.parser.ParseException;
import static eu.venusc.cdmi.CDMIContentType.*;

public class ContainerOperations{

	private URL endpoint;
	private DefaultHttpClient httpclient;

	public ContainerOperations(URL endpoint, DefaultHttpClient httpclient) {

		this.httpclient = httpclient;
		this.endpoint = endpoint;
	}


	public HttpResponse create(String remoteContainer, Map <String, Object> parameters)
			throws ClientProtocolException, IOException, CDMIOperationException {

		HttpPut httpput = new HttpPut(endpoint  + remoteContainer);
		httpput.setHeader("Content-Type", CDMI_CONTAINER);
		httpput.setHeader("Accept", CDMI_CONTAINER);
		httpput.setHeader("X-CDMI-Specification-Version",
				CDMI_SPEC_VERSION);

		ContainerCreateRequest createObj = new ContainerCreateRequest();
		StringWriter out = new StringWriter();
		createObj.writeJSONString(out);

		StringEntity entity = new StringEntity(out.toString());
		httpput.setEntity(entity);
		return httpclient.execute(httpput);
	}


	public HttpResponse delete(String remoteContainer)
			throws ClientProtocolException, IOException, CDMIOperationException {

		HttpDelete httpdelete = new HttpDelete(endpoint + remoteContainer);

		httpdelete.setHeader("Content-Type", CDMI_CONTAINER);
		httpdelete.setHeader("Accept", CDMI_CONTAINER);
		httpdelete.setHeader("X-CDMI-Specification-Version",
				CDMI_SPEC_VERSION);

		return httpclient.execute(httpdelete);
	}


	public HttpResponse read(String remoteContainer, List<String> fields)
			throws ClientProtocolException, IOException {
		String path = endpoint.toString() + remoteContainer + "?";
	
		for (String f : fields) {
			path = path + f;
		}
		HttpGet httpget = new HttpGet(path);
		httpget.setHeader("Content-Type", CDMI_CONTAINER);
		httpget.setHeader("Accept", CDMI_CONTAINER);
		httpget.setHeader("X-CDMI-Specification-Version",
				CDMI_SPEC_VERSION);

		return httpclient.execute(httpget);
	}


	public String[] getChildren(String remoteContainer)
			throws ClientProtocolException, IOException,
			CDMIOperationException, ParseException {
		
		String path = endpoint.toString();
		path = path + remoteContainer;

		List<String> fields = new ArrayList<String>();
		fields.add("children");
		HttpResponse response = read(remoteContainer, fields);
		// TODO: better conversion to String[]?
		List <Object> elements = Utils.getElementCollection(response, "children");		
		return (String[]) elements.toArray(new String[elements.size()]);
	}

}
