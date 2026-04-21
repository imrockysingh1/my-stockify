package com.example.stockify.config;

import com.example.stockify.auth.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "getAuditAware")
public class AppConfig {

    @Bean
    AuditorAware<String> getAuditAware(){
        return new AuditorAwareImpl();
    }
}
