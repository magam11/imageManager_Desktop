package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;
import sample.db.Manager;
import sample.util.Constrant;
import sample.util.HttpUtil;
import sample.util.Response;

import java.io.IOException;
import java.util.*;

public class Login {
    private Manager manager = new Manager();


  @FXML  public TextField verificationCode_textField;
  @FXML  public Button login_button;
  @FXML  public Label codeHint_label;
  @FXML  public TextField phoneNumber_tf;
    BaseController baseController;
    CloudController cloudController;

  public void initBaseController(BaseController baseController){
      this.baseController = baseController;
  }
  public void initCloudController(CloudController cloudController){
      this.cloudController = cloudController;
  }


  @FXML
  public void initialize(){
      verificationCode_textField.setVisible(false);
      codeHint_label.setVisible(false);
      phoneNumber_tf.setText("+37494704486");
      verificationCode_textField.setText("123456");

  }

    @FXML
    public void login(MouseEvent mouseEvent) throws IOException {
        System.out.println("login cliked");
        String inputPhoneNumber = phoneNumber_tf.getText();
        Map<String, Object> fields = new HashMap<>();
        fields.put("phoneNumber", inputPhoneNumber);
        FXMLLoader appLoader = new FXMLLoader(getClass().getResource("/templates/base.fxml"));
        appLoader.load();
        BaseController appController = appLoader.getController();
        if (login_button.getText().equals("Login")) {
            Response response = HttpUtil.sendPostRequestWithBody(Constrant.loginApi, fields);
            System.out.println("responseeeeeee " + response);
            if (response.getResponseCode() == 404) {
                FXMLLoader serverNotFoundModal = new FXMLLoader(getClass().getResource("/templates/modal/serverNotFound.fxml"));
                appController.loadModalByFXMLLoader(serverNotFoundModal);
            }
            if (response.getResponseCode() == 200) {
                boolean success = (boolean) response.getData().get("success");
                if (!success) {
                    codeHint_label.setVisible(true);
                    codeHint_label.setText("Phone number not registered");
                } else {
                    codeHint_label.setVisible(true);
                    codeHint_label.setText("Enter the code sent to the specified phone number");
                    phoneNumber_tf.setDisable(true);
                    codeHint_label.setVisible(true);
                    verificationCode_textField.setVisible(true);
                    login_button.setText("Commit");
                    return;
                }
            }
        }
        if (login_button.getText().equals("Commit")) {
            System.out.println("click_Commit");
            verifyCode();
        }
    }


    /**
     * Commit button onclick
     */
    public void verifyCode() {
        String verificationCode = verificationCode_textField.getText();
        String phoneNumber = phoneNumber_tf.getText();
        Map<String, Object> params = new HashMap<>();
        params.put("phoneNumber", phoneNumber);
        params.put("verifyCode", verificationCode);
        Response response = null;
        try {
            response = HttpUtil.sendPostRequestWithBody(Constrant.verifyCodeApi, params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.getResponseCode() == 200) {

            boolean success = (boolean) response.getData().get("success");
            if (success) {
                successfulyLogin(response);
            } else {
                verifyCanceled();
            }
        } else {
            System.out.println(response);
        }
    }

    public void verifyCanceled() {
        codeHint_label.setText("Incorrect activation code !");
        codeHint_label.setVisible(true);
    }

    public void successfulyLogin(Response response) {
        JSONObject data = response.getData();
        List<String> pickNames = getListFromObject(data.get("pickNames"));
        String name = data.getString("name");
        String token = data.getString("token");
        manager.setTokenAndName(token, name);
        baseController.loginPane_container.setVisible(false);
        baseController.mainContainter.setVisible(true);
        baseController.tabPane.setVisible(true);
        cloudController.close_icon_image.setVisible(false);
        cloudController.save_icon_image.setVisible(false);
        cloudController.delete_icon_image.setVisible(false);
        cloudController.percent.setVisible(false);
        cloudController.progressBar.setVisible(false);
        cloudController.general_image_imageView.setVisible(false);
        cloudController.save_icon_mouseHover.setVisible(false);
        cloudController.delete_icon_mouseHover.setVisible(false);
        baseController.logout_label.setVisible(true);
        baseController.userName_label.setVisible(true);
        baseController.userName_label.setText(name);
        cloudController.selectedImageName_label.setVisible(false);
        cloudController.open_button.setVisible(false);
        if (pickNames != null && pickNames.size() != 0 && !pickNames.get(0).isEmpty()) {
            cloudController.fill_List(pickNames);
        } else {
            cloudController.imageList.setVisible(false);
        }
    }

    public static List<String> getListFromObject(Object obj) {
        List<String> result = new ArrayList<>();
        String substring = obj.toString().substring(1, obj.toString().length() - 1);
        List<String> strings = Arrays.asList(substring.split("\\s*,\\s*"));
        for (String string : strings) {
            result.add(string.replace("\"", ""));
        }
        return result;
    }
}
