package eu.venusc.cdmi;

public interface CDMIResponseStatus {

	/** Valid response is enclosed. 200*/
	public static final int REQUEST_READ = 200;
	/** New container or data object was created. 201*/
	public static final int REQUEST_CREATED = 201;
	/**
	 * Request is in the process of being created. Investigate completionStatus
	 * and percent Complete parameters to determine the current status of the
	 * operation. 202*/
	public static final int REQUEST_ACCEPTED = 202;
	/** Data object or container was successfully deleted. 204*/
	public static final int REQUEST_DELETED = 204;
	/** The URI is a reference to another URI. 302*/
	public static final int REQUEST_FOUND = 302;
	/** The operation conflicts because the container already exists. 304*/
	public static final int REQUEST_CONFLICT = 304;
	/** Invalid parameter or field names in the request. 400*/
	public static final int REQUEST_BAD = 400;
	/** Incorrect or missing authentication credentials. 401*/
	public static final int REQUEST_UNAUTHENTICATED = 401;
	/** Client lacks the proper authorization to perform this request. 403*/
	public static final int REQUEST_UNAUTORIZED = 403;
	/** Request not found at the specified URI. 404*/
	public static final int REQUEST_NOT_FOUND = 404;
	/**
	 * The server is unable to provide the object in the content-type specified
	 * in the Accept header.406 */
	public static final int REQUEST_NOT_ACCEPTABLE = 406;
	/** The request containes a name which already exists. 409*/
	public static final int REQUEST_NAME_CONFLICT = 409;

}
