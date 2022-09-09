package com.siju.tools.kafkaclient;

import org.apache.kafka.clients.CommonClientConfigs;

import java.util.HashMap;
import java.util.Map;

import static com.siju.tools.kafkaclient.Configuration.BOOTSTRAP_SERVERS_SSL;

public class Util {

  /** Utility method used to fetch keystores from the disk */
  public static String getResourceFilePath(String resource) {
    return ClassLoader.getSystemClassLoader().getResource(resource).getFile();
  }


  public static Map<String, Object> getKafkaClientWithSSLConfig() {
    Map<String, Object> config = new HashMap<>();
    config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_SSL);

    // Check the KafkaSecurityUtil class for multiple examples of configuring SSL
    KafkaSecurityUtil.addSSLConfigUsingJKSStores(config);

    return config;
  }

}

