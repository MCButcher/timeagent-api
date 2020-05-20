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
package org.smithx.timeagent.api.engines;

import java.util.List;

import org.smithx.timeagent.api.configuration.TimeAgentMessages;
import org.smithx.timeagent.api.configuration.TimeAgentValues;
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentInfoSearch;
import org.smithx.timeagent.api.repositories.TimeAgentInfoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * search engine for validating the search model and execute the search on the
 * database.
 *
 * @author norman schmidt {smithx}
 * @since 15.05.2020
 * 
 */
@Data
@AllArgsConstructor
@Component
@Slf4j
public class TimeAgentSearchEngine {
  private TimeAgentValues agentValues;
  private TimeAgentInfoRepository agentInfoRepository;
  private TimeAgentMessages messages;

  public List<TimeAgentInfo> searchAgentInfo(TimeAgentInfoSearch searchModel) {
    int searchFlag = validateSearchModel(searchModel);
    String agentName = agentValues.getAgentName();

    PageRequest pagable = PageRequest.of(0, searchModel.getLimit());

    if (log.isDebugEnabled()) {
      log.debug(messages.getMessage("log.search.validate.end", searchModel, searchFlag));
    }

    if (searchFlag > 30) {
      return agentInfoRepository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(
          agentName, searchModel.getStatus(), searchModel.getExecutor(), searchModel.getFromStartTimeExecution(),
          searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 28) {
      return agentInfoRepository.findByAgentNameAndExecutorAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(
          agentName,
          searchModel.getExecutor(), searchModel.getFromStartTimeExecution(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 26) {
      return agentInfoRepository.findByAgentNameAndStatusAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(
          agentName,
          searchModel.getStatus(), searchModel.getFromStartTimeExecution(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 24) {
      return agentInfoRepository.findByAgentNameAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(agentName,
          searchModel.getFromStartTimeExecution(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 22) {
      return agentInfoRepository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(
          agentName,
          searchModel.getStatus(), searchModel.getExecutor(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 20) {
      return agentInfoRepository.findByAgentNameAndExecutorAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(
          agentName,
          searchModel.getExecutor(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 18) {
      return agentInfoRepository.findByAgentNameAndStatusAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(agentName,
          searchModel.getStatus(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 16) {
      return agentInfoRepository.findByAgentNameAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(agentName,
          searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 14) {
      return agentInfoRepository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionAfterOrderByUpdatedAtDesc(
          agentName,
          searchModel.getStatus(), searchModel.getExecutor(), searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 12) {
      return agentInfoRepository.findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByUpdatedAtDesc(
          agentName,
          searchModel.getExecutor(), searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 10) {
      return agentInfoRepository.findByAgentNameAndStatusAndStartTimeExecutionAfterOrderByUpdatedAtDesc(agentName,
          searchModel.getStatus(), searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 8) {
      return agentInfoRepository.findByAgentNameAndStartTimeExecutionAfterOrderByUpdatedAtDesc(agentName,
          searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 6) {
      return agentInfoRepository.findByAgentNameAndStatusAndExecutorOrderByUpdatedAtDesc(agentName,
          searchModel.getStatus(),
          searchModel.getExecutor(), pagable);
    } else if (searchFlag > 4) {
      return agentInfoRepository.findByAgentNameAndExecutorOrderByUpdatedAtDesc(agentName,
          searchModel.getExecutor(),
          pagable);
    } else if (searchFlag > 2) {
      return agentInfoRepository.findByAgentNameAndStatusOrderByUpdatedAtDesc(agentName, searchModel.getStatus(),
          pagable);
    }

    return agentInfoRepository.findByAgentNameOrderByUpdatedAtDesc(agentName, pagable);
  }

  private int validateSearchModel(TimeAgentInfoSearch searchModel) {
    int searchFlag = 1;

    if (searchModel == null) {
      throw new TimeAgentRuntimeException(TimeAgentExceptionCause.INVALID_SEARCH_MODEL, "search model is null");
    }

    if (searchModel.getLimit() <= 0 || searchModel.getLimit() > agentValues.getMaxLimitSearch()) {
      if (log.isDebugEnabled()) {
        log.debug(messages.getMessage("log.search.validate.limit", searchModel.getLimit(), agentValues.getMaxLimitSearch()));
      }
      searchModel.setLimit(agentValues.getMaxLimitSearch());
    }

    if (searchModel.getStatus() != null) {
      searchFlag = searchFlag + 2;
      if (log.isDebugEnabled()) {
        log.debug(messages.getMessage("log.search.validate.status", searchModel.getStatus(), searchFlag));
      }
    }

    if (searchModel.getExecutor() != null) {
      searchFlag = searchFlag + 4;
      if (log.isDebugEnabled()) {
        log.debug(messages.getMessage("log.search.validate.executor", searchModel.getExecutor(), searchFlag));
      }
    }

    if (searchModel.getFromStartTimeExecution() != null) {
      searchFlag = searchFlag + 8;
      if (log.isDebugEnabled()) {
        log.debug(messages.getMessage("log.search.validate.to.startTime", searchModel.getFromStartTimeExecution(), searchFlag));
      }
    }

    if (searchModel.getToStartTimeExecution() != null) {
      searchFlag = searchFlag + 16;
      if (log.isDebugEnabled()) {
        log.debug(messages.getMessage("log.search.validate.from.startTime", searchModel.getToStartTimeExecution(), searchFlag));
      }
    }

    return searchFlag;
  }
}
