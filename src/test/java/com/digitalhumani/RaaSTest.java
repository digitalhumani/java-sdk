package com.digitalhumani;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.concurrent.CompletableFuture;

import javax.naming.ConfigurationException;

import com.digitalhumani.exceptions.RaaSException;
import com.digitalhumani.interfaces.HTTPHelper;
import com.digitalhumani.interfaces.TreePlanter;
import com.digitalhumani.models.TreePlantingRequest;
import com.digitalhumani.models.TreesPlanted;
import com.digitalhumani.utils.TreePlanterHTTPHelper;

/**
 * Unit test for simple App.
 */
public class RaaSTest {
    /**
     * 
     */
    @Test
    public void should_Throw_IllegalArgumentException_When_API_Key_Not_Passed() {
        Exception exception = assertThrows(ConfigurationException.class, () -> {
            new RaaS("", "", "");
        });
        assertEquals("Error: @apiKey parameter required", exception.getMessage());
    }

    /**
     * 
     */
    @Test
    public void should_Throw_IllegalArgumentException_When_Environment_Not_Passed() {
        Exception exception = assertThrows(ConfigurationException.class, () -> {
            new RaaS("key", "", "");
        });
        assertEquals("Error: @environment parameter required", exception.getMessage());
    }

    /**
     * 
     */
    @Test
    public void should_Throw_IllegalArgumentException_When_Environment_Invalid() {
        Exception exception = assertThrows(ConfigurationException.class, () -> {
            new RaaS("key", "foo", "");
        });
        assertEquals("Error: @environment parameter must be one of 'production','sandbox'", exception.getMessage());
    }

    @Test
    public void should_Return_TreePlanted_Object_With_Valid_API_Configuration_One_Tree() throws Exception {
        RaaS raas = new RaaS();

        String projectId = "77222222";
        String user = "JUnit";
        Integer treeCount = 1;

        var future = raas.plantATree(projectId, user).thenAccept(s -> {
            assertTrue(s.isSuccess());
            assertEquals(null, s.getException());
            assertFalse(s.getUUId().isBlank());
            assertEquals(projectId, s.getProjectId());
            assertEquals(raas.getEnterpriseId(), s.getEnterpriseId());
            assertEquals(user, s.getUser());
            assertEquals(treeCount, s.getTreeCount());
        });
        future.get();
    }

    @Test
    public void should_Return_TreePlanted_Object_With_Valid_API_Configuration_Some_Trees() throws Exception {
        RaaS raas = new RaaS();

        String projectId = "77222222";
        String user = "JUnit";
        Integer treeCount = 15;

        var future = raas.plantSomeTrees(projectId, user, treeCount).thenAccept(s -> {
            assertTrue(s.isSuccess());
            assertEquals(null, s.getException());
            assertFalse(s.getUUId().isBlank());
            assertEquals(projectId, s.getProjectId());
            assertEquals(raas.getEnterpriseId(), s.getEnterpriseId());
            assertEquals(user, s.getUser());
            assertEquals(treeCount, s.getTreeCount());
        });
        future.get();
    }

    @Test
    public void should_Call_TreePlanter_Only_Once() throws Exception {
        TreePlanter mockPlanter = mock(TreePlanter.class);

        String uuid = "uuid";
        String url = "https://foo.bar";
        String enterpriseId = "foo";
        String projectId = "bar";
        String apiKey = "junit-api-key-test";
        String user = "JUnit";
        Integer treeCount = 1;

        TreesPlanted result = new TreesPlanted(uuid, enterpriseId, projectId, user, treeCount);

        doAnswer(invocation -> CompletableFuture.completedFuture(result)).when(mockPlanter).plantATree(url,
                enterpriseId, apiKey, projectId, user);

        RaaS raas = new RaaS(mockPlanter, url, enterpriseId, apiKey);

        var future = raas.plantATree(projectId, user).thenAccept(s -> {
            assertEquals(uuid, s.getUUId());
            assertEquals(projectId, s.getProjectId());
            assertEquals(enterpriseId, s.getEnterpriseId());
            assertEquals(user, s.getUser());
            assertEquals(treeCount, s.getTreeCount());
        });

        future.get();

        verify(mockPlanter, times(1)).plantATree(url, enterpriseId, apiKey, projectId, user);

    }

    @Test
    public void should_Set_Exception_On_TreesPlanted_Object_When_Parsing_Error_Occurs() throws Exception {
        
        String url = "https://example.com/";
        String enterpriseId = "foo";
        String projectId = "bar";
        String apiKey = "junit-api-key-test";
        String user = "JUnit";
        String expectedErrorString = "JUnit Exception";

        HTTPHelper<TreePlantingRequest, TreesPlanted> mockHttpHelper = mock(TreePlanterHTTPHelper.class);

        when(mockHttpHelper.toJson(any())).thenReturn("");

        when(mockHttpHelper.buildRequest(anyString(), anyString(), anyString()))
                .thenReturn(HttpRequest.newBuilder(URI.create(url)).build());

        when(mockHttpHelper.parseResponse()).thenReturn((stringBody) -> {
            RaaSException raasEx = new RaaSException(expectedErrorString);
            return new TreesPlanted(raasEx);
        });

        TreePlanter planter = new RaaSTreePlanter(mockHttpHelper);
        
        RaaS raas = new RaaS(planter, url, enterpriseId, apiKey);

        var future = raas.plantATree(projectId, user).thenAccept(s -> {
            assertFalse(s.isSuccess());
            assertEquals(expectedErrorString, s.getException().getMessage());
            assertEquals(null, s.getUUId());
            assertEquals(null, s.getProjectId());
            assertEquals(null, s.getEnterpriseId());
            assertEquals(null, s.getUser());
            assertEquals(null, s.getTreeCount());
        });
        
        future.get();

    }
}
