package com.chupaniko.dataworker;

import org.json.JSONObject;

import java.io.*;
import java.util.Iterator;
import java.util.stream.Collectors;

public class AccountsWorker {
    private JSONObject accounts;

    private volatile static AccountsWorker accountsWorker;

    //TODO: сделать зашифрованный/байтовый файл
    private final static String accountsJSONPath = "thingsboardAccounts.json";
    private AccountsWorker() {
        JSONObject systemAccounts;
        //чтение из ресурсов
        try (InputStream inputStream = getClass().getResourceAsStream("/accounts.json");
             BufferedReader reader = inputStream != null
                     ? new BufferedReader(new InputStreamReader(inputStream))
                     : null) {
            if (reader != null) {
                systemAccounts = new JSONObject(reader.lines()
                        .collect(Collectors.joining(System.lineSeparator())));
                File tbAccsFile = new File(accountsJSONPath);
                if (tbAccsFile.createNewFile()) {
                    accounts = systemAccounts;
                } else {
                    // обновляем в thingsboardAccounts.json инфу из accounts.json
                    JSONObject localAccounts;
                    try (BufferedReader reader1 = new BufferedReader(new FileReader(accountsJSONPath))) {
                        localAccounts = new JSONObject(reader1.lines()
                                .collect(Collectors.joining(System.lineSeparator())));
                        Iterator<String> iterator = systemAccounts.keys();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            localAccounts.put(key, systemAccounts.get(key));
                        }
                        accounts = localAccounts;
                    }
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountsJSONPath))) {
                    writer.write(accounts.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("JAR is damaged.");
            }
        } catch (IOException ignored){}
    }

    public static AccountsWorker getInstance() {
        if (accountsWorker == null) {
            synchronized (AccountsWorker.class) {
                if (accountsWorker == null) {
                    accountsWorker = new AccountsWorker();
                }
            }
        }
        return accountsWorker;
    }

    public JSONObject getAccounts() {
        return accounts;
    }

    public void addAccount(String accountKey,
                           String name,
                           String url,
                           String tenantAdminUsername,
                           String tenantAdminPassword,
                           String sysadminUsername,
                           String sysadminPassword) {
        JSONObject account = new JSONObject();
        account.put("name", name);
        account.put("url", url);
        account.put("tenantAdminUsername", tenantAdminUsername);
        account.put("tenantAdminPassword", tenantAdminPassword);
        account.put("sysadminUsername", sysadminUsername);
        account.put("sysadminPassword", sysadminPassword);
        accounts.put(accountKey, account);
        saveAccount();
    }

    public void deleteAccount(String accountKey) {
        accounts.remove(accountKey);
        saveAccount();
    }

    private void saveAccount() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountsJSONPath))) {
            writer.write(accounts.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
