package com.digitalhumani.enterprise.models;

import com.digitalhumani.exceptions.RaaSException;
import com.digitalhumani.models.RaaSResult;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class TreesPlantedForMonth extends RaaSResult {
    
    private Integer totalTrees;

    @JsonProperty("count")
    public Integer getTotalTrees() {
        return this.totalTrees;
    }

    public TreesPlantedForMonth(){
        super();
    }

    public TreesPlantedForMonth(Integer totalTrees) {
        super();
        this.totalTrees = totalTrees;
    }

    public TreesPlantedForMonth(RaaSException exception) {
        super(exception);
    }

}
