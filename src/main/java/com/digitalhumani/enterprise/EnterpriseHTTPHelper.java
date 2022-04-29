package com.digitalhumani.enterprise;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import com.digitalhumani.enterprise.models.EnterpriseRequest;
import com.digitalhumani.enterprise.models.TreesPlantedForMonth;
import com.digitalhumani.exceptions.RaaSException;
import com.digitalhumani.interfaces.HTTPHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EnterpriseHTTPHelper implements HTTPHelper<EnterpriseRequest, TreesPlantedForMonth> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private String url = "";
    private String apiKey = "";

    // constants
    private static final String RELATIVE_URL = "/enterprise";
    private static final String USER_AGENT = "Digital Humani Java SDK";
    private static final String CONTENT_TYPE = "application/json";

    public EnterpriseHTTPHelper(String url, String apiKey) {
        this.url = url;
        this.apiKey = apiKey;
    }

    @Override
    public String toJson(EnterpriseRequest request) throws RaaSException {
        String requestBody = "";
        try {
            requestBody = this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            RaaSException raaSEx = new RaaSException(
                    "Unable to build JSON body for enterprise request from supplied parameters");
            raaSEx.initCause(ex);
            throw raaSEx;
        }
        return requestBody;
    }

    @Override
    public HttpRequest buildPostRequest(String requestBody) {
        // Not currently required
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpRequest buildGetRequest(HashMap<String, String> queryParams) {
        // Not currently required
        throw new UnsupportedOperationException();
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
        // Not currently required
        throw new UnsupportedOperationException();
    }

    @Override
    public Function<HttpResponse<String>, TreesPlantedForMonth> parseResponse() {
        return (HttpResponse<String> response) -> {
            if (response.statusCode() == 404) {
                RaaSException raasEx = new RaaSException("Could not find enterprise data.");
                return new TreesPlantedForMonth(raasEx);
            }

            if (response.statusCode() == 401) {
                RaaSException raasEx = new RaaSException("Not authorised - check your API key.");
                return new TreesPlantedForMonth(raasEx);
            }
            
            try {
                return this.objectMapper.readValue(response.body(), TreesPlantedForMonth.class);
            } catch (JsonProcessingException e) {
                RaaSException raasEx = new RaaSException("Failed to parse response from RaaS API.");
                raasEx.initCause(e);
                return new TreesPlantedForMonth(raasEx);
            }
        };
    }

    @Override
    public Function<HttpResponse<String>, Boolean> wasSuccess() {
        // Not currently required
        throw new UnsupportedOperationException();
    }
    
}
