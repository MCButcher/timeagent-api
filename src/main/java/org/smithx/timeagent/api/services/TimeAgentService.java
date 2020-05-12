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
package org.smithx.timeagent.api.services;

import org.smithx.timeagent.api.configuration.TimeAgentValues;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.repositories.TimeAgentInfoRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * abstract service for the all implementation of services
 *
 * @author norman schmidt {smithx}
 * @since 12.05.2020
 * 
 */
@Data
@AllArgsConstructor
public abstract class TimeAgentService {
  private TimeAgentValues agentValues;
  private TimeAgentInfoRepository agentInfoRepository;
  private TimeAgentInfo agentInfo;

  public TimeAgentInfo updateInfo() {
    agentInfo = agentInfoRepository.save(agentInfo);
    return agentInfo;
  }

  public TimeAgentInfo insertInfo() {
    agentInfo.setId(null);
    return updateInfo();
  }
}