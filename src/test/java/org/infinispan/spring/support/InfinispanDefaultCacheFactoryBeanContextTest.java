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

package org.infinispan.spring.support;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.infinispan.Cache;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * <p>
 * Test {@link InfinispanDefaultCacheFactoryBean} deployed in a Spring application context.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ContextConfiguration("classpath:/org/infinispan/spring/support/InfinispanDefaultCacheFactoryBeanContextTest.xml")
public class InfinispanDefaultCacheFactoryBeanContextTest extends AbstractJUnit4SpringContextTests {

	private static final String DEFAULT_CACHE_NAME = "testDefaultCache";

	@Test
	public final void shouldProduceANonNullCache() {
		final Cache<Object, Object> testDefaultCache = this.applicationContext.getBean(DEFAULT_CACHE_NAME, Cache.class);

		assertNotNull("Spring application context should contain an INFINISPAN cache under the bean name \""
				+ DEFAULT_CACHE_NAME + "\". However, it doesn't.", testDefaultCache);
	}

	@Test
	public final void shouldAlwaysReturnTheSameCache() {
		final Cache<Object, Object> testDefaultCache1 = this.applicationContext
				.getBean(DEFAULT_CACHE_NAME, Cache.class);
		final Cache<Object, Object> testDefaultCache2 = this.applicationContext
				.getBean(DEFAULT_CACHE_NAME, Cache.class);

		assertSame("InfinispanDefaultCacheFactoryBean should always return the same cache instance when being "
				+ "called repeatedly. However, the cache instances are not the same.", testDefaultCache1,
				testDefaultCache2);
	}
}
