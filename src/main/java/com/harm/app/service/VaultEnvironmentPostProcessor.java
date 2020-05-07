package com.harm.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Optional;

public class VaultEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private Logger logger = LoggerFactory.getLogger(VaultEnvironmentPostProcessor.class);
    private final String VAULT_PROPERTY_SOURCE_NAME = "vaultPropertySource";
    private static MapPropertySource vaultProperty = null;
    public enum ENVIRONMENT_KEY {
        TOKEN("VAULT_TOKEN"),
        ENDPOINT("VAULT_ENDPOINT"),
        PATH_DATABASE_INFO("VAULT_PATH_DATABASE_INFO"),
        PATH_ELASTICSEARCH_INFO("VAULT_PATH_ELASTICSEARCH_INFO"),
        ;
        private final String value;
        ENVIRONMENT_KEY(String value) {
            this.value = value;
        }
        public String value() {
            return value;
        }
    }
    public enum VAULT_KEY {
        DATABASE_URL("url"),
        DATABASE_USERNAME("username"),
        DATABASE_PASSWORD("password"),
        ELASTICSEARCH_URL("url"),
        ELASTICSEARCH_PORT("port"),
        ELASTICSEARCH_SCHEME("scheme"),
        ;
        private final String value;
        VAULT_KEY(String value) {
            this.value = value;
        }
        public String value() {
            return value;
        }
    }
    public enum PROPERTY_KEY {
        DATABASE_URL("spring.datasource.url"),
        DATABASE_USERNAME("spring.datasource.username"),
        DATABASE_PASSWORD("spring.datasource.password"),
        ELASTICSEARCH_URL("elasticsearch.url"),
        ELASTICSEARCH_PORT("elasticsearch.port"),
        ELASTICSEARCH_SCHEME("elasticsearch.scheme"),
        ;
        private final String value;
        PROPERTY_KEY(String value) {
            this.value = value;
        }
        public String value() {
            return value;
        }
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if(environment.getPropertySources().get(VAULT_PROPERTY_SOURCE_NAME) == null) {
            initVaultProperty();
            environment.getPropertySources().addLast(vaultProperty);
        }
    }

    public void initVaultProperty() {

        if(vaultProperty != null) {
            return;
        }

        if(logger.isDebugEnabled()) {
            logger.debug("vault init");
        } else {
            System.out.println("vault init");
        }

        System.getenv().entrySet().stream().forEach(entry -> {
            if(logger.isDebugEnabled()) {
                logger.info("system enviroment -> {}, {}", entry.getKey(), entry.getValue());
            } else {
                System.out.println("system enviroment -> " + entry.getKey() + ", " + entry.getValue());
            }
        });
        //reason why using Optional.of is..
        //if application arguments associated with vault are not exist, NullPointerException occur.
        Optional<String> vaultToken = Optional.of(System.getenv(ENVIRONMENT_KEY.TOKEN.value()));
        Optional<String> vaultEndpoint = Optional.of(System.getenv(ENVIRONMENT_KEY.ENDPOINT.value()));
        Optional<String> vaultPathDatabaseInfo = Optional.of(System.getenv(ENVIRONMENT_KEY.PATH_DATABASE_INFO.value()));
        Optional<String> vaultPathElasticsearchInfo = Optional.of(System.getenv(ENVIRONMENT_KEY.PATH_ELASTICSEARCH_INFO.value()));

        if(logger.isDebugEnabled()) {
            logger.debug("property {} -> {}", ENVIRONMENT_KEY.TOKEN.toString(), vaultToken.get());
            logger.debug("property {} -> {}", ENVIRONMENT_KEY.ENDPOINT.toString(), vaultEndpoint.get());
        } else {
            System.out.println("property " + ENVIRONMENT_KEY.TOKEN.toString() + " -> " + vaultToken.get());
            System.out.println("property " + ENVIRONMENT_KEY.ENDPOINT.toString() + " -> " + vaultEndpoint.get());
        }

        VaultTemplate vaultTemplate = null;
        try {
            vaultTemplate = new VaultTemplate(VaultEndpoint.from(new URI(vaultEndpoint.get())), new TokenAuthentication(vaultToken.get()));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("vault endpoint is not valid -> " + vaultEndpoint.get());
        }
        Optional<VaultResponse> vaultDatabaseInfoResponse = Optional.of(vaultTemplate.read(vaultPathDatabaseInfo.get()));
        Optional<VaultResponse> vaultElasticsearchInfoResponse = Optional.of(vaultTemplate.read(vaultPathElasticsearchInfo.get()));

        if(logger.isDebugEnabled()) {
            logger.debug("vault database info read -> {}", vaultDatabaseInfoResponse.get().getData());
            logger.debug("vault elasticsearch info read -> {}", vaultElasticsearchInfoResponse.get().getData());

        } else {
            System.out.println("vault database info read -> " + vaultDatabaseInfoResponse.get().getData());
            System.out.println("vault elasticsearch info read -> " + vaultElasticsearchInfoResponse.get().getData());
        }

        HashMap<String, Object> mappedVaultProperty = new HashMap<>();
        setVaultProperty(mappedVaultProperty, PROPERTY_KEY.DATABASE_URL, vaultDatabaseInfoResponse, VAULT_KEY.DATABASE_URL);
        setVaultProperty(mappedVaultProperty, PROPERTY_KEY.DATABASE_USERNAME, vaultDatabaseInfoResponse, VAULT_KEY.DATABASE_USERNAME);
        setVaultProperty(mappedVaultProperty, PROPERTY_KEY.DATABASE_PASSWORD, vaultDatabaseInfoResponse, VAULT_KEY.DATABASE_PASSWORD);
        setVaultProperty(mappedVaultProperty, PROPERTY_KEY.ELASTICSEARCH_URL, vaultElasticsearchInfoResponse, VAULT_KEY.ELASTICSEARCH_URL);
        setVaultProperty(mappedVaultProperty, PROPERTY_KEY.ELASTICSEARCH_PORT, vaultElasticsearchInfoResponse, VAULT_KEY.ELASTICSEARCH_PORT);
        setVaultProperty(mappedVaultProperty, PROPERTY_KEY.ELASTICSEARCH_SCHEME, vaultElasticsearchInfoResponse, VAULT_KEY.ELASTICSEARCH_SCHEME);

        vaultProperty = new MapPropertySource(VAULT_PROPERTY_SOURCE_NAME, mappedVaultProperty);
    }
    private void setVaultProperty(HashMap<String, Object> vaultProperty, PROPERTY_KEY propertyKey, Optional<VaultResponse> vaultResponse, VAULT_KEY vaultKey) {
        vaultProperty.put(propertyKey.value(), String.valueOf(vaultResponse.get().getData().get(vaultKey.value())));
    }
}
