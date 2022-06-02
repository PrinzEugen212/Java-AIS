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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Обработчик формы изменения приёма
 * Перед использованием необходимо установить приём через метод
 */
public class EditVisitController {
    private int ID = 0;
    private int cost = 0;
    private List<Procedure> performedProcedures = new ArrayList<>();
    private List<Procedure> procedures;
    private List<Employee> employees;
    private Animal animal;
    private Employee employee;
    private Client client;
    public ImageView ivPhoto;
    public DatePicker dpDate;
    public TextField tfTime;
    public TextField tfClient;
    public TextField tfAnimal;
    public ComboBox cbHelper;
    public TextField tfEmployee;
    public ComboBox cbProcedures;
    public TextArea taDiagnosis;
    public TextArea taAssignment;
    public ListView lbProcedures;
    public Label lCost;
    /**
     * Начальная инициализация формы
     * @param visit приём который нужно изменить
     */
    public void initialize(Visit visit) throws SQLException, IOException {
        this.ID = visit.ID;
        this.animal = DB.getAnimal(visit.IDAnimal);
        this.employee = DB.getEmployee(visit.IDEmployee);
        tfAnimal.setText(animal.Name);
        this.client = DB.getClient(animal.ClientID);
        tfClient.setText(client.FullName);
        tfEmployee.setText(employee.Name);
        taAssignment.setText(visit.Assignment);
        taDiagnosis.setText(visit.Diagnosis);

        ivPhoto.setImage(new Image(new File(".").getCanonicalPath() + animal.Photo));

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String strDate = dateFormat.format(visit.Date);
        dpDate.setValue(LocalDate.parse(strDate, formatter));

        procedures = DB.getProcedures();
        cbProcedures.setItems(FXCollections.observableArrayList(procedures.stream().map(c -> c.Name).toList()));

        employees = DB.getEmployees();
        employees = employees.stream().filter(e -> e.CanHelp).toList();
        cbHelper.setItems(FXCollections.observableArrayList(employees.stream().map(c -> c.Name).toList()));
        Employee helper = employees.stream().filter(e -> e.ID == visit.IDHelperEmployee).findAny().orElse(null);
        cbHelper.getSelectionModel().select(helper.Name);

        performedProcedures = DB.getProcedures(visit.ID);
        lbProcedures.setItems(FXCollections.observableArrayList(performedProcedures.stream().map(p -> p.Name).toList()));
        cost = performedProcedures.stream().map(p->p.Cost).collect(Collectors.summingInt(Integer::intValue));
        lCost.setText(String.valueOf(cost));
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

    public void onHistory(ActionEvent actionEvent) {
    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) tfEmployee.getScene().getWindow();
        stage.close();
    }

    public void onChange(ActionEvent actionEvent) throws SQLException {
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
        DB.changeVisit(visit, performedProcedures);
        onCancel(null);
    }
}
