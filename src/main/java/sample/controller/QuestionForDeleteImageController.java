package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class QuestionForDeleteImageController {

    @FXML
    public Button yesButton;
    @FXML
    public Button noButton;

    private  CloudController cloudController;


    public void initCloudController(CloudController cloudController){
        System.out.println("vatol");
        this.cloudController = cloudController;
    }



    @FXML
    public void positiveAnsWer(MouseEvent mouseEvent) {
        System.out.println(cloudController);
    }


    @FXML
    public void negativeAnswer(MouseEvent mouseEvent) {
        closeModal();

    }

    public void closeModal() {
        Stage modalStage = (Stage) noButton.getScene().getWindow();
        modalStage.close();
    }
}
