package main.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.client.ui.components.ApplicationSetupDialog;
import main.common.connections.ApplicationConnectionListener;
import main.common.connections.ConnectionStatus;
import main.common.messages.ApplicationMessageListener;
import main.common.messages.ChatMessage;
import main.common.TimeUtils;

import java.util.Optional;

public class ClientApplication extends Application {
    private  int serverPort = 12345;
    private  String serverIP = "0.0.0.0";

    private Stage mainStage;
    private ChatClient client;
    private String userName;

    private Text connectionStatusTxt;
    private TextArea clientsField;
    private TextArea textArea;

    @Override
    public void start(Stage stage) {
        if(!setup()) {
            return;
        }

        createGUI(stage);

        startClient();
    }

    private boolean setup() {
        Optional<ApplicationSetupDialog.SetupData> setupData = ApplicationSetupDialog.showSetupDialog(serverIP,serverPort);

        if (setupData.isPresent()) {
            if(!validateSetupData(setupData.get())) {
                return false;
            }
        }
        else {
            showSetupAlert();
            return false;
        }

        serverIP = setupData.get().ip();
        serverPort = setupData.get().port();
        userName = setupData.get().userName();
        return true;
    }

    private boolean validateSetupData(ApplicationSetupDialog.SetupData setupData) {
        System.out.println("Validating setup data: " + setupData);

        if(setupData.ip() == null || setupData.ip().isEmpty() || setupData.port() <= 0 || setupData.userName() == null || setupData.userName().isEmpty()) {
            showSetupAlert();
            return false;
        }
        return true;
    }

    private void showSetupAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Setup Error");
        alert.setHeaderText("Setup Data Missing");
        alert.setContentText("You must provide valid setup data to start the client.");
        alert.showAndWait();
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
            client = new ChatClient(serverIP,serverPort);

            client.setMessageListener(new ApplicationMessageListener(this));
            client.setConnectionListener(new ApplicationConnectionListener(this));

            client.start();
        });

        serverClientThread.start();
    }

    public void initClient() {
        sendInitMessage();
    }

    public void updateConnectionStatus(ConnectionStatus status) {
        connectionStatusTxt.setText(status.toString());
    }

    public void updateClientsField(String[] clients) {
        clientsField.setText(String.join("\n", clients));
    }

    public void updateTextArea(String message,String userName) {
        textArea.appendText(userName + " : " + message + "\n");
    }

    public void setTextAreaText(String txt) {
        textArea.setText(txt);
    }

    private void sendInitMessage() {
        ChatMessage initMessage = new ChatMessage(userName,"Init", TimeUtils.currentTimestamp());
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

            ChatMessage chatMessage = new ChatMessage(userName,input,TimeUtils.currentTimestamp());

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
