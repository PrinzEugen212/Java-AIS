package main.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.Main;
import models.Employee;

import java.io.IOException;

/**
 * Контроллер для основной формы приложения
 */
public class MainController {
    public Employee employee;
    private FXMLLoader loader;
    private CardsController controller;
    public HBox hbMainPanel;
    private Parent pane;
    public void onCards(ActionEvent actionEvent) {
        try {
            loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("cards-view.fxml"));
            pane = loader.load();
            controller = loader.getController();
            controller.employee = employee;
            controller.initialize();
        } catch (IOException ignored) {

        }
        hbMainPanel.getChildren().clear();
        hbMainPanel.getChildren().add(pane);
    }

    public void onAddProcedure(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("add/addProcedure-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onAnimal(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("add/addAnimal-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
            stage.showAndWait();
            controller.onCancelSort(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClient(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("add/addClient-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
            stage.showAndWait();
            controller.onCancelSort(null);
            controller.updateClients();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onEmployee(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("add/addEmployee-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDBView(ActionEvent actionEvent) {
        try {
            loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("dataBase-view.fxml"));
            pane = loader.load();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
        hbMainPanel.getChildren().clear();
        hbMainPanel.getChildren().add(pane);

    }

    public void onReport(ActionEvent actionEvent) {
        try {
            loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("report-view.fxml"));
            pane = loader.load();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
        hbMainPanel.getChildren().clear();
        hbMainPanel.getChildren().add(pane);
    }
}