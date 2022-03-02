package com.example.rqchallenge.employees;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConfigurationProperties(prefix = "employee.service")
@Data
public class EmployeeServiceConfig {
  private String baseUrl;

  public WebClient buildWebClient() {
    return WebClient.create(baseUrl);
  }
}
