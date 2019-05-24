package com.arssystems.imageManager_Desktop.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;

public class DownloadedPageController {
    @FXML
    public ListView downloadedImageList;


    @FXML
    public void initialize(){
        BaseController baseController = new BaseController();
        ObservableList<ImageView> downloadedImage = baseController.getDownloadedImage();
        downloadedImageList.setItems(downloadedImage);


    }
}
