package julian;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

// Our provider provides this SSLContext
public class CustomSSLContextSpi extends SSLContextSpi {
  private SSLContext delegate;
  private TrustManagerFactory tmf;

  public CustomSSLContextSpi() throws NoSuchAlgorithmException {
    // Initialize with a standard SSLContext implementation
    System.out.println("JULIAN MysqlSSLContextSpi constructor");

    try {
      tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
      ks.load(null); // You don't need the KeyStore instance to come from a file.

      for (int i = 1; i <= 5; i++) {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        String path = "/Users/julian.bui/Downloads/us-east-1-bundle-" + i + ".der";
        System.out.println("Loading: " + path);
        InputStream is = new FileInputStream(path);
        X509Certificate mycert = (X509Certificate) cf.generateCertificate(is);

        ks.setCertificateEntry("mycert" + i, mycert);
      }
      tmf.init(ks);
    } catch (Exception e) {
      System.out.println("JULIAN, error: " + e);
      e.printStackTrace();
    }
  }

  @Override
  protected void engineInit(KeyManager[] km, TrustManager[] tm, SecureRandom sr)
      throws KeyManagementException {

    // JB: I'm not exactly clear on what the inputs to this function are, but from debugging, I saw that at least some of them were just null. I probably wanna merge in the inputs too to our new SSLContext

    System.out.println("JULIAN engineInit");
    try {

      // JB: I'm not sure this code is right or that we even have to do it this way, but it does seem to end up with our trust store's certs in it
      SSLContext sslContext = SSLContext.getInstance("TLS", "SunJSSE");
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init((KeyStore) null);
      TrustManager[] defaultTrustManagers = trustManagerFactory.getTrustManagers();

      CustomTrustManager customTrustManager = new CustomTrustManager(defaultTrustManagers[0], this.tmf.getTrustManagers()[0]);
      TrustManager[] mergedTms = new TrustManager[]{customTrustManager};
      // JB: From the api docs on .init, passing in a null KeyManager should mean we get the defaults. Furthermore, I'm not positive that appian puts any client certs in our KeyStore...anyway...need to investigate this
      sslContext.init(null, mergedTms, sr);
      delegate = sslContext;
    } catch (NoSuchAlgorithmException | NoSuchProviderException | KeyStoreException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected SSLSocketFactory engineGetSocketFactory() {
    System.out.println("JULIAN engineGetSocketFactory");
    return delegate.getSocketFactory();
  }

  @Override
  protected SSLServerSocketFactory engineGetServerSocketFactory() {
    System.out.println("JULIAN engineGetServerSocketFactory");
    return delegate.getServerSocketFactory();
  }

  @Override
  protected SSLEngine engineCreateSSLEngine() {
    System.out.println("JULIAN engineCreateSSLEngine");
    return delegate.createSSLEngine();
  }

  @Override
  protected SSLEngine engineCreateSSLEngine(String host, int port) {
    System.out.println("JULIAN engineCreateSSLEngine");
    return delegate.createSSLEngine(host, port);
  }

  @Override
  protected SSLSessionContext engineGetServerSessionContext() {
    System.out.println("JULIAN engineGetServerSessionContext");
    return delegate.getServerSessionContext();
  }

  @Override
  protected SSLSessionContext engineGetClientSessionContext() {
    System.out.println("JULIAN engineGetClientSessionContext");
    return delegate.getClientSessionContext();
  }

  @Override
  protected SSLParameters engineGetSupportedSSLParameters() {
    System.out.println("JULIAN engineGetSupportedSSLParameters");
    return delegate.getSupportedSSLParameters();
  }

  @Override
  protected SSLParameters engineGetDefaultSSLParameters() {
    System.out.println("JULIAN engineGetDefaultSSLParameters");
    return delegate.getDefaultSSLParameters();
  }
}
