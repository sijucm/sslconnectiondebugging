package com.siju.tools.kafkaclient;

public abstract class Configuration {

  public static final String TENANT = "axual";
  public static final String INSTANCE = "local";
  public static final String ENVIRONMENT = "default";

  // SASL credentials which have been generated in Self-Service
  public static final String SASL_USERNAME = "username";
  public static final String SASL_PASSWORD = "password";

  // Application configuration
  public static final String APPLICATION_ID = "-poc-test";
  public static final String GROUP_ID_RESOLVED = "app-t";

  public static final String APPLICATION_VERSION = "1.0.0";
  public static final String APPLICATION_NAME = "Axual Client Examples";
  public static final String APPLICATION_OWNER = "default";

  public static final String STREAM_NAME_STRING = "-eventstore";
  public static final String STREAM_RESOLVED_NAME_STRING = "cdc-eventstore";

  public static final String STREAM_NAME_AVRO= "applicationlogevents";
  public static final String STREAM_RESOLVED_NAME_AVRO = TENANT + '-' + INSTANCE + '-' + ENVIRONMENT + '-' + STREAM_NAME_AVRO;


  // Connectivity configuration - Axual Trial
  public static final String DISCOVERY_SAASTRIAL = "https://" + TENANT + "-" + INSTANCE + "-discoveryapi.axual.cloud";
  public static final String SCHEMA_REGISTRY_SAASTRIAL = "https://" + TENANT + "-" + INSTANCE + "-schemas.axual.cloud";
  public static final String BOOTSTRAP_SERVERS_SSL_SAASTRIAL = "server:9092";
  public static final String BOOTSTRAP_SERVERS_SASL_SAASTRIAL = "server.com:9093";

  // Connectivity configuration - Local platform deployment
  public static final String DISCOVERY_HELM = "https://platform.local:29000";
  public static final String SCHEMA_REGISTRY_HELM = "https://platform.local:25000";

  // Update constants values to switch between target platform: Axual Trial or local development deployment
  public static final String DISCOVERY_ENDPOINT = DISCOVERY_SAASTRIAL;
  public static final String SCHEMA_REGISTRY = SCHEMA_REGISTRY_SAASTRIAL;
  public static final String BOOTSTRAP_SERVERS_SSL = BOOTSTRAP_SERVERS_SSL_SAASTRIAL;
  public static final String BOOTSTRAP_SERVERS_SASL = BOOTSTRAP_SERVERS_SASL_SAASTRIAL;

}
