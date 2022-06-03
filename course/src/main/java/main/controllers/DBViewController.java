package main.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.DB;
import main.Main;
import main.controllers.Animal.EditAnimalController;
import main.controllers.Client.EditClientController;
import main.controllers.Employee.EditEmployeeController;
import main.controllers.Procedure.EditProcedureController;
import main.controllers.Visit.EditVisitController;
import models.*;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
/**
 * Контроллер для формы обозревателя БД
 */
public class DBViewController implements Initializable {
    private final List<Integer> idList = new ArrayList<>();
    public Model model;
    public TableView<List<StringProperty>> tTable;
    public Button bEmp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            onAnimals(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setEmployee(Employee employee){
        if (employee.Admin == false){
            bEmp.setDisable(true);
            bEmp.setVisible(false);
        }
    }
    public void onClients(ActionEvent actionEvent) throws SQLException {
        ResultSet set = DB.getTable("Clients");
        DisplaySet(set);
        model = Model.Client;
    }

    public void onAnimals(ActionEvent actionEvent) throws SQLException, ParseException {
        var set = DB.getTable("Animals");
        DisplaySet(set);
        model = Model.Animal;
    }

    public void onVisits(ActionEvent actionEvent) throws SQLException {
        ResultSet set = DB.getTable("Visits");
        DisplaySet(set);
        model = Model.Visit;
    }

    public void onEmployees(ActionEvent actionEvent) throws SQLException {
        ResultSet set = DB.getTable("Employees");
        DisplaySet(set);
        model = Model.Employee;
    }

    public void onProcedures(ActionEvent actionEvent) throws SQLException {
        ResultSet set = DB.getTable("ProceduresList");
        DisplaySet(set);
        model = Model.Procedure;
    }

    /**
     * Отображает переданный набор данных в таблицу на форме
     * Колонки генерируются на основе переданного набора
     */
    private void DisplaySet(ResultSet set) throws SQLException {
        idList.clear();
        tTable.getColumns().clear();
        tTable.getItems().clear();
        int count = set.getMetaData().getColumnCount();
        for (int i = 2; i < count + 1; i++) {
            TableColumn<List<StringProperty>, String> column = new TableColumn<>(set.getMetaData().getColumnLabel(i));
            int finalI = i;
            column.setCellValueFactory(data -> data.getValue().get(finalI - 1));
            tTable.getColumns().add(column);
        }

        while (set.next()) {
            List<StringProperty> Row = new ArrayList<>();
            idList.add(Integer.valueOf(set.getString("ID")));
            for (int i = 1; i <= count; i++) {
                Row.add(new SimpleStringProperty(set.getString(i)));
            }
            tTable.getItems().add(Row);
        }
    }

    /**
     * Обработчик клика по таблице
     * Реагирует только на двойной клик
     */
    public void onClick(MouseEvent mouseEvent) throws SQLException, ParseException {
        if (mouseEvent.getClickCount() == 2) {
            TablePosition pos = tTable.getSelectionModel().getSelectedCells().get(0);
            int row = pos.getRow();
            int ID = idList.get(row);
            switch (model) {
                case Animal -> {
                    showAnimal(ID);
                    onAnimals(null);
                }
                case Visit -> {
                    showVisit(ID);
                    onVisits(null);
                }
                case Client -> {
                    showClient(ID);
                    onClients(null);
                }
                case Employee -> {
                    showEmployee(ID);
                    onEmployees(null);
                }
                case Procedure -> {
                    showProcedure(ID);
                    onProcedures(null);
                }
            }
        }

    }

    /**
     * Загружает окно изменения питомца
     * @param id Идентификатор модели, для которой нужно вызвать окно изменения
     */
    public void showAnimal(int id) throws SQLException {
        Animal animal = DB.getAnimal(id);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("edit/editAnimal-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 800, 400));
            stage.setTitle("Изменение питомица");
            stage.setResizable(false);
            EditAnimalController controller = fxmlLoader.getController();
            controller.setAnimal(animal);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Загружает окно изменения клиента
     * @param id Идентификатор модели, для которой нужно вызвать окно изменения
     */
    public void showClient(int id) throws SQLException {
        Client client = DB.getClient(id);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("edit/editClient-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 600, 200));
            stage.setTitle("Изменение клиента");
            stage.setResizable(false);
            EditClientController controller = fxmlLoader.getController();
            controller.setClient(client);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Загружает окно изменения работника
     * @param id Идентификатор модели, для которой нужно вызвать окно изменения
     */
    public void showEmployee(int id) throws SQLException {
        Employee employee = DB.getEmployee(id);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("edit/editEmployee-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 700, 450));
            stage.setTitle("Изменение сотрудника");
            stage.setResizable(false);
            EditEmployeeController controller = fxmlLoader.getController();
            controller.setEmployee(employee);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Загружает окно изменения приёма
     * @param id Идентификатор модели, для которой нужно вызвать окно изменения
     */
    public void showVisit(int id) throws SQLException {
        Visit visit = DB.getVisit(id);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("edit/editVisit-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 800, 640));
            EditVisitController controller = fxmlLoader.getController();
            controller.initialize(DB.getVisit(id));

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Загружает окно изменения процедуры
     * @param id Идентификатор модели, для которой нужно вызвать окно изменения
     */
    public void showProcedure(int id) throws SQLException {
        Procedure procedure = DB.getProcedure(id);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("edit/editProcedure-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 400, 200));
            stage.setTitle("Изменение процедуры");
            stage.setResizable(false);
            EditProcedureController controller = fxmlLoader.getController();
            controller.setProcedure(procedure);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
