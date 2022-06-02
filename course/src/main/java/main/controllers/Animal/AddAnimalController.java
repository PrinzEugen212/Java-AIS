package main.controllers.Animal;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.DB;
import models.Animal;
import models.Client;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Обработчик формы добавления клиента
 */
public class AddAnimalController implements Initializable {
    private String photoPath;
    private List<Client> clients = new ArrayList<>();
    public ComboBox cbClients;
    public TextField tfName;
    public TextField tfType;
    public TextField tfBreed;
    public DatePicker dpBirthDate;
    public ComboBox cbGender;
    public ImageView ivPhoto;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbGender.setItems(FXCollections.observableArrayList("М", "Ж"));
        cbGender.getSelectionModel().select(0);
        try {
            clients = DB.getClients();
            cbClients.setItems(FXCollections.observableArrayList(clients.stream().map(c -> c.FullName).toList()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onAdd(ActionEvent actionEvent) {
        if (tfName.getText().length() == 0 || tfType.getText().length() == 0 || cbClients.getSelectionModel().isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Заполните все необходимые поля").show();
            return;
        }
        String name = tfName.getText();
        String type = tfType.getText();
        String breed = tfBreed.getText();
        Date date = java.sql.Date.valueOf(dpBirthDate.getValue());
        String gender = (String) cbGender.getSelectionModel().getSelectedItem();
        String clientName = (String) cbClients.getSelectionModel().getSelectedItem();
        Client client = clients.stream().filter(c -> Objects.equals(c.FullName, clientName)).findAny().orElse(null);
        String newPath = null;
        if (photoPath != null) {
            try {
                String path = new File(".").getCanonicalPath();
                newPath = "\\Photo\\" + name + date + ".png";
                Path source = Paths.get(photoPath);
                Path dst = Paths.get(path + newPath);
                Files.copy(source, dst, StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Animal animal = new Animal(client.ID, name, gender, type, breed, date, newPath);
        DB.addAnimal(animal);
        onCancel(null);
    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }

    public void onChoosePhoto(ActionEvent actionEvent) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All image Files", "*.png", "*.jpg",
                        "*.gif"),
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showOpenDialog(tfName.getScene().getWindow());
        if (file != null) {
            photoPath = file.getPath();
            Image img = new Image(photoPath);
            ivPhoto.setImage(img);
        }
    }

    public void onDeletePhoto(ActionEvent actionEvent) {
        ivPhoto.setImage(null);
        photoPath = null;
    }


}
