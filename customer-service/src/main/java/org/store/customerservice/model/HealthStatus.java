package org.store.customerservice.model;

import lombok.ToString;

@ToString
public enum HealthStatus {

    UP("UP"),
    DOWN("DOWN");

    private final String status;

    HealthStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
