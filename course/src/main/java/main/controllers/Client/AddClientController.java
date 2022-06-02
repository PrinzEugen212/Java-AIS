package main.controllers.Client;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.DB;
import models.Client;

import java.util.Date;

/**
 * Обработчик формы добавления клиента
 */
public class AddClientController {
    public TextField tfName;
    public TextField tfPhone;
    public DatePicker dpBirthDate;

    public void onAdd(ActionEvent actionEvent) {
        if (tfName.getText().length() == 0){
            new Alert(Alert.AlertType.ERROR, "Заполните все необходимые поля").show();
            return;
        }
        String name = tfName.getText();
        String phone = tfPhone.getText();
        Date birthDate = java.sql.Date.valueOf(dpBirthDate.getValue());
        Client client = new Client(name, phone, birthDate);
        DB.addClient(client);
        onCancel(null);
    }

    public void onCancel(ActionEvent actionEvent) {
        var stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }
}
