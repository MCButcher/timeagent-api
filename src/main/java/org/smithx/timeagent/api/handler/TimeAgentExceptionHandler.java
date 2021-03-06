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
package org.smithx.timeagent.api.handler;

import org.smithx.timeagent.api.exceptions.TimeAgentExceptionCause;
import org.smithx.timeagent.api.exceptions.TimeAgentRuntimeException;
import org.smithx.timeagent.api.models.TimeAgentError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * exception handler for the timeagent controllers.
 *
 * @author norman schmidt {smithx}
 * @since 12.05.2020
 * 
 */
@ControllerAdvice
public class TimeAgentExceptionHandler {
  @ExceptionHandler(TimeAgentRuntimeException.class)
  public ResponseEntity<TimeAgentError> handleException(TimeAgentRuntimeException exception) {
    TimeAgentExceptionCause cause = exception.getErrorCause();

    switch (cause) {
    case ALREADY_RUNNING:
    case CANCEL_TRIGGER:
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
          .body(new TimeAgentError(HttpStatus.UNPROCESSABLE_ENTITY, exception));
    case INVALID_TRIGGER:
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
          .body(new TimeAgentError(HttpStatus.NOT_ACCEPTABLE, exception));
    default:
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new TimeAgentError(HttpStatus.INTERNAL_SERVER_ERROR, exception));
    }
  }
}
