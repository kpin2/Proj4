module k.marchmadness {
    requires javafx.controls;
    requires javafx.fxml;


    opens k.marchmadness to javafx.fxml;
    exports k.marchmadness;
}