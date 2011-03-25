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

import java.util.Collection;
import java.util.Collections;

import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.util.Assert;

/**
 * <p>
 * A {@link org.springframework.cache.CacheManager <code>CacheManager</code>} implementation that is backed
 * by an {@link org.infinispan.manager.EmbeddedCacheManager <code>INFINISPAN EmbeddedCacheManager</code>}
 * instance.
 * </p>
 * <p>
 * Note that this <code>CacheManager</code> <strong>does</strong> support adding new
 * {@link org.infinispan.Cache <code>Caches</code>} at runtime, i.e. <code>Caches</code> added programmatically
 * to the backing <code>EmbeddedCacheManager</code> after this <code>CacheManager</code> has been constructed 
 * will be seen by this <code>CacheManager</code>.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class SpringEmbeddedCacheManager implements CacheManager {

	private final EmbeddedCacheManager nativeCacheManager;

	/**
	 * @param nativeCacheManager
	 */
	public SpringEmbeddedCacheManager(final EmbeddedCacheManager nativeCacheManager) {
		Assert.notNull(nativeCacheManager, "A non-null instance of EmbeddedCacheManager needs to be supplied");
		this.nativeCacheManager = nativeCacheManager;
	}

	@Override
	public <K, V> SpringCache<K, V> getCache(final String name) {
		return new SpringCache<K, V>(this.nativeCacheManager.<K, V> getCache(name));
	}

	@Override
	public Collection<String> getCacheNames() {
		return Collections.unmodifiableSet(this.nativeCacheManager.getCacheNames());
	}

	/**
	 * Return the {@link org.infinispan.manager.EmbeddedCacheManager 
	 * <code>org.infinispan.manager.EmbeddedCacheManager</code>} that backs this <code>CacheManager</code>.
	 * 
	 * @return The {@link org.infinispan.manager.EmbeddedCacheManager 
	 *         <code>org.infinispan.manager.EmbeddedCacheManager</code>} that backs this <code>CacheManager</code>
	 */
	public EmbeddedCacheManager getNativeCacheManager() {
		return this.nativeCacheManager;
	}

	/**
	 * Stop the {@link EmbeddedCacheManager <code>EmbeddedCacheManager</code>} this <code>CacheManager</code>
	 * delegates to. 
	 */
	public void stop() {
		this.nativeCacheManager.stop();
	}
}
