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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.smithx.timeagent.api.configuration.TimeAgentValues;
import org.smithx.timeagent.api.exceptions.TimeAgentException;
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentInfoSearch;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.smithx.timeagent.api.repositories.findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByCreatedAtDesc;
import org.springframework.data.domain.PageRequest;

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
  findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByCreatedAtDesc repository;

  @Mock
  TimeAgentValues values;

  TimeAgentInfoSearch searchModel;
  List<TimeAgentInfo> resultList;
  PageRequest pagable;

  @BeforeEach
  void beforeEach() {
    serviceUnderTest = new TimeAgentService(values, repository, timeAgentInfo);

    searchModel = new TimeAgentInfoSearch();
    resultList = Arrays.asList(new TimeAgentInfo());
    pagable = PageRequest.of(0, MAX_SEARCH_VALUE);

    when(timeAgentInfo.getAgentName()).thenReturn(AGENTNAME);
    when(values.getMaxLimitSearch()).thenReturn(MAX_SEARCH_VALUE);
  }

  @Test
  void testTimeAgentInfo() {
    assertEquals(AGENTNAME, serviceUnderTest.getInfo().getAgentName());
  }

  @Test
  void testSearchModelIsNull() {
    TimeAgentException exception = assertThrows(TimeAgentException.class, () -> serviceUnderTest.searchInfo(null));
    assertEquals(TimeAgentExceptionCause.INVALID_SEARCH_MODEL, exception.getErrorCause());
  }

  @Test
  void testSearchModelHasNoSearchValues() {

    when(repository.findByAgentNameOrderByCreatedAtDesc(AGENTNAME, pagable)).thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasLimitOverMax() {
    searchModel.setLimit(100);

    when(repository.findByAgentNameOrderByCreatedAtDesc(AGENTNAME, pagable)).thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasValidLimit() {
    searchModel.setLimit(10);
    pagable = PageRequest.of(0, 10);

    when(repository.findByAgentNameOrderByCreatedAtDesc(AGENTNAME, pagable)).thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatus() {
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(repository.findByAgentNameAndStatusOrderByCreatedAtDesc(AGENTNAME, TimeAgentStatus.FINISHED, pagable)).thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasExecutor() {
    searchModel.setExecutor("user");

    when(repository.findByAgentNameAndExecutorOrderByCreatedAtDesc(AGENTNAME, "user", pagable)).thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndExecutor() {
    searchModel.setExecutor("user");
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(repository.findByAgentNameAndStatusAndExecutorOrderByCreatedAtDesc(AGENTNAME, TimeAgentStatus.FINISHED, "user", pagable))
        .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasFromStartTime() {
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);

    when(repository.findByAgentNameAndStartTimeExecutionAfterOrderByCreatedAtDesc(AGENTNAME, fromStartTimeExecution, pagable))
        .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndFromStartTime() {
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(repository.findByAgentNameAndStatusAndStartTimeExecutionAfterOrderByCreatedAtDesc(AGENTNAME, TimeAgentStatus.FINISHED,
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

    when(repository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionAfterOrderByCreatedAtDesc(AGENTNAME,
        TimeAgentStatus.FINISHED, "user", fromStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);

    when(repository.findByAgentNameAndStartTimeExecutionBeforeOrderByCreatedAtDesc(AGENTNAME, toStartTimeExecution, pagable))
        .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(repository.findByAgentNameAndStatusAndStartTimeExecutionBeforeOrderByCreatedAtDesc(AGENTNAME, TimeAgentStatus.FINISHED,
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

    when(repository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBeforeOrderByCreatedAtDesc(AGENTNAME,
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

    when(repository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBetweenOrderByCreatedAtDesc(AGENTNAME,
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

    when(repository.findByAgentNameAndStatusAndStartTimeExecutionBetweenOrderByCreatedAtDesc(AGENTNAME,
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

    when(repository.findByAgentNameAndExecutorAndStartTimeExecutionBetweenOrderByCreatedAtDesc(AGENTNAME,
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

    when(repository.findByAgentNameAndStartTimeExecutionBetweenOrderByCreatedAtDesc(AGENTNAME,
        fromStartTimeExecution, toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasExecutorAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setExecutor("user");

    when(repository.findByAgentNameAndExecutorAndStartTimeExecutionBeforeOrderByCreatedAtDesc(AGENTNAME, "user",
        toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

  @Test
  void testSearchModelHasExecutorAndFromStartTime() {
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setExecutor("user");

    when(repository.findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByCreatedAtDesc(AGENTNAME, "user",
        fromStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, serviceUnderTest.searchInfo(searchModel));
  }

}
