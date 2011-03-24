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

package org.infinispan.spring.support.embedded;

import static org.junit.Assert.assertNotNull;

import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * <p>
 * Test {@link InfinispanEmbeddedCacheManagerFactoryBean} deployed in a Spring application context.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ContextConfiguration("classpath:/org/infinispan/spring/support/embedded/InfinispanEmbeddedCacheManagerFactoryBeanContextTest.xml")
public class InfinispanEmbeddedCacheManagerFactoryBeanContextTest extends AbstractJUnit4SpringContextTests {

	private static final String INFINISPAN_EMBEDDED_CACHE_MANAGER_WITH_DEFAULT_CONFIGURATION_BEAN_NAME = "infinispanEmbeddedCacheManagerWithDefaultConfiguration";

	private static final String INFINISPAN_EMBEDDED_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_FILE_BEAN_NAME = "infinispanEmbeddedCacheManagerConfiguredFromConfigurationFile";

	private static final String INFINISPAN_EMBEDDED_CACHE_MANAGER_CONFIGURED_USING_SETTERS_BEAN_NAME = "infinispanEmbeddedCacheManagerConfiguredUsingSetters";

	@Test
	public final void shouldCreateAnEmbeddedCacheManagerWithDefaultSettingsIfNoFurtherConfigurationGiven() {
		final EmbeddedCacheManager infinispanEmbeddedCacheManagerWithDefaultConfiguration = this.applicationContext
				.getBean(INFINISPAN_EMBEDDED_CACHE_MANAGER_WITH_DEFAULT_CONFIGURATION_BEAN_NAME,
						EmbeddedCacheManager.class);

		assertNotNull(
				"Spring application context should contain a EmbeddedCacheManager with default settings having bean name = \""
						+ INFINISPAN_EMBEDDED_CACHE_MANAGER_WITH_DEFAULT_CONFIGURATION_BEAN_NAME
						+ "\". However, it doesn't.", infinispanEmbeddedCacheManagerWithDefaultConfiguration);
	}

	@Test
	public final void shouldCreateAnEmbeddedCacheManagerConfiguredFromConfigurationFileIfConfigurationFileLocationGiven() {
		final EmbeddedCacheManager infinispanEmbeddedCacheManagerConfiguredFromConfigurationFile = this.applicationContext
				.getBean(INFINISPAN_EMBEDDED_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_FILE_BEAN_NAME,
						EmbeddedCacheManager.class);

		assertNotNull(
				"Spring application context should contain a EmbeddedCacheManager configured from configuration file having bean name = \""
						+ INFINISPAN_EMBEDDED_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_FILE_BEAN_NAME
						+ "\". However, it doesn't.", infinispanEmbeddedCacheManagerConfiguredFromConfigurationFile);
	}

	@Test
	public final void shouldCreateAnEmbeddedCacheManagerConfiguredUsingSettersIfPropertiesAreDefined() {
		final EmbeddedCacheManager infinispanEmbeddedCacheManagerConfiguredUsingSetters = this.applicationContext
				.getBean(INFINISPAN_EMBEDDED_CACHE_MANAGER_CONFIGURED_USING_SETTERS_BEAN_NAME,
						EmbeddedCacheManager.class);

		assertNotNull(
				"Spring application context should contain a EmbeddedCacheManager configured using properties having bean name = \""
						+ INFINISPAN_EMBEDDED_CACHE_MANAGER_CONFIGURED_USING_SETTERS_BEAN_NAME
						+ "\". However, it doesn't.", infinispanEmbeddedCacheManagerConfiguredUsingSetters);
	}
}
