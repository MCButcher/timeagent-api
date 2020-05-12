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
package org.smithx.timeagent.api.models;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.springframework.http.HttpStatus;

/**
 * testing the TimeAgentError.
 *
 * @author norman schmidt {smithx}
 * @since 12.05.2020
 * 
 */
public class TimeAgentErrorTest {
  TimeAgentError classUnderTest;

  @Test
  void testDefaultError() {
    classUnderTest = new TimeAgentError(HttpStatus.INTERNAL_SERVER_ERROR,
        new TimeAgentRuntimeException(TimeAgentExceptionCause.INVALID_SEARCH_MODEL, "default error"));

    assertAll("check the values of the error model",
        () -> assertNotNull(classUnderTest.getTimestamp()),
        () -> assertEquals(500, classUnderTest.getCode()),
        () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, classUnderTest.getStatus()),
        () -> assertEquals("default error", classUnderTest.getMessage()),
        () -> assertEquals("INVALID_SEARCH_MODEL", classUnderTest.getError()));
  }

  @Test
  void testAlreadyRunningError() {
    classUnderTest = new TimeAgentError(HttpStatus.UNPROCESSABLE_ENTITY,
        new TimeAgentRuntimeException(TimeAgentExceptionCause.ALREADY_RUNNING, "running"));

    assertAll("check the values of the error model",
        () -> assertNotNull(classUnderTest.getTimestamp()),
        () -> assertEquals(422, classUnderTest.getCode()),
        () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, classUnderTest.getStatus()),
        () -> assertEquals("running", classUnderTest.getMessage()),
        () -> assertEquals("ALREADY_RUNNING", classUnderTest.getError()));
  }

}
