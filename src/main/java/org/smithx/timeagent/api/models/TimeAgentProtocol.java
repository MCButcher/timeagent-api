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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * class for writing entries to a protocol, which is added to the status
 * information of an agent.
 *
 * @author norman schmidt {smithx}
 * @since 07.05.2020
 * 
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "protocol")
@ApiModel(description = "protocol for the agent info")
public class TimeAgentProtocol extends TimeAgentModel {
  @ManyToOne(fetch = FetchType.LAZY)
  @JsonBackReference
  @ApiModelProperty(value = "the corresponding info of the agent", position = 4)
  private TimeAgentInfo info;

  @Getter
  @ApiModelProperty(value = "a protocol message", example = "found 100 files", position = 5)
  private String message;
}
