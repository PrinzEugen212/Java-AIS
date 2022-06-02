package main.controllers.Employee;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.DB;
import models.Employee;

import java.sql.SQLException;

/**
 * Обработчик формы добавления работника
 */
public class AddEmployeeController {
    public TextField tfName;
    public TextField tfLogin;
    public TextField tfPassword;
    public TextField tfPhone;
    public TextField tfPost;
    public TextField tfSpeciality;
    public CheckBox cbHelper;
    public CheckBox cbAdmin;

    public void onAdd(ActionEvent actionEvent) throws SQLException {
        if (tfName.getText().length() == 0 || tfPost.getText().length() == 0){
            new Alert(Alert.AlertType.ERROR, "Заполните все необходимые поля").show();
            return;
        }
        Employee employee = new Employee(
                tfName.getText(),
                tfLogin.getText(),
                tfPassword.getText(),
                tfPhone.getText(),
                tfPost.getText(),
                tfSpeciality.getText(),
                cbAdmin.isSelected(),
                cbHelper.isSelected()
        );
        var existing = DB.getEmployee(employee.Login);
        if (existing != null){
            new Alert(Alert.AlertType.ERROR, "Данный логин уже занят").show();
            return;
        }
        DB.addEmployee(employee);
        onCancel(null);
    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }
}
