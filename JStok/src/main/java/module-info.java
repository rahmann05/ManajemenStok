module com.jejekatering.jstok {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    requires atlantafx.base;
    requires com.jfoenix;
    requires org.controlsfx.controls;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;

    opens com.jejekatering.jstok to javafx.fxml;
    opens com.jejekatering.jstok.controller to javafx.fxml;
    exports com.jejekatering.jstok;
}