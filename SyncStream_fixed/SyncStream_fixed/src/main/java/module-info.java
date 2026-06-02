module com.syncstream {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;

    opens com.syncstream.gui to javafx.fxml;
    exports com.syncstream.gui;
    exports com.syncstream.server;
    exports com.syncstream.client;
    exports com.syncstream.api;
    exports com.syncstream.model;
    exports com.syncstream.dao;
    exports com.syncstream.db;
    exports com.syncstream.util;
}
