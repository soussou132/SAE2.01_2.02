module com.example.perso {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.desktop;

    opens com.example.perso to javafx.fxml;
    exports com.example.perso;
}