package com.digitalhumani.interfaces;

import java.net.http.HttpRequest;
import java.util.function.Function;

import com.digitalhumani.exceptions.RaaSException;

public interface HTTPHelper<U, T> {
    String toJson(U request) throws RaaSException;
    HttpRequest buildRequest(String url, String apiKey, String requestBody);
    Function<String, T> parseResponse();
}
