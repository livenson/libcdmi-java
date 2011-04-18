package eu.venusc.cdmi;


/**
 * Static values of possible CDMI content types.
 */
public interface CDMIContentType {

	public static final String CDMI_CAPABILITY = "application/vnd.org.snia.cdmi.capabilities+json";
	public static final String CDMI_CONTAINER = "application/vnd.org.snia.cdmi.container+json";
	public static final String CDMI_DATA = "application/vnd.org.snia.cdmi.dataobject+json";
	public static final String CDMI_DOMAIN = "application/vnd.org.snia.cdmi.domain+json";
	public static final String CDMI_OBJECT = "application/vnd.org.snia.cdmi.object+json";
	public static final String CDMI_QUEUE = "application/vnd.org.snia.cdmi.queue+json";
	public static final String CDMI_SPEC_VERSION = "1.0";
}
