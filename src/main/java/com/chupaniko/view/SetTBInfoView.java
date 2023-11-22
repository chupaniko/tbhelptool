package com.chupaniko.view;

import com.chupaniko.dataworker.AccountsWorker;
import com.chupaniko.dataworker.SetTbInfo;
import com.chupaniko.view.helpers.InputReader;
import org.json.JSONObject;

/**
 *
 */
public class SetTBInfoView implements SimpleView {

    private InputReader reader;

    public SetTBInfoView(InputReader reader) {
        this.reader = reader;
    }

    @Override
    public void show() {
        String option = "0";
        while (option.equals("0")) {
            String[] accKeys = AccountsWorker.getInstance().getAccounts().keySet()
                    .stream().map(String::toString).toArray(String[]::new);

            System.out.println("Выберите платформу, в которую будет загружен образ");
            System.out.println("0 - Добавить новую платформу");
            for (int i = 0; i < accKeys.length; i++) {
                System.out.println((i + 1) + " - " + accKeys[i]);
            }
            System.out.print(" > ");
            option = reader.getUserInput();
            if (option.equals("0")) {
                System.out.print("Введите URL платформы (без последнего слеша, например: http://lk.aistiot24.ru, http://localhost:8080)\n > ");
                String url = reader.getUserInput();
                System.out.print("Введите имя платформы (можно использовать часть URL-адреса)\n > ");
                String accountKey = reader.getUserInput();
                System.out.print("Введите псевдоним платформы (он будет включен в имя файла с образом)\n > ");
                String name = reader.getUserInput();
                System.out.print("Введите tenant admin username\n > ");
                String tenantAdminUsername = reader.getUserInput();
                System.out.print("Введите tenant admin password\n > ");
                String tenantAdminPassword = reader.getUserInput();
                System.out.print("Введите sysadmin username\n > ");
                String sysadminUsername = reader.getUserInput();
                System.out.print("Введите sysadmin password\n > ");
                String sysadminPassword = reader.getUserInput();
                AccountsWorker.getInstance().addAccount(
                        accountKey,
                        name,
                        url,
                        tenantAdminUsername,
                        tenantAdminPassword,
                        sysadminUsername,
                        sysadminPassword
                );
            } else {
                String targetAccKey = "";
                try {
                    targetAccKey = accKeys[Integer.parseInt(option) - 1];
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                System.out.println("Вы хотите полностью очистить платформу по адресу \""
                        + ((JSONObject) AccountsWorker.getInstance().getAccounts().get(targetAccKey)).getString("url")
                        + "\" и забить в неё данные из бэкапа?");
                System.out.println("Для предварительного очищения платформы введите \"" + targetAccKey + "\"");
                System.out.println("Для дозаписи в платформу сущностей из бэкапа просто нажмите ENTER");
                SetTbInfo setTbInfo = new SetTbInfo(targetAccKey);
                setTbInfo.setTBInfo(reader.getUserInput().equals(targetAccKey));
            }
        }
    }
}
