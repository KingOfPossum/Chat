package main.common.messages;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Comparator;

public class MessageHistoryHandler {
    private final MessageHistory messageHistory;
    private final Gson gson = new Gson();
    private final Path jsonFilePath;

    public MessageHistoryHandler(Path path) {
        jsonFilePath = path;

        if(Files.exists(jsonFilePath)) {
            String json = "";
            try {
                json = Files.readString(jsonFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.messageHistory = gson.fromJson(json,MessageHistory.class);
        }
        else {
            this.messageHistory = new MessageHistory(new ArrayList<>());
            saveToFile();
        }
    }

    public MessageHistory getMessageHistory() {return messageHistory;}

    public void addMessage(ChatMessage message) {
        messageHistory.history().add(message);
        messageHistory.history().sort(Comparator.comparing(ChatMessage::timestamp));
        saveToFile();
    }

    private void saveToFile() {
        try (FileWriter fileWriter = new FileWriter(jsonFilePath.toFile())) {
            gson.toJson(messageHistory,fileWriter);
            System.out.println("Saved message history to " + jsonFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
