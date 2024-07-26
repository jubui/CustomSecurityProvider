package julian;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;

// Merges two trust managers
public class CustomTrustManager implements X509TrustManager {

  TrustManager a;
  TrustManager b;
  public CustomTrustManager(TrustManager a, TrustManager b) {
    this.a = a;
    this.b = b;
  }
  @Override
  public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    CertificateException exceptionA = null;
    CertificateException exceptionB = null;
    try {
      ((X509TrustManager)a).checkClientTrusted(chain, authType);
    } catch (CertificateException e) {
      exceptionA = e;
    }

    try {
      ((X509TrustManager)b).checkClientTrusted(chain, authType);
    } catch (CertificateException e) {
      exceptionB = e;
    }

    if (exceptionA == null || exceptionB == null) {
      return;
    }
    if (exceptionA != null) throw exceptionA;
    if (exceptionB != null) throw exceptionB;
  }

  @Override
  public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    CertificateException exceptionA = null;
    CertificateException exceptionB = null;
    try {
      ((X509TrustManager)a).checkServerTrusted(chain, authType);
    } catch (CertificateException e) {
      exceptionA = e;
    }

    try {
      ((X509TrustManager)b).checkServerTrusted(chain, authType);
    } catch (CertificateException e) {
      exceptionB = e;
    }

    if (exceptionA == null || exceptionB == null) {
      return;
    }
    if (exceptionA != null) throw exceptionA;
    if (exceptionB != null) throw exceptionB;
  }

  @Override
  public X509Certificate[] getAcceptedIssuers() {
    X509Certificate[] aCertificates = ((X509TrustManager)a).getAcceptedIssuers();
    X509Certificate[] bCertificates = ((X509TrustManager)b).getAcceptedIssuers();

    X509Certificate[] merged = new X509Certificate[aCertificates.length + bCertificates.length];
    for (int i = 0; i < aCertificates.length; i++) {
      merged[i] = aCertificates[i];
    }
    for (int i = 0; i < bCertificates.length; i++) {
      merged[aCertificates.length + i] = bCertificates[i];
    }
    return merged;
  }
}
