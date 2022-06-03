package main.controllers.Visit;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.DB;
import models.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Обработчик формы добавления приёма
 */
public class AddVisitController {
    public ImageView ivPhoto;
    private int cost = 0;
    private List<Procedure> performedProcedures = new ArrayList<>();
    private List<Procedure> procedures;
    private List<Employee> employees;
    public ComboBox cbProcedures;
    private Animal animal;
    private Employee employee;
    private Client client;
    public DatePicker dpDate;
    public TextField tfTime;
    public TextField tfClient;
    public TextField tfAnimal;
    public ComboBox cbHelper;
    public TextField tfEmployee;
    public TextArea taDiagnosis;
    public TextArea taAssignment;
    public ListView lbProcedures;
    public Label lCost;

    /**
     * Начальная инициализация формы
     *
     * @param employee работник, авторизованный в системе
     * @param animal   питомец, для которого записывается приём
     */
    public void initialize(Employee employee, Animal animal) throws SQLException, IOException {

        this.animal = animal;
        this.employee = employee;
        tfAnimal.setText(animal.Name);
        this.client = DB.getClient(animal.ClientID);
        tfClient.setText(client.FullName);
        tfEmployee.setText(employee.Name);

        if (animal.Photo != null) {
            String photoPath = new File(".").getCanonicalPath() + animal.Photo;
            ivPhoto.setImage(new Image(photoPath));
        }

        procedures = DB.getProcedures();
        cbProcedures.setItems(FXCollections.observableArrayList(procedures.stream().map(c -> c.Name).toList()));

        employees = DB.getEmployees();
        employees = employees.stream().filter(e -> e.CanHelp).toList();
        cbHelper.setItems(FXCollections.observableArrayList(employees.stream().map(c -> c.Name).toList()));
    }

    public void onAdd(ActionEvent actionEvent) throws SQLException, ParseException {
        if (taAssignment.getText().length() == 0 || taDiagnosis.getText().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Заполните все необходимые поля").show();
            return;
        }
        String helper = (String) cbHelper.getSelectionModel().getSelectedItem();
        Employee helperEmployee = employees.stream().filter(e -> Objects.equals(e.Name, helper)).findAny().orElse(null);
        int helperID = 0;
        if (helperEmployee != null) {
            helperID = helperEmployee.ID;
        }
        Date date = null;
        try {
            date = java.sql.Date.valueOf(dpDate.getValue());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String[] time = tfTime.getText().split(":");
            cal.set(Calendar.HOUR, Integer.parseInt(time[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
            cal.set(Calendar.SECOND, 0);
            date = cal.getTime();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Ошибка при обработке даты или времени").show();
            return;
        }
        Visit visit = new Visit(
                animal.ID,
                employee.ID,
                helperID,
                client.ID,
                date,
                taDiagnosis.getText(),
                taAssignment.getText(),
                cost
        );
        DB.addVisit(visit, performedProcedures);
        onCancel(null);
    }

    /**
     * При выборе процедуры в выпадающем списке добавляет её в список проведённых процедур
     */
    public void onProcedures(ActionEvent actionEvent) {
        String procedureName = (String) cbProcedures.getSelectionModel().getSelectedItem();
        if (procedureName == null) {
            return;
        }
        Procedure procedure = procedures.stream().filter(p -> Objects.equals(p.Name, procedureName)).findAny().orElse(null);
        if (lbProcedures.getItems().contains(String.format("%s: %d", procedure.Name, procedure.Cost)) == false) {
            performedProcedures.add(procedure);
            cost += procedure.Cost;
            lCost.setText(String.valueOf(cost));

            lbProcedures.getItems().add(String.format("%s: %d", procedure.Name, procedure.Cost));
        }
    }

    /**
     * При нажатии на процедуру в списке проведённых процедур убирает её
     */
    public void onLVMouseClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() != 2) {
            return;
        }
        String procedureName = (String) lbProcedures.getSelectionModel().getSelectedItem().toString().split(":")[0];
        Procedure procedure = performedProcedures.stream().filter(p -> Objects.equals(p.Name, procedureName)).findAny().orElse(null);
        performedProcedures.remove(procedure);
        cost -= procedure.Cost;
        lCost.setText(String.valueOf(cost));
        lbProcedures.getItems().remove(String.format("%s: %d", procedure.Name, procedure.Cost));
        cbProcedures.getSelectionModel().select(-1);
    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) tfEmployee.getScene().getWindow();
        stage.close();
    }
}
