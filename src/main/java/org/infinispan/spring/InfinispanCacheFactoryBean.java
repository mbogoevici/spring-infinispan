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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/**
 * <p>
 * A {@link org.springframework.beans.factory.FactoryBean <code>FactoryBean</code>} for creating a native
 * {@link #setCacheName(String) named} INFINISPAN {@link org.infinispan.Cache <code>org.infinispan.Cache</code>}, 
 * delegating to a {@link #setInfinispanCacheContainer(CacheContainer) <code>configurable</code>} 
 * {@link org.infinispan.manager.CacheContainer <code>org.infinispan.manager.CacheContainer</code>}. If no
 * cache name is explicitly set, this <code>FactoryBean</code>'s {@link #setBeanName(String) <code>beanName</code>}
 * will be used instead.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanCacheFactoryBean implements FactoryBean<Cache<Object, Object>>, BeanNameAware, InitializingBean,
		DisposableBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private CacheContainer infinispanCacheContainer;

	private String cacheName;

	private String beanName;

	private Cache<Object, Object> infinispanCache;

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
	 * <p>
	 * Sets the {@link org.infinispan.Cache#getName() name} of the {@link org.infinispan.Cache <code>org.infinispan.Cache</code>}
	 * to be created. If no explicit <code>cacheName</code> is set, this <code>FactoryBean</code> will use its
	 * {@link #setBeanName(String) <code>beanName</code>} as the <code>cacheName</code>.
	 * </p>
	 * 
	 * @param cacheName 
	 * 			The {@link org.infinispan.Cache#getName() name} of the {@link org.infinispan.Cache <code>org.infinispan.Cache</code>}
	 * 			to be created
	 */
	public void setCacheName(final String cacheName) {
		this.cacheName = cacheName;
	}

	/**
	 * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
	 */
	@Override
	public void setBeanName(final String name) {
		this.beanName = name;
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
		this.infinispanCache = this.infinispanCacheContainer.getCache(obtainEffectiveCacheName());
		this.logger.info("New INFINISPAN cache [" + this.infinispanCache + "] initialized");
	}

	private String obtainEffectiveCacheName() {
		if (StringUtils.hasText(this.cacheName)) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Using custom cache name [" + this.cacheName + "]");
			}
			return this.cacheName;
		} else {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Using bean name [" + this.beanName + "] as cache name");
			}
			return this.beanName;
		}
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public Cache<Object, Object> getObject() throws Exception {
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
