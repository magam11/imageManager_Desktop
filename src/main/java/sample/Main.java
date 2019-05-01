package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;

public class Main extends Application {
    private Parent rootNode;
    private Scene generalScene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        URL appIcon = getClass().getResource("/static/image/appIcon.png");
        primaryStage.getIcons().add(new Image(appIcon.toString()));
        primaryStage.setTitle("Image Cloud");
        Scene scene = new Scene(rootNode, 1300, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    @Override
    public void init() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/templates/base.fxml"));
        rootNode = fxmlLoader.load();
//        Scene scene = rootNode.getScene();
//        final AnchorPane generalContent_pane = (AnchorPane)scene.lookup("generalContent_pane");
//        scene.widthProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
//                generalContent_pane.setPrefWidth(newSceneWidth.doubleValue());
//                System.out.println("Width: " + newSceneWidth);
//            }
//        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
