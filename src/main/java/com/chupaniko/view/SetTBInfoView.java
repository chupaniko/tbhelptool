package com.chupaniko.view;

import com.chupaniko.dataworker.AccountsWorker;
import com.chupaniko.dataworker.SetTbInfo;
import com.chupaniko.view.helpers.InputReader;
import org.json.JSONObject;

/**
 *
 */
public class SetTBInfoView implements SimpleView {

    /**
     * Console input processor.
     */
    private InputReader reader;

    /**
     * The view initializer.
     *
     * @param reader Console input processor.
     */
    public SetTBInfoView(InputReader reader) {
        this.reader = reader;
    }

    /**
     * Starts interaction between the user and the view.
     */
    @Override
    public void show() {
        /*
        Опция, которую выбирает пользователь (номер платформы, начиная с "1", или "0", если пользователь решил
        добавить новую платформу.
         */
        String option = "0";

        SimpleView subView;
        /*
        Пользователь может добавлять платформы, сколько захочет. Выход из вьюхи будет, когда он выберет какую-то
        платформу для загрузки в неё бэкапа.
         */
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
                subView = new AddTbAccView(reader);

                subView.show();
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
