package com.harm.app.config;

import com.harm.app.service.VaultEnvironmentPostProcessor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/*
TODO
    - 엘라스틱 서치 연동
        - https://wedul.site/576
*/
@Configuration
public class ElasticsearchConfiguration {

    @Bean
    @Lazy
    public RestHighLevelClient restHighLevelClient(Environment environment) {

        String url = environment.getProperty(VaultEnvironmentPostProcessor.PROPERTY_KEY.ELASTICSEARCH_URL.value());
        int port = Integer.parseInt(environment.getProperty(VaultEnvironmentPostProcessor.PROPERTY_KEY.ELASTICSEARCH_PORT.value()));
        String scheme = environment.getProperty(VaultEnvironmentPostProcessor.PROPERTY_KEY.ELASTICSEARCH_SCHEME.value());

        List<HttpHost> hostList = new ArrayList<>();
        hostList.add(new HttpHost(url, port, scheme));

        RestClientBuilder builder = RestClient.builder(hostList.toArray(new HttpHost[hostList.size()]));
        return new RestHighLevelClient(builder);
    }
}
