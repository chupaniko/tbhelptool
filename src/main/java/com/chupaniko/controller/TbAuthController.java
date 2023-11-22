package com.chupaniko.controller;

import org.json.JSONObject;

public class TbAuthController {
    public String getToken(JSONObject account, TbUserRole tbUserRole) {
        String targetURL = account.getString("url");
        JSONObject loginPassword = new JSONObject();

        loginPassword.put("username", account.get(tbUserRole.getUsernameBackupKey()));
        loginPassword.put("password", account.get(tbUserRole.getPasswordBackupKey()));
        return MyHttpClient.getInstance().sendRequest(
                MyHttpClient.Method.POST,
                targetURL + "/api/auth/login",
                loginPassword,
                null
        ).getString("token");
    }


    public enum TbUserRole {
        TENANT {
            public String getUsernameBackupKey() {
                return "tenantAdminUsername";
            }
            public String getPasswordBackupKey() {
                return "tenantAdminPassword";
            }
        },
        SYSAMDIN {
            public String getUsernameBackupKey() {
                return "sysadminUsername";
            }
            public String getPasswordBackupKey() {
                return "sysadminPassword";
            }
        },
        CUSTOMER {
            public String getUsernameBackupKey() {
                //TODO: переписать заглушки на exceptions
                return "";
            }
            public String getPasswordBackupKey() {
                return "";
            }
        };
        public abstract String getUsernameBackupKey();
        public abstract String getPasswordBackupKey();
    }
}
