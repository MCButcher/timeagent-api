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
package org.smithx.timeagent.api.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.smithx.timeagent.api.models.TimeAgentArgument;
import org.smithx.timeagent.api.services.TimeAgentService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

/**
 * testing the TimeAgentAdminController.
 *
 * @author norman schmidt {smithx}
 * @since 12.05.2020
 * 
 */
@WebMvcTest(TimeAgentAdminController.class)
public class TimeAgentAdminControllerTest extends TimeAgentControllerTest {
  @MockBean
  TimeAgentService service;

  @Test
  void testRunWithArguments() throws Exception {
    TimeAgentArgument[] arguments = { new TimeAgentArgument("test", "123") };

    mvc.perform(post("/timeagent/admin/run").contentType(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsString(arguments)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testRunWithoutArguments() throws Exception {
    mvc.perform(post("/timeagent/admin/run")).andExpect(status().is2xxSuccessful());
  }

  @Test
  void testRunThrowsException() throws Exception {
    TimeAgentArgument[] arguments = { new TimeAgentArgument("test", "123") };

    doThrow(new TimeAgentRuntimeException(TimeAgentExceptionCause.ALREADY_RUNNING, "running")).when(service).run(arguments);
    mvc.perform(post("/timeagent/admin/run").contentType(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsString(arguments)))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void testSetTrigger() throws Exception {
    mvc.perform(post("/timeagent/admin/trigger").contentType(MediaType.TEXT_PLAIN).content("trigger"))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testSetInvalidTrigger() throws Exception {
    when(service.setTrigger(anyString()))
        .thenThrow(new TimeAgentRuntimeException(TimeAgentExceptionCause.INVALID_TRIGGER, "invalid trigger"));
    mvc.perform(post("/timeagent/admin/trigger").contentType(MediaType.TEXT_PLAIN).content("trigger"))
        .andExpect(status().isNotAcceptable());
  }

  @Test
  void testDeleteTrigger() throws Exception {
    mvc.perform(delete("/timeagent/admin/trigger")).andExpect(status().is2xxSuccessful());
  }

}
