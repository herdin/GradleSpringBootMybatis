package app.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import javax.swing.text.html.Option;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class VaultApplicationContextInitializer implements ApplicationContextInitializer {
    private Logger logger = LoggerFactory.getLogger(VaultApplicationContextInitializer.class);
    private ResourceLoader loader = new DefaultResourceLoader();

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        Optional<String> vaultEndpoint = Optional.of(configurableApplicationContext.getEnvironment().getProperty("vault.endpoint"));
        Optional<String> vaultToken = Optional.of(configurableApplicationContext.getEnvironment().getProperty("vault.token"));

        VaultTemplate vaultTemplate = null;
        try {
            vaultTemplate = new VaultTemplate(VaultEndpoint.from(new URI(vaultEndpoint.get())), new TokenAuthentication(vaultToken.get()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        VaultResponse vaultResponse = null;
        vaultResponse = vaultTemplate.read("secret/my-database-info");
        Optional<VaultResponse> vaultResponse1 = Optional.of(vaultResponse);
//        vaultResponse1.or
        logger.debug("vault read -> {}", vaultResponse.getData());
        System.setProperty("spring.datasource.url", String.valueOf(vaultResponse.getData().get("url")));
        System.setProperty("spring.datasource.username", String.valueOf(vaultResponse.getData().get("username")));
        System.setProperty("spring.datasource.password", String.valueOf(vaultResponse.getData().get("password")));
//        try {
//            configurableApplicationContext.getEnvironment().getPropertySources().get("systemProperties").getSource().
//        } catch (IOException ex) {
//            throw new IllegalStateException(ex);
//        }
    }
}
