module com.computo.cliente {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.computo.cliente to javafx.fxml;
    exports com.computo.cliente;
}