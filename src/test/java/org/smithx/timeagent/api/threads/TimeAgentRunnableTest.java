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
package org.smithx.timeagent.api.threads;

import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.smithx.timeagent.api.agent.TimeAgentRuntime;
import org.smithx.timeagent.api.models.TimeAgentArgument;

/**
 * testing the TimeAgentRunnable.
 *
 * @author norman schmidt {smithx}
 * @since 14.05.2020
 * 
 */
@MockitoSettings
public class TimeAgentRunnableTest {
  TimeAgentRunnable classUnderTest;

  @Mock
  TimeAgentRuntime workflow;

  @BeforeEach
  void beforeEach() {
    classUnderTest = new TimeAgentRunnable(workflow);
  }

  @Test
  void testRunWithArguments() {
    TimeAgentArgument arguments[] = { new TimeAgentArgument("key", "value") };
    classUnderTest.setArguments(arguments);
    classUnderTest.run();
    assertNull(classUnderTest.getArguments());
  }

  @Test
  void testRunWithoutArguments() {
    classUnderTest.run();
    assertNull(classUnderTest.getArguments());
  }

}
