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

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * class to search for infos of an agent in the database.
 *
 * @author norman schmidt {smithx}
 * @since 08.05.2020
 * 
 */
@Data
@ApiModel(description = "search model for infos of the agent from the past")
public class TimeAgentInfoSearch {
  @ApiModelProperty(value = "limit for the search result (max. limit is set by 'timeagent.values.max-limit-search')", example = "20", position = 1)
  private int limit;

  @ApiModelProperty(value = "user, who executed the agent", example = "x123456", position = 3)
  private String executor;

  @ApiModelProperty(value = "from start time of the agent", example = "2020-01-01T12:00:00.000000", position = 4)
  private LocalDateTime fromStartTimeExecution;

  @ApiModelProperty(value = "to start time of the agent", example = "2020-01-01T12:00:00.000000", position = 5)
  private LocalDateTime toStartTimeExecution;

  @ApiModelProperty(value = "status of the agent", example = "FINISHED", position = 2)
  private TimeAgentStatus status;
}
