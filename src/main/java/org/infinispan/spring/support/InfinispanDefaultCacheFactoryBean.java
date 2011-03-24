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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * <p>
 * A {@link org.springframework.beans.factory.FactoryBean <code>FactoryBean</code>} for creating a native
 * <em>default</em> INFINISPAN {@link org.infinispan.Cache <code>org.infinispan.Cache</code>}, 
 * delegating to a {@link #setInfinispanCacheContainer(CacheContainer) <code>configurable</code>} 
 * {@link org.infinispan.manager.CacheContainer <code>org.infinispan.manager.CacheContainer</code>}. A
 * default <code>Cache</code> is a <code>Cache</code> that uses its <code>CacheContainer</code>'s default
 * settings. This is contrary to a <em>named</em> <code>Cache</code> where select settings from a
 * <code>CacheContainer</code>'s default configuration may be overridden with settings specific to that
 * <code>Cache</code>.
 * </p>
 * <p>
 * In addition to creating a <code>Cache</code> this <code>FactoryBean</code> does also control that 
 * <code>Cache</code>'s {@link org.infinispan.lifecycle.Lifecycle lifecycle} by shutting it down when the 
 * enclosing Spring application context is closed. It is therefore advisable to <em>always</em> use this 
 * <code>FactoryBean</code> when creating a <code>Cache</code>.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanDefaultCacheFactoryBean<K, V> implements FactoryBean<Cache<K, V>>, InitializingBean,
		DisposableBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private CacheContainer infinispanCacheContainer;

	private Cache<K, V> infinispanCache;

	/**
	 * <p>
	 * Sets the {@link org.infinispan.manager.CacheContainer <code>org.infinispan.manager.CacheContainer</code>} to 
	 * be used for creating our {@link org.infinispan.Cache <code>Cache</code>} instance. Note that this is a 
	 * <strong>mandatory</strong> property.
	 * </p>
	 * 
	 * @param infinispanCacheContainer 
	 * 			The {@link org.infinispan.manager.CacheContainer <code>org.infinispan.manager.CacheContainer</code>} to
	 *          be used for creating our {@link org.infinispan.Cache <code>Cache</code>} instance
	 */
	public void setInfinispanCacheContainer(final CacheContainer infinispanCacheContainer) {
		this.infinispanCacheContainer = infinispanCacheContainer;
	}

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.infinispanCacheContainer == null) {
			throw new IllegalStateException("No INFINISPAN CacheContainer has been set");
		}
		this.logger.info("Initializing named INFINISPAN cache ...");
		this.infinispanCache = this.infinispanCacheContainer.getCache();
		this.logger.info("New INFINISPAN cache [" + this.infinispanCache + "] initialized");
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public Cache<K, V> getObject() throws Exception {
		return this.infinispanCache;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<? extends Cache> getObjectType() {
		return this.infinispanCache != null ? this.infinispanCache.getClass() : Cache.class;
	}

	/**
	 * Always returns <code>true</code>.
	 * 
	 * @return Always <code>true</code>
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Shuts down the <code>org.infinispan.Cache</code> created by this <code>FactoryBean</code>.
	 * 
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 * @see org.infinispan.Cache#stop()
	 */
	@Override
	public void destroy() throws Exception {
		// Probably being paranoid here ...
		if (this.infinispanCache != null) {
			this.infinispanCache.stop();
		}
	}
}
