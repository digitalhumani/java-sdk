package com.digitalhumani.tree;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

import com.digitalhumani.tree.interfaces.TreePlanter;
import com.digitalhumani.tree.models.TreePlantingRequest;
import com.digitalhumani.tree.models.TreesPlanted;
import com.digitalhumani.exceptions.RaaSException;
import com.digitalhumani.interfaces.HTTPHelper;

public class RaaSTreePlanter implements TreePlanter {

    private HTTPHelper<TreePlantingRequest, TreesPlanted> httpHelper;
    private HttpClient httpClient;

    RaaSTreePlanter(HTTPHelper<TreePlantingRequest, TreesPlanted> httpHelper) {
        this.httpHelper = httpHelper;
        this.httpClient = HttpClient.newHttpClient();
    }

    public RaaSTreePlanter(String url, String apiKey) {
        this.httpHelper = new TreePlanterHTTPHelper(url, apiKey);
        this.httpClient = HttpClient.newHttpClient();
    }

    public CompletableFuture<TreesPlanted> plantATree(String enterpriseId, String projectId,
            String user) throws RaaSException {

        TreePlantingRequest treeRequest = new TreePlantingRequest(enterpriseId, projectId, user, 1);

        String requestBody = this.httpHelper.toJson(treeRequest);

        HttpRequest request = this.httpHelper.buildRequest(requestBody);

        return this.httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body)
                .thenApply(this.httpHelper.parseResponse());
    }

    public CompletableFuture<TreesPlanted> plantSomeTrees(String enterpriseId,
            String projectId, String user, Integer treeCount) throws RaaSException {

        TreePlantingRequest treeRequest = new TreePlantingRequest(enterpriseId, projectId, user, treeCount);

        String requestBody = this.httpHelper.toJson(treeRequest);

        HttpRequest request = this.httpHelper.buildRequest(requestBody);

        return this.httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body)
                .thenApply(this.httpHelper.parseResponse());

    }

}