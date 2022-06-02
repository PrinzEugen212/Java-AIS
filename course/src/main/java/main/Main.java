package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
/**
 * Класс инициализации приложения
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("authorization-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 200);
        stage.setTitle("Авторизация!");
        stage.setScene(scene);
        stage.show();
        stage.setMaxHeight(400);
        stage.setMaxWidth(200);
        stage.setMinWidth(400);
        stage.setMinHeight(200);
        DB.start();
    }

    public static void main(String[] args) {
        launch();
    }
}