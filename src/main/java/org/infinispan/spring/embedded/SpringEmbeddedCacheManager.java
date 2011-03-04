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

import java.util.Collection;
import java.util.LinkedHashSet;

import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.spring.SpringCache;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.util.Assert;

/**
 * <p>
 * A {@link org.springframework.cache.CacheManager <code>CacheManager</code>} implementation that is backed
 * by an {@link org.infinispan.manager.EmbeddedCacheManager <code>INFINISPAN EmbeddedCacheManager</code>}
 * instance.
 * </p>
 * <p>
 * Note that this <code>CacheManager</code> does <strong>not</strong> support adding new
 * {@link org.infinispan.Cache <code>Caches</code>} at runtime, i.e. <code>Caches</code> added programmatically
 * to the backing <code>EmbeddedCacheManager</code> after this <code>CacheManager</code> has been constructed 
 * will not be seen by this <code>CacheManager</code>.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
class SpringEmbeddedCacheManager extends AbstractCacheManager {

	private final EmbeddedCacheManager nativeCacheManager;

	/**
	 * @param nativeCacheManager
	 */
	SpringEmbeddedCacheManager(final EmbeddedCacheManager nativeCacheManager) {
		Assert.notNull(nativeCacheManager, "A non-null instance of EmbeddedCacheManager needs to be supplied");
		checkNativeCacheManagerStatus(nativeCacheManager);
		this.nativeCacheManager = nativeCacheManager;
	}

	/**
	 * @param nativeCacheManager
	 */
	private void checkNativeCacheManagerStatus(final EmbeddedCacheManager nativeCacheManager) {
		final ComponentStatus currentCacheManagerStatus = nativeCacheManager.getStatus();
		Assert.isTrue(currentCacheManagerStatus == ComponentStatus.RUNNING,
				"The supplied EmbeddedCacheManager instance [" + nativeCacheManager
						+ "] is required to be in state RUNNING. Actual state: " + currentCacheManagerStatus);
	}

	/**
	 * @see org.springframework.cache.support.AbstractCacheManager#loadCaches()
	 */
	@Override
	protected Collection<Cache<?, ?>> loadCaches() {
		checkNativeCacheManagerStatus(this.nativeCacheManager);

		final Collection<Cache<?, ?>> allCaches = new LinkedHashSet<Cache<?, ?>>();
		for (final String cacheName : this.nativeCacheManager.getCacheNames()) {
			final org.infinispan.Cache<Object, Object> namedNativeCache = this.nativeCacheManager.getCache(cacheName);
			final Cache<?, ?> infinispanSpringCache = new SpringCache(namedNativeCache);
			allCaches.add(infinispanSpringCache);
		}

		return allCaches;
	}

}
