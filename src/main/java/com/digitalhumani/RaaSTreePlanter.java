package com.digitalhumani;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

import com.digitalhumani.models.TreePlantingRequest;
import com.digitalhumani.models.TreesPlanted;
import com.digitalhumani.utils.TreePlanterHTTPHelper;
import com.digitalhumani.exceptions.RaaSException;
import com.digitalhumani.interfaces.HTTPHelper;
import com.digitalhumani.interfaces.TreePlanter;

class RaaSTreePlanter implements TreePlanter {

    private HTTPHelper<TreePlantingRequest, TreesPlanted> httpHelper;

    RaaSTreePlanter(HTTPHelper<TreePlantingRequest, TreesPlanted> httpHelper) {
        this.httpHelper = httpHelper;
    }

    public RaaSTreePlanter() {
        this.httpHelper = new TreePlanterHTTPHelper();
    }

    public CompletableFuture<TreesPlanted> plantATree(String url, String enterpriseId, String apiKey, String projectId,
            String user) throws RaaSException {

        TreePlantingRequest treeRequest = new TreePlantingRequest(enterpriseId, projectId, user, 1);

        String requestBody = this.httpHelper.toJson(treeRequest);

        HttpRequest request = this.httpHelper.buildRequest(url + "/tree", apiKey, requestBody);

        return HttpClient.newHttpClient().sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body)
                .thenApply(this.httpHelper.parseResponse());
    }

    public CompletableFuture<TreesPlanted> plantSomeTrees(String url, String enterpriseId, String apiKey,
            String projectId, String user, Integer treeCount) throws RaaSException {

        TreePlantingRequest treeRequest = new TreePlantingRequest(enterpriseId, projectId, user, treeCount);

        String requestBody = this.httpHelper.toJson(treeRequest);

        HttpRequest request = this.httpHelper.buildRequest(url + "/tree", apiKey, requestBody);

        return HttpClient.newHttpClient().sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body)
                .thenApply(this.httpHelper.parseResponse());

    }

}