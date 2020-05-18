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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.scheduling.support.CronSequenceGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * entity of the status information of an agent.
 *
 * @author norman schmidt {smithx}
 * @since 07.05.2020
 * 
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "info")
@ApiModel(description = "model for the info model of the agent")
public class TimeAgentInfo extends TimeAgentModel {
  @Column(nullable = false, updatable = false, length = 60)
  @ApiModelProperty(value = "name of the agent set by the property 'timeagent.values.agent-name'", example = "my-first-agent", position = 4)
  private String agentName;

  @Column(length = 30)
  @ApiModelProperty(value = "crontrigger for scheduling the execution of the agent", example = "0 0/3 * 1/1 * ?", position = 6)
  private String crontrigger;

  @Column(length = 30)
  @ApiModelProperty(value = "user, who executed the agent", example = "x123456", position = 7)
  private String executor;

  @ApiModelProperty(value = "start time, when the agent was executed", example = "2020-01-01T12:00:00.000000", position = 8)
  private LocalDateTime startTimeExecution;

  @ApiModelProperty(value = "time, when the agent has finished the execution", example = "2020-01-01T12:00:00.000000", position = 9)
  private LocalDateTime finishTimeExecution;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @ApiModelProperty(value = "current status of the agent", example = "RUNNING", position = 5)
  private TimeAgentStatus status;

  @OneToMany(mappedBy = "info", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JsonManagedReference
  @ApiModelProperty(value = "protocol entries of the agent for a certain status", position = 10)
  private List<TimeAgentProtocol> protocol = new ArrayList<>();

  public TimeAgentInfo(String agentName, TimeAgentStatus status) {
    this.agentName = agentName;
    this.status = status;
  }

  @ApiModelProperty(value = "next start time of execution of the agent, when a crontrigger is set")
  public LocalDateTime getNextExecution() {
    if (CronSequenceGenerator.isValidExpression(crontrigger)) {
      Date next = new CronSequenceGenerator(crontrigger).next(Calendar.getInstance().getTime());
      return next.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    return null;
  }

  public void addProtocol(String message) {
    protocol.add(new TimeAgentProtocol(this, message));
  }

  public void clearProtocol() {
    protocol = new ArrayList<>();
  }

  public void deleteCrontrigger() {
    crontrigger = null;
  }

  public void init() {
    super.init();
    executor = null;
    status = TimeAgentStatus.READY;
    startTimeExecution = null;
    finishTimeExecution = null;
    clearProtocol();
  }
}
