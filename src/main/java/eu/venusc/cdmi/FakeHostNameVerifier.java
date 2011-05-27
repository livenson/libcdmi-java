package eu.venusc.cdmi;
import javax.net.ssl.HostnameVerifier;

public class FakeHostnameVerifier implements HostnameVerifier {

	public boolean verify(String hostname, 
	    javax.net.ssl.SSLSession session) {
	    return(true);
	}
}