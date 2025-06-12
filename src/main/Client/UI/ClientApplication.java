package main.Client.UI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Client.ClientMain;

import java.util.Optional;

public class ClientApplication extends Application {

    private Stage mainStage;
    private ClientMain client;
    private String userName;

    @Override
    public void start(Stage stage) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Choose Username");
        dialog.setHeaderText("Enter your username: ");
        dialog.setContentText("Username: ");

        Optional<String> newUsername = dialog.showAndWait();

        boolean success;
        if(!newUsername.isPresent() || newUsername.get().equals("")) {
            success = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No username entered!");
            alert.showAndWait();
        } else {
            System.out.println("name : " + newUsername.get() + ";");
            success = true;
            this.userName = newUsername.get();
        }

        if(!success) {
            return;
        }

        Thread serverClientThread = new Thread(() -> {
            this.client = new ClientMain();
            this.client.start();});

        serverClientThread.start();

        mainStage = stage;

        mainStage.setTitle("Client Application");
        mainStage.setWidth(500);
        mainStage.setHeight(500);
        mainStage.setResizable(false);

        mainStage.setOnCloseRequest(event -> {this.client.stop();});

        mainStage.setScene(createScene());
        mainStage.show();

    }

    public Scene createScene() {
        // Main Container
        HBox hBox = new HBox();
        hBox.setSpacing(20);
        hBox.setPadding(new Insets(10,10,10,10));

        // Text Output and Input
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPrefSize(350, 300);

        // TextArea for incoming chat messages
        TextArea textArea = new TextArea();
        textArea.setPrefSize(350,350);
        textArea.setEditable(false);

        // Input field for sending messages
        TextField inputField = new TextField();
        inputField.setPrefSize(350,70);

        // TextArea to list all connected clients
        TextArea clientsField = new TextArea();
        clientsField.setPrefSize(100,400);
        clientsField.setEditable(false);

        vBox.getChildren().addAll(textArea, inputField);
        hBox.getChildren().addAll(vBox, clientsField);

        inputField.setOnAction(event -> {
            String input = inputField.getText();
            this.client.sendMessage(input);
            textArea.setText(textArea.getText() + "\n" + userName + " : " + input + "\n");
            inputField.setText("");
        });

        Scene scene = new Scene(hBox,950,700);

        return scene;
    }
}
