module com.computo.nodo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.computo.nodo to javafx.fxml;
    exports com.computo.nodo;
}