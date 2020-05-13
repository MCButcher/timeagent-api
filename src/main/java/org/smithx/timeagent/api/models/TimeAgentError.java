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

import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.springframework.http.HttpStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

/**
 * model for an api error.
 *
 * @author norman schmidt {smithx}
 * @since 12.05.2020
 * 
 */
@Getter
@ApiModel(description = "model for the error response of the agent")
public class TimeAgentError {
  @ApiModelProperty(value = "time when the error occured", example = "2020-01-01T12:00:00.000000")
  private LocalDateTime timestamp;

  @ApiModelProperty(value = "name of the http status", example = "INTERNAL_SERVER_ERROR")
  private HttpStatus status;

  @ApiModelProperty(value = "error code of the http status", example = "500")
  private int code;

  @ApiModelProperty(value = "cause of the exception", example = "INVALID_TRIGGER")
  private String error;

  @ApiModelProperty(value = "error message of the exception", example = "invalid trigger")
  private String message;

  public TimeAgentError(HttpStatus status, TimeAgentRuntimeException exception) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.code = status.value();
    this.error = exception.getErrorCause().name();
    this.message = exception.getErrorMessage();
  }
}
