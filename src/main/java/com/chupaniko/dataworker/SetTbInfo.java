package com.chupaniko.dataworker;

import com.chupaniko.controller.SetTbInfoController;
import com.chupaniko.exceptions.InvalidEntityTypeException;
import org.json.JSONObject;

public class SetTbInfo {
    private final String backupPath = "backup.txt";
    private SetTbInfoController setTbInfoController;

    /**
     * Инициализация полей, получение токенов авторизации.
     *
     * @param accountKey Платформа, в которую будут загружаться сущности из бэкапа.
     */
    public SetTbInfo(String accountKey) {
        JSONObject account = AccountsWorker.getInstance().getAccounts().getJSONObject(accountKey);
        setTbInfoController = new SetTbInfoController(account);
        //TODO: проверка существования backup.txt (указать путь к файлу с бэкапом)
    }

    /**
     * Загружает все сущности из бэкапа на платформу.
     */
    //TODO: общий интерфейс и метод doWork() для SetTbInfo и GetTbInfo, универсальная вьюха
    public void setTBInfo(boolean doClear) {
        try {
            if (doClear) {
                setTbInfoController.clearPlatform();
            }
            pushEntitiesInfo(EntityType.CUSTOMER);
            pushEntitiesInfo(EntityType.ASSET);
            pushEntitiesInfo(EntityType.DEVICE);
        } catch (InvalidEntityTypeException | InterruptedException e) {
            e.printStackTrace();
        }

        pushRelationsInfo(EntityType.CUSTOMER);
        pushRelationsInfo(EntityType.ASSET);
        pushRelationsInfo(EntityType.DEVICE);

        //TODO: запостить атрибуты
        //TODO: установить relations

        System.out.println("OK");
    }

    private void pushEntitiesInfo(EntityType entitiesType) {
        try (BackupReader reader = new BackupReader(backupPath, entitiesType)) {
            while (reader.hasNextEntity()) {
                JSONObject entityInfo = reader.readEntity();
                if (entitiesType == EntityType.CUSTOMER) {
                    entityInfo = (JSONObject) entityInfo.get("customer");
                }
                pushEntityToPlatform(entityInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pushRelationsInfo(EntityType entitiesType) {
        try (BackupReader reader = new BackupReader(backupPath, entitiesType)) {
            while (reader.hasNextEntity()) {
                JSONObject entityInfo = reader.readEntity();
                if (entitiesType == EntityType.CUSTOMER) {
                    entityInfo = (JSONObject) entityInfo.get("customer");
                }
                // Установка relations
                if (setTbInfoController.saveEntityRelations(entityInfo)) {
                    System.out.println(
                            "(" + ((JSONObject) ((JSONObject) entityInfo.get("entity")).get("id")).getString("entityType")
                                    + " \"" + ((JSONObject) entityInfo.get("entity")).getString("name") + "\") "
                                    + "relations saved"
                    );
                } else System.out.println(
                        "(" + ((JSONObject) ((JSONObject) entityInfo.get("entity")).get("id")).getString("entityType")
                                + " \"" + ((JSONObject) entityInfo.get("entity")).getString("name") + "\") "
                                + "relations skipped!"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pushEntityToPlatform(JSONObject pEntityInfo) {
        JSONObject entityInfo = new JSONObject(pEntityInfo.toString());
        JSONObject entity = new JSONObject(entityInfo.get("entity").toString());
        if (setTbInfoController.saveEntity(entityInfo)) {
            // Отправка телеметрии
            setTbInfoController.saveEntityTelemetry(entityInfo);

            // Отправка атрибутов
            setTbInfoController.saveEntityAttributes(entityInfo);

            System.out.println(
                    "(" + ((JSONObject) entity.get("id")).getString("entityType")
                            + " \"" + entity.getString("name") + "\") "
                            + "saved"
            );
        } else {
            System.out.println("(" + ((JSONObject) entity.get("id")).getString("entityType")
                    + " \"" + entity.getString("name") + "\") "
                    + "skipped!");
        }
    }
}