package com.chupaniko.controller;

import com.chupaniko.dataworker.AttributesScope;
import com.chupaniko.dataworker.EntityType;
import com.chupaniko.exceptions.HttpRequestExecutionException;
import com.chupaniko.exceptions.InvalidEntityTypeException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SetTbInfoController {
    private final String targetURL;
    private final String tenantAuthToken;
    /**
     * Можно убрать, он нужен только для управления tenant admins.
     */
    private final String sysadminAuthToken;
    private Map<String, String> oldNewIds;

    private GetTbInfoController getTbInfoController;

    public SetTbInfoController(JSONObject account) {
        oldNewIds = new HashMap<>();
        this.targetURL = account.get("url").toString();
        TbAuthController authController = new TbAuthController();

        this.tenantAuthToken = authController.getToken(account, TbAuthController.TbUserRole.TENANT);
        this.sysadminAuthToken = authController.getToken(account, TbAuthController.TbUserRole.SYSAMDIN);

        this.getTbInfoController = new GetTbInfoController(account);
    }

    public boolean saveEntity(JSONObject entityInfo) {
        //TODO: решить проблему с клонированием JSON-ов
        JSONObject preparedEntity = new JSONObject(entityInfo.getJSONObject("entity").toString());
        // подготовка JSON-а сущности для загрузки на платформу
        String oldID = ((JSONObject) preparedEntity.get("id")).getString("id");
        String entityType = ((JSONObject) preparedEntity.get("id")).getString("entityType");

        preparedEntity.remove("tenantId");
        preparedEntity.remove("createdTime");
        // сначала должны сохраняться кастомеры, потом asset-ы и девайсы
        if (preparedEntity.has("customerId")) {
            JSONObject newCustomerId = (JSONObject) preparedEntity.get("customerId");
            String oldCustomerIdId = ((JSONObject) preparedEntity.get("customerId")).getString("id");
            if (oldNewIds.containsKey(oldCustomerIdId)) {
                newCustomerId.put("id", oldNewIds.get(oldCustomerIdId));
                preparedEntity.put("customerId", newCustomerId);
            } else {
                preparedEntity.remove("customerId");
                System.out.println("Ошибка присвоения кастомера сущности \""
                        + ((JSONObject) preparedEntity.get("id")).getString("entityType") + " "
                        + preparedEntity.get("name") + "\". "
                        + "Отсутствует соответствующий Customer.");
            }
        }

        String path = "";
        //TODO: привязать enum EntityType
        switch (entityType) {
            case "DEVICE":
                String deviceToken = ((JSONObject) preparedEntity.get("id")).getString("accessToken");
                preparedEntity.remove("id");
                path = targetURL + "/api/device?accessToken=" + deviceToken;
                break;
            case "ASSET":
                preparedEntity.remove("id");
                path = targetURL + "/api/asset";
                break;
            case "CUSTOMER":
                preparedEntity.remove("id");
                path = targetURL + "/api/customer";
                break;
            default:
                System.out.println("Неверный тип сущности для сохранения");
                break;
        }

        try {
            //TODO: обработка, что кастомер уже создан
            oldNewIds.put(oldID, ((JSONObject) MyHttpClient.getInstance().sendRequest(
                    MyHttpClient.Method.POST,
                    path,
                    preparedEntity,
                    tenantAuthToken
            ).get("id")).getString("id"));
        } catch (HttpRequestExecutionException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //TODO: убрать ненужные deepcopy параметров и протестировать
    public boolean saveEntityTelemetry(JSONObject entityInfo) {
        JSONObject entity = new JSONObject(entityInfo.get("entity").toString());
        JSONObject lastTelemetry = (JSONObject) entityInfo.get("lastTelemetry");
        String path = String.format(
                "%s/api/plugins/telemetry/%s/%s/timeseries/%s",
                targetURL,
                ((JSONObject) entity.get("id")).getString("entityType"),
                oldNewIds.get(((JSONObject) entity.get("id")).getString("id")),
                "TENANT_SCOPE");
        JSONObject telemetryBody = new JSONObject();

        lastTelemetry.keySet().forEach(key -> telemetryBody.put(
                key,
                ((JSONObject) ((JSONArray) lastTelemetry.get(key)).get(0)).getString("value")
        ));
        if (!telemetryBody.isEmpty()) {
            try {
                MyHttpClient.getInstance().sendRequest(
                        MyHttpClient.Method.POST,
                        path,
                        telemetryBody,
                        tenantAuthToken
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
// TODO: сделать для всех методов логику формирования возвращаемого boolean
    public boolean saveEntityAttributes(JSONObject entityInfo) {
        JSONObject entity = new JSONObject(entityInfo.get("entity").toString());

        for (AttributesScope scope : AttributesScope.values()) {
            JSONArray attributes = (JSONArray) entityInfo.get(scope.getBackupKey());
            if (!attributes.isEmpty()) {
                String path;

                if (scope == AttributesScope.CLIENT_SCOPE) {
                    if (((JSONObject) entity.get("id")).getString("entityType").equals("DEVICE")) {
                        path = String.format(
                                "%s/api/v1/%s/attributes",
                                targetURL,
                                ((JSONObject) entity.get("id")).getString("accessToken")
                        );
                    } else {
                        System.out.println("Ошибка сохранения клиентских атрибутов у сущности: " + entity);
                        System.out.println("Клиентские атрибуты есть только у девайсов!");

                        path = String.format(
                                "%s/api/plugins/telemetry/%s/%s/attributes/%s",
                                targetURL,
                                ((JSONObject) entity.get("id")).getString("entityType"),
                                oldNewIds.get(((JSONObject) entity.get("id")).getString("id")),
                                AttributesScope.SERVER_SCOPE);
                    }
                } else {
                    path = String.format(
                            "%s/api/plugins/telemetry/%s/%s/attributes/%s",
                            targetURL,
                            ((JSONObject) entity.get("id")).getString("entityType"),
                            oldNewIds.get(((JSONObject) entity.get("id")).getString("id")),
                            scope);
                }
                JSONObject attributesBody = new JSONObject();

                attributes.forEach(attribute -> attributesBody.put(
                        ((JSONObject) attribute).getString("key"),
                        ((JSONObject) attribute).get("value")
                ));
                try {
                    MyHttpClient.getInstance().sendRequest(
                            MyHttpClient.Method.POST,
                            path,
                            attributesBody,
                            tenantAuthToken
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public boolean saveEntityRelations(JSONObject entityInfo) {
        JSONArray oldIdRelations = ((JSONArray) entityInfo.get("relationsFrom")).putAll((JSONArray) entityInfo.get("relationsTo"));
        String path = targetURL + "/api/relation";

        JSONArray newIdRelations = new JSONArray();
        oldIdRelations.forEach(relation -> {
            JSONObject newIdRelation = (JSONObject) relation;

            JSONObject from = (JSONObject) ((JSONObject) relation).get("from");
            from.put("id", oldNewIds.get(from.getString("id")));
            newIdRelation.put("from", from);

            JSONObject to = (JSONObject) ((JSONObject) relation).get("to");
            to.put("id", oldNewIds.get(to.getString("id")));
            newIdRelation.put("to", to);

            newIdRelations.put(newIdRelation);
        });

        if (!newIdRelations.isEmpty()) {
            try {
                newIdRelations.forEach(relation -> MyHttpClient.getInstance().sendRequest(
                        MyHttpClient.Method.POST,
                        path,
                        (JSONObject) relation,
                        tenantAuthToken
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void clearPlatform() throws InvalidEntityTypeException, InterruptedException {
        //TODO: более корректная проверка
        if (targetURL.startsWith("http://localhost:")) {
            System.out.println(
                    "Платформа по адресу \""
                            + targetURL
                            + "\" будет очищена через:");
            for (int i = 5; i > 0 ; i--) {
                Thread.sleep(1000);
                System.out.println(i + "с");
            }
            System.out.println("Очистка платформы запущена!");
            getTbInfoController.getEntities(EntityType.DEVICE).forEach(device -> MyHttpClient.getInstance().sendRequest(
                    MyHttpClient.Method.DELETE,
                    targetURL + "/api/device/" + ((JSONObject) ((JSONObject) device).get("id")).get("id"),
                    null,
                    tenantAuthToken
            ));
            getTbInfoController.getEntities(EntityType.ASSET).forEach(asset -> MyHttpClient.getInstance().sendRequest(
                    MyHttpClient.Method.DELETE,
                    targetURL + "/api/asset/" + ((JSONObject) ((JSONObject) asset).get("id")).get("id"),
                    null,
                    tenantAuthToken
            ));
            getTbInfoController.getEntities(EntityType.CUSTOMER).forEach(customer -> MyHttpClient.getInstance().sendRequest(
                    MyHttpClient.Method.DELETE,
                    targetURL + "/api/customer/" + ((JSONObject) ((JSONObject) customer).get("id")).get("id"),
                    null,
                    tenantAuthToken
            ));
        } else {
            System.out.println("Очистка платформы по адресу \""+ targetURL + "\" не будет произведена. " +
                    "Для полной очистки доступны только платформы, адрес которых начинается с \"http://localhost:\"");
        }
    }
}
