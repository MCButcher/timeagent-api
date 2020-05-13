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
package org.smithx.timeagent.api.handler;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.smithx.timeagent.api.models.TimeAgentError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * testing the TimeAgentExceptionHandler.
 *
 * @author norman schmidt {smithx}
 * @since 12.05.2020
 * 
 */
public class TimeAgentExceptionHandlerTest {
  TimeAgentExceptionHandler classUnderTest;

  @BeforeEach
  void beforeEach() {
    classUnderTest = new TimeAgentExceptionHandler();
  }

  @Test
  void testDefaultHandler() {
    ResponseEntity<TimeAgentError> response = classUnderTest
        .handleException(new TimeAgentRuntimeException(TimeAgentExceptionCause.INVALID_SEARCH_MODEL, "default error"));

    assertAll("check the error response",
        () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
        () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody().getTimestamp()),
        () -> assertEquals(500, response.getBody().getCode()),
        () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getBody().getStatus()),
        () -> assertEquals("default error", response.getBody().getMessage()),
        () -> assertEquals("INVALID_SEARCH_MODEL", response.getBody().getError()));
  }

  @Test
  void testAlreadyRunningHandler() {
    ResponseEntity<TimeAgentError> response = classUnderTest
        .handleException(new TimeAgentRuntimeException(TimeAgentExceptionCause.ALREADY_RUNNING, "running"));

    assertAll("check the error response",
        () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getBody().getStatus()),
        () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode()),
        () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody().getTimestamp()),
        () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getBody().getCode()),
        () -> assertEquals("running", response.getBody().getMessage()),
        () -> assertEquals(TimeAgentExceptionCause.ALREADY_RUNNING.name(), response.getBody().getError()));
  }

  @Test
  void testInvalidTriggerHandler() {
    ResponseEntity<TimeAgentError> response = classUnderTest
        .handleException(new TimeAgentRuntimeException(TimeAgentExceptionCause.INVALID_TRIGGER, "invalid trigger"));

    assertAll("check the error response",
        () -> assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getBody().getStatus()),
        () -> assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode()),
        () -> assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody().getTimestamp()),
        () -> assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), response.getBody().getCode()),
        () -> assertEquals("invalid trigger", response.getBody().getMessage()),
        () -> assertEquals(TimeAgentExceptionCause.INVALID_TRIGGER.name(), response.getBody().getError()));
  }

}
