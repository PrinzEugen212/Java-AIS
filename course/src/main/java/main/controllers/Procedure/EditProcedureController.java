package main.controllers.Procedure;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.DB;
import models.Procedure;

/**
 * Обработчик формы изменения процедуры
 * Перед использованием необходимо установить процедуру через метод
 */
public class EditProcedureController {
    private int ID;
    public TextField tfName;
    public TextField tfCost;

    public void setProcedure(Procedure procedure){
        ID = procedure.ID;
        tfName.setText(procedure.Name);
        tfCost.setText(String.valueOf(procedure.Cost));
    }

    public void onChange(ActionEvent actionEvent) {
        Procedure procedure = new Procedure(
                tfName.getText(),
                Integer.parseInt(tfCost.getText())
        );
        procedure.ID = ID;
        DB.changeProcedure(procedure);
        onCancel(null);
    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }
}
