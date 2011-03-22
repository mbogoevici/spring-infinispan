/**
 * Copyright (C) 2010.
 * Olaf Bergner.
 * Hamburg, Germany. olaf.bergner@gmx.de
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.infinispan.spring.spi;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * <p>
 * Test {@link SpringEmbeddedCacheManagerFactoryBean} deployed in a Spring application context.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
@ContextConfiguration("classpath:/org/infinispan/spring/spi/SpringEmbeddedCacheManagerFactoryBeanContextTest.xml")
public class SpringEmbeddedCacheManagerFactoryBeanContextTest extends AbstractJUnit4SpringContextTests {

	private static final String SPRING_EMBEDDED_CACHE_MANAGER_WITH_DEFAULT_CONFIGURATION_BEAN_NAME = "springEmbeddedCacheManagerWithDefaultConfiguration";

	private static final String SPRING_EMBEDDED_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_FILE_BEAN_NAME = "springEmbeddedCacheManagerConfiguredFromConfigurationFile";

	private static final String SPRING_EMBEDDED_CACHE_MANAGER_CONFIGURED_USING_SETTERS_BEAN_NAME = "springEmbeddedCacheManagerConfiguredUsingSetters";

	@Test
	public final void shouldCreateAnEmbeddedCacheManagerWithDefaultSettingsIfNoFurtherConfigurationGiven() {
		final SpringEmbeddedCacheManager springEmbeddedCacheManagerWithDefaultConfiguration = this.applicationContext
				.getBean(SPRING_EMBEDDED_CACHE_MANAGER_WITH_DEFAULT_CONFIGURATION_BEAN_NAME,
						SpringEmbeddedCacheManager.class);

		assertNotNull(
				"Spring application context should contain a SpringEmbeddedCacheManager with default settings having bean name = \""
						+ SPRING_EMBEDDED_CACHE_MANAGER_WITH_DEFAULT_CONFIGURATION_BEAN_NAME
						+ "\". However, it doesn't.", springEmbeddedCacheManagerWithDefaultConfiguration);
		springEmbeddedCacheManagerWithDefaultConfiguration.stop();
	}

	@Test
	public final void shouldCreateAnEmbeddedCacheManagerConfiguredFromConfigurationFileIfConfigurationFileLocationGiven() {
		final SpringEmbeddedCacheManager springEmbeddedCacheManagerConfiguredFromConfigurationFile = this.applicationContext
				.getBean(SPRING_EMBEDDED_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_FILE_BEAN_NAME,
						SpringEmbeddedCacheManager.class);

		assertNotNull(
				"Spring application context should contain a SpringEmbeddedCacheManager configured from configuration file having bean name = \""
						+ SPRING_EMBEDDED_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_FILE_BEAN_NAME
						+ "\". However, it doesn't.", springEmbeddedCacheManagerConfiguredFromConfigurationFile);
		springEmbeddedCacheManagerConfiguredFromConfigurationFile.stop();
	}

	@Test
	public final void shouldCreateAnEmbeddedCacheManagerConfiguredUsingPropertiesIfPropertiesAreDefined() {
		final SpringEmbeddedCacheManager springEmbeddedCacheManagerConfiguredUsingProperties = this.applicationContext
				.getBean(SPRING_EMBEDDED_CACHE_MANAGER_CONFIGURED_USING_SETTERS_BEAN_NAME,
						SpringEmbeddedCacheManager.class);

		assertNotNull(
				"Spring application context should contain a SpringEmbeddedCacheManager configured using properties having bean name = \""
						+ SPRING_EMBEDDED_CACHE_MANAGER_CONFIGURED_USING_SETTERS_BEAN_NAME + "\". However, it doesn't.",
				springEmbeddedCacheManagerConfiguredUsingProperties);
		springEmbeddedCacheManagerConfiguredUsingProperties.stop();
	}
}
