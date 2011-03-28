package eu.venusc.cdmi;

import java.util.HashMap;
import java.util.Map;

public class CDMIErrorHandling {

	private static Map messages;
	static {
		messages = new HashMap();
		// default error codes/messages
		messages.put("default" + 400, "Invalid parameter of field names in the request.");
		messages.put("default" + 401, "Incorrect or missing authentication credentials.");
		messages.put("default" + 403, "Client lacks the proper authorization to perform this request.");
		messages.put("default" + 404, "The resource specified was not found.");
		messages.put("default" + 409, "The operation conflicts with a non-CDMI access protocol lock, or could cause a state transition error on the server or he data object cannot be deleted.");
		messages.put("default" + 406, "The server is unable to provide the object in the content-type specified in the Accept header.");
		// potentially could define object specific error messages
	}
	
	public static void checkResponseCode(String operationType, int code) throws CDMIOperationException {
		Object error_message = messages.get(operationType + code);
		if (error_message != null)
			throw new CDMIOperationException((String) error_message, code);	
	}
}
