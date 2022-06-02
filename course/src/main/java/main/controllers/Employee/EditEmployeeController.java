package main.controllers.Employee;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.DB;
import models.Employee;

/**
 * Обработчик формы изменения работника.
 * Перед использованием необходимо установить работника через метод
 */
public class EditEmployeeController {
    private int ID;
    public TextField tfName;
    public TextField tfLogin;
    public TextField tfPassword;
    public TextField tfPhone;
    public TextField tfPost;
    public TextField tfSpeciality;
    public CheckBox cbHelper;
    public CheckBox cbAdmin;

    public void setEmployee(Employee employee){
        ID = employee.ID;
        tfName.setText(employee.Name);
        tfLogin.setText(employee.Login);
        tfPassword.setText(employee.Password);
        tfPhone.setText(employee.Phone);
        tfPost.setText(employee.Post);
        tfSpeciality.setText(employee.Speciality);
        cbHelper.setSelected(employee.CanHelp);
        cbAdmin.setSelected(employee.Admin);
    }

    public void onChange(ActionEvent actionEvent) {
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
        employee.ID = ID;
        DB.changeEmployee(employee);
        onCancel(null);
    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }
}
