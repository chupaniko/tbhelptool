package com.chupaniko.dataworker;

public enum AttributesScope {
    SERVER_SCOPE {
        public String getBackupKey() {
            return "serverAttributes";
        }
    },
    CLIENT_SCOPE {
        public String getBackupKey() {
            return "clientAttributes";
        }
    },
    SHARED_SCOPE {
        public String getBackupKey() {
            return "sharedAttributes";
        }
    };

    public abstract String getBackupKey();
}
