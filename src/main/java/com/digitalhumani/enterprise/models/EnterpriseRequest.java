package com.digitalhumani.enterprise.models;

public class EnterpriseRequest {
    private String enterpriseId;
    private String month;

    public String getEnterpriseId() {
        return this.enterpriseId;
    }

    public String getMonth() {
        return this.month;
    }

    public EnterpriseRequest(String enterpriseId, String month) {
        this.enterpriseId = enterpriseId;
        this.month = month;
    }

}
