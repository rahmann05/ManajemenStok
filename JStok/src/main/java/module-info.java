module com.jejekatering.jstok {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires java.sql;
    requires mysql.connector.j;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    requires atlantafx.base;
    requires org.controlsfx.controls;
    requires AnimateFX;
    requires eu.hansolo.tilesfx;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;

    opens com.jejekatering.jstok to javafx.fxml;
    opens com.jejekatering.jstok.controller to javafx.fxml;
    exports com.jejekatering.jstok;
}