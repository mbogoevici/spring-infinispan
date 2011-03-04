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

package org.infinispan.spring;

import org.infinispan.lifecycle.ComponentStatus;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;

/**
 * <p>
 * A {@link org.springframework.cache.Cache <code>Cache</code>} implementation that delegates to
 * a {@link org.infinispan.Cache <code>org.infinispan.Cache</code>} instance supplied at construction
 * time.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class SpringCache implements Cache<Object, Object> {

	private final org.infinispan.Cache<Object, Object> nativeCache;

	/**
	 * @param nativeCache
	 */
	public SpringCache(final org.infinispan.Cache<Object, Object> nativeCache) {
		Assert.notNull(nativeCache, "A non-null Infinispan cache implementation is required");
		final ComponentStatus currentCacheStatus = nativeCache.getStatus();
		Assert.isTrue(currentCacheStatus == ComponentStatus.RUNNING,
				"Supplied Infinispan cache is required to be in state RUNNING. Actual state: " + currentCacheStatus);
		this.nativeCache = nativeCache;
	}

	/**
	 * @see org.springframework.cache.Cache#getName()
	 */
	@Override
	public String getName() {
		return this.nativeCache.getName();
	}

	/**
	 * @see org.springframework.cache.Cache#getNativeCache()
	 */
	@Override
	public org.infinispan.Cache<Object, Object> getNativeCache() {
		return this.nativeCache;
	}

	/**
	 * @see org.springframework.cache.Cache#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(final Object key) {
		return this.nativeCache.containsKey(key);
	}

	/**
	 * @see org.springframework.cache.Cache#get(java.lang.Object)
	 */
	@Override
	public Object get(final Object key) {
		return this.nativeCache.get(key);
	}

	/**
	 * @see org.springframework.cache.Cache#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object put(final Object key, final Object value) {
		return this.nativeCache.put(key, value);
	}

	/**
	 * @see org.springframework.cache.Cache#putIfAbsent(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object putIfAbsent(final Object key, final Object value) {
		return this.nativeCache.putIfAbsent(key, value);
	}

	/**
	 * @see org.springframework.cache.Cache#remove(java.lang.Object)
	 */
	@Override
	public Object remove(final Object key) {
		return this.nativeCache.remove(key);
	}

	/**
	 * @see org.springframework.cache.Cache#remove(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean remove(final Object key, final Object value) {
		return this.nativeCache.remove(key, value);
	}

	/**
	 * @see org.springframework.cache.Cache#replace(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean replace(final Object key, final Object oldValue, final Object newValue) {
		return this.nativeCache.replace(key, oldValue, newValue);
	}

	/**
	 * @see org.springframework.cache.Cache#replace(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object replace(final Object key, final Object value) {
		return this.nativeCache.replace(key, value);
	}

	/**
	 * @see org.springframework.cache.Cache#clear()
	 */
	@Override
	public void clear() {
		this.nativeCache.clear();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InfinispanCache [nativeCache = " + this.nativeCache + "]";
	}

}
