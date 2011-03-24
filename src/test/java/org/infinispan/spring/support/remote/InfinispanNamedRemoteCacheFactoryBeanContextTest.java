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

import org.infinispan.Cache;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * <p>
 * Test {@link InfinispanNamedRemoteCacheFactoryBean} deployed in a Spring application context.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ContextConfiguration("classpath:/org/infinispan/spring/support/remote/InfinispanNamedRemoteCacheFactoryBeanContextTest.xml")
public class InfinispanNamedRemoteCacheFactoryBeanContextTest extends AbstractJUnit4SpringContextTests {

	private static final String INFINISPAN_NAMED_REMOTE_CACHE_WITHOUT_FURTHER_CONFIGURATION_BEAN_NAME = "infinispanNamedRemoteCacheWithoutFurtherConfiguration";

	@Test
	public final void shouldCreateARemoteCacheWithDefaultSettingsIfNoFurtherConfigurationGiven() {
		final Cache<Object, Object> infinispanNamedRemoteCacheWithoutFurtherConfiguration = this.applicationContext
				.getBean(INFINISPAN_NAMED_REMOTE_CACHE_WITHOUT_FURTHER_CONFIGURATION_BEAN_NAME, Cache.class);

		assertNotNull("Spring application context should contain a named INFINISPAN cache having bean name = \""
				+ INFINISPAN_NAMED_REMOTE_CACHE_WITHOUT_FURTHER_CONFIGURATION_BEAN_NAME + "\". However, it doesn't.",
				infinispanNamedRemoteCacheWithoutFurtherConfiguration);
	}
}
