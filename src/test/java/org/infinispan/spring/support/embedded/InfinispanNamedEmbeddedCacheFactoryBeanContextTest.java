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

import org.infinispan.Cache;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * <p>
 * Test {@link InfinispanNameEmbeddedCacheFactoryBean} deployed in a Spring application context.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ContextConfiguration("classpath:/org/infinispan/spring/support/embedded/InfinispanNamedEmbeddedCacheFactoryBeanContextTest.xml")
public class InfinispanNamedEmbeddedCacheFactoryBeanContextTest extends AbstractJUnit4SpringContextTests {

	private static final String INFINISPAN_NAMED_EMBEDDED_CACHE_WITHOUT_FURTHER_CONFIGURATION_BEAN_NAME = "infinispanNamedEmbeddedCacheWithoutFurtherConfiguration";

	private static final String INFINISPAN_NAMED_EMBEDDED_CACHE_CONFIGURED_USING_MODE_NONE_BEAN_NAME = "infinispanNamedEmbeddedCacheConfiguredUsingModeNONE";

	private static final String INFINISPAN_NAMED_EMBEDDED_CACHE_CONFIGURED_USING_MODE_DEFAULT_BEAN_NAME = "infinispanNamedEmbeddedCacheConfiguredUsingModeDEFAULT";

	private static final String INFINISPAN_NAMED_EMBEDDED_CACHE_CONFIGURED_USING_MODE_NAMED_BEAN_NAME = "infinispanNamedEmbeddedCacheConfiguredUsingModeNAMED";

	@Test
	public final void shouldCreateAnEmbeddedCacheWithDefaultSettingsIfNoFurtherConfigurationGiven() {
		final Cache<Object, Object> infinispanNamedEmbeddedCacheWithoutFurtherConfiguration = this.applicationContext
				.getBean(INFINISPAN_NAMED_EMBEDDED_CACHE_WITHOUT_FURTHER_CONFIGURATION_BEAN_NAME, Cache.class);

		assertNotNull("Spring application context should contain a named INFINISPAN cache having bean name = \""
				+ INFINISPAN_NAMED_EMBEDDED_CACHE_WITHOUT_FURTHER_CONFIGURATION_BEAN_NAME + "\". However, it doesn't.",
				infinispanNamedEmbeddedCacheWithoutFurtherConfiguration);
	}

	@Test
	public final void shouldCreateAnEmbeddedCacheConfiguredUsingConfigurationModeNONE() {
		final Cache<Object, Object> infinispanNamedEmbeddedCacheConfiguredUsingConfigurationModeNone = this.applicationContext
				.getBean(INFINISPAN_NAMED_EMBEDDED_CACHE_CONFIGURED_USING_MODE_NONE_BEAN_NAME, Cache.class);

		assertNotNull("Spring application context should contain a named INFINISPAN cache having bean name = \""
				+ INFINISPAN_NAMED_EMBEDDED_CACHE_CONFIGURED_USING_MODE_NONE_BEAN_NAME
				+ "\" that has been configured using configuration mode NONE. However, it doesn't.",
				infinispanNamedEmbeddedCacheConfiguredUsingConfigurationModeNone);
	}

	@Test
	public final void shouldCreateAnEmbeddedCacheConfiguredUsingConfigurationModeDEFAULT() {
		final Cache<Object, Object> infinispanNamedEmbeddedCacheConfiguredUsingConfigurationModeDefault = this.applicationContext
				.getBean(INFINISPAN_NAMED_EMBEDDED_CACHE_CONFIGURED_USING_MODE_DEFAULT_BEAN_NAME, Cache.class);

		assertNotNull("Spring application context should contain a named INFINISPAN cache having bean name = \""
				+ INFINISPAN_NAMED_EMBEDDED_CACHE_CONFIGURED_USING_MODE_DEFAULT_BEAN_NAME
				+ "\" that has been configured using configuration mode DEFAULT. However, it doesn't.",
				infinispanNamedEmbeddedCacheConfiguredUsingConfigurationModeDefault);
	}

	@Test
	public final void shouldCreateAnEmbeddedCacheConfiguredUsingConfigurationModeNAMED() {
		final Cache<Object, Object> infinispanNamedEmbeddedCacheConfiguredUsingConfigurationModeNamed = this.applicationContext
				.getBean(INFINISPAN_NAMED_EMBEDDED_CACHE_CONFIGURED_USING_MODE_NAMED_BEAN_NAME, Cache.class);

		assertNotNull("Spring application context should contain a named INFINISPAN cache having bean name = \""
				+ INFINISPAN_NAMED_EMBEDDED_CACHE_CONFIGURED_USING_MODE_NAMED_BEAN_NAME
				+ "\" that has been configured using configuration mode NAMED. However, it doesn't.",
				infinispanNamedEmbeddedCacheConfiguredUsingConfigurationModeNamed);
	}
}
