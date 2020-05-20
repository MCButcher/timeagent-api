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
package org.smithx.timeagent.api.engines;

import org.smithx.timeagent.api.configuration.TimeAgentMessages;
import org.smithx.timeagent.api.configuration.TimeAgentValues;
import org.smithx.timeagent.api.models.TimeAgentInfo;
import org.smithx.timeagent.api.models.TimeAgentStatus;
import org.smithx.timeagent.api.repositories.TimeAgentInfoRepository;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * engine to create, get or update the next or current agent info model.
 *
 * @author norman schmidt {smithx}
 * @since 15.05.2020
 * 
 */
@Data
@AllArgsConstructor
@Component
@Slf4j
public class TimeAgentModelEngine {
  private TimeAgentValues agentValues;
  private TimeAgentInfoRepository agentInfoRepository;
  private TimeAgentMessages messages;

  public TimeAgentInfo nextAgentInfo() {
    // find info with status NOT_SET
    TimeAgentInfo agentInfo = agentInfoRepository.findTop1ByAgentNameAndStatusOrderByUpdatedAtDesc(agentValues.getAgentName(),
        TimeAgentStatus.NOT_SET);

    if (log.isDebugEnabled()) {
      log.debug(messages.getMessage("log.next.agent.notSet", agentInfo));
    }
    if (agentInfo == null) {
      // find last info
      agentInfo = agentInfoRepository.findTop1ByAgentNameOrderByUpdatedAtDesc(agentValues.getAgentName());
      if (log.isDebugEnabled()) {
        log.debug(messages.getMessage("log.next.agent.lastEntry", agentInfo));
      }
    }

    if (agentInfo == null) {
      agentInfo = createAgentInfo();
      if (log.isDebugEnabled()) {
        log.debug(messages.getMessage("log.next.agent.created", agentInfo));
      }
    } else if (TimeAgentStatus.NOT_SET.equals(agentInfo.getStatus())) {
      agentInfo.setStatus(TimeAgentStatus.READY);
      if (log.isDebugEnabled()) {
        log.debug(messages.getMessage("log.next.agent.notSet.init", agentInfo.getStatus()));
      }
    } else {
      agentInfo.init();
      if (log.isDebugEnabled()) {
        log.debug(messages.getMessage("log.next.agent.lastEntry.init", agentInfo));
      }
    }

    updateAgentInfo(agentInfo);

    return agentInfo;
  }

  public TimeAgentInfo saveTriggerToAgentInfo(String trigger, TimeAgentInfo currentAgentInfo) {
    if (TimeAgentStatus.READY.equals(currentAgentInfo.getStatus())) {
      currentAgentInfo.setCrontrigger(trigger);
      if (log.isDebugEnabled()) {
        log.debug(messages.getMessage("log.next.agent.trigger.current", currentAgentInfo.getCrontrigger()));
      }
      return updateAgentInfo(currentAgentInfo);

    } else {
      TimeAgentInfo agentInfo = agentInfoRepository.findTop1ByAgentNameAndStatusOrderByUpdatedAtDesc(agentValues.getAgentName(),
          TimeAgentStatus.NOT_SET);

      if (agentInfo == null) {
        agentInfo = new TimeAgentInfo(agentValues.getAgentName(), TimeAgentStatus.NOT_SET);
      }

      agentInfo.setCrontrigger(trigger);
      if (log.isDebugEnabled()) {
        log.debug(messages.getMessage("log.next.agent.trigger.new", agentInfo.getCrontrigger()));
      }
      return updateAgentInfo(agentInfo);
    }
  }

  public TimeAgentInfo updateAgentInfo(TimeAgentInfo agentInfo) {
    return agentInfoRepository.save(agentInfo);
  }

  private TimeAgentInfo createAgentInfo() {
    return new TimeAgentInfo(agentValues.getAgentName(), TimeAgentStatus.READY);
  }
}
