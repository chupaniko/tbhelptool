package com.chupaniko.dataworker;

public enum EntityType {
    CUSTOMER {
        public String getEntitiesKey() {
            return "customersInfo";
        }
    },
    ASSET {
        public String getEntitiesKey() {
            return "assetsInfo";
        }
    },
    DEVICE {
        public String getEntitiesKey() {
            return "devicesInfo";
        }
    };

    public abstract String getEntitiesKey();
}
