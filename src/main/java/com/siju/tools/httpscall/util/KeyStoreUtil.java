package com.siju.tools.httpscall.util;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.Base64;

public class KeyStoreUtil {

  public KeyStore getKeyStore(
      String keyVaultUrl, String certificateName) {
    KeyStore keyStore = null;
    try {

      String certificateData = new KeyVaultUtil().getSecret(keyVaultUrl, certificateName);
      try (ByteArrayInputStream inputStream =
          new ByteArrayInputStream(Base64.getDecoder().decode(certificateData))) {
        keyStore = KeyStore.getInstance(PKCS12);
        keyStore.load(inputStream, "".toCharArray());
      }
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
    return keyStore;
  }

  public static final String PKCS12 = "PKCS12";

  private byte[] base64Decode(String base64) {
    return Base64.getMimeDecoder().decode(base64.getBytes(US_ASCII));
  }

}
