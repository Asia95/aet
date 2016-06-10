/*
 * Cognifide AET :: Maven Plugin
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
package com.cognifide.aet.xmlparser.xml;

import java.io.File;
import java.io.IOException;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;

import com.cognifide.aet.communication.api.config.TestSuiteRun;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.xmlparser.api.ParseException;
import com.cognifide.aet.xmlparser.api.TestSuiteParser;
import com.cognifide.aet.xmlparser.xml.models.Collect;
import com.cognifide.aet.xmlparser.xml.models.Compare;
import com.cognifide.aet.xmlparser.xml.models.Reports;
import com.cognifide.aet.xmlparser.xml.models.TestSuite;
import com.cognifide.aet.xmlparser.xml.utils.EscapeUtils;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * @author lukasz.wieczorek
 */
public class XmlTestSuiteParser implements TestSuiteParser {

	@Override
	public TestSuiteRun parse(File testSuiteFile) throws ParseException {
		try {
			String testSuiteString = Files.toString(testSuiteFile, Charsets.UTF_8);
			return parse(testSuiteString);
		} catch (IOException e) {
			throw new ParseException(e.getMessage(), e);
		}
	}

	@Override
	public TestSuiteRun parse(String testSuiteString) throws ParseException {
		try {
			Serializer serializer = getSerializer();
			TestSuite testSuite = serializer
					.read(TestSuite.class, EscapeUtils.escapeUrls(testSuiteString));
			return testSuite.adaptToTestSuiteRun();
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), e);
		}
	}

	private Serializer getSerializer() throws AETException {
		Registry registry = new Registry();
		Serializer serializer = new Persister(new RegistryStrategy(registry));
		try {
			registry.bind(Collect.class, new CollectConverter());
			registry.bind(Compare.class, new CompareConverter());
			registry.bind(Reports.class, new ReportsConverter());
		} catch (Exception e) {
			throw new AETException("Error while serializing test suite.", e);
		}
		return serializer;
	}
}