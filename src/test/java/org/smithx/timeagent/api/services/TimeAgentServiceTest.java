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

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.smithx.timeagent.api.agent.TimeAgent;
import org.smithx.timeagent.api.configuration.TimeAgentValues;
import org.smithx.timeagent.api.exceptions.TimeAgentException;
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentInfoSearch;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.smithx.timeagent.api.repositories.TimeAgentInfoRepository;
import org.smithx.timeagent.api.threads.TimeAgentRunnable;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

/**
 * testing the TimeAgentService.
 *
 * @author norman schmidt {smithx}
 * @since 11.05.2020
 * 
 */
@MockitoSettings(strictness = Strictness.LENIENT)
public class TimeAgentServiceTest {
  static final String AGENTNAME = "agent";
  static final int MAX_SEARCH_VALUE = 50;

  TimeAgentService serviceUnderTest;

  @Mock
  TimeAgentInfo timeAgentInfo;

  @Mock
  TimeAgentInfoRepository repository;

  @Mock
  TimeAgentValues values;

  @Mock
  TimeAgent agent;

  @Mock
  ThreadPoolTaskScheduler scheduler;

  TimeAgentInfoSearch searchModel;
  List<TimeAgentInfo> resultList;
  PageRequest pagable;
  TimeAgentInfo initInfo;

  @BeforeEach
  void beforeEach() throws TimeAgentException {
    initInfo = new TimeAgentInfo(AGENTNAME, TimeAgentStatus.READY);
    when(repository.save(any(TimeAgentInfo.class))).thenReturn(initInfo);
    when(values.getAgentName()).thenReturn(AGENTNAME);
    when(values.getMaxLimitSearch()).thenReturn(MAX_SEARCH_VALUE);

    serviceUnderTest = new TimeAgentService(agent, values, repository, scheduler);

    searchModel = new TimeAgentInfoSearch();
    resultList = Arrays.asList(new TimeAgentInfo());
    pagable = PageRequest.of(0, MAX_SEARCH_VALUE);

    serviceUnderTest.initInfo();
  }

  @Test
  void testTimeAgentInfo() {
    assertEquals(AGENTNAME, serviceUnderTest.getAgentInfo().getAgentName());
    assertEquals(TimeAgentStatus.READY, serviceUnderTest.getAgentInfo().getStatus());
  }

  @Test
  void testTimeAgentInfoWithDatabaseEntry() throws TimeAgentException {
    initInfo.setStatus(TimeAgentStatus.FINISHED);
    when(repository.findTop1ByAgentNameOrderByUpdatedAtDesc(anyString())).thenReturn(initInfo);
    serviceUnderTest.initInfo();
    assertEquals(AGENTNAME, serviceUnderTest.getAgentInfo().getAgentName());
    assertEquals(TimeAgentStatus.READY, serviceUnderTest.getAgentInfo().getStatus());
  }

  @Test
  void testSetTrigger() throws TimeAgentException {
    serviceUnderTest.initAgent();

    String trigger = "0 0 0 1/1 * ?";
    initInfo.setCrontrigger(trigger);

    TimeAgentInfo result = serviceUnderTest.setTrigger(trigger);
    assertEquals(trigger, result.getCrontrigger());
  }

