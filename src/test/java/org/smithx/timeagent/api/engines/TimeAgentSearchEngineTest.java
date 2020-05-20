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
import org.smithx.timeagent.api.configuration.TimeAgentMessages;
import org.smithx.timeagent.api.configuration.TimeAgentValues;
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentInfoSearch;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.smithx.timeagent.api.repositories.TimeAgentInfoRepository;
import org.springframework.data.domain.PageRequest;

/**
 * testing the TimeAgentSearchEngine.
 *
 * @author norman schmidt {smithx}
 * @since 15.05.2020
 * 
 */
@MockitoSettings(strictness = Strictness.LENIENT)
public class TimeAgentSearchEngineTest {
  static final String AGENTNAME = "agent";
  static final int MAX_SEARCH_VALUE = 50;

  TimeAgentSearchEngine classUnderTest;

  @Mock
  TimeAgentValues agentValues;

  @Mock
  TimeAgentInfoRepository agentInfoRepository;

  @Mock
  TimeAgentMessages messages;

  PageRequest pagable;
  TimeAgentInfoSearch searchModel;
  List<TimeAgentInfo> resultList;

  @BeforeEach
  void beforeEach() {
    classUnderTest = new TimeAgentSearchEngine(agentValues, agentInfoRepository, messages);
    pagable = PageRequest.of(0, MAX_SEARCH_VALUE);
    searchModel = new TimeAgentInfoSearch();
    resultList = Arrays.asList(new TimeAgentInfo());

    when(agentValues.getAgentName()).thenReturn(AGENTNAME);
    when(agentValues.getMaxLimitSearch()).thenReturn(MAX_SEARCH_VALUE);
  }

  @Test
  void testSearchModelIsNull() {
    TimeAgentRuntimeException exception = assertThrows(TimeAgentRuntimeException.class, () -> classUnderTest.searchAgentInfo(null));
    assertEquals(TimeAgentExceptionCause.INVALID_SEARCH_MODEL, exception.getErrorCause());
  }

  @Test
  void testSearchModelHasNoSearchValues() {
    when(agentInfoRepository.findByAgentNameOrderByUpdatedAtDesc(AGENTNAME, pagable)).thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasLimitOverMax() {
    searchModel.setLimit(100);

    when(agentInfoRepository.findByAgentNameOrderByUpdatedAtDesc(AGENTNAME, pagable)).thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasValidLimit() {
    searchModel.setLimit(10);
    pagable = PageRequest.of(0, 10);

    when(agentInfoRepository.findByAgentNameOrderByUpdatedAtDesc(AGENTNAME, pagable)).thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatus() {
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(agentInfoRepository.findByAgentNameAndStatusOrderByUpdatedAtDesc(AGENTNAME, TimeAgentStatus.FINISHED, pagable))
        .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasExecutor() {
    searchModel.setExecutor("user");

    when(agentInfoRepository.findByAgentNameAndExecutorOrderByUpdatedAtDesc(AGENTNAME, "user", pagable)).thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndExecutor() {
    searchModel.setExecutor("user");
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(agentInfoRepository.findByAgentNameAndStatusAndExecutorOrderByUpdatedAtDesc(AGENTNAME, TimeAgentStatus.FINISHED, "user", pagable))
        .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasFromStartTime() {
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);

    when(agentInfoRepository.findByAgentNameAndStartTimeExecutionAfterOrderByUpdatedAtDesc(AGENTNAME, fromStartTimeExecution, pagable))
        .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndFromStartTime() {
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(agentInfoRepository.findByAgentNameAndStatusAndStartTimeExecutionAfterOrderByUpdatedAtDesc(AGENTNAME, TimeAgentStatus.FINISHED,
        fromStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndExecutorAndFromStartTime() {
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);
    searchModel.setExecutor("user");

    when(agentInfoRepository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionAfterOrderByUpdatedAtDesc(AGENTNAME,
        TimeAgentStatus.FINISHED, "user", fromStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);

    when(agentInfoRepository.findByAgentNameAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(AGENTNAME, toStartTimeExecution, pagable))
        .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(agentInfoRepository.findByAgentNameAndStatusAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(AGENTNAME, TimeAgentStatus.FINISHED,
        toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndExecutorAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);
    searchModel.setExecutor("user");

    when(agentInfoRepository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(AGENTNAME,
        TimeAgentStatus.FINISHED, "user", toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndExecutorAndFromStartTimeAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);
    searchModel.setExecutor("user");

    when(agentInfoRepository.findByAgentNameAndStatusAndExecutorAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(AGENTNAME,
        TimeAgentStatus.FINISHED, "user", fromStartTimeExecution, toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasStatusAndFromStartTimeAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setStatus(TimeAgentStatus.FINISHED);

    when(agentInfoRepository.findByAgentNameAndStatusAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(AGENTNAME,
        TimeAgentStatus.FINISHED, fromStartTimeExecution, toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasExecutorAndFromStartTimeAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setExecutor("user");

    when(agentInfoRepository.findByAgentNameAndExecutorAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(AGENTNAME,
        "user", fromStartTimeExecution, toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasFromStartTimeAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);

    when(agentInfoRepository.findByAgentNameAndStartTimeExecutionBetweenOrderByUpdatedAtDesc(AGENTNAME,
        fromStartTimeExecution, toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasExecutorAndToStartTime() {
    LocalDateTime toStartTimeExecution = LocalDateTime.now();
    searchModel.setToStartTimeExecution(toStartTimeExecution);
    searchModel.setExecutor("user");

    when(agentInfoRepository.findByAgentNameAndExecutorAndStartTimeExecutionBeforeOrderByUpdatedAtDesc(AGENTNAME, "user",
        toStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }

  @Test
  void testSearchModelHasExecutorAndFromStartTime() {
    LocalDateTime fromStartTimeExecution = LocalDateTime.now();
    searchModel.setFromStartTimeExecution(fromStartTimeExecution);
    searchModel.setExecutor("user");

    when(agentInfoRepository.findByAgentNameAndExecutorAndStartTimeExecutionAfterOrderByUpdatedAtDesc(AGENTNAME, "user",
        fromStartTimeExecution, pagable))
            .thenReturn(resultList);
    assertEquals(resultList, classUnderTest.searchAgentInfo(searchModel));
  }
}
