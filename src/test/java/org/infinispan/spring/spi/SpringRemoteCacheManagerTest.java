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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

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
	 * Test method for {@link org.infinispan.spring.spi.SpringRemoteCacheManager#SpringRemoteCacheManager(org.infinispan.client.hotrod.RemoteCacheManager)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void springRemoteCacheManagerConstructorShouldRejectNullRemoteCacheManager() {
		new SpringRemoteCacheManager(null);
	}

	/**
	 * Test method for {@link org.infinispan.spring.spi.SpringRemoteCacheManager#getCache(java.lang.String)}.
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
		nativeCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.spi.SpringRemoteCacheManager#getCacheNames()}.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public final void getCacheNamesShouldThrowAnUnsupportedOperationException() {
		final RemoteCacheManager nativeCacheManager = new RemoteCacheManager(true);
		final SpringRemoteCacheManager objectUnderTest = new SpringRemoteCacheManager(nativeCacheManager);
		nativeCacheManager.stop();

		objectUnderTest.getCacheNames();
	}

	/**
	 * Test method for {@link org.infinispan.spring.spi.SpringRemoteCacheManager#start()}.
	 * @throws IOException 
	 */
	@Test
	public final void startShouldStartTheNativeRemoteCacheManager() throws IOException {
		final RemoteCacheManager nativeCacheManager = new RemoteCacheManager(true);
		final SpringRemoteCacheManager objectUnderTest = new SpringRemoteCacheManager(nativeCacheManager);

		objectUnderTest.start();

		assertTrue("Calling start() on SpringRemoteCacheManager should start the enclosed "
				+ "INFINISPAN RemoteCacheManager. However, it is still not running.", nativeCacheManager.isStarted());
	}

	/**
	 * Test method for {@link org.infinispan.spring.spi.SpringRemoteCacheManager#stop()}.
	 * @throws IOException 
	 */
	@Test
	public final void stopShouldStopTheNativeRemoteCacheManager() throws IOException {
		final RemoteCacheManager nativeCacheManager = new RemoteCacheManager(true);
		final SpringRemoteCacheManager objectUnderTest = new SpringRemoteCacheManager(nativeCacheManager);

		objectUnderTest.stop();

		assertFalse("Calling stop() on SpringRemoteCacheManager should stop the enclosed "
				+ "INFINISPAN RemoteCacheManager. However, it is still running.", nativeCacheManager.isStarted());
	}

	/**
	 * Test method for {@link org.infinispan.spring.spi.SpringRemoteCacheManager#getNativeCache()}.
	 * @throws IOException 
	 */
	@Test
	public final void getNativeCacheShouldReturnTheRemoteCacheManagerSuppliedAtConstructionTime() throws IOException {
		final RemoteCacheManager nativeCacheManager = new RemoteCacheManager(true);
		final SpringRemoteCacheManager objectUnderTest = new SpringRemoteCacheManager(nativeCacheManager);

		final RemoteCacheManager nativeCacheManagerReturned = objectUnderTest.getNativeCacheManager();

		assertSame(
				"getNativeCacheManager() should have returned the RemoteCacheManager supplied at construction time. However, it retuned a different one.",
				nativeCacheManager, nativeCacheManagerReturned);
	}
}
