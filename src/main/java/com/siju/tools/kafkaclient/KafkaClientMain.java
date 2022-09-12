package com.siju.tools.kafkaclient;

import static com.siju.tools.kafkaclient.Configuration.GROUP_ID_RESOLVED;
import static com.siju.tools.kafkaclient.Configuration.STREAM_RESOLVED_NAME_STRING;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509ExtendedKeyManager;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaClientMain {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaClientMain.class);

  private static void keystoreWithKeyNotHavingPassword()
      throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException,
          CertificateException {
    String keyStorePassword = "changeit";
    String keyPassword =
        "changeit"; // if key does not have a password, then this should be same as keystore
                    // password
    KeyStore keyStore = KeyStore.getInstance("JKS");
    keyStore.load(
        new FileInputStream("keystore_no_key_password.jks"), keyStorePassword.toCharArray());
    Enumeration<String> aliases = keyStore.aliases();
    String alias = aliases.nextElement();
    Key key = keyStore.getKey(alias, keyPassword.toCharArray());
  }

  private static void keystoreWithKeyPassword()
      throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException,
          CertificateException {
    String keyStorePassword = "changeit";
    String keyPassword = "keypassword1"; // should be the key password
    KeyStore keyStore = KeyStore.getInstance("JKS");
    keyStore.load(
        new FileInputStream("keystore_with_key_password.jks"), keyStorePassword.toCharArray());
    Enumeration<String> aliases = keyStore.aliases();
    String alias = aliases.nextElement();
    Key key = keyStore.getKey(alias, keyPassword.toCharArray());
  }

  public static void loadtemp()
      throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException,
          UnrecoverableKeyException {
    SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
    KeyManager[] keyManagers = null;
    String kmfAlgorithm = "SunX509";
    KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfAlgorithm);
    KeyStore keystore = KeyStore.getInstance("JKS");
    keystore.load(new FileInputStream("keystore_no_key_password.jks"), "changeit".toCharArray());
    kmf.init(keystore, "changeit".toCharArray());
    keyManagers = kmf.getKeyManagers();
    Enumeration<String> aliases = keystore.aliases();
    String alias = aliases.nextElement();
    X509ExtendedKeyManager x509ExtendedKeyManager = (X509ExtendedKeyManager)keyManagers[0];
    System.out.println(x509ExtendedKeyManager.getPrivateKey(alias));

  }

  public static void main(String[] args)
      throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException,
          CertificateException {

    //loadtemp();
//    keystoreWithKeyNotHavingPassword();
    //    keystoreWithKeyPassword();

    // Prepare the Kafka Client configuration. Check the Util classes for multiple SSL setup
    // examples
    Map<String, Object> config = Util.getKafkaClientWithSSLConfig();

    // Prepare consumer-specific configuration
    config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_RESOLVED);
    // Deserialization config
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    // Offset management
    config.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(ENABLE_AUTO_COMMIT_CONFIG, "false");

    // Batching config
    config.put(MAX_POLL_RECORDS_CONFIG, 3);

    // Instantiate the Kafka consumer, for which we prepared the configuration at the beginning.
    // Using try-with-resources since it implements the closeable interface.
    // In production, you wouldn't use it only once, and not in the main thread:
    //     you would instead have it poll in a loop on a separate thread, until a trigger stops it,
    //     allowing the try-with-resources block to close it afterwards.
    LOG.info("Instantiating consumer with properties: {}", config);
    try (Consumer<String, String> consumer = new KafkaConsumer<>(config)) {
      LOG.info("Consumer instantiated successfully.");

      consumer.subscribe(Collections.singleton(STREAM_RESOLVED_NAME_STRING));

      for (int i = 0; i < 5; i++) {
        LOG.info("Consuming poll #" + i);
        ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(2));

        // Add custom processing for consumed events. In this example we just log them.
        LOG.info(
            "Consumed records: {}",
            StreamSupport.stream(consumerRecords.spliterator(), false)
                .map(ConsumerRecord::toString)
                .collect(Collectors.joining("\n\t", "\n\t", "\n")));

        LOG.info("Committing offsets");
        consumer.commitSync();
      }
      LOG.info("Closing consumer."); // The try-with-resources block will close it
    }
    LOG.info("Consumer closed.");
  }
}
