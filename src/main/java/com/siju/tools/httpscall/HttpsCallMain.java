package com.siju.tools.httpscall;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class HttpsCallMain {

  public static void main(String[] args)
      throws UnrecoverableKeyException, IOException, NoSuchAlgorithmException, KeyStoreException,
          InterruptedException, KeyManagementException, CertificateException {
    System.out.println("called");

    String restUrl = "https://examples.com/customers/v1/35'";
    if (args.length > 0) {
      restUrl = args[0];
    }

    String keystoreName = "keystore.jks";

    if (args.length > 1) {
      keystoreName = args[1];
    }

    String keyStorePassword = "changeit";

    APIClient.call(restUrl, keystoreName, keyStorePassword);
  }
}
