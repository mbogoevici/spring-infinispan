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

package org.infinispan.spring.embedded;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.Test;

/**
 * <p>
 * Test {@link SpringEmbeddedCacheManager}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class SpringEmbeddedCacheManagerTest {

	private static final String CACHE_NAME_FROM_CONFIGURATION_FILE = "asyncCache";

	private static final String NAMED_ASYNC_CACHE_CONFIG_LOCATION = "named-async-cache.xml";

	/**
	 * Test method for {@link org.infinispan.spring.embedded.SpringEmbeddedCacheManager#SpringEmbeddedCacheManager(org.infinispan.manager.EmbeddedCacheManager)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void springEmbeddedCacheManagerConstructorShouldRejectNullEmbeddedCacheManager() {
		new SpringEmbeddedCacheManager(null);
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.SpringEmbeddedCacheManager#SpringEmbeddedCacheManager(org.infinispan.manager.EmbeddedCacheManager)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void springEmbeddedCacheManagerConstructorShouldRejectStoppedEmbeddedCacheManager() {
		final EmbeddedCacheManager nativeCacheManager = new DefaultCacheManager();
		nativeCacheManager.stop();

		new SpringEmbeddedCacheManager(nativeCacheManager);
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.SpringEmbeddedCacheManager#loadCaches()}.
	 * @throws IOException 
	 */
	@Test
	public final void springEmbeddedCacheManagerShouldLoadAllCachesDefinedInConfigurationFile() throws IOException {
		final EmbeddedCacheManager nativeCacheManager = new DefaultCacheManager(
				SpringEmbeddedCacheManagerTest.class.getResourceAsStream(NAMED_ASYNC_CACHE_CONFIG_LOCATION));
		nativeCacheManager.getCache(); // Implicitly starts EmbeddedCacheManager

		final SpringEmbeddedCacheManager objectUnderTest = new SpringEmbeddedCacheManager(nativeCacheManager);
		objectUnderTest.afterPropertiesSet();

		final Collection<String> cacheNames = objectUnderTest.getCacheNames();

		assertTrue(
				"SpringEmbeddedCacheManager should load all named caches found in the configuration file of the wrapped "
						+ "native cache manager. However, it does not know about the cache named "
						+ CACHE_NAME_FROM_CONFIGURATION_FILE + " defined in said configuration file.",
				cacheNames.contains(CACHE_NAME_FROM_CONFIGURATION_FILE));
		nativeCacheManager.stop();
	}

}
