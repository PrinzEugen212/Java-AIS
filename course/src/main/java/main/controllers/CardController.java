package main.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import main.DB;
import main.Main;
import main.controllers.Animal.EditAnimalController;
import main.controllers.Client.EditClientController;
import main.controllers.Visit.AddVisitController;
import models.Animal;
import models.Client;
import models.Employee;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Контроллер для карточек
 * Для корректной работы необходимо установить работника, контроллер списка карточек и питомца
 * @see CardsController
 */
public class CardController {
    private Employee employee;
    /**
     * Контроллер формы с карточками.
     * Необходим для обновления списка клиентов в случае изменения клиента
     */
    private CardsController controller;
    public Label lAnimalType;
    private Animal animal;
    public Label lAnimal;
    public ImageView ivPhoto;
    public Label lClient;

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setController(CardsController controller) {
        this.controller = controller;
    }

    public void setAnimal(Animal animal) throws SQLException, IOException {
        Client client = DB.getClient(animal.ClientID);
        this.animal = animal;
        if (animal.Photo != null) {
            ivPhoto.setImage(new Image(new File(".").getCanonicalPath() + animal.Photo));
        }
        lAnimal.setText(animal.Name);
        lClient.setText(client.FullName);
        lAnimalType.setText(animal.Type);
    }

    /**
     * Вызывает форму изменения клиента карточки
     */
    public void onClient(ActionEvent actionEvent) throws SQLException {
        Client client = DB.getClient(animal.ClientID);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("edit/editClient-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
            EditClientController controller = fxmlLoader.getController();
            controller.setClient(client);
            stage.showAndWait();
            this.controller.onCancelSort(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Вызывает форму изменения питомца карточки
     */
    public void onAnimal(ActionEvent actionEvent) throws SQLException {
        Animal animal = DB.getAnimal(this.animal.ID);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("edit/editAnimal-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
            EditAnimalController controller = fxmlLoader.getController();
            controller.setAnimal(animal);
            stage.showAndWait();
            this.controller.onCancelSort(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Вызывает форму добавления приёма с данными карточки
     */
    public void onAddVisit(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("add/addVisit-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(fxmlLoader.load(), 800, 650));
            stage.setResizable(false);
            AddVisitController controller = fxmlLoader.getController();
            controller.initialize(employee, animal);
            stage.showAndWait();
            this.controller.onCancelSort(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
