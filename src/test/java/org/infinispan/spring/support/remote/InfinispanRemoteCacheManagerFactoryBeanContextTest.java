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

package org.infinispan.spring.support.remote;

import static org.junit.Assert.assertNotNull;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * <p>
 * Test {@link InfinispanRemoteCacheManagerFactoryBean} deployed in a Spring application context.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ContextConfiguration("classpath:/org/infinispan/spring/support/remote/InfinispanRemoteCacheManagerFactoryBeanContextTest.xml")
public class InfinispanRemoteCacheManagerFactoryBeanContextTest extends AbstractJUnit4SpringContextTests {

	private static final String INFINISPAN_REMOTE_CACHE_MANAGER_WITH_DEFAULT_CONFIGURATION_BEAN_NAME = "infinispanRemoteCacheManagerWithDefaultConfiguration";

	private static final String INFINISPAN_REMOTE_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_PROPERTIES_FILE_BEAN_NAME = "infinispanRemoteCacheManagerConfiguredFromConfigurationPropertiesFile";

	private static final String INFINISPAN_REMOTE_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_PROPERTIES_BEAN_NAME = "infinispanRemoteCacheManagerConfiguredFromConfigurationProperties";

	private static final String INFINISPAN_REMOTE_CACHE_MANAGER_CONFIGURED_USING_SETTERS_BEAN_NAME = "infinispanRemoteCacheManagerConfiguredUsingSetters";

	@Test
	public final void shouldCreateARemoteCacheManagerWithDefaultSettingsIfNoFurtherConfigurationGiven() {
		final RemoteCacheManager infinispanRemoteCacheManagerWithDefaultConfiguration = this.applicationContext
				.getBean(INFINISPAN_REMOTE_CACHE_MANAGER_WITH_DEFAULT_CONFIGURATION_BEAN_NAME, RemoteCacheManager.class);

		assertNotNull(
				"Spring application context should contain a RemoteCacheManager with default settings having bean name = \""
						+ INFINISPAN_REMOTE_CACHE_MANAGER_WITH_DEFAULT_CONFIGURATION_BEAN_NAME
						+ "\". However, it doesn't.", infinispanRemoteCacheManagerWithDefaultConfiguration);
	}

	@Test
	public final void shouldCreateARemoteCacheManagerConfiguredFromConfigurationPropertiesFileIfConfigurationPropertiesFileLocationGiven() {
		final RemoteCacheManager infinispanRemoteCacheManagerConfiguredFromConfigurationFile = this.applicationContext
				.getBean(INFINISPAN_REMOTE_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_PROPERTIES_FILE_BEAN_NAME,
						RemoteCacheManager.class);

		assertNotNull(
				"Spring application context should contain a RemoteCacheManager configured from configuration properties file having bean name = \""
						+ INFINISPAN_REMOTE_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_PROPERTIES_FILE_BEAN_NAME
						+ "\". However, it doesn't.", infinispanRemoteCacheManagerConfiguredFromConfigurationFile);
	}

	@Test
	public final void shouldCreateARemoteCacheManagerConfiguredFromConfigurationPropertiesIfConfigurationPropertiesGiven() {
		final RemoteCacheManager infinispanRemoteCacheManagerConfiguredFromConfigurationProperties = this.applicationContext
				.getBean(INFINISPAN_REMOTE_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_PROPERTIES_BEAN_NAME,
						RemoteCacheManager.class);

		assertNotNull(
				"Spring application context should contain a RemoteCacheManager configured from configuration properties having bean name = \""
						+ INFINISPAN_REMOTE_CACHE_MANAGER_CONFIGURED_FROM_CONFIGURATION_PROPERTIES_BEAN_NAME
						+ "\". However, it doesn't.", infinispanRemoteCacheManagerConfiguredFromConfigurationProperties);
	}

	@Test
	public final void shouldCreateARemoteCacheManagerConfiguredUsingSettersIfPropertiesAreDefined() {
		final RemoteCacheManager infinispanRemoteCacheManagerConfiguredUsingSetters = this.applicationContext.getBean(
				INFINISPAN_REMOTE_CACHE_MANAGER_CONFIGURED_USING_SETTERS_BEAN_NAME, RemoteCacheManager.class);

		assertNotNull(
				"Spring application context should contain a SpringRemoteCacheManager configured using properties having bean name = \""
						+ INFINISPAN_REMOTE_CACHE_MANAGER_CONFIGURED_USING_SETTERS_BEAN_NAME
						+ "\". However, it doesn't.", infinispanRemoteCacheManagerConfiguredUsingSetters);
	}
}
