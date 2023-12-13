package com.github.mertayhan.radarcamsync.kafka;

import java.io.InputStream;
import java.util.Properties;

public class KafkaConfig {

    public static Properties loadKafkaProperties() {
        Properties properties = loadPropertiesFromFile("application.properties");
        Properties kafkaProperties = new Properties();

        setPropertyFromEnvOrFile(kafkaProperties, properties, "bootstrap.servers", "KAFKA_BOOTSTRAP_SERVERS");
        setPropertyFromEnvOrFile(kafkaProperties, properties, "key.serializer", "KAFKA_KEY_SERIALIZER");
        setPropertyFromEnvOrFile(kafkaProperties, properties, "value.serializer", "KAFKA_VALUE_SERIALIZER");
        setPropertyFromEnvOrFile(kafkaProperties, properties, "key.deserializer", "KAFKA_KEY_DESERIALIZER");
        setPropertyFromEnvOrFile(kafkaProperties, properties, "value.deserializer", "KAFKA_VALUE_DESERIALIZER");
        setPropertyFromEnvOrFile(kafkaProperties, properties, "group.id", "KAFKA_GROUP_ID");
        setPropertyFromEnvOrFile(kafkaProperties, properties, "auto.offset.reset", "KAFKA_AUTO_OFFSET_RESET");

        return kafkaProperties;
    }

    private static Properties loadPropertiesFromFile(String fileName) {
        Properties properties = new Properties();
        try (InputStream input = KafkaConfig.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return properties;
    }

    private static void setPropertyFromEnvOrFile(Properties target, Properties source, String sourceKey, String envKey) {
        String value = System.getenv(envKey);

        if (value == null) {
            value = source.getProperty("kafka." + sourceKey);
        }

        if (value != null) {
            target.setProperty(sourceKey, value);
        }
    }
}
