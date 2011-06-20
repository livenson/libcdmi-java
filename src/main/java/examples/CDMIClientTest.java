/*
 * 
 */
package examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
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

// TODO: Auto-generated Javadoc
/**
 * The Class CDMIClientTest.
 */
public class CDMIClientTest {

	static URL localFileBackend;
	
	static Credentials creds;
	
	static CDMIConnection conn;
	
	static String dirname;
	
	/**
	 * Instantiates a new CDMI test client.
	 *
	 * @param vcdm_endpoint URL of the CDMI server
	 */
	CDMIClientTest(String vcdm_endpoint)
	{
		try {
			localFileBackend = new URL(vcdm_endpoint);
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
	
	/**
	 * The main method.
	 *
	 * @param args The first argument is the VCDM server endpoint arguments
	 */
	public static void main(String[] args) {

		// define custom parameters
		Map parameters = new HashMap();
		parameters.put("mimetype", "text/plain");


		
		CDMIClientTest client = new CDMIClientTest(args[0]);
		
		//client.listNonCdmiDir(client.conn, "/venuscolb/");
//	client.listCdmiDir(conn, "/");
//	client.readNonCdmiFile(conn,"/venuscolb/", "/Users/lezzi/venuscolb");
	//client.createNonCdmiDir(client.conn, parameters, "/venuscolb/");
//	client.uploadNonCdmi(conn, parameters, "/Users/lezzi/Documents/workspace/venusbes/venusbes.war", "/venuscolb/venusbes.war");
		//client.readNonCdmiFile(conn, "/venuscolb/venusbes.war", "/Users/lezzi/test.war");
		
//client.uploadNonCdmiDir(new File("/Users/lezzi/Desktop/Model-4"), true, "/venuscolb/");
client.getNonCDMIDir("/venuscolb/", "/Users/lezzi/venuscolb"); 
}



	/**
	 * Read CDMI file.
	 *
	 * @param remoteFileName The remote file path
	 * @param localFileName Absolute path of the local file
	 * @return Returns an integer containing the http response code
	 */
	public int readCdmiFile(String remoteFileName, String localFileName) {
		HttpResponse response = null;
		try {
			response = conn.getBlobProxy().read(remoteFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_READ)
			System.out.println("Download failed : " + remoteFileName +" response code: "+ responseCode);

		File data1;
		try {
			data1 = Utils.createTemporaryFile(Utils.getTextContent(response), localFileName, ".txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("File downloaded: " + data1.getAbsolutePath());
		return responseCode;
		
	}
	
	/**
	 * Read non CDMI file.
	 *
	 * @param remoteFileName The remote file path
	 * @param localFileName Absolute path of the local file
	 * @return Returns an integer containing the http response code
	 */
	public int readNonCdmiFile(String remoteFileName, String localFileName) {
		HttpResponse response = null;
		try {
			response = conn.getNonCdmiBlobProxy().read(remoteFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode != REQUEST_READ)
			System.out.println("Download failed : " + remoteFileName +" response code: "+ responseCode);

	//	File data1 = Utils.createFile(Utils.getTextContent(response), localFileName, ".war");
	
		//System.out.println("File downloaded: " + data1.getAbsolutePath());
		byte[] fileContent = null;
		try {
			fileContent = Utils.extractContents(response);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(localFileName);
			fileOut.write(fileContent);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responseCode;
	}
	
	/**
	 * Creates a remote CDMI directory
	 *
	 * @param parameters A Map object containing "mimetype", "text/plain" like parameters 
	 * @param dirName Path of the remote directory
	 * @return Returns an integer containing the http response code
	 */
	public int createCdmiDir(Map parameters, String dirName) {
		System.out.println("Creating directories.." + dirName);
		
		HttpResponse response = null;
		try {
			response = conn.getContainerProxy().create(dirName, parameters);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CDMIOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int responseCode = response.getStatusLine().getStatusCode();
		System.out.println(dirName + " created: "+ responseCode);
		
		return responseCode;

	}
	
	/**
	 * Creates a remote non CDMI directory
	 *
	 * @param parameters A Map object containing "mimetype", "text/plain" like parameters
	 * @param dirName Path of the remote directory
	 * @return Returns an integer containing the http response code
	 */
	public int createNonCdmiDir(Map parameters, String dirName) {
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
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(dirName + " created: "+ responseCode);
		
		return responseCode;

	}
	
	/**
	 * Upload a non CDMI file.
	 *
	 * @param parameters the parameters
	 * @param fileName the file name
	 * @param blobPath Path of the remote directory where the file has to be uploaded
	 * @return Returns an integer containing the http response code
	 */
	public int uploadNonCdmi(Map parameters, String fileName, String blobPath) {
		HttpResponse response = null;
	
		byte[] value;
		try {
			value = Utils.getBytesFromFile(new File(fileName));
			response = conn.getNonCdmiBlobProxy().create(blobPath, value, parameters);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println(blobPath+ " created: "+ responseCode);

		return responseCode;
	}
	
	/**
	 * Upload CDMI file.
	 *
	 * @param parameters A Map object containing "mimetype", "text/plain" like parameters
	 * @param fileName Absolute path of the file to be uploaded
	 * @param blobPath Path of the remote directory where the file has to be uploaded
	 * @return Returns an integer containing the http response code
	 */
	public int uploadCdmi(Map parameters, String fileName, String blobPath) {
		HttpResponse response = null;

		byte[] value;
		try {
			value = Utils.getBytesFromFile(new File(fileName));
			response = conn.getBlobProxy().create(blobPath, value, parameters);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println(blobPath+ " created: "+ responseCode);
		return responseCode;

	}
	
	/**
	 * Delete a remote CDMI directory.
	 *
	 * @param dirName Path of the remote directory to be deleted
	 * @return Returns an integer containing the http response code
	 * 
	 */
	public int deleteCdmiDir(String dirName) {
		HttpResponse response = null;
		try {
			response = conn.getContainerProxy().delete(dirName);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CDMIOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int responseCode = response.getStatusLine().getStatusCode();

		return responseCode;
	}
	
	/**
	 * Delete a non CDMI directory.
	 *
	 * @param conn A eu.venusc.cdmi.CDMIConnection object
	 * @param dirName Path of the remote directory to be deleted
	 * @return Returns an integer containing the http response code
	 */
	public int deleteNonCdmiDir(String dirName) {
		HttpResponse response = null;
		try {
			response = conn.getNonCdmiContainerProxy().delete(dirName);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CDMIOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int responseCode = response.getStatusLine().getStatusCode();

		return responseCode;
	}
	
	/**
	 * List the content of a CDMI directory.
	 * 
	 * @param dirName Path of the remote directory to be listed
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void listCdmiDir(String dirName) {
		
		try {
			for (String s : conn.getContainerProxy().getChildren(dirName)) {
				System.out.println(s);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CDMIOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * List the content of a non CDMI directory.
	 *
	 * @param dirName Path of the remote directory to be listed
	 * @throws IOException Signals that an I/O exception has occurred
	 */
	public void listNonCdmiDir(String dirName) {
		
		try {
			for (String s : conn.getNonCdmiContainerProxy().getChildren(dirName)) {
				System.out.println(s);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CDMIOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	/**
	 * Delete a CDMI file.
	 *
	 * @param fileName Path of the remote file name to be deleted
	 * @return Returns an integer containing the http response code
	 * 
	 */
	public int deleteCdmiFile(String fileName) {
		HttpResponse response = null;
		try {
			response = conn.getBlobProxy().delete(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int responseCode = response.getStatusLine().getStatusCode();
		
		return responseCode;
	}

	/**
	 * Delete a non CDMI file.
	 *
	 * @param fileName Path of the remote file name to be deleted
	 * @return Returns an integer containing the http response code
	 */
	public int deleteNonCdmiFile(String fileName) {
		HttpResponse response = null;
		try {
			response = conn.getNonCdmiBlobProxy().delete(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int responseCode = response.getStatusLine().getStatusCode();
		
		return responseCode;
	}
	
	/**
	 * Upload a non CDMI directory.
	 *
	 * @param directory the directory
	 * @param recurse the recurse
	 * @param dirDest the dir dest
	 * @return the int
	 */
	public int uploadNonCdmiDir(File directory, boolean recurse, String dirDest)
	{
		Map parameters = new HashMap();
		parameters.put("mimetype", "text/plain");
		
		dirname = dirDest + "/" + directory.getName();
		this.createNonCdmiDir(parameters, "/" + dirname +"/");

		int responseCode = 0;
		try {
			responseCode = this.uploadDir(directory, recurse, dirname);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//Java4: Collection files = listFiles(directory, filter, recurse);
		
		//File[] arr = new File[files.size()];
		
		return responseCode;
	}

	/**
	 * Upload dir.
	 *
	 * @param directory the directory
	 * @param recurse the recurse
	 * @param dirDest the dir dest
	 * @return the int
	 */
	public int uploadDir(File directory, boolean recurse, String dirDest) throws IOException
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
					//dirDest = dirDest + "/" + entry.getName();
					responseCode = this.createNonCdmiDir(parameters, "/" + dirDest + "/" + entry.getName());
					uploadDir(entry, recurse, dirDest + "/" + entry.getName());
				}
			}
			else
			{
				dirname = dirDest + "/" + entry.getName();

				System.out.println("Copying: " + entry.getAbsolutePath() + " to " + dirname);

				responseCode = this.uploadNonCdmi(parameters, entry.getAbsolutePath(), dirname);
			}
		}
		
		return responseCode;		
	}	
	
	/**
	 * Download a non CDMI directory.
	 *
	 * @param remoteDirName Path of the remote directory
	 * @param localPath Absolute path of the local directory
	 * @return Returns 0 on success, -1 on error
	 */
	int getNonCDMIDir(String remoteDirName, String localPath){
		 try {
			 // If localPat doesn't exist, create it
			File localDir = new File(localPath);
			boolean success = false;
			if (!localDir.exists())
				success = (new File(localPath)).mkdir();
			
			// Create locally the remote directory
			localDir = new File(localPath + remoteDirName);
			if (!localDir.exists())
				success = (new File(localPath + remoteDirName)).mkdir();
			
			System.out.println("Looking directory: " + remoteDirName);

			String[] files = conn.getNonCdmiContainerProxy().getChildren(remoteDirName);
			for (String file : files)
			{
				System.out.println(file);

				if (file.endsWith("/"))
				{
					System.out.println("Creating directory: " + localPath + remoteDirName + file);

					localDir = new File(localPath + remoteDirName + file);
					if(!localDir.exists()) {
						success = (new File(localPath + remoteDirName + file)).mkdir();
					if (!success) {
						System.out.println("Error creating directory: " + localPath + remoteDirName + file);
						return -1;
					}
					}
					this.getNonCDMIDir(remoteDirName + "/" + file, localPath);				

				}
				else
				{
					String remoteFileName = remoteDirName + "/" + file;
					System.out.println("Downloading file " + remoteFileName + " to " + localPath + "/" + remoteDirName + "/" + file);
					this.readNonCdmiFile(remoteFileName, localPath + "/" + remoteDirName + "/" + file);
				}
					
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CDMIOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return 0;
		 
	}
}
