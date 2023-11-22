package com.chupaniko.controller;

import com.chupaniko.dataworker.EntityType;
import com.chupaniko.exceptions.InvalidEntityTypeException;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetTbInfoController {
    private final String targetURL;
    private final String tenantAuthToken;
    private final String sysadminAuthToken;

    public GetTbInfoController(JSONObject account) {
        this.targetURL = account.get("url").toString();
        TbAuthController authController = new TbAuthController();

        this.tenantAuthToken = authController.getToken(account, TbAuthController.TbUserRole.TENANT);
        this.sysadminAuthToken = authController.getToken(account, TbAuthController.TbUserRole.SYSAMDIN);
    }

    /**
     * Gets all Thingsboard entities by specified type ({@link EntityType}).
     *
     * @param type Thingsboard entity type.
     * @return
     * @throws InvalidEntityTypeException
     */
    public JSONArray getEntities(EntityType type) throws InvalidEntityTypeException {
        String path;
        switch (type) {
            case CUSTOMER:
                path = targetURL + "/api/customers?limit=1000000000";
                break;
            case ASSET:
                path = targetURL + "/api/tenant/assets?limit=1000000000";
                break;
            case DEVICE:
                path = targetURL + "/api/tenant/devices?limit=1000000000";
                break;
            default:
                throw new InvalidEntityTypeException();
        }
        //TODO: попробовать просто getJSONArray("data")
        return new JSONArray(MyHttpClient.getInstance().sendRequest(
                MyHttpClient.Method.GET,
                path,
                null,
                tenantAuthToken
        ).get("data").toString());
    }

    public JSONObject getEntityInfo(JSONObject entityData) {
        JSONObject entityInfo = new JSONObject();
        if (entityData.getJSONObject("id").getString("entityType").equals("DEVICE")) {
            JSONObject id = entityData.getJSONObject("id");
            id.put("accessToken", requestJSONObject(
                    targetURL
                    + "/api/device/"
                    + entityData.getJSONObject("id").getString("id")
                    + "/credentials"
            ).getString("credentialsId"));
            entityData.put("id", id);
        }
        entityInfo.put("entity", entityData);
        entityInfo.put("clientAttributes", requestJSONArray(
                targetURL
                        + "/api/plugins/telemetry/"
                        + entityData.getJSONObject("id").getString("entityType")
                        + "/"
                        + entityData.getJSONObject("id").getString("id")
                        + "/values/attributes/CLIENT_SCOPE"
        ));
        entityInfo.put("serverAttributes", requestJSONArray(
                targetURL
                        + "/api/plugins/telemetry/"
                        + entityData.getJSONObject("id").getString("entityType")
                        + "/"
                        + entityData.getJSONObject("id").getString("id")
                        + "/values/attributes/SERVER_SCOPE"
        ));
        entityInfo.put("sharedAttributes", requestJSONArray(
                targetURL
                        + "/api/plugins/telemetry/"
                        + entityData.getJSONObject("id").getString("entityType")
                        + "/"
                        + entityData.getJSONObject("id").getString("id")
                        + "/values/attributes/SHARED_SCOPE"
        ));
        entityInfo.put("lastTelemetry", requestJSONObject(
                targetURL
                        + "/api/plugins/telemetry/"
                        + entityData.getJSONObject("id").getString("entityType")
                        + "/"
                        + entityData.getJSONObject("id").getString("id")
                        + "/values/timeseries?useStrictDataTypes=false"
        ));
        entityInfo.put("telemetryKeys", requestJSONArray(
                targetURL
                        + "/api/plugins/telemetry/"
                        + entityData.getJSONObject("id").getString("entityType")
                        + "/"
                        + entityData.getJSONObject("id").getString("id")
                        + "/keys/timeseries"
        ));
        entityInfo.put("relationsFrom", requestJSONArray(
                targetURL
                        + "/api/relations/info?fromId="
                        + entityData.getJSONObject("id").getString("id")
                        + "&fromType="
                        + entityData.getJSONObject("id").getString("entityType")
        ));
        entityInfo.put("relationsTo", requestJSONArray(
                targetURL
                        + "/api/relations/info?toId="
                        + entityData.getJSONObject("id").getString("id")
                        + "&toType="
                        + entityData.getJSONObject("id").getString("entityType")
        ));

        if (entityData.getJSONObject("id").getString("entityType").equals("CUSTOMER")) {
            JSONObject wrapperJSON = new JSONObject();
            wrapperJSON.put("customer", entityInfo);
            wrapperJSON.put("customerUsers", requestJSONArray(
                    targetURL
                            + "/api/customer/"
                            + entityData.getJSONObject("id").getString("id")
                            + "/users?limit=1000000"
            ));
            return wrapperJSON;
        }

        return entityInfo;
    }

    private JSONObject requestJSONObject(String path) {
        return MyHttpClient.getInstance().sendRequest(
                MyHttpClient.Method.GET,
                path
                , null
                , tenantAuthToken
        );
    }

    private JSONArray requestJSONArray(String path) {
        JSONObject response = MyHttpClient.getInstance().sendRequest(
                MyHttpClient.Method.GET,
                path
                , null
                , tenantAuthToken
        );
        return response.has("data")
                ? response.getJSONArray("data")
                : new JSONArray();
    }
}
