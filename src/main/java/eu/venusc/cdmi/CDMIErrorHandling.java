package eu.venusc.cdmi;

import java.util.HashMap;
import java.util.Map;

public class CDMIErrorHandling implements CDMIResponseStatus{

	private static Map messages;
	static {
		messages = new HashMap();
		// default error codes/messages
		messages.put("default" + REQUEST_BAD, "Invalid parameter of field names in the request.");
		messages.put("default" + REQUEST_UNAUTHENTICATED, "Incorrect or missing authentication credentials.");
		messages.put("default" + REQUEST_UNAUTORIZED, "Client lacks the proper authorization to perform this request.");
		messages.put("default" + REQUEST_NOT_FOUND, "The resource specified was not found.");
		messages.put("default" + REQUEST_NAME_CONFLICT, "The operation conflicts with a non-CDMI access protocol lock, or could cause a state transition error on the server or he data object cannot be deleted.");
		messages.put("default" + REQUEST_NOT_ACCEPTABLE, "The server is unable to provide the object in the content-type specified in the Accept header.");
		// potentially could define object specific error messages
	}
	
	public static void checkResponseCode(String operationType, int code) throws CDMIOperationException {
		Object error_message = messages.get(operationType + code);
		if (error_message != null)
			throw new CDMIOperationException((String) error_message, code);
	}	
}
