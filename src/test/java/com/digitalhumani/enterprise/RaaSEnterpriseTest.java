
package com.digitalhumani.enterprise;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.digitalhumani.exceptions.RaaSException;
import com.digitalhumani.tree.RaaSTreePlanter;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class RaaSEnterpriseTest {

    private static final String CONTENT_TYPE = "application/json";
    private static final String USER_AGENT = "Digital Humani Java SDK";
    private static final String X_API_KEY = "Junit";
    private static final Integer HTTP_PORT = 8080;
    
    @RegisterExtension
    static WireMockExtension raasMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(HTTP_PORT))
            .build();

    @Test
    public void should_Return_TreesPlantedForMonth_Object_With_Valid_EnterpriseRequest() throws Exception {

        String month = "02-2022";
        Integer totalTrees = 200;
        String enterpriseId = "123";

        raasMock.stubFor(get(String.format("/enterprise/%s/treeCount/%s", enterpriseId, month))
            .withHeader("Content-Type", equalTo(CONTENT_TYPE))
            .withHeader("User-Agent", equalTo(USER_AGENT))
            .withHeader("X-API-KEY", equalTo(X_API_KEY))
            // .withRequestBody(equalToJson(String.format("{ \"treeCount\": %s, \"enterpriseId\": \"%s\", \"projectId\": \"%s\", \"user\": \"%s\" }", treeCount, enterpriseId, projectId, user)))
            .willReturn(okJson(String.format("{ \"count\": \"%s\" }", totalTrees))));

        RaaSEnterprise raasEnterprise = new RaaSEnterprise("http://localhost:" + HTTP_PORT, X_API_KEY);

        var future = raasEnterprise.getTreesPlantedForMonth(enterpriseId, month).thenAccept(s -> {
            assertTrue(s.isSuccess());
            assertEquals(null, s.getException());
            assertEquals(totalTrees, s.getTotalTrees());
        });
        future.get();
    }

    @Test
    public void should_Return_RaaSException_With_TreesPlanted_For_Month_Object_With_Invalid_Response() throws Exception {
        String month = "02-2022";
        String enterpriseId = "123";

        raasMock.stubFor(get(String.format("/enterprise/%s/treeCount/%s", enterpriseId, month))
            .withHeader("Content-Type", equalTo(CONTENT_TYPE))
            .withHeader("User-Agent", equalTo(USER_AGENT))
            .withHeader("X-API-KEY", equalTo(X_API_KEY))
            .willReturn(badRequest().withBody("foo")));

        RaaSEnterprise raasEnterprise = new RaaSEnterprise("http://localhost:" + HTTP_PORT, X_API_KEY);

        var future = raasEnterprise.getTreesPlantedForMonth(enterpriseId, month).thenAccept(s -> {
            assertFalse(s.isSuccess());
            assertEquals(RaaSException.class, s.getException().getClass());
            assertEquals("Failed to parse response from RaaS API.", s.getException().getMessage());
            assertEquals(null, s.getTotalTrees());
        });
        future.get();

    }

    @Test
    public void should_Return_Correct_Error_Message_When_Trees_Planted_For_Month_Not_Found() throws Exception {
        String month = "02-2022";
        String enterpriseId = "123";

        raasMock.stubFor(get(String.format("/enterprise/%s/treeCount/%s", enterpriseId, month))
        .withHeader("Content-Type", equalTo(CONTENT_TYPE))
        .withHeader("User-Agent", equalTo(USER_AGENT))
        .withHeader("X-API-KEY", equalTo(X_API_KEY))
        .willReturn(notFound()));

        RaaSEnterprise raasEnterprise = new RaaSEnterprise("http://localhost:" + HTTP_PORT, X_API_KEY);

        var future = raasEnterprise.getTreesPlantedForMonth(enterpriseId, month).thenAccept(s -> {
            assertFalse(s.isSuccess());
            assertEquals(RaaSException.class, s.getException().getClass());
            assertEquals("Could not find enterprise data.", s.getException().getMessage());
        });
        future.get();

    }

    @Test
    public void should_Return_Correct_Error_Message_When_Get_Trees_Planted_For_Month_Not_Authorised() throws Exception {
        String month = "02-2022";
        String enterpriseId = "123";

        raasMock.stubFor(get(String.format("/enterprise/%s/treeCount/%s", enterpriseId, month))
        .withHeader("Content-Type", equalTo(CONTENT_TYPE))
        .withHeader("User-Agent", equalTo(USER_AGENT))
        .withHeader("X-API-KEY", equalTo(X_API_KEY))
        .willReturn(unauthorized()));

        RaaSEnterprise raasEnterprise = new RaaSEnterprise("http://localhost:" + HTTP_PORT, X_API_KEY);

        var future = raasEnterprise.getTreesPlantedForMonth(enterpriseId, month).thenAccept(s -> {
            assertFalse(s.isSuccess());
            assertEquals(RaaSException.class, s.getException().getClass());
            assertEquals("Not authorised - check your API key.", s.getException().getMessage());
        });
        future.get();
    }

}