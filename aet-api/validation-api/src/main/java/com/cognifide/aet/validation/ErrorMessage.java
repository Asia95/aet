/*
 * Cognifide AET :: Validation API
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.validation;

/**
 * Represents error message in validation
 */
public interface ErrorMessage {

	/**
	 * Gets error message
	 *
	 * @return error messsage
	 */
	String getMessage();

	/**
	 * Gets Throwable related to given error
	 *
	 * @return Throwable
	 */
	Throwable getThrowable();
}