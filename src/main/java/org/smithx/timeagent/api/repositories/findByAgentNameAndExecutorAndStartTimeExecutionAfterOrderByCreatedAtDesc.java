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
package org.smithx.timeagent.api.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * repository for the info of an agent.
 *
 * @author norman schmidt {smithx}
 * @since 08.05.2020
 * 
 */
@Repository
public interface findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByCreatedAtDesc extends CrudRepository<TimeAgentInfo, Long> {
  public List<TimeAgentInfo> findByAgentNameOrderByCreatedAtDesc(String agentName, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusOrderByCreatedAtDesc(String agentName, TimeAgentStatus status, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndExecutorOrderByCreatedAtDesc(String agentName, String lastExecutor, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndExecutorOrderByCreatedAtDesc(String agentName, TimeAgentStatus status,
      String lastExecutor, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStartTimeExecutionAfterOrderByCreatedAtDesc(String agentName,
      LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStartTimeExecutionBeforeOrderByCreatedAtDesc(String agentName,
      LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByCreatedAtDesc(String agentName,
      String lastExecutor, LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndExecutorAndStartTimeExecutionBeforeOrderByCreatedAtDesc(String agentName,
      String lastExecutor, LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndStartTimeExecutionAfterOrderByCreatedAtDesc(String agentName,
      TimeAgentStatus status, LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndStartTimeExecutionBeforeOrderByCreatedAtDesc(String agentName,
      TimeAgentStatus status, LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndExecutorAndStartTimeExecutionAfterOrderByCreatedAtDesc(String agentName,
      TimeAgentStatus status, String lastExecutor, LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBeforeOrderByCreatedAtDesc(String agentName,
      TimeAgentStatus status, String lastExecutor, LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStartTimeExecutionBetweenOrderByCreatedAtDesc(String agentName,
      LocalDateTime fromStartTimeLastExecution, LocalDateTime toStartTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndStartTimeExecutionBetweenOrderByCreatedAtDesc(String agentName,
      TimeAgentStatus status, LocalDateTime fromStartTimeLastExecution, LocalDateTime toStartTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndExecutorAndStartTimeExecutionBetweenOrderByCreatedAtDesc(String agentName,
      String lastExecutor, LocalDateTime fromStartTimeLastExecution, LocalDateTime toStartTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBetweenOrderByCreatedAtDesc(String agentName,
      TimeAgentStatus status, String lastExecutor, LocalDateTime fromStartTimeLastExecution, LocalDateTime toStartTimeLastExecution,
      Pageable pagable);

}
