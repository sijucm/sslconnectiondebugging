package com.siju.tools.httpscall;

import com.siju.tools.httpscall.util.SSLUtils;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.UUID;
import javax.net.ssl.SSLContext;

public class APIClient {

  public static HttpResponse<String> call(
      String restUrl, String keystoreName, String keyStorePassword)
      throws IOException, InterruptedException, UnrecoverableKeyException, NoSuchAlgorithmException,
          KeyStoreException, KeyManagementException, CertificateException {

    final SSLContext sslContext = SSLUtils.getSslContext(keystoreName, keyStorePassword);
    HttpClient httpClient =
        HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .sslContext(sslContext)
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    HttpRequest httpRequest =
        HttpRequest.newBuilder()
            .GET()
            .setHeader("Content-Type", "application/xml")
            .header("Trace-Id", String.valueOf(UUID.randomUUID()))
            .uri(URI.create(restUrl))
            .build();

    HttpResponse<String> httpResponse =
        httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

    System.out.println(httpResponse);

    return httpResponse;
  }
}
