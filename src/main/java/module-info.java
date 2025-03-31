module com.torquetitans {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires java.naming;

    opens com.torquetitans to javafx.fxml;
    exports com.torquetitans;
}
