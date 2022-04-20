package com.digitalhumani.interfaces;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import com.digitalhumani.exceptions.RaaSException;

public interface HTTPHelper<U, T> {
    String toJson(U request) throws RaaSException;
    HttpRequest buildPostRequest(String requestBody);
    HttpRequest buildGetRequest(HashMap<String, String> queryParams);
    HttpRequest buildGetRequest(List<String> params);
    HttpRequest buildDeleteRequest(List<String> params);
    Function<HttpResponse<String>, T> parseResponse();
    Function<HttpResponse<String>, Boolean> wasSuccess();
}
