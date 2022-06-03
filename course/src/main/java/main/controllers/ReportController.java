package main.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import main.DB;
import models.Employee;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Контроллер для формы с отчётами
 */
public class ReportController implements Initializable {
    private boolean enabled = false;
    public CheckBox cbEmployee;
    public CheckBox cbDoctor;
    public CheckBox cbHelper;
    public DatePicker dpStart;
    public DatePicker dpEnd;
    public TableView tvTable;
    public Label lCount;
    public ComboBox cmbEmployee;

    public void onEmployee(ActionEvent actionEvent) {
        cmbEmployee.setDisable(enabled);
        cbDoctor.setDisable(enabled);
        cbHelper.setDisable(enabled);
        if (enabled) {
            cmbEmployee.getSelectionModel().select(-1);
            enabled = !enabled;
        } else {
            enabled = !enabled;
        }
    }

    public void onForm(ActionEvent actionEvent) throws SQLException {

        ResultSet set;
        Date start = new Date(Date.from(dpStart.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
        Date end = new Date(Date.from(dpEnd.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
        if (enabled) {
            if ((cbDoctor.isSelected() || cbHelper.isSelected()) == false) {
                new Alert(Alert.AlertType.ERROR, "Пожалуйста, выберите тип участия (врач и/или помощник").show();
                return;
            }
            int id = DB.getEmployees().stream()
                    .filter(e -> Objects.equals(e.Name, cmbEmployee.getSelectionModel().getSelectedItem()))
                    .map(e -> e.ID).findAny().orElse(0);
            set = DB.getEmployeeRep(id, cbDoctor.isSelected(), cbHelper.isSelected(), start, end);
        } else {
            set = DB.getEmployeeRep(0, false, false, start, end);
        }
        DisplaySet(set);
    }

    /**
     * Отображает переданный набор данных в таблицу на форме
     * Колонки генерируются на основе переданного набора
     */
    private void DisplaySet(ResultSet set) throws SQLException {
        tvTable.getColumns().clear();
        tvTable.getItems().clear();
        int count = set.getMetaData().getColumnCount();
        for (int i = 1; i <= count; i++) {
            TableColumn<List<StringProperty>, String> column = new TableColumn<>(set.getMetaData().getColumnLabel(i));
            int finalI = i;
            column.setCellValueFactory(data -> data.getValue().get(finalI - 1));
            tvTable.getColumns().add(column);
        }

        while (set.next()) {
            List<StringProperty> Row = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                Row.add(new SimpleStringProperty(set.getString(i)));
            }
            tvTable.getItems().add(Row);
        }
        lCount.setText(String.valueOf(tvTable.getItems().size()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Employee> employees = null;
        try {
            employees = DB.getEmployees();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        cmbEmployee.setItems(FXCollections.observableArrayList(employees.stream().map(c -> c.Name).toList()));
        lCount.setText("0");
        onEmployee(null);
        cbEmployee.setSelected(true);
    }
}
