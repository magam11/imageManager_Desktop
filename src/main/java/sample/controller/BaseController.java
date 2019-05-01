package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;
import sample.db.Manager;
import sample.util.Constrant;
import sample.util.HttpRequestMethod;
import sample.util.HttpUtil;
import sample.util.Response;

import javax.swing.event.ChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseController {
    Manager manager = new Manager();
    @FXML
    CloudController cloudController;
    @FXML
    Login loginController;
    @FXML
    DownloadedPageController downloadedPageController;
    @FXML
    public AnchorPane DownloadTab_cell;
    @FXML
    public TabPane tabPane;
    @FXML
    public AnchorPane mainContainter;
    @FXML
    public Label logout_label;
    @FXML
    public Label userName_label;

    @FXML
    public AnchorPane loginPane_container;
    @FXML
    public ScrollPane generalPane;
    @FXML
    public AnchorPane generalContent_pane;


    @FXML
    public void initialize() {
        mainContainter.setVisible(false);
        tabPane.setVisible(false);
        loginController.initBaseController(this);
        loginController.initCloudController(cloudController);

        tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                if (oldValue.toString().equals("1")) {
                    cloudController.progressBar.setProgress(0);
                    cloudController.percent.setVisible(false);
                    cloudController.progressBar.setVisible(false);
                    cloudController. general_image_imageView.setVisible(false);
                    cloudController.save_icon_image.setVisible(false);
                    cloudController.delete_icon_image.setVisible(false);
                    cloudController.close_icon_image.setVisible(false);
                    cloudController.open_button.setVisible(false);
                    String token = manager.getToken();
                    Map<String, String> header = new HashMap<>();
                    header.put("Authorization", token);
                    Response response = null;
                    try {
                        response = HttpUtil.sendRequestVithHeader(Constrant.listPickNames, HttpRequestMethod.POST, new HashMap<>(), header);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response != null && response.getResponseCode() == 200) {
                        JSONObject data = response.getData();
                        cloudController.refreshPageWhenTapChanged(data);
                    } else {
                        System.out.println(response);
                    }
                } else {
                    cloudController.delete_icon_mouseHover.setVisible(false);
                    cloudController.save_icon_mouseHover.setVisible(false);
                    downloadedPageController.downloadedImageList.setItems(getDownloadedImage());
                }
            }
        });

    }


    public ObservableList<ImageView> getDownloadedImage(){
        String home = System.getProperty("user.home");
        File file = new File(home + "/Downloads/imageManager");
        File[] files = file.listFiles();
        ObservableList<ImageView> items = FXCollections.observableArrayList();
        for (File file1 : files) {
            File imageFile = new File(file1.getAbsolutePath());
            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setFitWidth(300);
            items.add(imageView);
        }
        return items;
    }

    public static Parent loadModalByFXMLLoader(FXMLLoader fxmlLoader) {
        Parent rootofModal = null;
        try {
            rootofModal = fxmlLoader.load();
        } catch (IOException e) {
        }
        Stage stageofModal = new Stage();
        Scene scene = new Scene(rootofModal);
        stageofModal.setScene(scene);
        stageofModal.initModality(Modality.APPLICATION_MODAL);
        stageofModal.initStyle(StageStyle.TRANSPARENT);
        stageofModal.showAndWait();
        return rootofModal;
    }


    @FXML
    public void logut(MouseEvent mouseEvent) {
        userName_label.setVisible(false);
        mainContainter.setVisible(false);
        loginController.login_button.setVisible(true);
        loginPane_container.setVisible(true);
        logout_label.setVisible(false);
        loginController.codeHint_label.setVisible(false);
        loginController.login_button.setText("Login");
        loginController.phoneNumber_tf.setVisible(true);
        loginController.phoneNumber_tf.setDisable(false);
        loginController.phoneNumber_tf.setText("");
        loginController.verificationCode_textField.setText("");
        loginController.verificationCode_textField.setVisible(false);
        manager.deleteToken();
        cloudController.delete_icon_mouseHover.setVisible(false);
        cloudController.save_icon_mouseHover.setVisible(false);
    }

    @FXML
    public void logoutButton_mouseEntered(MouseEvent mouseEvent) {
        logout_label.setTextFill(Color.rgb(210, 26, 63));
    }

    @FXML
    public void logoutButton_mouseExited(MouseEvent mouseEvent) {
        logout_label.setTextFill(Color.rgb(48, 161, 112));
    }
}
