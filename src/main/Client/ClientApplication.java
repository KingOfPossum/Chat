package main.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Common.Connections.ConnectionListener;
import main.Common.Connections.ConnectionStatus;
import main.Common.Messages.ChatMessage;

import java.util.Optional;

public class ClientApplication extends Application {

    private Stage mainStage;
    private ChatClient client;
    private String userName;

    private Text connectionStatusTxt;
    private TextArea clientsField;
    private TextArea textArea;

    @Override
    public void start(Stage stage) {
        Optional<String> newUsername = showUserNameDialog();

        if(!validateUserName(newUsername)) {
            return;
        }

        createGUI(stage);

        startClient();
    }

    private void createGUI(Stage stage) {
        mainStage = stage;

        mainStage.setTitle("Client Application");
        mainStage.setWidth(500);
        mainStage.setHeight(500);
        mainStage.setResizable(false);

        mainStage.setOnCloseRequest(event -> {client.closeConnection();});

        mainStage.setScene(createScene());
        mainStage.show();
    }

    private void startClient() {
        Thread serverClientThread = new Thread(() -> {
            client = new ChatClient("0.0.0.0",12345);

            client.setMessageListener((sender,chatMessage) -> {
                if(chatMessage.userName() == null) {
                    System.out.println(chatMessage.message());
                    String msg = chatMessage.message().replaceAll("([\\[\\] ])","");
                    String[] splitMsg = msg.split(",");

                    Platform.runLater(() -> clientsField.setText(String.join("\n", splitMsg)));
                }
                else {
                    Platform.runLater(() -> textArea.appendText(chatMessage.userName() + " : " + chatMessage.message() + "\n"));
                }
            });

            client.setConnectionListener(new ConnectionListener() {
                @Override
                public void onConnect(){
                    Platform.runLater(() -> sendInitMessage());
                }

                @Override
                public void onReconnect() {
                    System.out.println("Reconnected");
                }

                @Override
                public void onDisconnect() {
                    System.out.println("Disconnected");
                }

                @Override
                public void onConnectionStatusChange(ConnectionStatus previousStatus, ConnectionStatus currentStatus) {
                    Platform.runLater(() -> connectionStatusTxt.setText(currentStatus.name()));
                }

                @Override
                public void onConnectionFailed() {
                    System.out.println("Connection failed");
                }
            });

            client.start();
        });

        serverClientThread.start();
    }

    private void sendInitMessage() {
        ChatMessage initMessage = new ChatMessage(userName,"Init");
        client.sendMessage(initMessage);
    }

    private Optional<String> showUserNameDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Choose Username");
        dialog.setHeaderText("Enter your username: ");
        dialog.setContentText("Username: ");

        return dialog.showAndWait();
    }

    private boolean validateUserName(Optional<String> username) {
        if(username.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No username entered!");
            alert.showAndWait();
            return false;
        } else {
            System.out.println("name : " + username.get() + ";");
            this.userName = username.get().trim();
            return true;
        }
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
        textArea = new TextArea();
        textArea.setPrefSize(350,350);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        connectionStatusTxt = new Text("");

        // Input field for sending messages
        TextField inputField = new TextField();
        inputField.setPrefSize(350,70);

        // TextArea to list all connected clients
        clientsField = new TextArea();
        clientsField.setPrefSize(100,400);
        clientsField.setEditable(false);

        vBox.getChildren().addAll(textArea, connectionStatusTxt,inputField);
        hBox.getChildren().addAll(vBox, clientsField);

        inputField.setOnAction(event -> {
            String input = inputField.getText();

            ChatMessage chatMessage = new ChatMessage(userName,input);

            client.sendMessage(chatMessage);
            Platform.runLater(() -> {
                textArea.appendText(chatMessage.userName() + " : " + chatMessage.message() + "\n");
                inputField.setText("");
            });
        });

        Scene scene = new Scene(hBox,950,700);

        return scene;
    }
}
