module com.github.mertayhan.world {
    requires javafx.controls;
    requires javafx.fxml;
    requires kafka;
    requires kafka.clients;

    opens com.github.mertayhan.radarcamsync.world to javafx.fxml;
    exports com.github.mertayhan.radarcamsync.world;
}