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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
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
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ContextConfiguration("classpath:/org/infinispan/spring/spi/SpringRemoteCacheManagerFactoryBeanContextTest.xml")
public class SpringRemoteCacheManagerFactoryBeanContextTest extends AbstractJUnit4SpringContextTests {

	private static final String SPRING_REMOTE_CACHE_MANAGER_WITH_DEFAULT_CONFIGURATION_BEAN_NAME = "springRemoteCacheManagerWithDefaultConfiguration";

	private static final String SPRING_REMOTE_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_PROPERTIES_FILE_BEAN_NAME = "springRemoteCacheManagerConfiguredFromConfigurationPropertiesFile";

	private static final String SPRING_REMOTE_CACHE_MANAGER_CONFIGURED_USING_CONFIGURATION_PROPERTIES_BEAN_NAME = "springRemoteCacheManagerConfiguredUsingConfigurationProperties";

	private static final String SPRING_REMOTE_CACHE_MANAGER_CONFIGURED_USING_SETTERS_BEAN_NAME = "springRemoteCacheManagerConfiguredUsingSetters";

	@Test
	public final void shouldCreateARemoteCacheManagerWithDefaultSettingsIfNoFurtherConfigurationGiven() {
		final SpringRemoteCacheManager springRemoteCacheManagerWithDefaultConfiguration = this.applicationContext
				.getBean(SPRING_REMOTE_CACHE_MANAGER_WITH_DEFAULT_CONFIGURATION_BEAN_NAME,
						SpringRemoteCacheManager.class);

		assertNotNull(
				"Spring application context should contain a SpringRemoteCacheManager with default settings having bean name = \""
						+ SPRING_REMOTE_CACHE_MANAGER_WITH_DEFAULT_CONFIGURATION_BEAN_NAME + "\". However, it doesn't.",
				springRemoteCacheManagerWithDefaultConfiguration);
	}

	@Test
	public final void shouldCreateARemoteCacheManagerConfiguredFromConfigurationFileIfConfigurationFileLocationGiven() {
		final SpringRemoteCacheManager springRemoteCacheManagerConfiguredFromConfigurationFile = this.applicationContext
				.getBean(SPRING_REMOTE_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_PROPERTIES_FILE_BEAN_NAME,
						SpringRemoteCacheManager.class);

		assertNotNull(
				"Spring application context should contain a SpringRemoteCacheManager configured from configuration file having bean name = \""
						+ SPRING_REMOTE_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_PROPERTIES_FILE_BEAN_NAME
						+ "\". However, it doesn't.", springRemoteCacheManagerConfiguredFromConfigurationFile);
	}

	@Test
	public final void shouldCreateARemoteCacheManagerConfiguredUsingConfigurationPropertiesSetInApplicationContext() {
		final SpringRemoteCacheManager springRemoteCacheManagerConfiguredUsingConfigurationProperties = this.applicationContext
				.getBean(SPRING_REMOTE_CACHE_MANAGER_CONFIGURED_USING_CONFIGURATION_PROPERTIES_BEAN_NAME,
						SpringRemoteCacheManager.class);

		assertNotNull(
				"Spring application context should contain a SpringRemoteCacheManager configured using configuration properties set in application context having bean name = \""
						+ SPRING_REMOTE_CACHE_MANAGER_CONFIGURED_USING_CONFIGURATION_PROPERTIES_BEAN_NAME
						+ "\". However, it doesn't.", springRemoteCacheManagerConfiguredUsingConfigurationProperties);
	}

	@Test
	public final void shouldCreateARemoteCacheManagerConfiguredUsingSettersIfPropertiesAreDefined() {
		final SpringRemoteCacheManager springRemoteCacheManagerConfiguredUsingSetters = this.applicationContext
				.getBean(SPRING_REMOTE_CACHE_MANAGER_CONFIGURED_USING_SETTERS_BEAN_NAME, SpringRemoteCacheManager.class);

		assertNotNull(
				"Spring application context should contain a SpringRemoteCacheManager configured using properties having bean name = \""
						+ SPRING_REMOTE_CACHE_MANAGER_CONFIGURED_USING_SETTERS_BEAN_NAME + "\". However, it doesn't.",
				springRemoteCacheManagerConfiguredUsingSetters);
	}
}
