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
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentInfoSearch;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.smithx.timeagent.api.repositories.TimeAgentInfoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * service for accessing and handling the status information of an agent.
 *
 * @author norman schmidt {smithx}
 * @since 07.05.2020
 * 
 */
@Service
public class TimeAgentInfoService extends TimeAgentService {

  public TimeAgentInfoService(TimeAgentValues agentValues, TimeAgentInfoRepository agentInfoRepository, TimeAgentInfo agentInfo) {
    super(agentValues, agentInfoRepository, agentInfo);
  }

  @PostConstruct
  private void init() {
    getAgentInfo().setStatus(TimeAgentStatus.READY);
    insertInfo();
  }

  public List<TimeAgentInfo> searchInfo(TimeAgentInfoSearch searchModel) {
    int searchFlag = validateSearchModel(searchModel);
    PageRequest pagable = PageRequest.of(0, searchModel.getLimit());

    if (searchFlag > 30) {
      return getAgentInfoRepository().findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBetweenOrderByCreatedAtDesc(
          getAgentInfo().getAgentName(), searchModel.getStatus(), searchModel.getExecutor(), searchModel.getFromStartTimeExecution(),
          searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 28) {
      return getAgentInfoRepository().findByAgentNameAndExecutorAndStartTimeExecutionBetweenOrderByCreatedAtDesc(
          getAgentInfo().getAgentName(),
          searchModel.getExecutor(), searchModel.getFromStartTimeExecution(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 26) {
      return getAgentInfoRepository().findByAgentNameAndStatusAndStartTimeExecutionBetweenOrderByCreatedAtDesc(
          getAgentInfo().getAgentName(),
          searchModel.getStatus(), searchModel.getFromStartTimeExecution(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 24) {
      return getAgentInfoRepository().findByAgentNameAndStartTimeExecutionBetweenOrderByCreatedAtDesc(getAgentInfo().getAgentName(),
          searchModel.getFromStartTimeExecution(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 22) {
      return getAgentInfoRepository().findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBeforeOrderByCreatedAtDesc(
          getAgentInfo().getAgentName(),
          searchModel.getStatus(), searchModel.getExecutor(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 20) {
      return getAgentInfoRepository().findByAgentNameAndExecutorAndStartTimeExecutionBeforeOrderByCreatedAtDesc(
          getAgentInfo().getAgentName(),
          searchModel.getExecutor(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 18) {
      return getAgentInfoRepository().findByAgentNameAndStatusAndStartTimeExecutionBeforeOrderByCreatedAtDesc(getAgentInfo().getAgentName(),
          searchModel.getStatus(), searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 16) {
      return getAgentInfoRepository().findByAgentNameAndStartTimeExecutionBeforeOrderByCreatedAtDesc(getAgentInfo().getAgentName(),
          searchModel.getToStartTimeExecution(), pagable);
    } else if (searchFlag > 14) {
      return getAgentInfoRepository().findByAgentNameAndStatusAndExecutorAndStartTimeExecutionAfterOrderByCreatedAtDesc(
          getAgentInfo().getAgentName(),
          searchModel.getStatus(), searchModel.getExecutor(), searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 12) {
      return getAgentInfoRepository().findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByCreatedAtDesc(
          getAgentInfo().getAgentName(),
          searchModel.getExecutor(), searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 10) {
      return getAgentInfoRepository().findByAgentNameAndStatusAndStartTimeExecutionAfterOrderByCreatedAtDesc(getAgentInfo().getAgentName(),
          searchModel.getStatus(), searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 8) {
      return getAgentInfoRepository().findByAgentNameAndStartTimeExecutionAfterOrderByCreatedAtDesc(getAgentInfo().getAgentName(),
          searchModel.getFromStartTimeExecution(), pagable);
    } else if (searchFlag > 6) {
      return getAgentInfoRepository().findByAgentNameAndStatusAndExecutorOrderByCreatedAtDesc(getAgentInfo().getAgentName(),
          searchModel.getStatus(),
          searchModel.getExecutor(), pagable);
    } else if (searchFlag > 4) {
      return getAgentInfoRepository().findByAgentNameAndExecutorOrderByCreatedAtDesc(getAgentInfo().getAgentName(),
          searchModel.getExecutor(),
          pagable);
    } else if (searchFlag > 2) {
      return getAgentInfoRepository().findByAgentNameAndStatusOrderByCreatedAtDesc(getAgentInfo().getAgentName(), searchModel.getStatus(),
          pagable);
    }

    return getAgentInfoRepository().findByAgentNameOrderByCreatedAtDesc(getAgentInfo().getAgentName(), pagable);
  }

  private int validateSearchModel(TimeAgentInfoSearch searchModel) {
    int searchFlag = 1;

    if (searchModel == null) {
      throw new TimeAgentRuntimeException(TimeAgentExceptionCause.INVALID_SEARCH_MODEL, "search model is null");
    }

    if (searchModel.getLimit() <= 0 || searchModel.getLimit() > getAgentValues().getMaxLimitSearch()) {
      searchModel.setLimit(getAgentValues().getMaxLimitSearch());
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
