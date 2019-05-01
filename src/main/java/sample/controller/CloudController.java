package sample.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONObject;
import sample.db.Manager;
import sample.util.Constrant;
import sample.util.HttpRequestMethod;
import sample.util.HttpUtil;
import sample.util.Response;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudController {

    @FXML
    public AnchorPane cloudTab_Content_AnchorPane;
    @FXML
    public AnchorPane imageListContainer;
    @FXML
    public ListView<ImageView> imageList;
    @FXML
    public AnchorPane right_ContentOfCloud;
    @FXML
    public ImageView general_image_imageView;
    @FXML
    public ImageView save_icon_image;
    @FXML
    public ImageView delete_icon_image;
    @FXML
    public ImageView close_icon_image;
    @FXML
    public Label selectedImageName_label;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Label percent;
    @FXML
    public ImageView delete_icon_mouseHover;
    @FXML
    public ImageView save_icon_mouseHover;
    @FXML
    public Button open_button;
    public int hoveredIndex;
    public Image imageFlat;
    public Manager manager = new Manager();
    public int clikedImageID;
    public Image clikedImage;

    @FXML
    public void initialize() {
        imageList.setCellFactory(new Callback<ListView<ImageView>, ListCell<ImageView>>() {
            @Override
            public ListCell<ImageView> call(ListView<ImageView> list) {
                ListCell<ImageView> cell = new ListCell<ImageView>() {
                    @Override
                    public void updateItem(ImageView item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(item);
                    }
                };
                cell.hoverProperty().addListener(observable -> {
                    if (cell != null && !cell.isEmpty() && imageList.getItems().size() != 0) {
//                        cell.setLayoutX(cell.getLayoutX());
                        hoveredIndex = cell.getIndex();
                        mouseOnlistItem(cell);
                    }
                });
                return cell;
            }
        });


    }


    public void refreshPageWhenTapChanged(JSONObject data) {
        imageList.getItems().clear();
        Login login = new Login();
        List<String> pickNames = login.getListFromObject(data.get("pickNames"));
        if (pickNames != null && !pickNames.get(0).isEmpty()) {
            fill_List(pickNames);
        } else {
            imageList.setVisible(false);
        }

    }


    private void mouseOnlistItem(ListCell<ImageView> cell) {
        imageFlat = cell.getItem().getImage();
        double layoutY = cell.getLayoutY();
        delete_icon_mouseHover.setVisible(true);
        save_icon_mouseHover.setVisible(true);
        delete_icon_mouseHover.setLayoutY(layoutY + 32);
        save_icon_mouseHover.setLayoutY(layoutY + 32);

    }


    @FXML
    public void viewImage(MouseEvent mouseEvent) {
        save_icon_image.setVisible(true);
        delete_icon_image.setVisible(true);
        close_icon_image.setVisible(true);

        ImageView selectedItem = (ImageView) imageList.getSelectionModel().getSelectedItem();
        clikedImageID = imageList.getSelectionModel().getSelectedIndex();
        Image image = selectedItem.getImage();
        clikedImage = image;
        String[] path_part = selectedItem.getImage().impl_getUrl().split("/");
        String pictureName = path_part[path_part.length - 1];
        selectedImageName_label.setText(pictureName);
        general_image_imageView.setImage(image);

        general_image_imageView.setFitWidth(950.0);
        general_image_imageView.setFitHeight(742.0);
        general_image_imageView.setVisible(true);
    }

    @FXML
    public void downloadImage(MouseEvent mouseEvent) throws IOException {

        save_icon_mouseHover.setVisible(false);
        delete_icon_mouseHover.setVisible(false);
        String dirPath = decideDirectionPath();
        when_Save_Direction_Path_is_detemined(dirPath);
    }

    public String operationSystem() {
        String os = "";
        String operationSysstemName = System.getProperty("os.name").toLowerCase();
        String userName = System.getProperty("user.name");
        if (operationSysstemName.contains("win")) { //WINDOWS
            os = "WINDOWS";
        } else if (operationSysstemName.contains("nix") || operationSysstemName.contains("nux") || operationSysstemName.contains("aix")) { //LINUX
            os = "LINUX";
        } else if (operationSysstemName.contains("mac")) { //MAC
            os = "MAX";
        } else if (operationSysstemName.contains("sunos")) { //SOLARIS
            os = "SOLARIS";
        }
        return os;
    }

    public void when_Save_Direction_Path_is_detemined(String savDirectionPath) throws IOException {
        progressBar.setVisible(true);
        ImageView selectedItem = (ImageView) imageList.getSelectionModel().getSelectedItem();
//        URL url = new URL(selectedItem.getImage().impl_getUrl());
        URL url = new URL(imageFlat.impl_getUrl());
        URLConnection urlConnection = url.openConnection();
        double pictureSize = new Double(urlConnection.getHeaderField("pictureSize") + "D");
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        int bufferSize = 512;
        byte[] buffer = new byte[bufferSize];
        File file = new File(savDirectionPath + getSelectedImageName());
        file.setExecutable(true);
        BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(savDirectionPath + selectedImageName_label.getText()));
        int len = 0;
        progressBar.setProgress(0);
        percent.setVisible(true);
        double downloadedSize = 0;
        while ((len = inputStream.read(buffer, 0, bufferSize)) > -1) {
            outputStream.write(buffer, 0, len);
            downloadedSize += progressBar.getProgress() + len;
            progressBar.setAccessibleText((downloadedSize / pictureSize) * 100 + "%");
            progressBar.setProgress((downloadedSize) / pictureSize);
            double v = (downloadedSize) / pictureSize;
            int percentval = (int) v * 100;
            percent.setText(percentval + "%");
        }
        outputStream.flush();
        outputStream.close();
        delete_icon_image.setVisible(false);
        imageList.getItems().remove(selectedItem);
        Map<String, Object> params = new HashMap<>();
        params.put("picName", selectedImageName_label.getText());
        Map<String, String> headers = new HashMap<>();
        headers.put(Constrant.authorization_key, manager.getToken());
        save_icon_image.setVisible(false);
        open_button.setVisible(true);
        Response response = HttpUtil.sendRequestVithHeader(Constrant.deleteImageApi, HttpRequestMethod.POST, params, headers);
    }


    public String getSelectedImageName() {
        ImageView selectedItem = (ImageView) imageList.getSelectionModel().getSelectedItem();
        String[] path_part = clikedImage.impl_getUrl().split("/");
//        String[] path_part = imageFlat.impl_getUrl().split("/");
        imageList.getItems().remove(selectedItem);
        String pictureName = path_part[path_part.length - 1];
        return pictureName;

    }


    @FXML
    public void deleteImage(MouseEvent mouseEvent) throws IOException {
        ButtonType Yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType No = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert =
                new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to delete?",
                        ButtonType.YES,
                        ButtonType.NO);
        alert.setHeaderText("");
        ObjectProperty<ButtonType> result = showDialog(alert);
        System.out.println("Result is " + result);
        if (result.get() == ButtonType.YES) {
            System.out.println("yes");
            deleteConfirmed(mouseEvent);
        }

    }

    public void deleteConfirmed(MouseEvent mouseEvent) throws IOException {
        general_image_imageView.setVisible(false);
        save_icon_mouseHover.setVisible(false);
        delete_icon_mouseHover.setVisible(false);
        open_button.setVisible(false);
//        imageList.getItems().remove(clikedImageID);
        String pictureName = getSelectedImageName();
        commonFunctionOne(mouseEvent, pictureName);
    }

    public void commonFunctionOne(MouseEvent mouseEvent, String pictureName) throws IOException {
        closeImage(mouseEvent);
        Map<String, Object> params = new HashMap<>();
        params.put("picName", pictureName);
        Map<String, String> headers = new HashMap<>();
        headers.put(Constrant.authorization_key, manager.getToken());
        Response response = HttpUtil.sendRequestVithHeader(Constrant.deleteImageApi, HttpRequestMethod.POST, params, headers);
        if (response.getResponseCode() == 200) {
            System.out.println("serveric staca deleti patasxany ");
        } else {
            System.out.println("serveric deleti patasxany 200 che");
            System.out.println(response.getResponseCode());
        }
    }


    public ObjectProperty<ButtonType> showDialog(Alert alert) {
        DialogPane pane = alert.getDialogPane();
        ObjectProperty<ButtonType> result = new SimpleObjectProperty<>();
        for (ButtonType type : pane.getButtonTypes()) {
            ButtonType resultValue = type;
            ((Button) pane.lookupButton(type)).setOnAction(e -> {
                result.set(resultValue);
                pane.getScene().getWindow().hide();
            });
        }
        pane.getScene().setRoot(new Label());
        Scene scene = new Scene(pane);
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.resizableProperty().setValue(Boolean.FALSE);
        dialog.setScene(scene);

        dialog.setTitle("CONFIRMATION");
        dialog.getIcons().add(new Image("/static/image/appIcon.png"));
        result.set(null);
        dialog.showAndWait();
        return result;
    }

    @FXML
    public void closeImage(MouseEvent mouseEvent) {
        save_icon_mouseHover.setVisible(false);
        delete_icon_mouseHover.setVisible(false);
        open_button.setVisible(false);
        percent.setVisible(false);
        progressBar.setProgress(0);
        progressBar.setVisible(false);
        delete_icon_image.setVisible(false);
        close_icon_image.setVisible(false);
        save_icon_image.setVisible(false);
        general_image_imageView.setVisible(false);
    }

    @FXML
    public void dleteImageFromHoverIcon(MouseEvent mouseEvent) throws IOException {
        Alert alert =
                new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to delete?",
                        ButtonType.YES,
                        ButtonType.NO);
        alert.setHeaderText("");
        ObjectProperty<ButtonType> result = showDialog(alert);
        System.out.println("Result is " + result);
        if (result.get() == ButtonType.YES) {
            deleteConfirmedFromHovereIcon(mouseEvent);
        }
    }

    private void deleteConfirmedFromHovereIcon(MouseEvent mouseEvent) throws IOException {

        general_image_imageView.setVisible(false);
        save_icon_mouseHover.setVisible(false);
        delete_icon_mouseHover.setVisible(false);
        right_ContentOfCloud.setVisible(false);
        String[] hoveredImageUrlParts = imageFlat.impl_getUrl().split("/");
        String pictureName = hoveredImageUrlParts[hoveredImageUrlParts.length - 1];
        imageList.getItems().remove(hoveredIndex);
        commonFunctionOne(mouseEvent, pictureName);

    }

    @FXML
    public void saveImageFromHoverIcon(MouseEvent mouseEvent) throws IOException {

        imageFlat.impl_getUrl();
        imageList.getItems().remove(hoveredIndex);
        save_icon_image.setVisible(false);
        open_button.setVisible(false);
        delete_icon_image.setVisible(false);
        progressBar.setVisible(false);
        right_ContentOfCloud.setVisible(true);
        String dirPath = decideDirectionPath();
        URL url = new URL(imageFlat.impl_getUrl());
        URLConnection urlConnection = url.openConnection();
        double pictureSize = new Double(urlConnection.getHeaderField("pictureSize") + "D");
        urlConnection.connect();
        save_icon_mouseHover.setVisible(false);
        delete_icon_mouseHover.setVisible(false);
        InputStream inputStream = urlConnection.getInputStream();
        int bufferSize = 512;
        byte[] buffer = new byte[bufferSize];
        String[] parts = imageFlat.impl_getUrl().split("/");
        String hoveredImageName = parts[parts.length - 1];
        BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(dirPath + hoveredImageName));
        int len = 0;
        double downloadedSize = 0;
        progressBar.setProgress(0);
        percent.setVisible(true);
        progressBar.setVisible(true);
        while ((len = inputStream.read(buffer, 0, bufferSize)) > -1) {
            outputStream.write(buffer, 0, len);
            downloadedSize += progressBar.getProgress() + len;
            double v = (downloadedSize) / pictureSize;
            int percentval = (int) v * 100;
            percent.setText(percentval + "%");

        }
        general_image_imageView.setImage(imageFlat);
        general_image_imageView.setVisible(true);
        close_icon_image.setVisible(true);
        outputStream.flush();
        outputStream.close();
        Map<String, Object> params = new HashMap<>();
        params.put("picName", hoveredImageName);
        Map<String, String> headers = new HashMap<>();
        headers.put(Constrant.authorization_key, manager.getToken());
        Response response = HttpUtil.sendRequestVithHeader(Constrant.deleteImageApi, HttpRequestMethod.POST, params, headers);

    }

    private String decideDirectionPath() {
        String home = System.getProperty("user.home");
        String os_name = operationSystem();
        String dirPath = "";
        if (os_name.equals("WINDOWS")) {
            File file = new File(home + "/Downloads/imageManager");
            if (!file.exists()) {
                file.mkdirs();
            }
            dirPath = home + "/Downloads/imageManager/";
        }
        if (os_name.equals("LINUX")) {
            File file = new File(home + "\\Downloads\\imageManager");
            if (!file.exists()) {
                file.mkdirs();
            }
            dirPath = home + "\\Downloads\\imageManager\\";
        }
        return dirPath;
    }

    public void fill_List(List<String> pickNames) {
        ObservableList<ImageView> items = FXCollections.observableArrayList();
        for (String pickName : pickNames) {
            Image image = new Image(Constrant.serverAddres + Constrant.image + pickName);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setFitWidth(320);
            items.add(imageView);
        }
        imageList.setVisible(true);
        imageList.setItems(items);
    }
}
