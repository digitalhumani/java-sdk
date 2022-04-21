package com.digitalhumani.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.digitalhumani.exceptions.RaaSException;
import com.digitalhumani.tree.models.TreePlantingRequest;
import com.digitalhumani.tree.models.TreesPlanted;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class TreePlanterHTTPHelperTest {

    @Test
    public void should_Return_JSON_String_For_Valid_TreePlantingRequest() throws Exception {
        String url = "http://foo.bar";
        String apiKey = "key";
        String enterpriseId = "123";
        String projectId = "123";
        String user = "JUnit";
        Integer treeCount = 1;

        TreePlanterHTTPHelper helper = new TreePlanterHTTPHelper(url, apiKey);
        TreePlantingRequest request = new TreePlantingRequest(enterpriseId, projectId, user, treeCount);

        String expected = String.format(
                "{ \"enterpriseId\" : \"%s\", \"projectId\" : \"%s\", \"user\" : \"%s\", \"treeCount\" : %s }",
                enterpriseId, projectId, user, treeCount);
        String actual = helper.toJson(request);

        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    public void should_Throw_RaaSException_With_Invalid_TreePlantingRequest() {
        String url = "http://foo.bar";
        String apiKey = "key";

        TreePlanterHTTPHelper helper = new TreePlanterHTTPHelper(url, apiKey);
        TreePlantingRequest mockRequest = mock(TreePlantingRequest.class);

        // https://stackoverflow.com/questions/26716020/how-to-get-a-jsonprocessingexception-using-jackson
        when(mockRequest.toString()).thenReturn(mockRequest.getClass().getName());

        RaaSException exception = assertThrows(RaaSException.class, () -> {
            helper.toJson(mockRequest);
        });
        assertEquals("Unable to build JSON body for tree planting request from supplied parameters",
                exception.getMessage());
    }

    @Test
    public void should_Build_A_Valid_HTTP_POST_Request() {
        String url = "http://foo.bar";
        String apiKey = "key";

        TreePlanterHTTPHelper helper = new TreePlanterHTTPHelper(url, apiKey);
        HttpRequest request = helper.buildPostRequest("foo");

        assertEquals("application/json", request.headers().firstValue("Content-Type").get());
        assertEquals("Digital Humani Java SDK", request.headers().firstValue("User-Agent").get());
        assertEquals(apiKey, request.headers().firstValue("X-API-KEY").get());
        assertEquals(url + "/tree", request.uri().toString());
        assertEquals("POST", request.method());
    }

    @Test
    public void should_Build_A_Valid_HTTP_GET_Request_With_Query_Params() {
        String url = "http://foo.bar";
        String apiKey = "key";
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("foo", "bar");

        TreePlanterHTTPHelper helper = new TreePlanterHTTPHelper(url, apiKey);
        HttpRequest request = helper.buildGetRequest(params);

        assertEquals("application/json", request.headers().firstValue("Content-Type").get());
        assertEquals("Digital Humani Java SDK", request.headers().firstValue("User-Agent").get());
        assertEquals(apiKey, request.headers().firstValue("X-API-KEY").get());
        assertEquals(url + "/tree?foo=bar", request.uri().toString());
        assertEquals("GET", request.method());

    }

    @Test
    public void should_Build_A_Valid_HTTP_GET_Request_With_Params() {
        String url = "http://foo.bar";
        String apiKey = "key";
        
        List<String> param = new ArrayList<>();
        param.add("foobar");

        TreePlanterHTTPHelper helper = new TreePlanterHTTPHelper(url, apiKey);
        HttpRequest request = helper.buildGetRequest(param);

        assertEquals("application/json", request.headers().firstValue("Content-Type").get());
        assertEquals("Digital Humani Java SDK", request.headers().firstValue("User-Agent").get());
        assertEquals(apiKey, request.headers().firstValue("X-API-KEY").get());
        assertEquals(url + "/tree/foobar", request.uri().toString());
        assertEquals("GET", request.method());

    }

    @Test
    public void should_Build_A_Valid_HTTP_DELETE_Request_With_Params() {
        String url = "http://foo.bar";
        String apiKey = "key";
        
        List<String> param = new ArrayList<>();
        param.add("foobar");

        TreePlanterHTTPHelper helper = new TreePlanterHTTPHelper(url, apiKey);
        HttpRequest request = helper.buildDeleteRequest(param);

        assertEquals("application/json", request.headers().firstValue("Content-Type").get());
        assertEquals("Digital Humani Java SDK", request.headers().firstValue("User-Agent").get());
        assertEquals(apiKey, request.headers().firstValue("X-API-KEY").get());
        assertEquals(url + "/tree/foobar", request.uri().toString());
        assertEquals("DELETE", request.method());

    }

    @Test
    public void should_Return_A_TreesPlanted_Object_For_A_Valid_JSON_String() {
        String url = "http://foo.bar";
        String apiKey = "key";

        String uuid = "eef9f369-9ae0-45b8-ab07-10650f53a71e";
        String created = "2019-05-17T00:36:25.797Z";
        String enterpriseId = "123";
        String projectId = "123";
        String user = "JUnit";
        Integer treeCount = 1;

        TreePlanterHTTPHelper helper = new TreePlanterHTTPHelper(url, apiKey);
        String treesPlantedAsString = String.format(
                "{ \"uuid\": \"%s\", \"created\": \"%s\", \"treeCount\": %s, \"enterpriseId\": \"%s\", \"projectId\": \"%s\", \"user\": \"%s\" }",
                uuid, created, treeCount, enterpriseId, projectId, user);

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(treesPlantedAsString);

        TreesPlanted result = helper.parseResponse().apply(mockResponse);

        assertEquals(uuid, result.getUUId());

        // Just checking day, month and year since timezones/offsets just add
        // unnecessary complexity here.
        Calendar cal = Calendar.getInstance();
        cal.setTime(result.getCreated());
        assertEquals(17, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(4, cal.get(Calendar.MONTH)); // julian / gregorian month starts from 0
        assertEquals(2019, cal.get(Calendar.YEAR));

        assertEquals(enterpriseId, result.getEnterpriseId());
        assertEquals(projectId, result.getProjectId());
        assertEquals(user, result.getUser());
        assertEquals(treeCount, result.getTreeCount());
    }

    @Test
    public void should_Throw_RaaSException_When_Parsing_Invalid_Response(){
        String url = "http://foo.bar";
        String apiKey = "key";

        TreePlanterHTTPHelper helper = new TreePlanterHTTPHelper(url, apiKey);

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("Foo");

        TreesPlanted result = helper.parseResponse().apply(mockResponse);
        assertFalse(result.isSuccess());
        assertEquals(RaaSException.class, result.getException().getClass());
        assertEquals("Failed to parse response from RaaS API.", result.getException().getMessage());

    }

    @Test
    public void should_Return_Correct_Error_Message_When_HTTP_Code_Is_401(){
        String url = "http://foo.bar";
        String apiKey = "key";

        TreePlanterHTTPHelper helper = new TreePlanterHTTPHelper(url, apiKey);

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(401);
        when(mockResponse.body()).thenReturn("Foo");

        TreesPlanted result = helper.parseResponse().apply(mockResponse);
        assertFalse(result.isSuccess());
        assertEquals(RaaSException.class, result.getException().getClass());
        assertEquals("Not authorised - check your API key.", result.getException().getMessage());

    }

    @Test
    public void should_Return_Correct_Error_Message_When_HTTP_Code_Is_404(){
        String url = "http://foo.bar";
        String apiKey = "key";

        TreePlanterHTTPHelper helper = new TreePlanterHTTPHelper(url, apiKey);

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockResponse.body()).thenReturn("Foo");

        TreesPlanted result = helper.parseResponse().apply(mockResponse);
        assertFalse(result.isSuccess());
        assertEquals(RaaSException.class, result.getException().getClass());
        assertEquals("Could not find tree planted.", result.getException().getMessage());

    }

    @Test
    public void should_Return_True_When_HTTP_Code_Is_Success(){
        String url = "http://foo.bar";
        String apiKey = "key";

        TreePlanterHTTPHelper helper = new TreePlanterHTTPHelper(url, apiKey);

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        
        Boolean result = helper.wasSuccess().apply(mockResponse);
        assertTrue(result);

    }

    @Test
    public void should_Return_True_When_HTTP_Code_Is_Not_Success(){
        String url = "http://foo.bar";
        String apiKey = "key";

        TreePlanterHTTPHelper helper = new TreePlanterHTTPHelper(url, apiKey);

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(500);
        
        Boolean result = helper.wasSuccess().apply(mockResponse);
        assertFalse(result);

    }
}
