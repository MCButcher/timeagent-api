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
 * repository for the info table of the agent.
 *
 * @author norman schmidt {smithx}
 * @since 08.05.2020
 * 
 */
@Repository
public interface TimeAgentInfoRepository extends CrudRepository<TimeAgentInfo, Long> {
  public List<TimeAgentInfo> findByAgentNameOrderByUpdatedAtDesc(String agentName, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusOrderByUpdatedAtDesc(String agentName, TimeAgentStatus status, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndExecutorOrderByUpdatedAtDesc(String agentName, String lastExecutor, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndExecutorOrderByUpdatedAtDesc(String agentName, TimeAgentStatus status,
      String lastExecutor, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStartTimeExecutionAfterOrderByUpdatedAtDesc(String agentName,
      LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(String agentName,
      LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByUpdatedAtDesc(String agentName,
      String lastExecutor, LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndExecutorAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(String agentName,
      String lastExecutor, LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndStartTimeExecutionAfterOrderByUpdatedAtDesc(String agentName,
      TimeAgentStatus status, LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(String agentName,
      TimeAgentStatus status, LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndExecutorAndStartTimeExecutionAfterOrderByUpdatedAtDesc(String agentName,
      TimeAgentStatus status, String lastExecutor, LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(String agentName,
      TimeAgentStatus status, String lastExecutor, LocalDateTime startTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(String agentName,
      LocalDateTime fromStartTimeLastExecution, LocalDateTime toStartTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(String agentName,
      TimeAgentStatus status, LocalDateTime fromStartTimeLastExecution, LocalDateTime toStartTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndExecutorAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(String agentName,
      String lastExecutor, LocalDateTime fromStartTimeLastExecution, LocalDateTime toStartTimeLastExecution, Pageable pagable);

  public List<TimeAgentInfo> findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(String agentName,
      TimeAgentStatus status, String lastExecutor, LocalDateTime fromStartTimeLastExecution, LocalDateTime toStartTimeLastExecution,
      Pageable pagable);

  public TimeAgentInfo findTop1ByAgentNameOrderByUpdatedAtDesc(String agentName);

  public TimeAgentInfo findTop1ByAgentNameAndStatusOrderByUpdatedAtDesc(String agentName, TimeAgentStatus status);
}
