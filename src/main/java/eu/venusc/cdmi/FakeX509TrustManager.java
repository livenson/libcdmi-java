package eu.venusc.cdmi;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class FakeX509TrustManager implements X509TrustManager {

	private X509Certificate[] acceptedIssuers = new X509Certificate[] {};

	public void checkClientTrusted(X509Certificate[] chain, String authType) {
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) {
	}

	public X509Certificate[] getAcceptedIssuers() {
		return (acceptedIssuers);
	}
}