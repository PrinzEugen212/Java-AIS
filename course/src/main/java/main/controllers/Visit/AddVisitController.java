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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * @param employee работник, авторизованный в системе
     * @param animal питомец, для которого записывается приём
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

    public void onHistory(ActionEvent actionEvent) {
    }

    public void onAdd(ActionEvent actionEvent) throws SQLException {
        String helper = (String) cbHelper.getSelectionModel().getSelectedItem();
        Employee helperEmployee = employees.stream().filter(e -> e.Name == helper).findAny().orElse(null);
        Visit visit = new Visit(
                animal.ID,
                employee.ID,
                helperEmployee.ID,
                client.ID,
                java.sql.Date.valueOf(dpDate.getValue()),
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
        if(procedureName == null){
            return;
        }
        Procedure procedure = procedures.stream().filter(p -> Objects.equals(p.Name, procedureName)).findAny().orElse(null);
        performedProcedures.add(procedure);
        cost += procedure.Cost;
        lCost.setText(String.valueOf(cost));
        lbProcedures.setItems(FXCollections.observableArrayList(performedProcedures.stream().map(p -> p.Name).toList()));
    }

    /**
     * При нажатии на процедуру в списке проведённых процедур убирает её
     */
    public void onLVMouseClick(MouseEvent mouseEvent) {
        String procedureName = (String) cbProcedures.getSelectionModel().getSelectedItem();
        Procedure procedure = procedures.stream().filter(p -> Objects.equals(p.Name, procedureName)).findAny().orElse(null);
        performedProcedures.remove(procedure);
        cost -= procedure.Cost;
        lCost.setText(String.valueOf(cost));
        lbProcedures.setItems(FXCollections.observableArrayList(performedProcedures.stream().map(p -> p.Name).toList()));
        cbProcedures.getSelectionModel().select(-1);
    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) tfEmployee.getScene().getWindow();
        stage.close();
    }
}
