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
package org.smithx.timeagent.api.agent;

import org.smithx.timeagent.api.exceptions.TimeAgentException;
import org.smithx.timeagent.api.models.TimeAgentArgument;
import org.smithx.timeagent.api.services.TimeAgentService;

/**
 * agent to be implemented for the execution.
 *
 * @author norman schmidt {smithx}
 * @since 14.05.2020
 * 
 */
public abstract class TimeAgent {

  public abstract void execute(TimeAgentService service, TimeAgentArgument... arguments) throws TimeAgentException;
}
