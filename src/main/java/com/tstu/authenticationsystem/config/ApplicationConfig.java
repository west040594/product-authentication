package com.tstu.authenticationsystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tstu.commons.mapping.DateMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class ApplicationConfig {
    @Bean
    public DateMapper dateMapper() {
        return new DateMapper();
    }
}
