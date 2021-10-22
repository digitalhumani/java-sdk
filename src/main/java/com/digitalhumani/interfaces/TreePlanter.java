package com.digitalhumani.interfaces;

import java.util.concurrent.CompletableFuture;

import com.digitalhumani.exceptions.RaaSException;
import com.digitalhumani.models.TreesPlanted;

public interface TreePlanter {
    public CompletableFuture<TreesPlanted> plantATree(String url, String enterpriseId, String apiKey, String projectId, String user) throws RaaSException;
    public CompletableFuture<TreesPlanted> plantSomeTrees(String url, String enterpriseId, String apiKey, String projectId, String user, Integer treeCount) throws RaaSException;
}
