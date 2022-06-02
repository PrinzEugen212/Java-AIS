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
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Обработчик формы изменения питомца
 * Перед использованием необходимо установить питомца через метод
 */
public class EditAnimalController implements Initializable {
    public ImageView ivPhoto;
    private int ID;
    private String photoPath;
    private List<Client> clients = new ArrayList<>();
    public ComboBox cbClients;
    public TextField tfName;
    public TextField tfType;
    public TextField tfBreed;
    public DatePicker dpBirthDate;
    public ComboBox cbGender;

    public void setAnimal(Animal animal) throws IOException {
        ID = animal.ID;
        if (animal.Photo != null) {
            photoPath = new File(".").getCanonicalPath() + animal.Photo;
            ivPhoto.setImage(new Image(photoPath));
        }
        tfName.setText(animal.Name);
        tfType.setText(animal.Type);
        tfBreed.setText(animal.Breed);
        cbGender.getSelectionModel().select(animal.Gender);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String strDate = dateFormat.format(animal.BirthDate);
        dpBirthDate.setValue(LocalDate.parse(strDate, formatter));
        try {
            Client client = DB.getClient(animal.ClientID);
            cbClients.getSelectionModel().select(client.FullName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onChange(ActionEvent actionEvent) {
        String name = tfName.getText();
        String type = tfType.getText();
        String breed = tfBreed.getText();
        Date date = java.sql.Date.valueOf(dpBirthDate.getValue());
        String gender = (String) cbGender.getSelectionModel().getSelectedItem();
        String clientName = (String) cbClients.getSelectionModel().getSelectedItem();
        Client client = clients.stream().filter(c -> c.FullName == clientName).findAny().orElse(null);
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
        animal.ID = ID;
        DB.changeAnimal(animal);
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

    public void onClearPhoto(ActionEvent actionEvent) {
        ivPhoto.setImage(null);
        photoPath = null;
    }

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
}
