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
package org.smithx.timeagent.api.exceptions;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * testing the TimeAgentRuntimeException.
 *
 * @author norman schmidt {smithx}
 * @since 12.05.2020
 * 
 */
public class TimeAgentRuntimeExceptionTest {
  TimeAgentRuntimeException classUnderTest;

  @Test
  void testMessageAndCause() {
    classUnderTest = new TimeAgentRuntimeException(TimeAgentExceptionCause.ALREADY_RUNNING, "running");
    assertAll("check error message",
        () -> assertEquals(TimeAgentExceptionCause.ALREADY_RUNNING, classUnderTest.getErrorCause()),
        () -> assertEquals("running", classUnderTest.getErrorMessage()),
        () -> assertEquals(String.format("%s: %s", TimeAgentExceptionCause.ALREADY_RUNNING.name(), "running"),
            classUnderTest.getFullErrorMessage()));
  }

}
