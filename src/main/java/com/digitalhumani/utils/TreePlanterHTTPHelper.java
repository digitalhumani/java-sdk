package com.digitalhumani.utils;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.function.Function;

import com.digitalhumani.exceptions.RaaSException;
import com.digitalhumani.interfaces.HTTPHelper;
import com.digitalhumani.models.TreePlantingRequest;
import com.digitalhumani.models.TreesPlanted;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TreePlanterHTTPHelper implements HTTPHelper<TreePlantingRequest, TreesPlanted> {

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

    public HttpRequest buildRequest(String requestBody) {
        return HttpRequest.newBuilder(URI.create(this.url + RELATIVE_URL)).setHeader("Content-Type", CONTENT_TYPE)
                .setHeader("X-API-KEY", this.apiKey).setHeader("User-Agent", USER_AGENT)
                .POST(BodyPublishers.ofString(requestBody)).build();
    }

    public Function<String, TreesPlanted> parseResponse() {
        return (String stringBody) -> {
            try {
                return this.objectMapper.readValue(stringBody, TreesPlanted.class);
            } catch (JsonProcessingException e) {
                RaaSException raasEx = new RaaSException("Failed to parse response from RaaS API");
                raasEx.initCause(e);
                return new TreesPlanted(raasEx);
            }
        };
    }
}
