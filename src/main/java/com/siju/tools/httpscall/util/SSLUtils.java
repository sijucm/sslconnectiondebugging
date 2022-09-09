package com.siju.tools.httpscall.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

/**
 * Class containing the utilities required to create SSL enabled feign clients for self-signed
 * certificate servers
 */
public class SSLUtils {


  private final String theTrustStoreFile;
  private final String theKeyStoreFile;

  /**
   * Creates a new SSL Util
   * @param iagCertFileContent Certificate file content
   * @param iagKeyFileContent key file content
   */
  public SSLUtils( String iagCertFileContent,
       String iagKeyFileContent) {
    this.theTrustStoreFile = iagCertFileContent;
    this.theKeyStoreFile = iagKeyFileContent;
  }


  public static SSLContext getSslContext(String keystoreName, String keyStorePassword)
      throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException,
      UnrecoverableKeyException, IOException, CertificateException {
    String vaultUrl =  System.getenv("KEY_VAULT_URL");
    // temporarily using a G2 cert from another RG till POPS adds the new G2 cert
    vaultUrl = "https://keyvaultname.vault.azure.net/";
    String certificateName =  System.getenv("CERT_NAME");
    certificateName = "func-d-cert-03";
//        "coak-func-d-cert-03";

//        String certificateDetails = getCertificateDetails(vaultUrl, certificateName);
//    KeyStore keyStore = new KeyStoreUtil().getKeyStore(vaultUrl, certificateName);


    KeyStore keyStore  = KeyStore.getInstance("JKS");
    keyStore.load(new FileInputStream(keystoreName), keyStorePassword.toCharArray());

    TrustStrategy acceptingTrustStrategy = (chain1, authType) -> true;

    final SSLContext sslContext =
        SSLContexts.custom()
            .loadKeyMaterial(keyStore,keyStorePassword.toCharArray())
            .loadTrustMaterial(null, acceptingTrustStrategy)
            .build();
    return sslContext;
  }

  /**
   * returns SSL context
   * @return SSL context to be used with the certificate and key
   */
  public SSLContext getSSLContext() {

    final String theKeyStorePassword = "";

    try (InputStream is = new ByteArrayInputStream(
        theTrustStoreFile.getBytes(StandardCharsets.UTF_8))) {
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      X509Certificate caCert = (X509Certificate) cf.generateCertificate(is);

      Certificate[] chain = {caCert};

      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      keyStore.load(null, theKeyStorePassword.toCharArray());
      keyStore
          .setKeyEntry("key", loadPrivateKey(theKeyStoreFile), theKeyStorePassword.toCharArray(),
              chain);
      KeyManagerFactory kmf = KeyManagerFactory
          .getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(keyStore, theKeyStorePassword.toCharArray());

      TrustStrategy acceptingTrustStrategy = (chain1, authType) -> true;

      return SSLContexts.custom()
          .loadKeyMaterial(keyStore, theKeyStorePassword.toCharArray())
          .loadTrustMaterial(null, acceptingTrustStrategy).build();

    } catch (IOException
        | CertificateException
        | KeyStoreException
        | NoSuchAlgorithmException
        | UnrecoverableKeyException
        | KeyManagementException
        | InvalidKeySpecException e) {

//      log.error("Exception while loading the client certificates and key store.", e);
      throw new IllegalStateException("Not able to load the client certificates. ", e);
    }

  }

  private PrivateKey loadPrivateKey(String theKeyStoreFile)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

    try (InputStream in = new ByteArrayInputStream(
        theKeyStoreFile.getBytes(StandardCharsets.UTF_8))) {
//      String passwordData = StreamUtils.copyToString(in, Charset.defaultCharset());

      String passwordData = copyToString(in);

      String privateKeyPEM = passwordData
          .replace("-----BEGIN PRIVATE KEY-----", "")
          .replace("-----END PRIVATE KEY-----", "")
          .replaceAll("\\s", "");

      byte[] privateKeyDER = Base64.getDecoder().decode(privateKeyPEM);

      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyDER));
    }
  }

  private String copyToString(InputStream in) throws IOException {
    StringBuilder out = new StringBuilder();
    InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
    char[] buffer = new char[4096];
    boolean var5 = true;

    int bytesRead;
    while((bytesRead = reader.read(buffer)) != -1) {
      out.append(buffer, 0, bytesRead);
    }

    String passwordData = out.toString();
    return passwordData;
  }

}
