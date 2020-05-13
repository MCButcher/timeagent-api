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

import org.smithx.timeagent.api.models.TimeAgentArgument;
import org.smithx.timeagent.api.services.TimeAgentAdminService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;

/**
 * controller for the administration endpoints of an agent.
 *
 * @author norman schmidt {smithx}
 * @since 12.05.2020
 * 
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class TimeAgentAdminController {
  private TimeAgentAdminService service;

  @PostMapping(path = "/run", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "start the run of the agent at once")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "restart initiated successfully"),
      @ApiResponse(code = 422, message = "agent already running"),
      @ApiResponse(code = 500, message = "internal error")
  })
  public void run(@RequestBody(required = false) TimeAgentArgument... arguments) {
    service.run(arguments);
  }
}
