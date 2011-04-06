package eu.venusc.cdmi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
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
	 * @return
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

	/**
	 * This method can be used to extract an element array from the response.
	 * 
	 * @param response
	 * @param elementName
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static Object[] getElementArrary(HttpResponse response,
			String elementName) throws IllegalStateException, IOException {
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
		try {
			Map json = (Map) parser.parse(is, containerFactory);
			LinkedList theList = (LinkedList) json.get(elementName);
			return theList.toArray();
		} catch (ParseException pe) {
			System.out.println(pe);
		}
		return null;
	}

	/**
	 * This method extracts a specific element from the response
	 * 
	 * @param response
	 * @param elementName
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static Object getElement(HttpResponse response, String elementName)
			throws IllegalStateException, IOException {
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
		try {
			Map json = (Map) parser.parse(is, containerFactory);
			return json.get(elementName);
		} catch (ParseException pe) {
			System.out.println(pe);
		}
		return null;
	}

	/**
	 * This method returns the content of a binary file decoded using Base64
	 * 
	 * @param response
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static Object getObjectContent(HttpResponse response)
			throws IllegalStateException, IOException {
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
		try {
			Map json = (Map) parser.parse(is, containerFactory);
			String content = json.get("value").toString();
			Base64 decoder = new Base64();
			return new String(decoder.decodeBase64(content));
		} catch (ParseException pe) {
			System.out.println(pe);
		}
		return null;
	}
}
