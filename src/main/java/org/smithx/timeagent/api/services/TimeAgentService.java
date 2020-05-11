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

import javax.annotation.PostConstruct;

import org.smithx.timeagent.api.configuration.TimeAgentValues;
import org.smithx.timeagent.api.exceptions.TimeAgentException;
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentInfoSearch;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.smithx.timeagent.api.repositories.findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByCreatedAtDesc;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

/**
 * service for accessing and handling the status information of an agent.
 *
 * @author norman schmidt {smithx}
 * @since 07.05.2020
 * 
 */
@Service
@AllArgsConstructor
public class TimeAgentService {
  private TimeAgentValues values;
  private findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByCreatedAtDesc repository;
  private TimeAgentInfo timeAgentInfo;

  @PostConstruct
  private void init() {
    timeAgentInfo.setStatus(TimeAgentStatus.READY);
    repository.save(timeAgentInfo);
  }

  public TimeAgentInfo getInfo() {
    return timeAgentInfo;
  }

  public List<TimeAgentInfo> searchInfo(TimeAgentInfoSearch searchModel) {
    int searchFlag = validateSearchModel(searchModel);
    PageRequest pagable = PageRequest.of(0, searchModel.getLimit());

    if (searchFlag > 30) {
      return repository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBetweenOrderByCreatedAtDesc(
          timeAgentInfo.getAgentName(), searchModel.getStatus(), searchModel.getExecutor(), searchModel.getFromStartTimeExecution(),
          searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 28) {
      return repository.findByAgentNameAndExecutorAndStartTimeExecutionBetweenOrderByCreatedAtDesc(timeAgentInfo.getAgentName(),
          searchModel.getExecutor(), searchModel.getFromStartTimeExecution(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 26) {
      return repository.findByAgentNameAndStatusAndStartTimeExecutionBetweenOrderByCreatedAtDesc(timeAgentInfo.getAgentName(),
          searchModel.getStatus(), searchModel.getFromStartTimeExecution(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 24) {
      return repository.findByAgentNameAndStartTimeExecutionBetweenOrderByCreatedAtDesc(timeAgentInfo.getAgentName(),
          searchModel.getFromStartTimeExecution(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 22) {
      return repository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBeforeOrderByCreatedAtDesc(
          timeAgentInfo.getAgentName(),
          searchModel.getStatus(), searchModel.getExecutor(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 20) {
      return repository.findByAgentNameAndExecutorAndStartTimeExecutionBeforeOrderByCreatedAtDesc(timeAgentInfo.getAgentName(),
          searchModel.getExecutor(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 18) {
      return repository.findByAgentNameAndStatusAndStartTimeExecutionBeforeOrderByCreatedAtDesc(timeAgentInfo.getAgentName(),
          searchModel.getStatus(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 16) {
      return repository.findByAgentNameAndStartTimeExecutionBeforeOrderByCreatedAtDesc(timeAgentInfo.getAgentName(),
          searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 14) {
      return repository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionAfterOrderByCreatedAtDesc(
          timeAgentInfo.getAgentName(),
          searchModel.getStatus(), searchModel.getExecutor(), searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 12) {
      return repository.findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByCreatedAtDesc(timeAgentInfo.getAgentName(),
          searchModel.getExecutor(), searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 10) {
      return repository.findByAgentNameAndStatusAndStartTimeExecutionAfterOrderByCreatedAtDesc(timeAgentInfo.getAgentName(),
          searchModel.getStatus(), searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 8) {
      return repository.findByAgentNameAndStartTimeExecutionAfterOrderByCreatedAtDesc(timeAgentInfo.getAgentName(),
          searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 6) {
      return repository.findByAgentNameAndStatusAndExecutorOrderByCreatedAtDesc(timeAgentInfo.getAgentName(), searchModel.getStatus(),
          searchModel.getExecutor(), pagable);
    } else if (searchFlag > 4) {
      return repository.findByAgentNameAndExecutorOrderByCreatedAtDesc(timeAgentInfo.getAgentName(), searchModel.getExecutor(),
          pagable);
    } else if (searchFlag > 2) {
      return repository.findByAgentNameAndStatusOrderByCreatedAtDesc(timeAgentInfo.getAgentName(), searchModel.getStatus(), pagable);
    }

    return repository.findByAgentNameOrderByCreatedAtDesc(timeAgentInfo.getAgentName(), pagable);
  }

  private int validateSearchModel(TimeAgentInfoSearch searchModel) {
    int searchFlag = 1;

    if (searchModel == null) {
      throw new TimeAgentException(TimeAgentExceptionCause.INVALID_SEARCH_MODEL, "search model is null");
    }

    if (searchModel.getLimit() <= 0 || searchModel.getLimit() > values.getMaxLimitSearch()) {
      searchModel.setLimit(values.getMaxLimitSearch());
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

}
