package com.github.mertayhan.radarcamsync.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class KafkaConsumerService {

    private static KafkaConsumerService instance;
    private final KafkaConsumer<String, String> consumer;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    private KafkaConsumerService() {
        Properties kafkaProperties = KafkaConfig.loadKafkaProperties();
        this.consumer = new KafkaConsumer<>(kafkaProperties);

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    public static synchronized KafkaConsumerService getInstance() {
        if (instance == null) {
            instance = new KafkaConsumerService();
        }
        return instance;
    }

    public void subscribe(String... topics) {
        this.consumer.subscribe(Arrays.asList(topics));
    }

    public void consumeMessages(Consumer<ConsumerRecord<String, String>> messageProcessor) {
        try {
            while (!closed.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    messageProcessor.accept(record);
                }
            }
        } finally {
            consumer.close();
        }
    }

    public void close() {
        closed.set(true);
    }
}