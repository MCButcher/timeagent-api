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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentInfoSearch;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.smithx.timeagent.api.services.TimeAgentService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * testing the TimeAgentInfoController.
 *
 * @author norman schmidt {smithx}
 * @since 11.05.2020
 * 
 */
@WebMvcTest(TimeAgentInfoController.class)
public class TimeAgentInfoControllerTest extends TimeAgentControllerTest {
  @MockBean
  TimeAgentService service;

  TimeAgentInfo info;

  @BeforeEach
  void beforeEach() {
    info = new TimeAgentInfo();
    info.setAgentName("agent");
    info.setExecutor("user");
    info.setCrontrigger("0 0 0 1/1 * ?");
    info.setStatus(TimeAgentStatus.RUNNING);
    info.setStartTimeExecution(LocalDateTime.of(2020, 1, 1, 12, 00));
    info.setFinishTimeExecution(LocalDateTime.of(2020, 1, 2, 8, 30));
  }

  @Test
  void testGetInfo() throws Exception {
    when(service.getAgentInfo()).thenReturn(info);

    MockHttpServletResponse response = mvc.perform(get("/timeagent/info")).andExpect(status().is2xxSuccessful()).andReturn().getResponse();
    TimeAgentInfo mappedResponse = mapper.reader().forType(TimeAgentInfo.class).readValue(response.getContentAsString());
    assertAll("check timeagent info",
        () -> assertEquals(info.getAgentName(), mappedResponse.getAgentName()),
        () -> assertEquals(info.getCrontrigger(), mappedResponse.getCrontrigger()),
        () -> assertEquals(info.getStatus(), mappedResponse.getStatus()),
        () -> assertEquals(info.getExecutor(), mappedResponse.getExecutor()),
        () -> assertEquals(info.getStartTimeExecution(), mappedResponse.getStartTimeExecution()),
        () -> assertEquals(info.getFinishTimeExecution(), mappedResponse.getFinishTimeExecution()),
        () -> assertEquals(info.getNextExecution(), mappedResponse.getNextExecution()));
  }

  @Test
  void testSearch() throws Exception {
    TimeAgentInfoSearch searchModel = new TimeAgentInfoSearch();
    when(service.searchInfo(searchModel)).thenReturn(Arrays.asList(new TimeAgentInfo()));

    MockHttpServletResponse response = mvc
        .perform(
            post("/timeagent/info/search").contentType(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsString(searchModel)))
        .andExpect(status().is2xxSuccessful()).andReturn().getResponse();
    List<TimeAgentInfo> mappedResponse = mapper.readValue(response.getContentAsString(), new TypeReference<List<TimeAgentInfo>>() {});
    assertEquals(1, mappedResponse.size());
  }

}
