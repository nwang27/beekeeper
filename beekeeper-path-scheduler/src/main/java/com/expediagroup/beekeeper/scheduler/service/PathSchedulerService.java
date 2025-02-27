/**
 * Copyright (C) 2019 Expedia, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expediagroup.beekeeper.scheduler.service;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.micrometer.core.annotation.Timed;

import com.expediagroup.beekeeper.core.error.BeekeeperException;
import com.expediagroup.beekeeper.core.model.EntityHousekeepingPath;
import com.expediagroup.beekeeper.core.model.HousekeepingPath;
import com.expediagroup.beekeeper.core.repository.HousekeepingPathRepository;

@Service
public class PathSchedulerService implements SchedulerService {

  private HousekeepingPathRepository housekeepingPathRepository;

  @Autowired
  public PathSchedulerService(HousekeepingPathRepository housekeepingPathRepository) {
    this.housekeepingPathRepository = housekeepingPathRepository;
  }

  @Override
  @Timed("scheduled-paths")
  public void scheduleForHousekeeping(HousekeepingPath cleanUpPath) {
    try {
      housekeepingPathRepository.save((EntityHousekeepingPath) cleanUpPath);
    } catch (Exception e) {
      throw new BeekeeperException(format("Unable to schedule path '%s' for deletion", cleanUpPath.getPath()), e);
    }
  }
}
