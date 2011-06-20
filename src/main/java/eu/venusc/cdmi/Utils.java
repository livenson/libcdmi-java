package eu.venusc.cdmi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.HttpResponse;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Utils {

	/**
	 * This methods returns content of a file with bytes. Taken from
	 * http://www.exampledepot.com/egs/java.io/File2ByteArray.html
	 * 
	 * @param file
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
			throw new IOException("File is too large: " + file.getName());

		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	public static String getContent(String file) throws IOException {

		FileReader fileReader = new FileReader(file);
		BufferedReader in = new BufferedReader(fileReader);
		StringBuilder content = new StringBuilder("");
		String str;
		while ((str = in.readLine()) != null) {
			content.append(str);
		}
		return content.toString();

	}

	public static List getElementCollection(HttpResponse response,
			String elementName) throws IOException, ParseException {

		JSONParser parser = new JSONParser();
		InputStream stream = response.getEntity().getContent();
		InputStreamReader is = new InputStreamReader(stream);

		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		Map jsonMap = (Map) parser.parse(is, containerFactory);
		List foundElements = (LinkedList) jsonMap.get(elementName);
		stream.close();
		is.close();
		return foundElements;
	}

	public static Object getElement(HttpResponse response, String elementName)
			throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		InputStream stream = response.getEntity().getContent();
		InputStreamReader is = new InputStreamReader(stream);

		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};

		Map jsonMap = (Map) parser.parse(is, containerFactory);

		Object content = jsonMap.get(elementName);
		stream.close();
		is.close();
		return content;

	}

	/**
	 * To extract a binary CDMI object contents. The binary files are decoded as
	 * JSON BASE64 rules.
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String getObjectContent(HttpResponse response)
			throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		InputStream stream = response.getEntity().getContent();
		InputStreamReader is = new InputStreamReader(stream);

		parser = new JSONParser();
		stream = response.getEntity().getContent();
		is = new InputStreamReader(stream);

		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		Map jsonMap = (Map) parser.parse(is, containerFactory);

		String content = jsonMap.get("value").toString();
		byte[] decodedObj = Base64.decodeBase64(content);
		stream.close();
		is.close();
		return new String(decodedObj);
	}

	public static String getTextContent(HttpResponse response)
			throws IOException, ParseException {

		JSONParser parser = new JSONParser();
		InputStream stream = response.getEntity().getContent();
		InputStreamReader is = new InputStreamReader(stream);

		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};

		Map jsonMap = (Map) parser.parse(is, containerFactory);
		String content = jsonMap.get("value").toString();
		stream.close();
		is.close();
		return content;

	}

	public static File createTemporaryFile(String content, String prefix, String suffix)
			throws IOException {

		File tempFile = File.createTempFile(prefix, suffix);
		// Write to temporary file
		BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
		out.write(content);
		out.close();
		tempFile.deleteOnExit();
		return tempFile;
	}

	/**
	 * This method returns contents of a NonCDMI object as an byte array.
	 */
	public static byte[] extractContents(HttpResponse response)
			throws IllegalStateException, IOException {

		InputStream in = response.getEntity().getContent();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int bytesRead = 0;
		byte[] buffer = new byte[2048];
		while ((bytesRead = in.read(buffer, 0, buffer.length)) > 0) {
			outputStream.write(buffer, 0, bytesRead);
		}
		response.getEntity().consumeContent();
		byte[] outBuffer = outputStream.toByteArray();
		return outBuffer;
	}

	public static File createZip(String zipname) throws IOException {

		byte[] buf = new byte[2048];
		File file = Utils.createTemporaryFile("Put your data here to be zipped.",
				"libcdmi-java", ".txt");
		FileInputStream fis = new FileInputStream(file);
		fis.read(buf, 0, buf.length);

		CRC32 crc = new CRC32();
		ZipOutputStream out = new ZipOutputStream(
				(OutputStream) new FileOutputStream(zipname));
		out.setLevel(6);
		ZipEntry entry = new ZipEntry(file.getName());
		entry.setSize((long) buf.length);
		crc.reset();
		crc.update(buf);
		entry.setCrc(crc.getValue());
		out.putNextEntry(entry);
		out.write(buf, 0, buf.length);
		out.finish();
		out.close();
		file = new File(zipname);
		file.deleteOnExit();
		return file;
	}
	
	public static URI getURI(URL endpoint, String path) throws URISyntaxException, URIException {
		return getURI(endpoint, path, false);	
	}
	
	public static URI getURI(URL endpoint, String path, boolean endWithSlash) throws URISyntaxException, URIException {
		String ending = endWithSlash ? "/" : "";
		return new URI(endpoint.toString() + URIUtil.encodePathQuery(path) + ending);	
	}	
}
