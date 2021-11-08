package com.digitalhumani.tree.models;

public class TreePlantingRequest {
    private String enterpriseId;
    private String projectId;
    private String user;
    private Integer treeCount;

    public String getEnterpriseId() {
        return this.enterpriseId;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public String getUser(){
        return this.user;
    }

    public Integer getTreeCount() {
        return this.treeCount;
    }

    public TreePlantingRequest(String enterpriseId, String projectId, String user, Integer treeCount) {
        this.enterpriseId = enterpriseId;
        this.projectId = projectId;
        this.user = user;
        this.treeCount = treeCount;
    }
}
