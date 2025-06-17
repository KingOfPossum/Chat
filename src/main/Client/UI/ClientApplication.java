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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Client.ChatClient;
import main.Client.ChatMessage;
import main.Connections.ConnectionListener;
import main.Connections.ConnectionStatus;

import java.util.Optional;

public class ClientApplication extends Application {

    private Stage mainStage;
    private ChatClient client;
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
            client = new ChatClient("0.0.0.0",12345);
            client.start();

            while(!client.getConnectionStatus().equals(ConnectionStatus.CONNECTED)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // Initialization message to send the clients userName to the server
            sendInitMessage();
        });

        serverClientThread.start();

        // Make sure the client has started
        //TODO replace with better solution
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mainStage = stage;

        mainStage.setTitle("Client Application");
        mainStage.setWidth(500);
        mainStage.setHeight(500);
        mainStage.setResizable(false);

        mainStage.setOnCloseRequest(event -> {client.closeConnection();});

        mainStage.setScene(createScene());
        mainStage.show();

    }

    private void sendInitMessage() {
        ChatMessage initMessage = new ChatMessage(userName,"Init");
        client.sendMessage(initMessage);
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
        textArea.setWrapText(true);

        Text connectionStatusTxt = new Text(client.getConnectionStatus().toString());

        // Input field for sending messages
        TextField inputField = new TextField();
        inputField.setPrefSize(350,70);

        // TextArea to list all connected clients
        TextArea clientsField = new TextArea();
        clientsField.setPrefSize(100,400);
        clientsField.setEditable(false);

        vBox.getChildren().addAll(textArea, connectionStatusTxt,inputField);
        hBox.getChildren().addAll(vBox, clientsField);

        inputField.setOnAction(event -> {
            String input = inputField.getText();

            ChatMessage chatMessage = new ChatMessage(userName,input);

            client.sendMessage(chatMessage);
            textArea.setText(textArea.getText() + "\n" + chatMessage.userName() + " : " + chatMessage.message() + "\n");
            inputField.setText("");
        });

        client.setMessageListener((sender,chatMessage) -> {
            if(chatMessage.userName() == null) {
                System.out.println(chatMessage.message());
                String msg = chatMessage.message().replaceAll("([\\[\\] ])","");;
                String[] splitMsg = msg.split(",");

                clientsField.setText(String.join("\n", String.join("\n", splitMsg)));
            }
            else {
                textArea.setText(textArea.getText() + "\n" + chatMessage.userName() + " : " + chatMessage.message() + "\n");
            }
        });

        client.setConnectionListener(new ConnectionListener() {
            @Override
            public void onConnected(){
                sendInitMessage();
            }

            @Override
            public void onDisconnected() {
                System.out.println("Disconnected");
            }

            @Override
            public void onConnectionStatusChanged(ConnectionStatus previousStatus, ConnectionStatus currentStatus) {
                connectionStatusTxt.setText(currentStatus.name());
            }
        });

        Scene scene = new Scene(hBox,950,700);

        return scene;
    }
}
