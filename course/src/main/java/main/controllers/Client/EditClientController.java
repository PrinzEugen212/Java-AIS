package main.controllers.Client;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.DB;
import models.Client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Обработчик формы изменения клиента
 * Перед использованием необходимо установить клиента через метод
 */
public class EditClientController {
    private int ID;
    public TextField tfName;
    public TextField tfPhone;
    public DatePicker dpBirthDate;

    public void setClient(Client client){
        ID = client.ID;
        tfName.setText(client.FullName);
        tfPhone.setText(client.Phone);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String strDate = dateFormat.format(client.BirthDate);
        dpBirthDate.setValue(LocalDate.parse(strDate,formatter));
    }

    public void onChange(ActionEvent actionEvent) {
        String name = tfName.getText();
        String phone = tfPhone.getText();
        Date birthDate = java.sql.Date.valueOf(dpBirthDate.getValue());
        Client client = new Client(name, phone, birthDate);
        client.ID = ID;
        DB.changeClient(client);
        onCancel(null);
    }

    public void onCancel(ActionEvent actionEvent) {
        var stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }
}
