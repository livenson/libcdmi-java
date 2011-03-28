import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;

import eu.venusc.cdmi.CDMIConnection;
import eu.venusc.cdmi.CDMIOperationException;

public class CDMIClient {

	public static void main(String[] args) {

		String remoteFNM = "hello1.txt";
		URL endpoint;
		try {
			endpoint = new URL("http://localhost:2364/");

			String localfile = "hello1.txt";
			String localfile2 = "hello2.txt";
			String container = "mycontainer";

			Map parameters = new HashMap();
			parameters.put("mimetype", "text/plain");

			Credentials creds = new UsernamePasswordCredentials("aaa", "aaa");

			CDMIConnection cd = new CDMIConnection(creds, endpoint);

			// perform basic operations on blob
			cd.createBlob(localfile, remoteFNM, parameters);
			cd.updateBlob(localfile, remoteFNM, parameters);
			File f = cd.readBlob(remoteFNM, localfile2);
			System.out.println("Saved blob contents into file "
					+ f.getAbsolutePath());

			// create a new root subfolder
			cd.createContainer(container, parameters);

			// see what's in the folder
			String p = "/";
			System.out.println("======= " + p + " =======");

			for (String s : cd.getChildren(p)) {
				System.out.println(s);
			}
			System.out.println("==============");

			cd.delete(remoteFNM);
			cd.deleteContainer(container);

			p = "/";
			System.out.println("\n======= " + p + " =======");

			for (String s : cd.getChildren(p)) {
				System.out.println(s);
			}
			System.out.println("==============");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CDMIOperationException e) {
			System.err.println("CDMI protocol exception. Response code: "
					+ e.getResponseCode());
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
