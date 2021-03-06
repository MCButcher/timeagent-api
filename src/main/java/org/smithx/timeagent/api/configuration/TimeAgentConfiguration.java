/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smithx.timeagent.api.configuration;

import java.util.Collections;

import javax.sql.DataSource;

import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * configuration for an agent.
 *
 * @author norman schmidt {smithx}
 * @since 08.05.2020
 * 
 */
@Configuration
@PropertySource("classpath:/timeagent.properties")
@EnableSwagger2
public class TimeAgentConfiguration {
  @Autowired
  private TimeAgentValues values;

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "timeagent.datasource")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(1);
    scheduler.setThreadNamePrefix("timeagent-thread");
    scheduler.setRemoveOnCancelPolicy(true);

    return scheduler;
  }

  @Bean
  public TimeAgentInfo agentInfo() {
    return new TimeAgentInfo(values.getAgentName(), TimeAgentStatus.READY);
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("org.smithx.timeagent.api"))
        .build()
        .apiInfo(apiInfo());
  }

  private ApiInfo apiInfo() {
    return new ApiInfo(
        values.getSwagger().getTitle(),
        values.getSwagger().getDescription(),
        values.getSwagger().getVersion(),
        "",
        new Contact(values.getSwagger().getContact().getCompany(), values.getSwagger().getContact().getUrl(),
            values.getSwagger().getContact().getMail()),
        values.getSwagger().getLicense(), values.getSwagger().getLicenseUrl(), Collections.emptyList());
  }
}
