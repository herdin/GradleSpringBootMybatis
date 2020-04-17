package app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import org.springframework.vault.core.VaultTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class VaultConfig {
    @Bean
    public VaultTemplate vaultTemplate() throws URISyntaxException {
        return new VaultTemplate(VaultEndpoint.from(new URI("http://postgre.anmani.link:8200")), new TokenAuthentication("s.rnljSNtD3QdZVJOP2bmneQUF"));
    }
//    @Override
//    public VaultEndpoint vaultEndpoint() {
//        VaultEndpoint vaultEndpoint = null;
//        try {
//            vaultEndpoint = VaultEndpoint.from(new URI("http://postgre.anmani.link:8200"));
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        return vaultEndpoint;
//    }
//
//    @Override
//    public ClientAuthentication clientAuthentication() {
//        return new TokenAuthentication("s.rnljSNtD3QdZVJOP2bmneQUF");
//    }
}
