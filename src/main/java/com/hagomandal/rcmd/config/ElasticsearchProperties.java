package com.hagomandal.rcmd.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("elasticsearch")
public class ElasticsearchProperties {

    private String host;
    private int port;
}
