package com.digitalhumani;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import javax.naming.ConfigurationException;

import com.digitalhumani.config.Config;
import com.digitalhumani.enterprise.RaaSEnterprise;
import com.digitalhumani.enterprise.interfaces.Enterprise;
import com.digitalhumani.enterprise.models.TreesPlantedForMonth;
import com.digitalhumani.exceptions.RaaSException;
import com.digitalhumani.models.RaaSResult;
import com.digitalhumani.tree.RaaSTreePlanter;
import com.digitalhumani.tree.interfaces.TreePlanter;
import com.digitalhumani.tree.models.TreesPlanted;

/**
 * This is the main class for the SDK, providing access to RaaS functionality.
 * 
 * Before using any of the methods, valid configuration must be supplied either in
 * /resources/raas.properties or via the constructor. 
 * 
 * Please see the <a href="https://github.com/digitalhumani/java-sdk">README</a> in the Github repo for more info.
 */
public final class RaaS {
    private String apiKey;
    private String environment;
    private String enterpriseId;
    private String url;

    private TreePlanter treePlanter;
    private Enterprise enterprise;

    /**
     * The enterprise Id this istance of RaaS is associated with.
     * 
     * @return The Id of the currently configured enterprise.
     */
    public String getEnterpriseId() {
        return enterpriseId;
    }

    private void setUrl() throws ConfigurationException {
        switch (this.environment) {
        case "production":
            this.url = "https://api.digitalhumani.com";
            break;

        case "sandbox":
            this.url = "https://api.sandbox.digitalhumani.com";
            break;

        default:
            throw new ConfigurationException(
                    String.format("Unable to set RaaS API URL based on supplied environment '%s'", this.environment));
        }
    }

    private void validateConfiguration(String apiKey, String enterpriseId, String environment)
            throws ConfigurationException {
        if (apiKey.isBlank())
            throw new ConfigurationException("Error: @apiKey parameter required");
        if (environment.isBlank())
            throw new ConfigurationException("Error: @environment parameter required");
        if (Arrays.stream(new String[] { "production", "sandbox" }).noneMatch(environment::contains))
            throw new ConfigurationException("Error: @environment parameter must be one of 'production','sandbox'");
    }

    RaaS(TreePlanter treePlanter, Enterprise enterprise, String url, String enterpriseId, String apiKey) {
        this.treePlanter = treePlanter;
        this.enterprise = enterprise;
        this.url = url;
        this.enterpriseId = enterpriseId;
        this.apiKey = apiKey;
    }

    /**
     * Public constructor. Relies on a valid `raas.properties` being configured in the `resources` folder of the project.
     * 
     * Please see the <a href="https://github.com/digitalhumani/java-sdk">README</a> in the Github repo for more info.
     * 
     * @throws ConfigurationException if an invalid `raas.properties` is detected.
     */
    public RaaS() throws ConfigurationException {
        String apiKeyFromConfig = "";
        String enterpriseIdFromConfig = "";
        String environmentFromConfig = "";

        try {
            apiKeyFromConfig = Config.getAPIKey();
            enterpriseIdFromConfig = Config.getEnterprise();
            environmentFromConfig = Config.getEnvironment();
        } catch (IOException e) {
            System.out.println(
                    "Failed to load the configuration for RaaS Java SDK - have you configured 'raas.properties' correctly?");
            System.out.println("For more info about configuring the RaaS Java SDK, please refer to <TODO - add URL>");
            var configEx = new ConfigurationException("Unable to load configuration from 'raas.properties'");
            configEx.initCause(e);
            throw configEx;
        }

        validateConfiguration(apiKeyFromConfig, enterpriseIdFromConfig, environmentFromConfig);

        this.apiKey = apiKeyFromConfig;
        this.environment = environmentFromConfig;
        this.enterpriseId = enterpriseIdFromConfig;

        setUrl();

        this.treePlanter = new RaaSTreePlanter(this.url, this.apiKey);
        this.enterprise = new RaaSEnterprise(this.url, this.apiKey);
    }

    /**
     * Public constructor. Relies on the required configuration items being passed as parameters.
     * 
     * Please see the <a href="https://github.com/digitalhumani/java-sdk">README</a> in the Github repo for more info.
     * 
     * @param apiKey your unique API key
     * @param environment the environment against which the requests will be made (either 'sandbox' or 'production').
     * @param enterpriseId your unique enterprise Id.
     * @throws ConfigurationException if invalid configuration items are detected.
     */
    public RaaS(String apiKey, String environment, String enterpriseId) throws ConfigurationException {

        validateConfiguration(apiKey, enterpriseId, environment);

        this.apiKey = apiKey;
        this.environment = environment;
        this.enterpriseId = enterpriseId;

        setUrl();

        this.treePlanter = new RaaSTreePlanter(this.url, this.apiKey);
        this.enterprise = new RaaSEnterprise(this.url, this.apiKey);
    }

    /**
     * Plants a single tree. 
     * 
     * Please see the <a href="https://github.com/digitalhumani/java-sdk">README</a> in the Github repo for more info.
     * 
     * @param projectId the Id of the project this tree request relates to.
     * @param user an arbitary user (or system) identifier.
     * @return a {@code CompletableFuture<TreesPlanted>} containing the details of the tree planting request (including it's Id (uuid)).
     * @throws RaaSException if an error occurs while making the request.
     */
    public CompletableFuture<TreesPlanted> plantATree(String projectId, String user) throws RaaSException {
        return this.treePlanter.plantATree(this.enterpriseId, projectId, user);
    }

    /**
     * Plants multiple trees in one request.
     * 
     * Please see the <a href="https://github.com/digitalhumani/java-sdk">README</a> in the Github repo for more info.
     * 
     * @param projectId the Id of the project this tree request relates to.
     * @param user an arbitary user (or system) identifier.
     * @param treeCount the number of trees to be planted.
     * @return a {@code CompletableFuture<TreesPlanted>} containing the details of the tree planting request (including it's Id (uuid)).
     * @throws RaaSException if an error occurs while making the request.
     */
    public CompletableFuture<TreesPlanted> plantSomeTrees(String projectId, String user, Integer treeCount)
            throws RaaSException {
        return this.treePlanter.plantSomeTrees(this.enterpriseId, projectId, user, treeCount);
    }

    /**
     * Retrieves the details of a tree planting request by it's Id (uuid).
     * 
     * Please see the <a href="https://github.com/digitalhumani/java-sdk">README</a> in the Github repo for more info.
     * 
     * @param uuid the Id of the tree planting request to retrieve.
     * @return a {@code CompletableFuture<TreesPlanted>} containing the details of the tree planting request
     * @throws RaaSException if an error occurs while making the request.
     */
    public CompletableFuture<TreesPlanted> getATreePlanted(String uuid) 
            throws RaaSException {
        return this.treePlanter.getATreePlanted(uuid);
    }

    /**
     * Deletes a previously submitted tree planting request by its Id (uuid)
     * 
     * @param uuid the Id of the tree planting request to delete.
     * @return Boolean indicating success / failure
     * @throws RaaSException if an error occurs while making the request.
     */
    public CompletableFuture<Boolean> deleteATreePlanted(String uuid)
            throws RaaSException {
        return this.treePlanter.deleteATreePlanted(uuid);
    }

    
    public CompletableFuture<TreesPlantedForMonth> getTreesPlantedForMonth(String month)
        throws RaaSException {
            return this.enterprise.getTreesPlantedForMonth(this.enterpriseId, month);
    }
}
