package com.digitalhumani.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import com.digitalhumani.RaaS;
import com.digitalhumani.exceptions.RaaSException;

import org.junit.jupiter.api.BeforeAll;
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
    private String tree_uuid;

    boolean runIntegrationTest()
        throws FileNotFoundException, IOException {

        // Attempt to load required config from env variables
        enterpriseId = System.getenv("ENTID");
        apiKey = System.getenv("APIKEY");

        // If config not found in env, attempt to load from config file
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
            tree_uuid = treePlanted.getUUId();
        });
        future.get();
    }

    @Test
    @EnabledIf("runIntegrationTest")
    @Order(2)
    public void get_a_tree() 
        throws javax.naming.ConfigurationException,
        RaaSException,
        InterruptedException,
        ExecutionException {

        RaaS raas = new RaaS(apiKey, "sandbox", enterpriseId);
        var future = raas.getATreePlanted(tree_uuid).thenAccept(treePlanted -> {
            assertTrue(treePlanted.isSuccess());
            assertEquals(null, treePlanted.getException());
            assertEquals(tree_uuid, treePlanted.getUUId());
            assertEquals(PROJECT_ID, treePlanted.getProjectId());
            assertEquals(enterpriseId, treePlanted.getEnterpriseId());
            assertEquals(USER_ID, treePlanted.getUser());
            assertEquals(1, treePlanted.getTreeCount());
        });
        future.get();
    }

    @Test
    @EnabledIf("runIntegrationTest")
    @Order(3)
    public void delete_a_tree() 
        throws javax.naming.ConfigurationException,
        RaaSException,
        InterruptedException,
        ExecutionException {

        RaaS raas = new RaaS(apiKey, "sandbox", enterpriseId);
        var future = raas.deleteATreePlanted(tree_uuid).thenAccept(result -> {
            assertTrue(result);
        });
        future.get();
    }

}