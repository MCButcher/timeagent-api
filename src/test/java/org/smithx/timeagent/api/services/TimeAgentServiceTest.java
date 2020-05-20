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
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.smithx.timeagent.api.agent.TimeAgent;
import org.smithx.timeagent.api.configuration.TimeAgentMessages;
import org.smithx.timeagent.api.engines.TimeAgentModelEngine;
import org.smithx.timeagent.api.engines.TimeAgentSearchEngine;
import org.smithx.timeagent.api.exceptions.TimeAgentException;
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentInfoSearch;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

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

  TimeAgentService serviceUnderTest;

  @Mock
  TimeAgent agent;

  @Mock
  ThreadPoolTaskScheduler scheduler;

  @Mock
  TimeAgentSearchEngine searchEngine;

  @Mock
  TimeAgentModelEngine modelEngine;

  @Mock
  TimeAgentMessages messages;

  TimeAgentInfo initAgentInfo;

  @BeforeEach
  void beforeEach() throws TimeAgentException {
    initAgentInfo = new TimeAgentInfo(AGENTNAME, TimeAgentStatus.READY);

    when(modelEngine.nextAgentInfo()).thenReturn(initAgentInfo);

    serviceUnderTest = new TimeAgentService(agent, modelEngine, searchEngine, scheduler, messages);
    serviceUnderTest.initAgentInfo();
  }

  @Test
  void testGetTimeAgentInfo() {
    assertEquals(AGENTNAME, serviceUnderTest.getAgentInfo().getAgentName());
    assertEquals(TimeAgentStatus.READY, serviceUnderTest.getAgentInfo().getStatus());
  }

  @Test
  void testSetTrigger() throws TimeAgentException {
    serviceUnderTest.initAgent();

    String trigger = "0 0 0 1/1 * ?";
    initAgentInfo.setCrontrigger(trigger);

    when(modelEngine.saveTriggerToAgentInfo(trigger, initAgentInfo)).thenReturn(initAgentInfo);

    TimeAgentInfo result = serviceUnderTest.setTrigger(trigger);
    assertEquals(trigger, result.getCrontrigger());
  }

  @Test
  void testSetTriggerThrowsExceptionInvalidTrigger() {
    String trigger = "xyz";
    TimeAgentRuntimeException exception = assertThrows(TimeAgentRuntimeException.class, () -> serviceUnderTest.setTrigger(trigger));
    assertEquals(TimeAgentExceptionCause.INVALID_TRIGGER, exception.getErrorCause());
  }

  @Test
  void testUpdateAgentInfo() {
    when(modelEngine.updateAgentInfo(initAgentInfo)).thenReturn(initAgentInfo);
    TimeAgentInfo agentInfo = serviceUnderTest.updateAgentInfo();
    assertEquals(initAgentInfo, agentInfo);

  }

  @Test
  void testSearchInfo() {
    TimeAgentInfoSearch searchModel = new TimeAgentInfoSearch();
    searchModel.setLimit(10);

    when(searchEngine.searchAgentInfo(searchModel)).thenReturn(Arrays.asList(new TimeAgentInfo(AGENTNAME, TimeAgentStatus.READY)));

    List<TimeAgentInfo> list = serviceUnderTest.searchInfo(searchModel);
    assertEquals(1, list.size());
  }

  @Test
  void testRun() {
    serviceUnderTest.initAgent();

    when(modelEngine.updateAgentInfo(any(TimeAgentInfo.class))).thenReturn(initAgentInfo);

    serviceUnderTest.run();
  }

  @Test
  void testAlreadyRunning() {
    serviceUnderTest.getAgentInfo().setStatus(TimeAgentStatus.RUNNING);
    TimeAgentRuntimeException exception = assertThrows(TimeAgentRuntimeException.class, () -> serviceUnderTest.run());
    assertEquals(TimeAgentExceptionCause.ALREADY_RUNNING, exception.getErrorCause());
  }

  @Test
  void testDeleteTrigger() throws TimeAgentException {
    when(modelEngine.saveTriggerToAgentInfo(null, initAgentInfo)).thenReturn(initAgentInfo);

    TimeAgentInfo result = serviceUnderTest.deleteTrigger();
    assertNull(result.getCrontrigger());
  }

}
