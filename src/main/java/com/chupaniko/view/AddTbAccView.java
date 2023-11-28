package com.chupaniko.view;

import com.chupaniko.dataworker.AccountsWorker;
import com.chupaniko.view.helpers.InputReader;

/**
 * Добавляет аккаунт Thingsboard в файл thingsboardAccounts.json
 */
public class AddTbAccView implements SimpleView{
    /**
     * Console input processor.
     */
    private InputReader reader;

    public AddTbAccView(InputReader reader) {
        this.reader = reader;
    }

    /**
     * Starts interaction between the user and the view.
     */
    @Override
    public void show() {
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
    }
}
