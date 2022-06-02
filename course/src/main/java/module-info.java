module com.example.course {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;


    exports main.controllers;
    opens main.controllers to javafx.fxml;
    exports main;
    opens main to javafx.fxml;
    exports main.controllers.Animal;
    opens main.controllers.Animal;
    exports main.controllers.Client;
    opens main.controllers.Client;
    exports main.controllers.Employee;
    opens main.controllers.Employee;
    exports main.controllers.Procedure;
    opens main.controllers.Procedure;
    exports main.controllers.Visit;
    opens main.controllers.Visit;
}