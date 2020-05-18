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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.smithx.timeagent.api.configuration.TimeAgentValues;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.smithx.timeagent.api.repositories.TimeAgentInfoRepository;

/**
 * testing the TimeAgentModelEngine.
 *
 * @author norman schmidt {smithx}
 * @since 15.05.2020
 * 
 */
@MockitoSettings(strictness = Strictness.LENIENT)
public class TimeAgentModelEngineTest {
  static final String TRIGGER = "trigger";
  static final String AGENTNAME = "agent";

  TimeAgentModelEngine classUnderTest;

  @Mock
  TimeAgentValues agentValues;

  @Mock
  TimeAgentInfoRepository agentInfoRepository;

  @BeforeEach
  void beforeEach() {
    classUnderTest = new TimeAgentModelEngine(agentValues, agentInfoRepository);
    when(agentValues.getAgentName()).thenReturn(AGENTNAME);
  }

  @Test
  void testNextAgentInfoCreateNew() {
    TimeAgentInfo agentInfo = classUnderTest.nextAgentInfo();

    assertAll("check new model",
        () -> assertEquals(AGENTNAME, agentInfo.getAgentName()),
        () -> assertEquals(TimeAgentStatus.READY, agentInfo.getStatus()));
  }

  @Test
  void testNextAgentInfoCreateGetLastModel() {
    TimeAgentInfo expected = new TimeAgentInfo(AGENTNAME, TimeAgentStatus.FINISHED);

    expected.setCrontrigger(TRIGGER);
    expected.setCreatedAt(LocalDateTime.now());
    expected.setUpdatedAt(LocalDateTime.now());
    expected.setStartTimeExecution(LocalDateTime.now());
    expected.setFinishTimeExecution(LocalDateTime.now());
    expected.setExecutor("executor");
    expected.setId(1L);
    expected.addProtocol("message");

    when(agentInfoRepository.findTop1ByAgentNameOrderByUpdatedAtDesc(AGENTNAME)).thenReturn(expected);

    TimeAgentInfo agentInfo = classUnderTest.nextAgentInfo();

    assertAll("check new model",
        () -> assertEquals(AGENTNAME, agentInfo.getAgentName()),
        () -> assertEquals(TimeAgentStatus.READY, agentInfo.getStatus()),
        () -> assertEquals(TRIGGER, agentInfo.getCrontrigger()),
        () -> assertNull(agentInfo.getCreatedAt()),
        () -> assertNull(agentInfo.getUpdatedAt()),
        () -> assertNull(agentInfo.getStartTimeExecution()),
        () -> assertNull(agentInfo.getFinishTimeExecution()),
        () -> assertNull(agentInfo.getExecutor()),
        () -> assertNull(agentInfo.getId()),
        () -> assertTrue(agentInfo.getProtocol().isEmpty()));
  }

  @Test
  void testNextAgentInfoBetNotSet() {
    LocalDateTime timestamp = LocalDateTime.now();
    TimeAgentInfo expected = new TimeAgentInfo(AGENTNAME, TimeAgentStatus.NOT_SET);
    expected.setCrontrigger(TRIGGER);
    expected.setCreatedAt(timestamp);
    expected.setId(1L);
    expected.addProtocol("message");

    when(agentInfoRepository.findTop1ByAgentNameAndStatusOrderByUpdatedAtDesc(AGENTNAME, TimeAgentStatus.NOT_SET)).thenReturn(expected);

    TimeAgentInfo agentInfo = classUnderTest.nextAgentInfo();

    assertAll("check new model",
        () -> assertEquals(AGENTNAME, agentInfo.getAgentName()),
        () -> assertEquals(TimeAgentStatus.READY, agentInfo.getStatus()),
        () -> assertEquals(TRIGGER, agentInfo.getCrontrigger()),
        () -> assertEquals(timestamp, agentInfo.getCreatedAt()),
        () -> assertEquals(1L, agentInfo.getId()),
        () -> assertEquals(1, agentInfo.getProtocol().size()));
  }

  @Test
  void testSaveTriggerToCurrentModel() {
    TimeAgentInfo agentInfo = new TimeAgentInfo(AGENTNAME, TimeAgentStatus.READY);
    TimeAgentInfo expected = new TimeAgentInfo(AGENTNAME, TimeAgentStatus.READY);

    expected.setCrontrigger(TRIGGER);

    when(agentInfoRepository.save(agentInfo)).thenReturn(expected);

    agentInfo = classUnderTest.saveTriggerToAgentInfo(TRIGGER, agentInfo);

    assertEquals(TRIGGER, agentInfo.getCrontrigger());
  }

  @Test
  void testSaveTriggerToNewDatabaseModel() {
    LocalDateTime timestamp = LocalDateTime.now();

    TimeAgentInfo agentInfo = new TimeAgentInfo(AGENTNAME, TimeAgentStatus.FINISHED);
    TimeAgentInfo expected = new TimeAgentInfo(AGENTNAME, TimeAgentStatus.NOT_SET);

    expected.setCrontrigger(TRIGGER);
    expected.setCreatedAt(timestamp);
    expected.setId(1L);
    expected.addProtocol("message");

    when(agentInfoRepository.findTop1ByAgentNameAndStatusOrderByUpdatedAtDesc(AGENTNAME, TimeAgentStatus.NOT_SET)).thenReturn(expected);
    when(agentInfoRepository.save(expected)).thenReturn(expected);

    TimeAgentInfo agentInfoReturned = classUnderTest.saveTriggerToAgentInfo(TRIGGER, agentInfo);

    assertAll("check new model",
        () -> assertEquals(TimeAgentStatus.NOT_SET, agentInfoReturned.getStatus()),
        () -> assertEquals(TRIGGER, agentInfoReturned.getCrontrigger()));
  }

  @Test
  void testSaveTriggerToNewCreatedModel() {
    LocalDateTime timestamp = LocalDateTime.now();

    TimeAgentInfo agentInfo = new TimeAgentInfo(AGENTNAME, TimeAgentStatus.FINISHED);
    TimeAgentInfo expected = new TimeAgentInfo(AGENTNAME, TimeAgentStatus.NOT_SET);

    expected.setCrontrigger(TRIGGER);
    expected.setCreatedAt(timestamp);
    expected.setId(1L);
    expected.addProtocol("message");

    when(agentInfoRepository.save(any(TimeAgentInfo.class))).thenReturn(expected);

    TimeAgentInfo agentInfoReturned = classUnderTest.saveTriggerToAgentInfo(TRIGGER, agentInfo);

    assertAll("check new model",
        () -> assertEquals(TimeAgentStatus.NOT_SET, agentInfoReturned.getStatus()),
        () -> assertEquals(TRIGGER, agentInfoReturned.getCrontrigger()));
  }

}
