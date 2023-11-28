package com.chupaniko.view;

import com.chupaniko.dataworker.AccountsWorker;
import com.chupaniko.dataworker.EntityType;
import com.chupaniko.dataworker.GetTbInfo;
import com.chupaniko.view.helpers.InputReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * View that gets "backup" from Thingsboard platform.
 * <p>
 * "Backup" is a .txt file that contains unprettified JSON string with entities structure in the specified Thingsboard platform.
 * It may be hard to prettify the JSON got from the result "backup" file. In order to get the prettified JSON
 * use <b>resources/getTBInfo.js</b> script.
 */
public class GetTBInfoView implements SimpleView {

    /**
     * Console input processor.
     */
    private InputReader reader;

    /**
     * The view initializer.
     *
     * @param reader Console input processor.
     */
    public GetTBInfoView(InputReader reader) {
        this.reader = reader;
    }

    /**
     * {@link SimpleView#show()}
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
        платформу для бэкапа.
         */
        while (option.equals("0")) {
            // keys of account objects from resources/accounts.json
            String[] accKeys = AccountsWorker.getInstance().getAccounts().keySet()
                    .stream().map(String::toString).toArray(String[]::new);

            System.out.println("Выберите платформу, для которой надо сделать бэкап");
            System.out.println("0 - Добавить новую платформу");
            // Вывод порядкого номера каждой платформы, чтобы потом можно было выбрать
            for (int i = 0; i < accKeys.length; i++) {
                System.out.println((i + 1) + " - " + accKeys[i]);
            }
            System.out.print(" > ");
            option = reader.getUserInput();
            // user wants to add new platform for futher use
            if (option.equals("0")) {
                subView = new AddTbAccView(reader);
                subView.show();
                // user selected some platform
            } else {
                // account key selected by user
                String targetAccKey = "";
                try {
                    // getting account key by user defined number
                    targetAccKey = accKeys[Integer.parseInt(option) - 1];
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                GetTbInfo getTbInfo = new GetTbInfo(targetAccKey);
                Map<String, JSONArray> allTypeEntities = getTbInfo.getEntitiesFromPlatform();
                System.out.println("Если Вы хотите выбрать конкретные сущности каждого типа, " +
                        "которые попадут в бэкап, введите \"1\"");
                System.out.print("Если Вы хотите выбрать CUSTOMER-ов, все сущности которых попадут в бэкап, " +
                        "введите \"2\"\n > ");
                Map<String, JSONArray> selectedTypedEntities = new HashMap<>();
                // пользователь вручную выбирает сущности
                if (reader.getUserInput().equals("1")) {
                    for (Map.Entry<String, JSONArray> typeEntities : allTypeEntities.entrySet()) {
                        selectedTypedEntities.put(
                                typeEntities.getKey(),
                                selectEntities(typeEntities.getKey(), typeEntities.getValue())
                        );
                    }
                    // пользователь выбирает CUSTOMER-ов, все сущности которых попадут в бэкап
                } else {
                    JSONArray customers = selectEntities(
                            EntityType.CUSTOMER.toString(),
                            allTypeEntities.get(EntityType.CUSTOMER.toString())
                    );
                    selectedTypedEntities.putAll(getCustomerChildEntities(customers, allTypeEntities));
                    selectedTypedEntities.put(EntityType.CUSTOMER.toString(), customers);
                }
                getTbInfo.getBackup(selectedTypedEntities);
            }
        }
    }

    //TODO: поместить в контроллер
    /**
     * Отбирает сущности типа "ASSET" и "DEVICE", принадлежащие каким-то CUSTOMER-ам.
     *
     * @param customers Массив CUSTOMER-ов.
     * @param possibleChildEntities Мапа "ТИП_СУЩНОСТИ" -> "JSON_сущности" из сущностей,
     *                              которые возможно принадлежат CUSTOMER-ам
     * @return Мапа "ТИП_СУЩНОСТИ" -> "JSON_сущности" из сущностей, принадлежащих CUSTOMER-ам
     */
    private Map<String, JSONArray> getCustomerChildEntities(JSONArray customers,
                                                            Map<String, JSONArray> possibleChildEntities) {
        Map<String, JSONArray> resultMap = new HashMap<>();
        Set<String> customerIds = new HashSet<>();

        for (Object customer : customers) {
            customerIds.add(((JSONObject) customer).getJSONObject("id").getString("id"));
        }
        for (Map.Entry<String, JSONArray> typedEntities : possibleChildEntities.entrySet()) {
            if (!typedEntities.getKey().equals(EntityType.CUSTOMER.toString())) {
                JSONArray childEntities = new JSONArray();

                for (Object entity : typedEntities.getValue()) {
                    if (((JSONObject) entity).has("customerId")) {
                        for (String customerId : customerIds) {
                            if (((JSONObject) entity).getJSONObject("customerId").getString("id")
                                    .equals(customerId)) {
                                childEntities.put(entity);
                                break;
                            }
                        }
                    }
                }
                resultMap.put(typedEntities.getKey(), childEntities);
            }
        }
        return resultMap;
    }

    /**
     * Предлагает пользователю выбрать сущности Thingsboard заданного типа из списка.
     *
     * @param entityType Тип сущностей, которые подлежат отбору. Строковое представление {@link EntityType}.
     * @param allTypedEntities Список всех сущностей указанного типа, которые подлежат отбору
     * @return Список выбранных пользователем сущностей указанного типа.
     */
    private JSONArray selectEntities(String entityType, JSONArray allTypedEntities) {
        System.out.printf("Введите через пробел номера всех сущностей типа %S, которые должны попасть в бэкап. " +
                        "Если нужно записать в бэкап все сущности, просто нажмите ENTER.%n",
                entityType
        );
        int counter = 1;
        for (Object entity : allTypedEntities) {
            System.out.printf("%-5d %s%n",
                    counter++,
                    ((JSONObject) entity).getString("name")
            );
        }
        System.out.print(" > ");
        JSONArray selectedEntities = new JSONArray();
        String entityNumbers = reader.getUserInput();
        if (entityNumbers.isEmpty()) {
            selectedEntities = allTypedEntities;
        } else {
            int[] indexes = Arrays.stream(entityNumbers.split(" "))
                    .mapToInt(index -> {
                        int result = Integer.MAX_VALUE;
                        try {
                            result = Integer.parseInt(index) - 1;
                        } catch (NumberFormatException e) {
                            System.out.printf("'%s' - не число!%n", index);
                        }
                        return result;

                    })
                    .filter(index -> index > 0 && index < allTypedEntities.length())
                    .toArray();
            for (int index : indexes) {
                selectedEntities.put(allTypedEntities.get(index));
            }
        }
        return selectedEntities;
    }
}
