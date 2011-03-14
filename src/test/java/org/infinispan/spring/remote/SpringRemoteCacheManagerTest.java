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

package org.infinispan.spring.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.Test;
import org.springframework.cache.Cache;

/**
 * <p>
 * Test {@link SpringRemoteCacheManager}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class SpringRemoteCacheManagerTest {

	/**
	 * Test method for {@link org.infinispan.spring.remote.SpringRemoteCacheManager#SpringRemoteCacheManager(org.infinispan.client.hotrod.RemoteCacheManager)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void springRemoteCacheManagerConstructorShouldRejectNullRemoteCacheManager() {
		new SpringRemoteCacheManager(null);
	}

	/**
	 * Test method for {@link org.infinispan.spring.remote.SpringRemoteCacheManager#SpringRemoteCacheManager(org.infinispan.client.hotrod.RemoteCacheManager)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void springRemoteCacheManagerConstructorShouldRejectStoppedRemoteCacheManager() {
		final RemoteCacheManager nativeStoppedCacheManager = new RemoteCacheManager(false);
		new SpringRemoteCacheManager(nativeStoppedCacheManager);
	}

	/**
	 * Test method for {@link org.infinispan.spring.remote.SpringRemoteCacheManager#getCache(java.lang.String)}.
	 */
	@Test
	public final void springRemoteCacheManagerShouldProperlyCreateCache() {
		final String cacheName = "spring.remote.cache.manager.Test";

		final RemoteCacheManager nativeCacheManager = new RemoteCacheManager(true);
		final SpringRemoteCacheManager objectUnderTest = new SpringRemoteCacheManager(nativeCacheManager);

		final Cache<Object, Object> defaultCache = objectUnderTest.getCache(cacheName);

		assertNotNull("getCache(" + cacheName + ") should have returned a default cache. However, it returned null.",
				defaultCache);
		assertEquals("getCache(" + cacheName + ") should have returned a cache name \"" + cacheName
				+ "\". However, the returned cache has a different name.", cacheName, defaultCache.getName());
	}

	/**
	 * Test method for {@link org.infinispan.spring.remote.SpringRemoteCacheManager#getCacheNames()}.
	 */
	@Test
	public final void springRemoteCacheManagerShouldReturnTheNamesOfAllCreatedCaches() {
		final String cacheName1 = "spring.remote.cache.manager.Test1";
		final String cacheName2 = "spring.remote.cache.manager.Test2";

		final RemoteCacheManager nativeCacheManager = new RemoteCacheManager(true);
		final SpringRemoteCacheManager objectUnderTest = new SpringRemoteCacheManager(nativeCacheManager);
		objectUnderTest.getCache(cacheName1);
		objectUnderTest.getCache(cacheName2);

		final Collection<String> allCacheNames = objectUnderTest.getCacheNames();

		assertNotNull("getCacheNames() should have returned the names of all known caches. However, it returned null.",
				allCacheNames);
		assertEquals(
				"getCacheNames() should have returned exactly two cache names. However, it returned less or more than two cache names.",
				2, allCacheNames.size());
		assertTrue("The names of all known caches returned by getCacheNames() should include the cache name \""
				+ cacheName1 + "\". However, it doesn't.", allCacheNames.contains(cacheName1));
		assertTrue("The names of all known caches returned by getCacheNames() should include the cache name \""
				+ cacheName2 + "\". However, it doesn't.", allCacheNames.contains(cacheName2));
	}

}
