module fise2.image4.projetinfo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.bytedeco.opencv;

    opens fise2.image4.projetinfo to javafx.fxml;
    exports fise2.image4.projetinfo;
}