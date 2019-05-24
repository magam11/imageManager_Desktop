package com.arssystems.imageManager_Desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ServerNotFoundController {
    @FXML
    public Button ok_button;

    @FXML
    public void deleteServernotFoundModalFromWindow(MouseEvent mouseEvent) {
        Stage modalStage = (Stage) ok_button.getScene().getWindow();
        modalStage.close();
    }

}



