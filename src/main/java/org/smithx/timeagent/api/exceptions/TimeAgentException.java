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
package org.smithx.timeagent.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * exception throwing, when an error in the implementation occurs.
 *
 * @author norman schmidt {smithx}
 * @since 12.05.2020
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class TimeAgentException extends Exception {
  private static final long serialVersionUID = 1L;

  private TimeAgentExceptionCause errorCause;
  private String errorMessage;

  public String getFullErrorMessage() {
    return String.format("%s: %s", errorCause.name(), errorMessage);
  }

}