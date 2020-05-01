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
    public enum VAULT_ENVIRONMENT_KEY {
        TOKEN("VAULT_TOKEN"),
        ENDPOINT("VAULT_ENDPOINT"),
        PATH_DATABASE_INFO("VAULT_PATH_DATABASE_INFO"),
        ;
        private final String value;
        VAULT_ENVIRONMENT_KEY(String value) {
            this.value = value;
        }
        public String value() {
            return value;
        }
    }
    public enum VAULT_DATABASE_KEY {
        URL("url"),
        USERNAME("username"),
        PASSWORD("password"),
        ;
        private final String value;
        VAULT_DATABASE_KEY(String value) {
            this.value = value;
        }
        public String value() {
            return value;
        }
    }
    public enum SPRING_DATABASE_PROPERTY_KEY {
        URL("spring.datasource.url"),
        USERNAME("spring.datasource.username"),
        PASSWORD("spring.datasource.password"),
        ;
        private final String value;
        SPRING_DATABASE_PROPERTY_KEY(String value) {
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
        Optional<String> vaultToken = Optional.of(System.getenv(VAULT_ENVIRONMENT_KEY.TOKEN.value()));
        Optional<String> vaultEndpoint = Optional.of(System.getenv(VAULT_ENVIRONMENT_KEY.ENDPOINT.value()));
        Optional<String> vaultPathDatabaseInfo = Optional.of(System.getenv(VAULT_ENVIRONMENT_KEY.PATH_DATABASE_INFO.value()));

        if(logger.isDebugEnabled()) {
            logger.debug("property {} -> {}", VAULT_ENVIRONMENT_KEY.TOKEN.toString(), vaultToken.get());
            logger.debug("property {} -> {}", VAULT_ENVIRONMENT_KEY.ENDPOINT.toString(), vaultEndpoint.get());
        } else {
            System.out.println("property " + VAULT_ENVIRONMENT_KEY.TOKEN.toString() + " -> " + vaultToken.get());
            System.out.println("property " + VAULT_ENVIRONMENT_KEY.ENDPOINT.toString() + " -> " + vaultEndpoint.get());
        }

        VaultTemplate vaultTemplate = null;
        try {
            vaultTemplate = new VaultTemplate(VaultEndpoint.from(new URI(vaultEndpoint.get())), new TokenAuthentication(vaultToken.get()));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("vault endpoint is not valid -> " + vaultEndpoint.get());
        }
        Optional<VaultResponse> vaultResponse = Optional.of(vaultTemplate.read(vaultPathDatabaseInfo.get()));
        if(logger.isDebugEnabled()) {
            logger.debug("vault read -> {}", vaultResponse.get().getData());
        } else {
            System.out.println("vault read -> " + vaultResponse.get().getData());
        }

        HashMap<String, Object> mappedVaultProperty = new HashMap<>();
        setVaultProperty(mappedVaultProperty, SPRING_DATABASE_PROPERTY_KEY.URL, vaultResponse, VAULT_DATABASE_KEY.URL);
        setVaultProperty(mappedVaultProperty, SPRING_DATABASE_PROPERTY_KEY.USERNAME, vaultResponse, VAULT_DATABASE_KEY.USERNAME);
        setVaultProperty(mappedVaultProperty, SPRING_DATABASE_PROPERTY_KEY.PASSWORD, vaultResponse, VAULT_DATABASE_KEY.PASSWORD);
        vaultProperty = new MapPropertySource(VAULT_PROPERTY_SOURCE_NAME, mappedVaultProperty);
    }
    private void setVaultProperty(HashMap<String, Object> vaultProperty, SPRING_DATABASE_PROPERTY_KEY springDatabasePropertyKey, Optional<VaultResponse> vaultResponse, VAULT_DATABASE_KEY vaultKey) {
        vaultProperty.put(springDatabasePropertyKey.value(), String.valueOf(vaultResponse.get().getData().get(vaultKey.value())));
    }
}
