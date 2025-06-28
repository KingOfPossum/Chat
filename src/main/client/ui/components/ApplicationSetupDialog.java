package main.client.ui.components;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class ApplicationSetupDialog {

    public static Optional<SetupData> showSetupDialog(String defaultIP, int defaultPort) {
        Dialog<SetupData> dialog = new Dialog<>();
        dialog.setTitle("Setup");
        dialog.setHeaderText("Set IP ,Port and Username");

        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField ipText = new TextField();
        ipText.setText(defaultIP);

        TextField portText = new TextField();
        portText.setText(Integer.toString(defaultPort));

        TextField usernameTxt = new TextField();
        usernameTxt.setPromptText("Username");

        grid.add(new Label("IP: "),0,0);
        grid.add(ipText,1,0);
        grid.add(new Label("Port: "),0,1);
        grid.add(portText,1,1);
        grid.add(new Label("Username: "),0,2);
        grid.add(usernameTxt,1,2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == connectButtonType) {
                return new SetupData(ipText.getText(), Integer.parseInt(portText.getText()), usernameTxt.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public record SetupData( String ip, int port, String userName) {}
}
