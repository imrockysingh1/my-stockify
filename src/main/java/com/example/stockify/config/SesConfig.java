package com.example.stockify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class SesConfig {

    @Value("${aws.region:ap-south-1}")
    private String awsRegion;

    @Bean
    public SesClient sesClient() {

        System.out.println("AWS Region: " + awsRegion);

        System.out.println(
                "AWS Access Key exists: "
                        + (System.getenv("AWS_ACCESS_KEY_ID") != null)
        );

        System.out.println(
                "AWS Secret Key exists: "
                        + (System.getenv("AWS_SECRET_ACCESS_KEY") != null)
        );

        return SesClient.builder()
                .region(Region.of(awsRegion))
                .build();
    }
}