package org.smithx.timeagent.api;

import org.smithx.timeagent.api.agent.TimeAgent;
import org.smithx.timeagent.api.exceptions.TimeAgentException;
import org.smithx.timeagent.api.models.TimeAgentArgument;
import org.smithx.timeagent.api.services.TimeAgentService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DummyApplication {

  public static void main(String[] args) {
    SpringApplication.run(DummyApplication.class, args);
  }

  @Bean
  TimeAgent timeAgent() {
    return new TimeAgent() {

      @Override
      public void execute(TimeAgentService service, TimeAgentArgument... arguments) throws TimeAgentException {
        service.getAgentInfo().addProtocol("i'm a dummy agent");
      }
    };
  }
}
