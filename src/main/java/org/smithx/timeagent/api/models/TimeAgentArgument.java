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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * argument for a manual start of the agent.
 *
 * @author norman schmidt {smithx}
 * @since 12.05.2020
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "model for setting arguments for a manual run")
public class TimeAgentArgument {
  @ApiModelProperty(value = "key of the argument", example = "file.name")
  private String key;

  @ApiModelProperty(value = "value of the argument", example = "/path/file")
  private String value;
}
