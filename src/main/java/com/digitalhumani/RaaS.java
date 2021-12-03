package com.digitalhumani;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import javax.naming.ConfigurationException;

import com.digitalhumani.config.Config;
import com.digitalhumani.exceptions.RaaSException;
import com.digitalhumani.tree.RaaSTreePlanter;
import com.digitalhumani.tree.interfaces.TreePlanter;
import com.digitalhumani.tree.models.TreesPlanted;

/**
 * Hello world!
 *
 */
public final class RaaS {
    private String apiKey;
    private String environment;
    private String enterpriseId;
    private String url;

    private TreePlanter treePlanter;

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

    RaaS(TreePlanter treePlanter, String url, String enterpriseId, String apiKey) {
        this.treePlanter = treePlanter;
        this.url = url;
        this.enterpriseId = enterpriseId;
        this.apiKey = apiKey;
    }

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
    }

    public RaaS(String apiKey, String environment, String enterpriseId) throws ConfigurationException {

        validateConfiguration(apiKey, enterpriseId, environment);

        this.apiKey = apiKey;
        this.environment = environment;
        this.enterpriseId = enterpriseId;

        setUrl();

        this.treePlanter = new RaaSTreePlanter(this.url, this.apiKey);
    }

    public CompletableFuture<TreesPlanted> plantATree(String projectId, String user) throws RaaSException {
        return this.treePlanter.plantATree(this.enterpriseId, projectId, user);
    }

    public CompletableFuture<TreesPlanted> plantSomeTrees(String projectId, String user, Integer treeCount)
            throws RaaSException {
        return this.treePlanter.plantSomeTrees(this.enterpriseId, projectId, user, treeCount);
    }

    public CompletableFuture<TreesPlanted> getATreePlanted(String uuid) 
            throws RaaSException {
        return this.treePlanter.getATreePlanted(uuid);
    }
}
