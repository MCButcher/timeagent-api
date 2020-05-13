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

import org.smithx.timeagent.api.agent.TimeAgent;
import org.smithx.timeagent.api.configuration.TimeAgentValues;
import org.smithx.timeagent.api.exceptions.TimeAgentException;
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.smithx.timeagent.api.models.TimeAgentArgument;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentInfoSearch;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.smithx.timeagent.api.repositories.TimeAgentInfoRepository;
import org.smithx.timeagent.api.threads.TimeAgentRunnable;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

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
  private TimeAgentValues agentValues;
  private TimeAgentInfoRepository agentInfoRepository;
  private TimeAgentInfo agentInfo;

  private TimeAgentRunnable agent;
  private ThreadPoolTaskScheduler scheduler;
  private ScheduledFuture<?> future;

  public TimeAgentService(TimeAgentValues agentValues, TimeAgentInfoRepository agentInfoRepository, ThreadPoolTaskScheduler scheduler) {
    this.agentValues = agentValues;
    this.agentInfoRepository = agentInfoRepository;
    this.scheduler = scheduler;
  }

  public TimeAgentInfo updateInfo() {
    agentInfo = agentInfoRepository.save(agentInfo);
    return agentInfo;
  }

  public TimeAgentInfo getAgentInfo() {
    return agentInfo;
  }

  public List<TimeAgentInfo> searchInfo(TimeAgentInfoSearch searchModel) {
    int searchFlag = validateSearchModel(searchModel);
    PageRequest pagable = PageRequest.of(0, searchModel.getLimit());

    if (searchFlag > 30) {
      return agentInfoRepository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(
          agentInfo.getAgentName(), searchModel.getStatus(), searchModel.getExecutor(), searchModel.getFromStartTimeExecution(),
          searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 28) {
      return agentInfoRepository.findByAgentNameAndExecutorAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(
          agentInfo.getAgentName(),
          searchModel.getExecutor(), searchModel.getFromStartTimeExecution(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 26) {
      return agentInfoRepository.findByAgentNameAndStatusAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(
          agentInfo.getAgentName(),
          searchModel.getStatus(), searchModel.getFromStartTimeExecution(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 24) {
      return agentInfoRepository.findByAgentNameAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(agentInfo.getAgentName(),
          searchModel.getFromStartTimeExecution(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 22) {
      return agentInfoRepository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(
          agentInfo.getAgentName(),
          searchModel.getStatus(), searchModel.getExecutor(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 20) {
      return agentInfoRepository.findByAgentNameAndExecutorAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(
          agentInfo.getAgentName(),
          searchModel.getExecutor(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 18) {
      return agentInfoRepository.findByAgentNameAndStatusAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(agentInfo.getAgentName(),
          searchModel.getStatus(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 16) {
      return agentInfoRepository.findByAgentNameAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(agentInfo.getAgentName(),
          searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 14) {
      return agentInfoRepository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionAfterOrderByUpdatedAtDesc(
          agentInfo.getAgentName(),
          searchModel.getStatus(), searchModel.getExecutor(), searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 12) {
      return agentInfoRepository.findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByUpdatedAtDesc(
          agentInfo.getAgentName(),
          searchModel.getExecutor(), searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 10) {
      return agentInfoRepository.findByAgentNameAndStatusAndStartTimeExecutionAfterOrderByUpdatedAtDesc(agentInfo.getAgentName(),
          searchModel.getStatus(), searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 8) {
      return agentInfoRepository.findByAgentNameAndStartTimeExecutionAfterOrderByUpdatedAtDesc(agentInfo.getAgentName(),
          searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 6) {
      return agentInfoRepository.findByAgentNameAndStatusAndExecutorOrderByUpdatedAtDesc(agentInfo.getAgentName(),
          searchModel.getStatus(),
          searchModel.getExecutor(), pagable);
    } else if (searchFlag > 4) {
      return agentInfoRepository.findByAgentNameAndExecutorOrderByUpdatedAtDesc(agentInfo.getAgentName(),
          searchModel.getExecutor(),
          pagable);
    } else if (searchFlag > 2) {
      return agentInfoRepository.findByAgentNameAndStatusOrderByUpdatedAtDesc(agentInfo.getAgentName(), searchModel.getStatus(),
          pagable);
    }

    return agentInfoRepository.findByAgentNameOrderByUpdatedAtDesc(agentInfo.getAgentName(), pagable);
  }

  private int validateSearchModel(TimeAgentInfoSearch searchModel) {
    int searchFlag = 1;

    if (searchModel == null) {
      throw new TimeAgentRuntimeException(TimeAgentExceptionCause.INVALID_SEARCH_MODEL, "search model is null");
    }

    if (searchModel.getLimit() <= 0 || searchModel.getLimit() > agentValues.getMaxLimitSearch()) {
      searchModel.setLimit(agentValues.getMaxLimitSearch());
    }

    if (searchModel.getStatus() != null) {
      searchFlag = searchFlag + 2;
    }

    if (searchModel.getExecutor() != null) {
      searchFlag = searchFlag + 4;
    }

    if (searchModel.getFromStartTimeExecution() != null) {
      searchFlag = searchFlag + 8;
    }

    if (searchModel.getToStartTimeExecution() != null) {
      searchFlag = searchFlag + 16;
    }

    return searchFlag;
  }

  public TimeAgentInfo deleteTrigger() {
    if (future != null && !future.isCancelled()) {
      if (future.cancel(false)) {
        agentInfo.deleteCrontrigger();
        updateInfo();
      }
    }
    return agentInfo;
  }

  public TimeAgentInfo setTrigger(String value) {
    if (CronSequenceGenerator.isValidExpression(value)) {
      agentInfo.setCrontrigger(value);
      updateInfo();

      future = scheduler.schedule(agent, new CronTrigger(value));

      return agentInfo;
    }
    throw new TimeAgentRuntimeException(TimeAgentExceptionCause.INVALID_TRIGGER, String.format("invalid trigger: ", value));
  }

  public void run(TimeAgentArgument... arguments) {
    agent.run();
  }

  @PostConstruct
  public void initInfo() {
    TimeAgentInfo agentInfo = agentInfoRepository.findTop1ByAgentNameOrderByUpdatedAtDesc(agentValues.getAgentName());
    if (agentInfo != null) {
      this.agentInfo = agentInfo;
      agentInfo.init();
    } else {
      this.agentInfo = new TimeAgentInfo(agentValues.getAgentName(), TimeAgentStatus.READY);
    }
    updateInfo();
  }

  @PostConstruct
  public void initAgent() {
    agent = new TimeAgentRunnable(new TimeAgent(this) {

      @Override
      protected void runImplementation(TimeAgentArgument... arguments) throws TimeAgentException {

      }
    });
  }

}
