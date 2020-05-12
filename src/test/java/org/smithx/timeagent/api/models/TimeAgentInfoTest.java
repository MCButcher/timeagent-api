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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * testing the TimeAgentInfo.
 *
 * @author norman schmidt {smithx}
 * @since 11.05.2020
 * 
 */
public class TimeAgentInfoTest {
  TimeAgentInfo classUnderTest;

  @BeforeEach
  void beforeEach() {
    classUnderTest = new TimeAgentInfo();
  }

  @Test
  void testNextExecutionWithValidCrontrigger() {
    classUnderTest.setCrontrigger("0 0 0 1/1 * ?");
    assertNotNull(classUnderTest.getNextExecution());
  }

  @Test
  void testNextExecutionWithInvalidCrontrigger() {
    classUnderTest.setCrontrigger("0 0 1/1 * ?");
    assertNull(classUnderTest.getNextExecution());
  }

  @Test
  void testNextExecutionWithoutCrontrigger() {
    assertNull(classUnderTest.getNextExecution());
  }

  @Test
  void testAddMessage() {
    classUnderTest.addProtocol("first message");
    assertAll("check protocol message",
        () -> assertEquals(1, classUnderTest.getProtocol().size()),
        () -> assertEquals("first message", classUnderTest.getProtocol().get(0).getMessage()));
  }

  @Test
  void testClearProtocol() {
    classUnderTest.addProtocol("first message");
    classUnderTest.clearProtocol();
    assertTrue(classUnderTest.getProtocol().isEmpty());
  }

}
