package com.digitalhumani.models;

import java.util.Date;

import com.digitalhumani.exceptions.RaaSException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public final class TreesPlanted extends RaaSResult {
    
    private String uuid;
    private String enterpriseId;
    private String projectId;
    private String user;
    private Integer treeCount;
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date created;

    public String getUUId() {
        return this.uuid;
    }

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

    public Date getCreated() {
        return this.created;
    }

    public TreesPlanted() {
        super();
    }

    public TreesPlanted(String uuid, String enterpriseId, String projectId, String user, Integer treeCount) {
        super();
        this.uuid = uuid;
        this.enterpriseId = enterpriseId;
        this.projectId = projectId;
        this.user = user;
        this.treeCount = treeCount;
    }

    public TreesPlanted(RaaSException exception) {
        super(exception);
    }

}
