package com.github.mertayhan.radarcamsync.world;

import com.github.mertayhan.radarcamsync.kafka.KafkaConsumerService;
import com.github.mertayhan.radarcamsync.kafka.KafkaProducerService;
import com.github.mertayhan.radarcamsync.kafka.MessageProcessor;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Random;

public class WorldController {

    private static final String CAMERA = "camera";
    private static final String AIRPLANE = "airplane";
    private static final String RADAR = "radar";
    private static final String TOPIC_TOWER_POSITION = "TowerPosition";
    private static final String TOPIC_TARGET_POINT_POSITION = "TargetPointPosition";
    private static final String TOPIC_CAMERA_LOS_STATUS = "CameraLosStatus";
    private static final String KEY_RADAR_POSITION = "radarPosition";
    private static final String KEY_CAMERA_POSITION = "cameraPosition";
    private static final String KEY_AIRPLANE_POSITION = "airplanePosition";

    private Timeline timeline;
    private final Random random = new Random();
    private static final double GRAPH_OFFSET = 400;
    private static final HashMap<String, Position> INITIAL_POSITIONS = new HashMap<>();
    private static Polygon targetViewpoint;
    private Thread kafkaConsumerThread;

    @FXML
    private AnchorPane pane;

    @FXML
    private ImageView airplane;

    @FXML
    private ImageView radar;

    @FXML
    private ImageView camera;

    static {
        INITIAL_POSITIONS.put(AIRPLANE, new Position(10.0, 200.0));
        INITIAL_POSITIONS.put(RADAR, new Position(0.0, 0.0));
        INITIAL_POSITIONS.put(CAMERA, new Position(350.0, 0.0));
    }

    @FXML
    private void initialize() {
        setInitialPositions();

        targetViewpoint = new Polygon();
        pane.getChildren().add(targetViewpoint);
        targetViewpoint.toBack();

        double angleRadians = Math.toRadians(INITIAL_POSITIONS.get(CAMERA).calculateAngleTo(INITIAL_POSITIONS.get(AIRPLANE)));
        double distance = INITIAL_POSITIONS.get(CAMERA).calculateDistanceTo(INITIAL_POSITIONS.get(AIRPLANE));

        updateTriangleAndCamRot(angleRadians, distance);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            airplane.setLayoutX(airplane.getLayoutX() + random.nextInt(5, 10));
            airplane.setLayoutY(airplane.getLayoutY() + random.nextInt(-5, 5));

            sendTargetPositionToKafka();

            if (airplane.getLayoutX() >= 800) {
                timeline.stop();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    @FXML
    private void handlePlay() {
        timeline.play();

        if (kafkaConsumerThread == null) {
            KafkaConsumerService kafkaConsumerService = KafkaConsumerService.getInstance();
            kafkaConsumerService.subscribe(TOPIC_CAMERA_LOS_STATUS);

            kafkaConsumerThread = new Thread(() ->
                    kafkaConsumerService.consumeMessages(record -> {
                        Viewpoint viewpoint = new Viewpoint(MessageProcessor.parsePositionFromString(record.value()));
                        Platform.runLater(() -> updateTriangleAndCamRot(Math.toRadians(viewpoint.getAngle()), viewpoint.getDistance()));
                    })
            );

            kafkaConsumerThread.setDaemon(true);
            kafkaConsumerThread.start();
        }
    }

    @FXML
    private void handleStop() {
        timeline.stop();
    }

    private void setInitialPositions() {
        setLayoutPosition(airplane, INITIAL_POSITIONS.get(AIRPLANE));
        setLayoutPosition(radar, INITIAL_POSITIONS.get(RADAR));
        setLayoutPosition(camera, INITIAL_POSITIONS.get(CAMERA));

        sendTowerPositionsToKafka();
    }

    private void setLayoutPosition(ImageView imageView, Position position) {
        imageView.setLayoutX(position.getX() + GRAPH_OFFSET - imageView.getFitWidth() / 2);
        imageView.setLayoutY(GRAPH_OFFSET - position.getY() - imageView.getFitHeight() / 2);
    }

    private Position calculatePositionFromLayout(ImageView imageView) {
        return new Position(imageView.getLayoutX() - GRAPH_OFFSET + imageView.getFitWidth() / 2,
                -1 * (imageView.getLayoutY() - GRAPH_OFFSET + imageView.getFitHeight() / 2));
    }

    private void sendTowerPositionsToKafka() {
        sendKafkaMessage(TOPIC_TOWER_POSITION, KEY_RADAR_POSITION, INITIAL_POSITIONS.get(RADAR).toString());
        sendKafkaMessage(TOPIC_TOWER_POSITION, KEY_CAMERA_POSITION, INITIAL_POSITIONS.get(CAMERA).toString());
    }

    private void sendTargetPositionToKafka() {
        sendKafkaMessage(TOPIC_TARGET_POINT_POSITION, KEY_AIRPLANE_POSITION, calculatePositionFromLayout(airplane).toString());
    }

    private void sendKafkaMessage(String topic, String key, String message) {
        try {
            KafkaProducerService.getInstance().sendMessage(topic, key, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTriangleAndCamRot(double angle, double distance) {
        angle = -angle;

        double x_c = camera.getLayoutX() + camera.getFitWidth() / 2;
        double y_c = camera.getLayoutY() + camera.getFitHeight() / 2;

        double baseHalf = airplane.getFitWidth() / 2;

        double angleLeft = angle + Math.PI / 2;
        double angleRight = angle - Math.PI / 2;

        double leftCornerX = x_c + distance * Math.cos(angle) + baseHalf * Math.cos(angleLeft);
        double leftCornerY = y_c + distance * Math.sin(angle) + baseHalf * Math.sin(angleLeft);

        double rightCornerX = x_c + distance * Math.cos(angle) + baseHalf * Math.cos(angleRight);
        double rightCornerY = y_c + distance * Math.sin(angle) + baseHalf * Math.sin(angleRight);

        targetViewpoint.getPoints().clear();
        targetViewpoint.getPoints().addAll(x_c, y_c,
                leftCornerX, leftCornerY,
                rightCornerX, rightCornerY);

        targetViewpoint.setStroke(Color.BLUE);
        targetViewpoint.setFill(Color.LIGHTBLUE);

        camera.setRotate(Math.toDegrees(angle));
    }

}