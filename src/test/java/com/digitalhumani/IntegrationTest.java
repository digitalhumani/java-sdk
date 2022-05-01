package com.digitalhumani;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.digitalhumani.RaaS;
import com.digitalhumani.exceptions.RaaSException;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.condition.EnabledIf;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class IntegrationTest {

    private final String USER_ID = "Java SDK Integration Test";
    private final String PROJECT_ID = "93322350";

    private String enterpriseId;
    private String apiKey;
    private ArrayList<String> tree_uuids = new ArrayList<String>();

    boolean runIntegrationTest()
        throws FileNotFoundException, IOException {

        // Attempt to load required config from env variables
        enterpriseId = System.getenv("ENTID");
        apiKey = System.getenv("APIKEY");

        // If config not found in env, attempt to load from config file - loading from file is useful
        // if debugging unit tests if unit test runner doesn't have access to env variables.
        if (enterpriseId == null || apiKey == null) {
            Properties prop = new Properties();
            String fileName = "int-test.config";
            FileInputStream fis;
            try {
                fis = new FileInputStream(fileName);                
            } catch (Exception e) {
                return false;
            }

            prop.load(fis);

            enterpriseId = prop.getProperty("ENTID");
            apiKey = prop.getProperty("APIKEY");
        }

        return enterpriseId != null && apiKey != null;
    }

    @Test
    @EnabledIf("runIntegrationTest")
    @Order(1)
    public void plant_a_tree() 
        throws javax.naming.ConfigurationException,
        RaaSException,
        InterruptedException,
        ExecutionException {

        RaaS raas = new RaaS(apiKey, "sandbox", enterpriseId);
        var future = raas.plantATree(PROJECT_ID, USER_ID).thenAccept(treePlanted -> {
            assertTrue(treePlanted.isSuccess());
            assertEquals(null, treePlanted.getException());
            assertNotNull(treePlanted.getUUId());
            assertEquals(PROJECT_ID, treePlanted.getProjectId());
            assertEquals(enterpriseId, treePlanted.getEnterpriseId());
            assertEquals(USER_ID, treePlanted.getUser());
            assertEquals(1, treePlanted.getTreeCount());
            tree_uuids.add(treePlanted.getUUId());
        });
        future.get();
    }

    @Test
    @EnabledIf("runIntegrationTest")
    @Order(2)
    public void plant_some_trees() 
        throws javax.naming.ConfigurationException,
        RaaSException,
        InterruptedException,
        ExecutionException {

        RaaS raas = new RaaS(apiKey, "sandbox", enterpriseId);
        var future = raas.plantSomeTrees(PROJECT_ID, USER_ID, 10).thenAccept(treePlanted -> {
            assertTrue(treePlanted.isSuccess());
            assertEquals(null, treePlanted.getException());
            assertNotNull(treePlanted.getUUId());
            assertEquals(PROJECT_ID, treePlanted.getProjectId());
            assertEquals(enterpriseId, treePlanted.getEnterpriseId());
            assertEquals(USER_ID, treePlanted.getUser());
            assertEquals(10, treePlanted.getTreeCount());
            tree_uuids.add(treePlanted.getUUId());
        });
        future.get();
    }

    @Test
    @EnabledIf("runIntegrationTest")
    @Order(3)
    public void get_a_tree() 
        throws javax.naming.ConfigurationException,
        RaaSException,
        InterruptedException,
        ExecutionException {

        RaaS raas = new RaaS(apiKey, "sandbox", enterpriseId);
        var future = raas.getATreePlanted(tree_uuids.get(0)).thenAccept(treePlanted -> {
            assertTrue(treePlanted.isSuccess());
            assertEquals(null, treePlanted.getException());
            assertEquals(tree_uuids.get(0), treePlanted.getUUId());
            assertEquals(PROJECT_ID, treePlanted.getProjectId());
            assertEquals(enterpriseId, treePlanted.getEnterpriseId());
            assertEquals(USER_ID, treePlanted.getUser());
            assertEquals(1, treePlanted.getTreeCount());
        });
        future.get();
    }

    @Test
    @EnabledIf("runIntegrationTest")
    @Order(4)
    public void get_trees_planted_for_month() 
        throws javax.naming.ConfigurationException,
        RaaSException,
        InterruptedException,
        ExecutionException {

        RaaS raas = new RaaS(apiKey, "sandbox", enterpriseId);

        LocalDate now = LocalDate.now();
        String month = String.valueOf(now.getMonthValue());
        month = month.length() == 1 ? String.format("0%s", month) : month;
        String year = String.valueOf(now.getYear());
        String yearMonth = String.format("%s-%s", year, month);

        var future = raas.getTreesPlantedForMonth(yearMonth).thenAccept(treesPlantedForMonth -> {
            assertTrue(treesPlantedForMonth.isSuccess());
            assertEquals(null, treesPlantedForMonth.getException());
            assertNotNull(treesPlantedForMonth.getTotalTrees());
            assertTrue(treesPlantedForMonth.getTotalTrees() > 0);
        });
        future.get();
    }

    @Test
    @EnabledIf("runIntegrationTest")
    @Order(5)
    public void delete_trees_planted() 
        throws javax.naming.ConfigurationException,
        RaaSException,
        InterruptedException,
        ExecutionException {

        RaaS raas = new RaaS(apiKey, "sandbox", enterpriseId);
        ArrayList<CompletableFuture<Boolean>> futures = new ArrayList<CompletableFuture<Boolean>>();

        for (String uuid : tree_uuids) {
            futures.add(raas.deleteATreePlanted(uuid));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

        for (CompletableFuture<Boolean> future : (Iterable<CompletableFuture<Boolean>>) futures::iterator) {
            assertTrue(future.get());
        }

    }

}