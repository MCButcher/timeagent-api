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

import java.util.List;

import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentInfoSearch;
import org.smithx.timeagent.api.services.TimeAgentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;

/**
 * controller for the info endpoints of an agent.
 *
 * @author norman schmidt {smithx}
 * @since 07.05.2020
 * 
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
public class TimeAgentInfoController {
  private TimeAgentService service;

  @GetMapping
  @ApiOperation(value = "getting the current status and information of the agent")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "ok"),
      @ApiResponse(code = 500, message = "internal error")
  })
  public TimeAgentInfo getAgentInfo() {
    return service.getInfo();
  }

  @GetMapping(path = "/search", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "searching for a past information of the agent")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "search successful"),
      @ApiResponse(code = 500, message = "internal error")
  })
  public List<TimeAgentInfo> findAgentInfo(@RequestBody TimeAgentInfoSearch searchModel) {
    return service.searchInfo(searchModel);
  }

}
