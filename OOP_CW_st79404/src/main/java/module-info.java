module com.example.oop_cw_st79404 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.oop_cw_st79404 to javafx.fxml;
    exports com.example.oop_cw_st79404;
}