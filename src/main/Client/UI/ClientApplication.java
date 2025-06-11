package main.Client.UI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientApplication extends Application {

    private Stage mainStage;

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;

        mainStage.setTitle("Client Application");
        mainStage.setWidth(500);
        mainStage.setHeight(500);
        mainStage.setResizable(false);

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

        Scene scene = new Scene(hBox,950,700);

        return scene;
    }
}
