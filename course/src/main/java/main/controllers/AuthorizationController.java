package main.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import main.DB;
import main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Employee;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Контроллер для формы авторизации
 */
public class AuthorizationController {
    public TextField tfLogin;
    public PasswordField tfPassword;

    public void onEnter(ActionEvent actionEvent) throws SQLException {
        Employee employee = DB.getEmployee(tfLogin.getText());
        if (employee == null || employee.Password.equals(tfPassword.getText()) == false) {
            new Alert(Alert.AlertType.ERROR, "Пользователь с введёнными данными не существует").show();
            return;
        }
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("hello-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 900, 600));
            stage.setTitle("АИС Ветеринарной клиники");
            stage.setResizable(false);
            MainController helloController = fxmlLoader.getController();
            helloController.employee = employee;
            helloController.onCards(null);
            stage.show();
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
