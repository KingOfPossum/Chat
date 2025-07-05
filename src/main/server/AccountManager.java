package main.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import main.common.Account;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountManager {
    public Path accountsFilePath;
    public List<Account> accounts;
    public Map<Account, Socket> accountToSocketMap = new HashMap<>();

    private Gson gson = new Gson();

    public AccountManager(Path accountsFilePath) {
        this.accountsFilePath = accountsFilePath;

        String json = "";

        if(Files.exists(accountsFilePath)) {
            try {
                json = Files.readString(accountsFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            accounts = gson.fromJson(json, new TypeToken<List<Account>>(){}.getType());
        }
        else {
            accounts = new ArrayList<>();
        }
    }

    public void createAccount(Account account) {
        accounts.add(account);

        try(FileWriter fileWriter = new FileWriter(accountsFilePath.toFile())) {
            fileWriter.write(gson.toJson(accounts));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean tryLogin(String username,String password) {
        for(Account account : accounts) {
            if(account.username().equals(username)) {
                if(account.password().equals(password)) {
                    System.out.println("Login successful for user: " + username);
                    return true;
                } else {
                    System.out.println("Login failed for user: " + username + ". Incorrect password.");
                    return false;
                }
            }
        }

        createAccount(new Account(username,password));
        return true;
    }
}
