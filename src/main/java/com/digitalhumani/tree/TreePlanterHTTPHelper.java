package com.digitalhumani.tree;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.digitalhumani.exceptions.RaaSException;
import com.digitalhumani.interfaces.HTTPHelper;
import com.digitalhumani.tree.models.TreePlantingRequest;
import com.digitalhumani.tree.models.TreesPlanted;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class TreePlanterHTTPHelper implements HTTPHelper<TreePlantingRequest, TreesPlanted> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private String url = "";
    private String apiKey = "";

    // constants
    private static final String RELATIVE_URL = "/tree";
    private static final String USER_AGENT = "Digital Humani Java SDK";
    private static final String CONTENT_TYPE = "application/json";

    public TreePlanterHTTPHelper(String url, String apiKey) {
        this.url = url;
        this.apiKey = apiKey;
    }

    @Override
    public String toJson(TreePlantingRequest request) throws RaaSException {
        String requestBody = "";
        try {
            requestBody = this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            RaaSException raaSEx = new RaaSException(
                    "Unable to build JSON body for tree planting request from supplied parameters");
            raaSEx.initCause(ex);
            throw raaSEx;
        }
        return requestBody;
    }

    @Override
    public HttpRequest buildPostRequest(String requestBody) {
        return HttpRequest.newBuilder(URI.create(this.url + RELATIVE_URL)).setHeader("Content-Type", CONTENT_TYPE)
                .setHeader("X-API-KEY", this.apiKey).setHeader("User-Agent", USER_AGENT)
                .POST(BodyPublishers.ofString(requestBody)).build();
    }

    @Override
    public Function<HttpResponse<String>, TreesPlanted> parseResponse() {
        return (HttpResponse<String> response) -> {
            if (response.statusCode() == 404) {
                RaaSException raasEx = new RaaSException("Could not find tree planted.");
                return new TreesPlanted(raasEx);
            }

            if (response.statusCode() == 401) {
                RaaSException raasEx = new RaaSException("Not authorised - check your API key.");
                return new TreesPlanted(raasEx);
            }
            
            try {
                return this.objectMapper.readValue(response.body(), TreesPlanted.class);
            } catch (JsonProcessingException e) {
                RaaSException raasEx = new RaaSException("Failed to parse response from RaaS API.");
                raasEx.initCause(e);
                return new TreesPlanted(raasEx);
            }
        };
    }

    @Override
    public HttpRequest buildGetRequest(HashMap<String, String> queryParams) {

        StringBuilder queryStringBuilder = new StringBuilder();
        queryParams.forEach((k, v) -> queryStringBuilder.append(String.format("&%s=%s", k, v)));
        String queryString = queryStringBuilder.toString().substring(1); // remove first &

        return HttpRequest
                .newBuilder(URI.create(String.format("%s%s?%s", this.url, RELATIVE_URL, queryString)))
                .setHeader("Content-Type", CONTENT_TYPE).setHeader("X-API-KEY", this.apiKey)
                .setHeader("User-Agent", USER_AGENT).GET().build();

    }

    @Override
    public HttpRequest buildGetRequest(List<String> params) {
        
        StringBuilder paramBuilder = new StringBuilder();
        params.forEach(item -> paramBuilder.append(String.format("/%s", item)));
        
        return HttpRequest
                .newBuilder(URI.create(String.format("%s%s%s", this.url, RELATIVE_URL, paramBuilder.toString())))
                .setHeader("Content-Type", CONTENT_TYPE).setHeader("X-API-KEY", this.apiKey)
                .setHeader("User-Agent", USER_AGENT).GET().build();

    }

    @Override
    public HttpRequest buildDeleteRequest(List<String> params) {

        StringBuilder paramBuilder = new StringBuilder();
        params.forEach(item -> paramBuilder.append(String.format("/%s", item)));
    
        return HttpRequest
                .newBuilder(URI.create(String.format("%s%s%s", this.url, RELATIVE_URL, paramBuilder.toString())))
                .setHeader("Content-Type", CONTENT_TYPE).setHeader("X-API-KEY", this.apiKey)
                .setHeader("User-Agent", USER_AGENT).DELETE().build();

    }

    @Override
    public Function<HttpResponse<String>, Boolean> wasSuccess() {
        return (HttpResponse<String> response) -> {
            if (IntStream.range(200, 299).filter(item -> item == response.statusCode()).findAny().isEmpty()) {
                return false;
            } else {
                return true;
            }
        };
    }

}
