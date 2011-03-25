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

import org.infinispan.client.hotrod.RemoteCacheManager;
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
		this.nativeCacheManager = nativeCacheManager;
	}

	/**
	 * @see org.springframework.cache.CacheManager#getCache(java.lang.String)
	 */
	@Override
	public <K, V> Cache<K, V> getCache(final String name) {
		return new SpringCache<K, V>(this.nativeCacheManager.<K, V> getCache(name));
	}

	/**
	 * <p>
	 * As of INFINISPAN 4.2.0.FINAL <code>org.infinispan.client.hotrod.RemoteCache</code> does <strong>not</strong>
	 * support retrieving the set of all cache names from the hotrod server. This restriction may be lifted in
	 * the future. Currently, this operation will always throw an <code>UnsupportedOperationException</code>.
	 * </p>
	 *  
	 * @see org.springframework.cache.CacheManager#getCacheNames()
	 */
	@Override
	public Collection<String> getCacheNames() {
		throw new UnsupportedOperationException("Operation getCacheNames() is currently not supported.");
	}

	/**
	 * Return the {@link org.infinispan.client.hotrod.RemoteCacheManager 
	 * <code>org.infinispan.client.hotrod.RemoteCacheManager</code>} that backs this
	 * <code>SpringRemoteCacheManager</code>.
	 * 
	 * @return The {@link org.infinispan.client.hotrod.RemoteCacheManager 
	 *         <code>org.infinispan.client.hotrod.RemoteCacheManager</code>} that backs this 
	 *         <code>SpringRemoteCacheManager</code>
	 */
	public RemoteCacheManager getNativeCacheManager() {
		return this.nativeCacheManager;
	}

	/**
	 * Start the {@link org.infinispan.client.hotrod.RemoteCacheManager 
	 * <code>org.infinispan.client.hotrod.RemoteCacheManager</code>} that backs this 
	 * <code>SpringRemoteCacheManager</code>.
	 */
	public void start() {
		this.nativeCacheManager.start();
	}

	/**
	 * Stop the {@link org.infinispan.client.hotrod.RemoteCacheManager 
	 * <code>org.infinispan.client.hotrod.RemoteCacheManager</code>} that backs this 
	 * <code>SpringRemoteCacheManager</code>.
	 */
	public void stop() {
		this.nativeCacheManager.stop();
	}
}
