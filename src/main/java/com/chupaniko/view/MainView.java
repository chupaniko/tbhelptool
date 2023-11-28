package com.chupaniko.view;

import com.chupaniko.view.helpers.InputReader;

import java.util.Scanner;

/**
 * Main view of tbhelptool (main console menu).
 */
public class MainView implements SimpleView {

    /**
     * {@link SimpleView#show()}
     */
    @Override
    public void show() {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean exit = false;

            while (!exit) {
                InputReader reader = new InputReader(scanner);

                System.out.println("1 - Создать образ платформы");
                System.out.println("2 - Восстановить платформу из образа");
                System.out.println("3 - Удалить в платформе сущности из образа");
                System.out.println("4 - Удалить старую телеметрию");
                System.out.println("5 - Обработать фотографии для слайдера");
                System.out.print("0 - Выйти\n > ");

                String option = reader.getUserInput();
                SimpleView subView;

                switch (option) {
                    case "0":
                        // пустая вьюха
                        subView = () -> {};
                        exit = true;
                        break;
                    case "1":
                        subView = new GetTBInfoView(reader);
                        break;
                    case "2":
                        subView = new SetTBInfoView(reader);
                        break;
                    case "3":
                        subView = new PageNotFoundView();
                        break;
                    case "4":
                        subView = new PageNotFoundView();
                        break;
                    case "5":
                        subView = new PageNotFoundView();
                        break;
                    default:
                        subView = new PageNotFoundView();
                        break;
                }
                subView.show();
            }
        }
    }
}