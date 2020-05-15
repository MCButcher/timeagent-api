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
package org.smithx.timeagent.api.agent;

import java.time.LocalDateTime;

import org.smithx.timeagent.api.exceptions.TimeAgentException;
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.smithx.timeagent.api.models.TimeAgentArgument;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.smithx.timeagent.api.services.TimeAgentService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * abstract class for the implementation.
 *
 * @author norman schmidt {smithx}
 * @since 12.05.2020
 * 
 */
@Data
@AllArgsConstructor
@Slf4j
public class TimeAgentWorkflow {
  private TimeAgentService service;
  private TimeAgent agent;

  private void logArguments(TimeAgentArgument... arguments) {
    if (arguments == null || arguments.length == 0) {
      service.getAgentInfo().addProtocol("no arguments set");
    } else {
      service.getAgentInfo().addProtocol("arguments are set:");
      for (TimeAgentArgument argument : arguments) {
        service.getAgentInfo().addProtocol(String.format("%s: %s", argument.getKey(), argument.getValue()));
      }
    }
    service.updateInfo();
  }

  private void logError(TimeAgentException exception) {
    setStatusEnd(TimeAgentStatus.ABORTED);
    service.getAgentInfo().addProtocol(String.format("%s - %s", exception.getClass(), exception.getFullErrorMessage()));
    log.error("an error while processing the agent occured:", exception);
  }

  private void isAlreadyRunning() {
    if (TimeAgentStatus.RUNNING.equals(service.getAgentInfo().getStatus())) {
      log.warn("agent is already running");
      throw new TimeAgentRuntimeException(TimeAgentExceptionCause.ALREADY_RUNNING,
          "the agent is already running since " + service.getAgentInfo().getStartTimeExecution());
    }
  }

  private void setStatusStart() {
    log.info("start: " + service.getAgentInfo().getStatus());
    service.getAgentInfo().setStatus(TimeAgentStatus.RUNNING);
    service.getAgentInfo().setStartTimeExecution(LocalDateTime.now());
    service.getAgentInfo().clearProtocol();
    service.updateInfo();
  }

  private void setStatusEnd(TimeAgentStatus status) {
    log.info("finish: " + service.getAgentInfo().getStatus());
    service.getAgentInfo().setStatus(status);
    service.getAgentInfo().setFinishTimeExecution(LocalDateTime.now());
    service.updateInfo();
    log.info("end: " + service.getAgentInfo().getStatus());
  }

//  abstract protected void runImplementation(TimeAgentArgument... arguments) throws TimeAgentException;

  public void run(TimeAgentArgument... arguments) {
    isAlreadyRunning();
    setStatusStart();
    logArguments(arguments);
    try {
      log.info("start agent");
      agent.execute(service, arguments);
      log.info("finish agent");
      setStatusEnd(TimeAgentStatus.FINISHED);
      service.initInfo();
    } catch (TimeAgentException exception) {
      logError(exception);
    }
    // send protocol
  }

}
