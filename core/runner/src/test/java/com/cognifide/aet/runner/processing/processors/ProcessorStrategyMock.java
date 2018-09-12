/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.runner.processing.processors;

import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.runner.processing.data.RunIndexWrappers.SuiteRunIndexWrapper;
import com.cognifide.aet.vs.StorageException;

public class ProcessorStrategyMock extends ProcessorStrategy{

  @Override
  public Object getObjectToRun() {
    return objectToRunWrapper.getObjectToRun();
  }

  @Override
  void prepareSuiteWrapper() throws StorageException {
    runIndexWrapper = new SuiteRunIndexWrapper(objectToRunWrapper);
  }

  @Override
  void save() throws ValidatorException, StorageException { }

}