  @Test
  void testSetTriggerThrowsExceptionInvalidTrigger() {
    String trigger = "xyz";
    TimeAgentRuntimeException exception = assertThrows(TimeAgentRuntimeException.class, () -> serviceUnderTest.setTrigger(trigger));
    assertEquals(TimeAgentExceptionCause.INVALID_TRIGGER, exception.getErrorCause());
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  void testDeleteTriggerAfterSetTrigger() throws TimeAgentException {
    ScheduledFuture future = mock(ScheduledFuture.class);
    when(scheduler.schedule(any(TimeAgentRunnable.class), any(CronTrigger.class))).thenReturn(future);
    when(future.cancel(false)).thenReturn(true);

    String trigger = "0 0 0 1/1 * ?";
    initInfo.setCrontrigger(trigger);

    serviceUnderTest.initAgent();
    serviceUnderTest.setTrigger(trigger);

    initInfo.deleteCrontrigger();

    TimeAgentInfo result = serviceUnderTest.deleteTrigger();
    assertNull(result.getCrontrigger());
  }

  @Test
  void testDeleteTrigger() throws TimeAgentException {
    TimeAgentInfo result = serviceUnderTest.deleteTrigger();
    assertNull(result.getCrontrigger());
  }

  @Test
  void testSearchModelIsNull() {
    TimeAgentRuntimeException exception = assertThrows(TimeAgentRuntimeException.class, () -> serviceUnderTest.searchInfo(null));
    assertEquals(TimeAgentExceptionCause.INVALID_SEARCH_MODEL, exception.getErrorCause());
  }

  @Test
  void testSearchModelHasNoSearchValues() {

    when(repository.findByAgentNameOrderByUpdatedAtDesc(AGENTNAME, pagable)).thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasLimitOverMax() {
    searchModel.setLimit(100);

    when(repository.findByAgentNameOrderByUpdatedAtDesc(AGENTNAME, pagable)).thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasValidLimit() {
    searchModel.setLimit(10);
    pagable = PageRequest.of(0, 10);

    when(repository.findByAgentNameOrderByUpdatedAtDesc(AGENTNAME, pagable)).thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatus() {
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(repository.findByAgentNameAndStatusOrderByUpdatedAtDesc(AGENTNAME, TimeAgentStatus.FINISHED, pagable)).thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasExecutor() {
    searchModel.setExecutor("user");

    when(repository.findByAgentNameAndExecutorOrderByUpdatedAtDesc(AGENTNAME, "user", pagable)).thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndExecutor() {
    searchModel.setExecutor("user");
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(repository.findByAgentNameAndStatusAndExecutorOrderByUpdatedAtDesc(AGENTNAME, TimeAgentStatus.FINISHED, "user", pagable))
        .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasFromStartTime() {
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);

    when(repository.findByAgentNameAndStartTimeExecutionAfterOrderByUpdatedAtDesc(AGENTNAME, fromStartTimeExecution, pagable))
        .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndFromStartTime() {
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(repository.findByAgentNameAndStatusAndStartTimeExecutionAfterOrderByUpdatedAtDesc(AGENTNAME, TimeAgentStatus.FINISHED,
        fromStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndExecutorAndFromStartTime() {
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);
    searchModel.setExecutor("user");

    when(repository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionAfterOrderByUpdatedAtDesc(AGENTNAME,
        TimeAgentStatus.FINISHED, "user", fromStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);

    when(repository.findByAgentNameAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(AGENTNAME, toStartTimeExecution, pagable))
        .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(repository.findByAgentNameAndStatusAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(AGENTNAME, TimeAgentStatus.FINISHED,
        toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndExecutorAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);
    searchModel.setExecutor("user");

    when(repository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(AGENTNAME,
        TimeAgentStatus.FINISHED, "user", toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndExecutorAndFromStartTimeAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);
    searchModel.setExecutor("user");

    when(repository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(AGENTNAME,
        TimeAgentStatus.FINISHED, "user", fromStartTimeExecution, toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndFromStartTimeAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(repository.findByAgentNameAndStatusAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(AGENTNAME,
        TimeAgentStatus.FINISHED, fromStartTimeExecution, toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasExecutorAndFromStartTimeAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setExecutor("user");

    when(repository.findByAgentNameAndExecutorAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(AGENTNAME,
        "user", fromStartTimeExecution, toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasFromStartTimeAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);

    when(repository.findByAgentNameAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(AGENTNAME,
        fromStartTimeExecution, toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasExecutorAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setExecutor("user");

    when(repository.findByAgentNameAndExecutorAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(AGENTNAME, "user",
        toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasExecutorAndFromStartTime() {
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setExecutor("user");

    when(repository.findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByUpdatedAtDesc(AGENTNAME, "user",
        fromStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

}
