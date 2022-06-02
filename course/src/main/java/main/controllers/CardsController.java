package main.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import main.DB;
import main.Main;
import models.Animal;
import models.Client;
import models.Employee;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Контроллер списка с карточками
 * @see CardController
 */
public class CardsController{
    public Employee employee;
    public VBox vbCards;
    public ComboBox cbClients;
    private List<Client> clients = new ArrayList<>();

    /**
     * Обновляет список клиентов и сбрасывает сортировку карточек
     */
    public void updateClients(){
        try {
            clients = DB.getClients();
            cbClients.setItems(FXCollections.observableArrayList(clients.stream().map(c -> c.FullName).toList()));
            setCards(null);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error").show();
        }
    }

    /**
     * При выборе клиента сортирует карточки по нему
     */
    public void onClientSelect(ActionEvent actionEvent) {
        String clientName = (String) cbClients.getSelectionModel().getSelectedItem();
        Client client = clients.stream().filter(c -> Objects.equals(c.FullName, clientName)).findAny().orElse(null);
        setCards(client);
    }

    public void onCancelSort(ActionEvent actionEvent) {
        setCards(null);
        cbClients.getSelectionModel().select(-1);
    }

    /**
     * Загружает питомцев из базы данных, для каждого из них создаёт
     * свою карточку и добавляет в форму
     * @see CardController
     */
    private void setCards(Client client) {
        try {
            vbCards.getChildren().clear();
            List<Animal> animals = DB.getAnimals();
            if (client != null){
                animals = animals.stream().filter(a->a.ClientID == client.ID).toList();
            }
            for (Animal animal : animals) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource("card-view.fxml"));
                Parent pane = loader.load();
                CardController cardController = loader.getController();
                cardController.setAnimal(animal);
                cardController.setController(this);
                cardController.setEmployee(employee);
                vbCards.getChildren().add(pane);
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error").show();
        }

    }

    public void initialize() {
        try {
            clients = DB.getClients();
            cbClients.setItems(FXCollections.observableArrayList(clients.stream().map(c -> c.FullName).toList()));
            setCards(null);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error").show();
        }
    }
}
