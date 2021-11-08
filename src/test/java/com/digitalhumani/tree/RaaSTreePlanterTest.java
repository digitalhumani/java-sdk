package com.digitalhumani.tree;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.digitalhumani.exceptions.RaaSException;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class RaaSTreePlanterTest  {

    private static final String CONTENT_TYPE = "application/json";
    private static final String USER_AGENT = "Digital Humani Java SDK";
    private static final String X_API_KEY = "Junit";
    private static final Integer HTTP_PORT = 8080;

    @RegisterExtension
    static WireMockExtension raasMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(HTTP_PORT))
            .build();

    @Test
    public void should_Return_TreePlanted_Object_With_Valid_Request_One_Tree() throws Exception {

        String uuid = "eef9f369-9ae0-45b8-ab07-10650f53a71e";
        String created = "2019-05-17T00:36:25.797Z";
        String enterpriseId = "123";
        String projectId = "123";
        String user = "JUnit";
        Integer treeCount = 1;

        raasMock.stubFor(post("/tree")
            .withHeader("Content-Type", equalTo(CONTENT_TYPE))
            .withHeader("User-Agent", equalTo(USER_AGENT))
            .withHeader("X-API-KEY", equalTo(X_API_KEY))
            .withRequestBody(equalToJson(String.format("{ \"treeCount\": %s, \"enterpriseId\": \"%s\", \"projectId\": \"%s\", \"user\": \"%s\" }", treeCount, enterpriseId, projectId, user)))
            .willReturn(okJson(String.format("{ \"uuid\": \"%s\", \"created\": \"%s\", \"treeCount\": %s, \"enterpriseId\": \"%s\", \"projectId\": \"%s\", \"user\": \"%s\" }", uuid, created, treeCount, enterpriseId, projectId, user))));

        RaaSTreePlanter raasPlanter = new RaaSTreePlanter("http://localhost:" + HTTP_PORT, X_API_KEY);

        var future = raasPlanter.plantATree(enterpriseId, projectId, user).thenAccept(s -> {
            assertTrue(s.isSuccess());
            assertEquals(null, s.getException());
            assertEquals(uuid, s.getUUId());
            assertEquals(projectId, s.getProjectId());
            assertEquals(enterpriseId, s.getEnterpriseId());
            assertEquals(user, s.getUser());
            assertEquals(treeCount, s.getTreeCount());
        });
        future.get();
    }

    @Test
    public void should_Return_RaaSException_With_TreePlanted_Object_With_Invalid_Request_One_Tree() throws Exception {
        String enterpriseId = "123";
        String projectId = "123";
        String user = "JUnit";

        raasMock.stubFor(post("/tree")
            .withHeader("Content-Type", equalTo(CONTENT_TYPE))
            .withHeader("User-Agent", equalTo(USER_AGENT))
            .withHeader("X-API-KEY", equalTo(X_API_KEY))
            .willReturn(badRequest().withBody("foo")));

        RaaSTreePlanter raasPlanter = new RaaSTreePlanter("http://localhost:" + HTTP_PORT, X_API_KEY);

        var future = raasPlanter.plantATree(enterpriseId, projectId, user).thenAccept(s -> {
            assertFalse(s.isSuccess());
            assertEquals(RaaSException.class, s.getException().getClass());
            assertEquals(null, s.getUUId());
            assertEquals(null, s.getProjectId());
            assertEquals(null, s.getEnterpriseId());
            assertEquals(null, s.getUser());
            assertEquals(null, s.getTreeCount());
        });
        future.get();
    }
}
