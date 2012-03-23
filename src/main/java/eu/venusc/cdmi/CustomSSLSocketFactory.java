package eu.venusc.cdmi;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

public class CustomSSLSocketFactory extends SSLSocketFactory {

    SSLContext sslContext = SSLContext.getInstance("TLS");

    class X509NaiveTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    public CustomSSLSocketFactory(KeyStore keystore, String password)
            throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {

        super(keystore, password);
        TrustManager tm = new X509NaiveTrustManager();

        KeyManagerFactory keyManagerFactory;

        try {
            keyManagerFactory = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
            keyManagerFactory.init(keystore, password.toCharArray());

            X509KeyManager x509KeyManager = null;
            for (KeyManager keyManager : keyManagerFactory.getKeyManagers()) {
                if (keyManager instanceof X509KeyManager) {
                    x509KeyManager = (X509KeyManager) keyManager;
                    break;
                }
            }

            if (x509KeyManager == null) {
                throw new NullPointerException();
            }

            sslContext.init(new KeyManager[] { x509KeyManager },
                    new TrustManager[] { tm }, null);
        } catch (NoSuchProviderException e) {
            // TODO not propagating this error error as it would break the BSC CLI, not a good idea before the demo
            e.printStackTrace();
        }
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port,
            boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port,
                autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}
