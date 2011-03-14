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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.impl.RemoteCacheImpl;
import org.infinispan.spring.SpringCache;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;

/**
 * <p>
 * A {@link org.springframework.cache.CacheManager <code>CacheManager</code>} implementation that is backed
 * by an {@link org.infinispan.client.hotrod.RemoteCacheManager <code>INFINISPAN RemoteCacheManager</code>}
 * instance.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class SpringRemoteCacheManager implements org.springframework.cache.CacheManager {

	private final RemoteCacheManager nativeCacheManager;

	/**
	 * @param nativeCacheManager
	 */
	public SpringRemoteCacheManager(final RemoteCacheManager nativeCacheManager) {
		Assert.notNull(nativeCacheManager, "A non-null instance of EmbeddedCacheManager needs to be supplied");
		checkNativeCacheManagerStatus(nativeCacheManager);
		this.nativeCacheManager = nativeCacheManager;
	}

	/**
	 * @param nativeCacheManager
	 */
	private void checkNativeCacheManagerStatus(final RemoteCacheManager nativeCacheManager) {
		Assert.isTrue(nativeCacheManager.isStarted(), "The supplied RemoteCacheManager instance [" + nativeCacheManager
				+ "] is required to be running");
	}

	@Override
	public <K, V> Cache<K, V> getCache(final String name) {
		checkNativeCacheManagerStatus(this.nativeCacheManager);
		return new SpringCache<K, V>(this.nativeCacheManager.<K, V> getCache(name));
	}

	@Override
	public Collection<String> getCacheNames() {
		checkNativeCacheManagerStatus(this.nativeCacheManager);
		return getCacheMapViaReflection().keySet();
	}

	private final Map<String, RemoteCacheImpl<?, ?>> getCacheMapViaReflection() {
		try {
			final Field cacheName2CacheMapField = RemoteCacheManager.class.getDeclaredField("cacheName2RemoteCache");
			cacheName2CacheMapField.setAccessible(true);

			return (Map<String, RemoteCacheImpl<?, ?>>) cacheName2CacheMapField.get(this.nativeCacheManager);
		} catch (final Exception e) {
			throw new RuntimeException("Could not read map of known caches from RemoteCacheManager instance: "
					+ e.getMessage(), e);
		}
	}
}
