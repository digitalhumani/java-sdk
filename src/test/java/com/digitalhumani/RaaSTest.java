package com.digitalhumani;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.CompletableFuture;

import javax.naming.ConfigurationException;

import com.digitalhumani.enterprise.interfaces.Enterprise;
import com.digitalhumani.enterprise.models.TreesPlantedForMonth;
import com.digitalhumani.tree.interfaces.TreePlanter;
import com.digitalhumani.tree.models.TreesPlanted;

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
    public void should_Call_TreePlanter_Only_Once_For_One_Tree() throws Exception {
        TreePlanter mockPlanter = mock(TreePlanter.class);
        Enterprise mockEnterprise = mock(Enterprise.class);

        String uuid = "uuid";
        String url = "https://foo.bar";
        String enterpriseId = "foo";
        String projectId = "bar";
        String apiKey = "junit-api-key-test";
        String user = "JUnit";
        Integer treeCount = 1;

        TreesPlanted result = new TreesPlanted(uuid, enterpriseId, projectId, user, treeCount);

        doAnswer(invocation -> CompletableFuture.completedFuture(result)).when(mockPlanter).plantATree(enterpriseId,
                projectId, user);

        RaaS raas = new RaaS(mockPlanter, mockEnterprise, url, enterpriseId, apiKey);

        var future = raas.plantATree(projectId, user).thenAccept(s -> {
            assertEquals(uuid, s.getUUId());
            assertEquals(projectId, s.getProjectId());
            assertEquals(enterpriseId, s.getEnterpriseId());
            assertEquals(user, s.getUser());
            assertEquals(treeCount, s.getTreeCount());
        });

        future.get();

        verify(mockPlanter, times(1)).plantATree(enterpriseId, projectId, user);

    }

    @Test
    public void should_Call_TreePlanter_Only_Once_For_Some_Trees() throws Exception {
        TreePlanter mockPlanter = mock(TreePlanter.class);
        Enterprise mockEnterprise = mock(Enterprise.class);

        String uuid = "uuid";
        String url = "https://foo.bar";
        String enterpriseId = "foo";
        String projectId = "bar";
        String apiKey = "junit-api-key-test";
        String user = "JUnit";
        Integer treeCount = 5;

        TreesPlanted result = new TreesPlanted(uuid, enterpriseId, projectId, user, treeCount);

        doAnswer(invocation -> CompletableFuture.completedFuture(result)).when(mockPlanter).plantSomeTrees(enterpriseId,
                projectId, user, treeCount);

        RaaS raas = new RaaS(mockPlanter, mockEnterprise, url, enterpriseId, apiKey);

        var future = raas.plantSomeTrees(projectId, user, treeCount).thenAccept(s -> {
            assertEquals(uuid, s.getUUId());
            assertEquals(projectId, s.getProjectId());
            assertEquals(enterpriseId, s.getEnterpriseId());
            assertEquals(user, s.getUser());
            assertEquals(treeCount, s.getTreeCount());
        });

        future.get();

        verify(mockPlanter, times(1)).plantSomeTrees(enterpriseId, projectId, user, treeCount);

    }

    @Test
    public void should_Call_TreePlanter_Only_Once_For_Get_A_Tree_Planted() throws Exception {

        TreePlanter mockPlanter = mock(TreePlanter.class);
        Enterprise mockEnterprise = mock(Enterprise.class);

        String uuid = "uuid";
        String url = "https://foo.bar";
        String enterpriseId = "foo";
        String projectId = "bar";
        String apiKey = "junit-api-key-test";
        String user = "JUnit";
        Integer treeCount = 5;

        TreesPlanted result = new TreesPlanted(uuid, enterpriseId, projectId, user, treeCount);

        doAnswer(invocation -> CompletableFuture.completedFuture(result)).when(mockPlanter).getATreePlanted(uuid);

        RaaS raas = new RaaS(mockPlanter, mockEnterprise, url, enterpriseId, apiKey);

        var future = raas.getATreePlanted(uuid).thenAccept(s -> {
            assertEquals(uuid, s.getUUId());
            assertEquals(projectId, s.getProjectId());
            assertEquals(enterpriseId, s.getEnterpriseId());
            assertEquals(user, s.getUser());
            assertEquals(treeCount, s.getTreeCount());
        });

        future.get();

        verify(mockPlanter, times(1)).getATreePlanted(uuid);

    }

    @Test
    public void should_Call_TreePlanter_Only_Once_For_Delete_A_Tree_Planted() throws Exception {

        TreePlanter mockPlanter = mock(TreePlanter.class);
        Enterprise mockEnterprise = mock(Enterprise.class);

        String uuid = "uuid";
        String url = "https://foo.bar";
        String enterpriseId = "foo";
        String apiKey = "junit-api-key-test";

        doAnswer(invocation -> CompletableFuture.completedFuture(true)).when(mockPlanter).deleteATreePlanted(uuid);

        RaaS raas = new RaaS(mockPlanter, mockEnterprise, url, enterpriseId, apiKey);

        var future = raas.deleteATreePlanted(uuid).thenAccept(success -> {
            assertTrue(success);
        });

        future.get();

        verify(mockPlanter, times(1)).deleteATreePlanted(uuid);
    }

    @Test
    public void should_Call_Enterprise_Only_Once_For_Get_Trees_Planted_For_Month() throws Exception {

        TreePlanter mockPlanter = mock(TreePlanter.class);
        Enterprise mockEnterprise = mock(Enterprise.class);

        String url = "https://foo.bar";
        String enterpriseId = "foo";
        String apiKey = "junit-api-key-test";
        String month = "01-2022";

        TreesPlantedForMonth result = new TreesPlantedForMonth(100);

        doAnswer(invocation -> CompletableFuture.completedFuture(result)).when(mockEnterprise).getTreesPlantedForMonth(enterpriseId, "01-2022");

        RaaS raas = new RaaS(mockPlanter, mockEnterprise, url, enterpriseId, apiKey);

        var future = raas.getTreesPlantedForMonth(month).thenAccept(resp -> {
            assertEquals(100, resp.getTotalTrees());
        });

        future.get();

        verify(mockEnterprise, times(1)).getTreesPlantedForMonth(enterpriseId, month);
    }

}
