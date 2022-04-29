package com.digitalhumani.enterprise.interfaces;

import java.util.concurrent.CompletableFuture;

import com.digitalhumani.enterprise.models.TreesPlantedForMonth;

public interface Enterprise {
    public CompletableFuture<TreesPlantedForMonth> getTreesPlantedForMonth(String enterpriseId, String month);
}
