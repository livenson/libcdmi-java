import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;

import eu.venusc.cdmi.CDMIConnection;

public class CDMIClient {

	public static void main(String[] args) {
		try {
			String remoteFNM = "hello1.txt";
			URL endpoint = new URL("http://localhost:2364/");
			String localfile = "hello1.txt";
			String localfile2 = "hello2.txt";
			String container = "mycontainer";

			Map parameters = new HashMap();
			parameters.put("mimetype", "text/plain");

			Credentials creds = new UsernamePasswordCredentials("aaa",
					"aaa");

			CDMIConnection cd = new CDMIConnection(creds, endpoint);

			// perform basic operations on blob
			cd.createBlob(localfile, remoteFNM, parameters);
			cd.updateBlob(localfile, remoteFNM, parameters);
			File f = cd.readBlob(remoteFNM, localfile2);
			System.out.println("Saved blob contents into file " + f.getAbsolutePath());

			// create a new root subfolder 
			//cd.createContainer(container);

			// see what's in the root folder
			for (String s : cd.getChildren("/")) {
				System.out.println(s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
