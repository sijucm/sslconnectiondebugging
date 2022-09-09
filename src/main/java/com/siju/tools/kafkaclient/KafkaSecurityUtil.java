package com.siju.tools.kafkaclient;

import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_KEY_PASSWORD_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG;

import java.util.Map;
import org.apache.kafka.common.security.auth.SecurityProtocol;

public class KafkaSecurityUtil {

  public static final String KEY_MATERIAL_PASSWORD = "changeit";
  private static final String KEYSTORE_TYPE_PEM = "PEM";
  private static final String KEYSTORE_TYPE_PKCS12 = "PKCS12";
  private static final String JAAS_TEMPLATE = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
  private static Map<String, Object> config;

  // Option 1: using JKS type keystore & truststore
  public static Map<String, Object> addSSLConfigUsingJKSStores(Map<String, Object> config) {
    KafkaSecurityUtil.config = config;
    config.put(SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SSL.name);
    config.put(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
    config.put(SSL_KEYSTORE_LOCATION_CONFIG, getResourceFilePath("keystore.jks"));
    config.put(SSL_KEYSTORE_PASSWORD_CONFIG, "changeit");
    config.put(SSL_KEY_PASSWORD_CONFIG, "changeit");
    config.put(SSL_TRUSTSTORE_LOCATION_CONFIG,getResourceFilePath("truststore.jks"));
    config.put(SSL_TRUSTSTORE_PASSWORD_CONFIG, KEY_MATERIAL_PASSWORD);
    return config;
  }

  public static String getResourceFilePath(String resource) {
    return ClassLoader.getSystemClassLoader().getResource(resource).getFile();
  }

}
