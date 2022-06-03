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
import java.util.*;
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
     *
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

        dateFormat = new SimpleDateFormat("HH:mm");
        tfTime.setText(dateFormat.format(visit.Date));

        procedures = DB.getProcedures();
        cbProcedures.setItems(FXCollections.observableArrayList(procedures.stream().map(c -> c.Name).toList()));

        employees = DB.getEmployees();
        employees = employees.stream().filter(e -> e.CanHelp).toList();
        cbHelper.setItems(FXCollections.observableArrayList(employees.stream().map(c -> c.Name).toList()));
        Employee helper = employees.stream().filter(e -> e.ID == visit.IDHelperEmployee).findAny().orElse(null);
        if(helper != null){
            cbHelper.getSelectionModel().select(helper.Name);
        }

        performedProcedures = DB.getProcedures(visit.ID);
        for(Procedure procedure : performedProcedures){
            lbProcedures.getItems().add(String.format("%s: %d", procedure.Name, procedure.Cost));
        }
        cost = performedProcedures.stream().map(p -> p.Cost).collect(Collectors.summingInt(Integer::intValue));
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
        if (mouseEvent.getClickCount() != 2){
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

    public void onChange(ActionEvent actionEvent) throws SQLException {
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
        visit.ID = ID;
        DB.changeVisit(visit, performedProcedures);
        onCancel(null);
    }
}
