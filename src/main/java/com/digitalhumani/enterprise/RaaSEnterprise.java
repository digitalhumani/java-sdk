package com.digitalhumani.enterprise;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.digitalhumani.enterprise.interfaces.Enterprise;
import com.digitalhumani.enterprise.models.EnterpriseRequest;
import com.digitalhumani.enterprise.models.TreesPlantedForMonth;
import com.digitalhumani.interfaces.HTTPHelper;

public class RaaSEnterprise implements Enterprise {

    private HTTPHelper<EnterpriseRequest, TreesPlantedForMonth> httpHelper;
    private HttpClient httpClient;

    RaaSEnterprise(HTTPHelper<EnterpriseRequest, TreesPlantedForMonth> httpHelper) {
        this.httpHelper = httpHelper;
        this.httpClient = HttpClient.newHttpClient();
    }

    public RaaSEnterprise(String url, String apiKey) {
        this.httpHelper = new EnterpriseHTTPHelper(url, apiKey);
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public CompletableFuture<TreesPlantedForMonth> getTreesPlantedForMonth(String enterpriseId, String month) {
        List<String> params = new ArrayList<>();
        params.add(enterpriseId);
        params.add("treeCount");
        params.add(month);

        HttpRequest request = this.httpHelper.buildGetRequest(params);

        return this.httpClient.sendAsync(request, BodyHandlers.ofString()).thenApply(this.httpHelper.parseResponse());
    }
    
}
