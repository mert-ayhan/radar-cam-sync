package com.github.mertayhan.radarcamsync.radar;

import com.github.mertayhan.radarcamsync.kafka.KafkaConsumerService;
import com.github.mertayhan.radarcamsync.kafka.KafkaProducerService;
import com.github.mertayhan.radarcamsync.kafka.MessageProcessor;

public class Main {

    private static final String TOPIC_TOWER_POSITION = "TowerPosition";
    private static final String TOPIC_TARGET_POINT_POSITION = "TargetPointPosition";
    private static final String TOPIC_TARGET_BEARING_POSITION = "TargetBearingPosition";
    private static final String KEY_RADAR_POSITION = "radarPosition";
    private static final String KEY_TARGET_BEARING_DATA = "targetBearingData";
    private static Position radarPosition;

    public static void main(String[] args) {
        KafkaConsumerService kafkaConsumerService = KafkaConsumerService.getInstance();
        KafkaProducerService kafkaProducerService = KafkaProducerService.getInstance();

        kafkaConsumerService.subscribe(TOPIC_TOWER_POSITION, TOPIC_TARGET_POINT_POSITION);

        kafkaConsumerService.consumeMessages(record -> {
            if (record.topic().equals(TOPIC_TOWER_POSITION) && record.key().equals(KEY_RADAR_POSITION)) {
                radarPosition = new Position(MessageProcessor.parsePositionFromString(record.value()));
            } else if (record.topic().equals(TOPIC_TARGET_POINT_POSITION)) {
                Position targetPosition = new Position(MessageProcessor.parsePositionFromString(record.value()));
                Viewpoint viewpoint = new Viewpoint(radarPosition.calculateAngleTo(targetPosition), radarPosition.calculateDistanceTo(targetPosition));
                kafkaProducerService.sendMessage(TOPIC_TARGET_BEARING_POSITION, KEY_TARGET_BEARING_DATA, viewpoint.toString());
            }
        });
    }
}