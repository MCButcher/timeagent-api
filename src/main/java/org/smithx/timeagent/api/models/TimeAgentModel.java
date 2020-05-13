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

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * abstract model class for an agent entity.
 *
 * @author norman schmidt {smithx}
 * @since 07.05.2020
 * 
 */
@Data
@MappedSuperclass
public abstract class TimeAgentModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(value = "the generated id of the model", example = "1")
  private Long id;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  @ApiModelProperty(value = "the time of the model saved to the database", example = "2020-01-01T12:00:00.000000")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @ApiModelProperty(value = "the time of the model saved to the database", example = "2020-01-01T12:00:00.000000")
  private LocalDateTime updatedAt;

  public void init() {
    id = null;
    createdAt = null;
    updatedAt = null;
  }
}
