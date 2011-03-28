package eu.venusc.cdmi;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;

public class CDMIClient {

	public static void main(String[] args) {
		try {
			String remoteFNM = "hello1.txt";
			URL endpoint = new URL("http://localhost:2364/");
			String localfile = "hello1.txt";
			String localfile2 = "hello2.txt";
			
			Map map = new HashMap();
			map.put("mimetype", "multipart/alternative");
			
			Credentials creds = new UsernamePasswordCredentials("username",
					"password");

			CDMIConnect cd = new CDMIConnect(creds, endpoint);
             
			
			cd.delete(remoteFNM);
			
			cd.create(localfile, remoteFNM, map);
			
			cd.update(localfile, remoteFNM, map);

			File f = cd.readFile(remoteFNM, localfile2);

			for (String s : cd.getChildren("mydata")) {
				System.out.println(s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
