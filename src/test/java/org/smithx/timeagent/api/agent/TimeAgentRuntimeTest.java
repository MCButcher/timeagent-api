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
package org.smithx.timeagent.api.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.smithx.timeagent.api.exceptions.TimeAgentException;
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.smithx.timeagent.api.models.TimeAgentArgument;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.smithx.timeagent.api.services.TimeAgentService;

/**
 * testing the TimeAgentRuntime.
 *
 * @author norman schmidt {smithx}
 * @since 18.05.2020
 * 
 */
@MockitoSettings
public class TimeAgentRuntimeTest {
  TimeAgentRuntime classUnderTest;

  @Mock
  TimeAgentService service;

  @Mock
  TimeAgent agent;

  TimeAgentInfo agentInfo;

  @BeforeEach
  void beforeEach() {
    classUnderTest = new TimeAgentRuntime(service, agent);
    agentInfo = new TimeAgentInfo();
    when(service.getAgentInfo()).thenReturn(agentInfo);
  }

  @Test
  void testRunWithArguments() {
    classUnderTest.run(new TimeAgentArgument("key", "value"));
  }

  @Test
  void testAlreadyRunning() {
    agentInfo.setStatus(TimeAgentStatus.RUNNING);
    TimeAgentRuntimeException exception = assertThrows(TimeAgentRuntimeException.class, () -> classUnderTest.run());
    assertEquals(TimeAgentExceptionCause.ALREADY_RUNNING, exception.getErrorCause());
  }

  @Test
  void testExceptionOnExecution() throws TimeAgentException {
    doThrow(new TimeAgentException(TimeAgentExceptionCause.ALREADY_RUNNING, "already running")).when(agent).execute(service);
    classUnderTest.run();
    assertEquals(TimeAgentStatus.ABORTED, agentInfo.getStatus());
  }
}
