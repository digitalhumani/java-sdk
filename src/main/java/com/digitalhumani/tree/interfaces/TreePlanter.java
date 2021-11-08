package com.digitalhumani.tree.interfaces;

import java.util.concurrent.CompletableFuture;

import com.digitalhumani.exceptions.RaaSException;
import com.digitalhumani.tree.models.TreesPlanted;

public interface TreePlanter {
    public CompletableFuture<TreesPlanted> plantATree(String enterpriseId, String projectId, String user) throws RaaSException;
    public CompletableFuture<TreesPlanted> plantSomeTrees(String enterpriseId, String projectId, String user, Integer treeCount) throws RaaSException;
}
