package com.chupaniko.dataworker;

import com.chupaniko.controller.GetTbInfoController;
import com.chupaniko.exceptions.InvalidEntityTypeException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Получает "сырой" бэкап с Thingsboard
 */
public class GetTbInfo {

    private GetTbInfoController controller;
    private final String rawBackupFileName;

    public GetTbInfo(String accountKey) {
        JSONObject account = AccountsWorker.getInstance().getAccounts().getJSONObject(accountKey);
        controller = new GetTbInfoController(account);
        this.rawBackupFileName = "rawbackup_" + account.getString("name") + ".txt";
    }

    public void getBackup(Map<String, JSONArray> entities) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(rawBackupFileName)), StandardCharsets.UTF_8))) {
            //JSONArray devices = controller.getEntities(EntityType.DEVICE);
            JSONArray devices = entities.get(EntityType.DEVICE.toString());
            Iterator<Object> iterator = devices.iterator();

            int counter = 0;
            writer.write("{\"devicesInfo\":[");
            if (iterator.hasNext()) {
                JSONObject entity = (JSONObject) iterator.next();
                writer.write(controller.getEntityInfo(entity).toString());
                counter++;
                System.out.println("Getting devices info: " + Math.round(((double) counter / devices.length()) * 100) + "%");
            }
            while (iterator.hasNext()) {
                JSONObject entity = (JSONObject) iterator.next();
                writer.write("," + controller.getEntityInfo(entity).toString());
                counter++;
                System.out.println("Getting devices info: " + Math.round(((double) counter / devices.length()) * 100) + "%");
            }
            writer.write("],");

            counter = 0;
            //JSONArray assets = controller.getEntities(EntityType.ASSET);
            JSONArray assets = entities.get(EntityType.ASSET.toString());
            writer.write("\"assetsInfo\":[");
            iterator = assets.iterator();
            if (iterator.hasNext()) {
                JSONObject entity = (JSONObject) iterator.next();
                writer.write(controller.getEntityInfo(entity).toString());
                counter++;
                System.out.println("Getting assets info: " + Math.round(((double) counter / assets.length()) * 100) + "%");
            }
            while (iterator.hasNext()) {
                JSONObject entity = (JSONObject) iterator.next();
                writer.write("," + controller.getEntityInfo(entity).toString());
                counter++;
                System.out.println("Getting assets info: " + Math.round(((double) counter / assets.length()) * 100) + "%");
            }
            writer.write("],");

            counter = 0;
            //JSONArray customers = controller.getEntities(EntityType.CUSTOMER);
            JSONArray customers = entities.get(EntityType.CUSTOMER.toString());
            writer.write("\"customersInfo\":[");
            iterator = customers.iterator();
            if (iterator.hasNext()) {
                JSONObject entity = (JSONObject) iterator.next();
                writer.write(controller.getEntityInfo(entity).toString());
                counter++;
                System.out.println("Getting customers info: " + Math.round(((double) counter / customers.length()) * 100) + "%");
            }
            while (iterator.hasNext()) {
                JSONObject entity = (JSONObject) iterator.next();
                writer.write("," + controller.getEntityInfo(entity).toString());
                counter++;
                System.out.println("Getting customers info: " + Math.round(((double) counter / customers.length()) * 100) + "%");
            }
            writer.write("]}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public Map<String, JSONArray> getEntitiesFromPlatform() {
        Map<String, JSONArray> result = new HashMap<>();
        Arrays.stream(EntityType.values()).forEach(
                entityType -> {
                    try {
                        result.put(entityType.toString(), controller.getEntities(entityType));
                    } catch (InvalidEntityTypeException e) {
                        e.printStackTrace();
                    }
                }
        );
        return result;
    }
}
