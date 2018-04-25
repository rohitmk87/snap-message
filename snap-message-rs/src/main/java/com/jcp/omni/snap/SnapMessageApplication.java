/*
 * Copyright (c) 2018 JCPenney Co. All rights reserved.
 */

package com.jcp.omni.snap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class,
    ManagementWebSecurityAutoConfiguration.class,
    JmsAutoConfiguration.class})
//@PropertySource("classpath:version.properties")
@EnableConfigurationProperties
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.jcp", "com.jcpenney"})
public class SnapMessageApplication {

  public static void main(String[] args) {

    SpringApplication.run(SnapMessageApplication.class, args);
  }
}
