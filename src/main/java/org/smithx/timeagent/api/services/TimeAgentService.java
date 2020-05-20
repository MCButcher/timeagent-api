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
package org.smithx.timeagent.api.services;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.smithx.timeagent.api.agent.TimeAgent;
import org.smithx.timeagent.api.agent.TimeAgentRuntime;
import org.smithx.timeagent.api.configuration.TimeAgentMessages;
import org.smithx.timeagent.api.engines.TimeAgentModelEngine;
import org.smithx.timeagent.api.engines.TimeAgentSearchEngine;
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.smithx.timeagent.api.models.TimeAgentArgument;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentInfoSearch;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.smithx.timeagent.api.threads.TimeAgentRunnable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * service for accessing the agent and its administration.
 *
 * @author norman schmidt {smithx}
 * @since 07.05.2020
 * 
 */
@Service
@Slf4j
public class TimeAgentService {
  private TimeAgent agent;
  private TimeAgentInfo agentInfo;
  private TimeAgentModelEngine modelEngine;
  private TimeAgentSearchEngine searchEngine;

  @Getter
  private TimeAgentMessages messages;

  private TimeAgentRunnable agentRunnable;
  private ThreadPoolTaskScheduler scheduler;
  private ScheduledFuture<?> future;

  public TimeAgentService(TimeAgent agent, TimeAgentModelEngine modelEngine, TimeAgentSearchEngine searchEngine,
      ThreadPoolTaskScheduler scheduler, TimeAgentMessages messages) {
    this.agent = agent;
    this.modelEngine = modelEngine;
    this.searchEngine = searchEngine;
    this.scheduler = scheduler;
    this.messages = messages;
  }

  public TimeAgentInfo getAgentInfo() {
    return agentInfo;
  }

  public TimeAgentInfo updateAgentInfo() {
    agentInfo = modelEngine.updateAgentInfo(agentInfo);
    return agentInfo;
  }

  public List<TimeAgentInfo> searchInfo(TimeAgentInfoSearch searchModel) {
    if (log.isDebugEnabled()) {
      log.debug(messages.getMessage("log.search", searchModel));
    }
    return searchEngine.searchAgentInfo(searchModel);
  }

  public TimeAgentInfo deleteTrigger() {
    if (cancelTriggerOk()) {
      if (log.isDebugEnabled()) {
        log.debug(messages.getMessage("log.delete.trigger"));
      }
      return modelEngine.saveTriggerToAgentInfo(null, this.agentInfo);
    }
    throw new TimeAgentRuntimeException(TimeAgentExceptionCause.CANCEL_TRIGGER, "error on trigger cancellation");
  }

  public TimeAgentInfo setTrigger(String trigger) {
    if (CronSequenceGenerator.isValidExpression(trigger)) {
      if (cancelTriggerOk()) {
        scheduleTrigger(trigger);
        return modelEngine.saveTriggerToAgentInfo(trigger, this.agentInfo);
      }
    }
    throw new TimeAgentRuntimeException(TimeAgentExceptionCause.INVALID_TRIGGER, String.format("invalid trigger: ", trigger));
  }

  public void run(TimeAgentArgument... arguments) {
    isAlreadyRunning();
    agentRunnable.setArguments(arguments);
    new Thread(agentRunnable).start();
  }

  @Transactional
  public void initAgentInfo() {
    agentInfo = modelEngine.nextAgentInfo();
    scheduleTrigger(agentInfo.getCrontrigger());
    if (log.isDebugEnabled()) {
      log.debug(messages.getMessage("log.init.agent", agentInfo));
    }
  }

  public void isAlreadyRunning() {
    if (TimeAgentStatus.RUNNING.equals(agentInfo.getStatus())) {
      log.warn("agent is already running");
      throw new TimeAgentRuntimeException(TimeAgentExceptionCause.ALREADY_RUNNING,
          "the agent is already running since " + agentInfo.getStartTimeExecution());
    }
  }

  @PostConstruct
  protected void initAgent() {
    agentRunnable = new TimeAgentRunnable(new TimeAgentRuntime(this, agent));
    initAgentInfo();
  }

  private boolean cancelTriggerOk() {
    return future != null && !future.isCancelled() && future.cancel(false) || (future == null || future.isCancelled());
  }

  private void scheduleTrigger(String trigger) {
    if (!StringUtils.isEmpty(trigger)) {
      future = scheduler.schedule(agentRunnable, new CronTrigger(trigger));
    }
  }

}
