package com.github.mertayhan.radarcamsync.camera;

import com.github.mertayhan.radarcamsync.kafka.KafkaConsumerService;
import com.github.mertayhan.radarcamsync.kafka.KafkaProducerService;
import com.github.mertayhan.radarcamsync.kafka.MessageProcessor;

public class Main {

    private static final String TOPIC_TOWER_POSITION = "TowerPosition";
    private static final String TOPIC_CAMERA_LOS_STATUS = "CameraLosStatus";
    private static final String TOPIC_TARGET_BEARING_POSITION = "TargetBearingPosition";
    private static final String KEY_RADAR_POSITION = "radarPosition";
    private static final String KEY_CAMERA_POSITION = "cameraPosition";
    private static final String KEY_CAMERA_LOS_STATUS_DATA = "cameraLosStatusData";
    private static Position cameraPosition;
    private static Position radarPosition;

    public static void main(String[] args) {
        KafkaConsumerService kafkaConsumerService = KafkaConsumerService.getInstance();
        KafkaProducerService kafkaProducerService = KafkaProducerService.getInstance();

        kafkaConsumerService.subscribe(TOPIC_TOWER_POSITION, TOPIC_TARGET_BEARING_POSITION);

        kafkaConsumerService.consumeMessages(record -> {
            if (record.topic().equals(TOPIC_TOWER_POSITION)) {
                if (record.key().equals(KEY_CAMERA_POSITION)) {
                    cameraPosition = new Position(MessageProcessor.parsePositionFromString(record.value()));
                } else if (record.key().equals(KEY_RADAR_POSITION)) {
                    radarPosition = new Position(MessageProcessor.parsePositionFromString(record.value()));
                }
            } else if (record.topic().equals(TOPIC_TARGET_BEARING_POSITION)) {
                Viewpoint targetViewpoint = new Viewpoint(MessageProcessor.parsePositionFromString(record.value()));
                Position targetPosition = radarPosition.findPositionFromViewpoint(targetViewpoint);
                Viewpoint cameraViewpoint = new Viewpoint(cameraPosition.calculateAngleTo(targetPosition), cameraPosition.calculateDistanceTo(targetPosition));
                kafkaProducerService.sendMessage(TOPIC_CAMERA_LOS_STATUS, KEY_CAMERA_LOS_STATUS_DATA, cameraViewpoint.toString());
            }
        });
    }
}