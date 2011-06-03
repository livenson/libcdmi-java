package eu.venusc.cdmi;

import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;

import eu.venusc.cdmi.CDMIConnection;
import eu.venusc.cdmi.CDMIOperationException;
import eu.venusc.cdmi.Utils;

import static eu.venusc.cdmi.CDMIResponseStatus.*;

public class CDMIClientTest {

	static URL localFileBackend;
	static Credentials creds;
	static CDMIConnection conn;
	static String dirname;
	
	CDMIClientTest()
	{
		try {
			localFileBackend = new URL("http://bscgrid05.bsc.es:20839");
			dirname = null;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		creds = new UsernamePasswordCredentials("aaa", "aaa");

		try {
			conn = new CDMIConnection(creds,	localFileBackend);
			
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws CDMIOperationException, ParseException {

		try {
		//	URL localFileBackend = new URL("http://bscgrid05.bsc.es:20839");
			

			// define custom parameters
			Map parameters = new HashMap();
			parameters.put("mimetype", "text/plain");

		
			
			CDMIClientTest client = new CDMIClientTest();
			String dirname = "/venuscolb/";
			
			//client.listNonCdmiDir(client.conn, "/venuscolb/");
		//	client.listCdmiDir(conn, "/");
			client.readNonCdmiFile(conn,"/venuscolb/", "/Users/lezzi/venuscolb");
		//	client.createNonCdmiDir(client.conn, parameters, "/venuscolb/");
		//	client.uploadNonCdmi(conn, parameters, "/Users/lezzi/Documents/workspace/venusbes/venusbes.war", "/venuscolb/venusbes.war");
			//client.readNonCdmiFile(conn, "/venuscolb/venusbes.war", "/Users/lezzi/test.war");
			
		//	client.uploadNonCdmiDir(new File("/Users/lezzi/Desktop/Model-4"), true, "/venuscolb");
			client.listNonCdmiDir(conn, "/");

		   } catch (MalformedURLException e) {
               e.printStackTrace();
       } catch (ClientProtocolException e) {
               e.printStackTrace();
       } catch (IOException e) {
               e.printStackTrace();
       } 
}



	public int readCdmiFile(CDMIConnection conn, String remoteFileName, String localFileName) throws IOException, ParseException{
		HttpResponse response = conn.getBlobProxy().read(remoteFileName);
		
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_READ)
			System.out.println("Download failed : " + remoteFileName +" response code: "+ responseCode);

		File data1 = Utils.createFile(Utils.getTextContent(response), localFileName, ".txt");

		System.out.println("File downloaded: " + data1.getAbsolutePath());
		return responseCode;
		
	}
	
	public int readNonCdmiFile(CDMIConnection conn, String remoteFileName, String localFileName) throws IOException, ParseException{
		HttpResponse response = conn.getNonCdmiBlobProxy().read(remoteFileName);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_READ)
			System.out.println("Download failed : " + remoteFileName +" response code: "+ responseCode);

	//	File data1 = Utils.createFile(Utils.getTextContent(response), localFileName, ".war");
	
		//System.out.println("File downloaded: " + data1.getAbsolutePath());
		byte[] fileContent = Utils.extractContents(response);
		FileOutputStream fileOut = new FileOutputStream(localFileName);
		
		fileOut.write(fileContent);
		
		return responseCode;
	}
	
	public int createCdmiDir(CDMIConnection conn, Map parameters, String dirName) throws IOException, CDMIOperationException{
		System.out.println("Creating directories.." + dirName);
		
		HttpResponse response = conn.getContainerProxy().create(dirName, parameters);
		int responseCode = response.getStatusLine().getStatusCode();
		System.out.println(dirName + " created: "+ responseCode);
		
		return responseCode;

	}
	
	public int createNonCdmiDir(CDMIConnection conn, Map parameters, String dirName) {
		System.out.println("Creating directories.. ");
		
		HttpResponse response;
		int responseCode = 0;
		try {
			response = conn.getNonCdmiContainerProxy().create(dirName, parameters);
			responseCode = response.getStatusLine().getStatusCode();

		} catch (ClientProtocolException e) {
	
			e.printStackTrace();
		} catch (IOException e) {
	
			e.printStackTrace();
		} catch (CDMIOperationException e) {
			e.printStackTrace();
		}
		System.out.println(dirName + " created: "+ responseCode);
		
		return responseCode;

	}
	
	public int uploadNonCdmi(CDMIConnection conn, Map parameters, String fileName, String blobPath) throws IOException, CDMIOperationException{
				
		byte[] value = Utils.getBytesFromFile(new File(fileName));
		HttpResponse response = conn.getNonCdmiBlobProxy().create(blobPath, value, parameters);
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println(blobPath+ " created: "+ responseCode);

		return responseCode;
	}
	
	public int uploadCdmi(CDMIConnection conn, Map parameters, String fileName, String blobPath) throws IOException, CDMIOperationException{
		
		byte[] value = Utils.getBytesFromFile(new File(fileName));
		HttpResponse response = conn.getBlobProxy().create(blobPath, value, parameters);
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println(blobPath+ " created: "+ responseCode);
		return responseCode;

	}
	
	public int deleteCdmiDir(CDMIConnection conn, String dirName) throws IOException, CDMIOperationException{
		HttpResponse response = conn.getContainerProxy().delete(dirName);
		int responseCode = response.getStatusLine().getStatusCode();

		return responseCode;
	}
	
	public int deleteNonCdmiDir(CDMIConnection conn, String dirName) throws IOException, CDMIOperationException{
		HttpResponse response = conn.getNonCdmiContainerProxy().delete(dirName);
		int responseCode = response.getStatusLine().getStatusCode();

		return responseCode;
	}
	
	public void listCdmiDir(CDMIConnection conn, String dirName) throws IOException, CDMIOperationException, ParseException{
		
		for (String s : conn.getContainerProxy().getChildren(dirName)) {
			System.out.println(s);
		}
	}
	
	public void listNonCdmiDir(CDMIConnection conn, String dirName) throws IOException, CDMIOperationException, ParseException{
		
		for (String s : conn.getNonCdmiContainerProxy().getChildren(dirName)) {
			System.out.println(s);
		}	
	}

	public int deleteCdmiFile(CDMIConnection conn, String fileName) throws IOException{
		HttpResponse response = conn.getBlobProxy().delete(fileName);
		int responseCode = response.getStatusLine().getStatusCode();
		
		return responseCode;
	}

	public int deleteNonCdmiFile(CDMIConnection conn, String fileName) throws IOException{
		HttpResponse response = conn.getNonCdmiBlobProxy().delete(fileName);
		int responseCode = response.getStatusLine().getStatusCode();
		
		return responseCode;
	}
	
	public int uploadNonCdmiDir(File directory, boolean recurse, String dirDest)
	{
		Map parameters = new HashMap();
		parameters.put("mimetype", "text/plain");
		
		dirname = dirDest + "/" + directory.getName();
		this.createNonCdmiDir(conn, parameters, "/" + dirname +"/");

		int responseCode = this.uploadDir(directory, recurse, dirname);
	//Java4: Collection files = listFiles(directory, filter, recurse);
		
		//File[] arr = new File[files.size()];
		
		return responseCode;
	}

	public int uploadDir(File directory, boolean recurse, String dirDest)
	{
		// List of files / directories
		//Vector<File> files = new Vector<File>();
		Map parameters = new HashMap();
		parameters.put("mimetype", "text/plain");
		int responseCode = 0;
		
		// Get files / directories in the directory
		File[] entries = directory.listFiles();
		
		// Go over entries
		for (File entry : entries)
		{

			// If the file is a directory and the recurse flag
			// is set, recurse into the directory
			if (recurse && entry.isDirectory())
			{
				System.out.println("Directory: "+ entry.getName());
				if (entry.getName() != ".." && entry.getName() != ".")
				{
					dirDest = dirDest + "/" + entry.getName();
					responseCode = this.createNonCdmiDir(conn, parameters, "/" + entry.getName()+"/");
					//files.addAll(uploadDir(entry, recurse, dirDest));
				}
			}
			else
			{
				try {
					dirname = dirDest + "/" + entry.getName();

					System.out.println("Copying: " + entry.getAbsolutePath() + " to " + dirname);

					responseCode = this.uploadNonCdmi(conn, parameters, entry.getAbsolutePath(), dirname);
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CDMIOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return responseCode;		
	}	
}
