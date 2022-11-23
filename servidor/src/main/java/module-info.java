module com.computo.servidor {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.computo.servidor to javafx.fxml;
    exports com.computo.servidor;
}