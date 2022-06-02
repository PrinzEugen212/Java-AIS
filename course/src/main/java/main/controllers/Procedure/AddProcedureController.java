package main.controllers.Procedure;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.DB;
import models.Procedure;

/**
 * Обработчик формы добавления процедуры
 */
public class AddProcedureController {
    public TextField tfCost;
    public TextField tfName;

    public void onAdd(ActionEvent actionEvent) {
        try{
            int cost = Integer.parseInt(tfCost.getText());
        }
        catch (Exception e){
            new Alert(Alert.AlertType.ERROR, "Указана неверная цена").show();
            return;
        }
        Procedure procedure = new Procedure(
                tfName.getText(),
                Integer.parseInt(tfCost.getText())
        );
        DB.addProcedure(procedure);
        onCancel(null);
    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }
}
