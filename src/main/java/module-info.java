module com.torquetitans {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;

    opens com.torquetitans to javafx.fxml;
    exports com.torquetitans;
}
