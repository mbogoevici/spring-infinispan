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

import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.spring.AbstractInfinispanEmbeddedCacheManagerBackedCacheManagerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * <p>
 * A {@link org.springframework.beans.factory.FactoryBean <code>FactoryBean</code>} for creating an
 * {@link org.infinispan.spring.spi.SpringEmbeddedCacheManager <code>SpringEmbeddedCacheManager</code>}
 * instance. The location of the INFINISPAN configuration file used to provide the default
 * {@link org.infinispan.config.Configuration configuration} for the <code>EmbeddedCacheManager</code>
 * instance created by this <code>FactoryBean</code> is {@link #setConfigurationFileLocation(Resource) configurable}.
 * </p>
 * <p>
 * If no configuration file location is set the <code>SpringEmbeddedCacheManager</code> instance created by this
 * <code>FactoryBean</code> will use INFINISPAN's default settings. See INFINISPAN's 
 * <a href="http://www.jboss.org/infinispan/docs">documentation</a> for what those default settings are.
 * </p>
 * <p>
 * A user may further customize the <code>SpringEmbeddedCacheManager</code>'s configuration using explicit setters
 * on this <code>FactoryBean</code>. The properties thus defined will be applied either to the configuration
 * loaded from INFINISPAN's configuration file in case one has been specified, or to a configuration initialized
 * with INFINISPAN's default settings. Either way, the net effect is that explicitly set configuration
 * properties take precedence over both those loaded from a configuration file as well as INFNISPAN's
 * default settings.
 * </p>
 * <p>
 * In addition to creating an <code>SpringEmbeddedCacheManager</code> this <code>FactoryBean</code> does also
 * control that <code>SpringEmbeddedCacheManager</code>'s {@link org.infinispan.lifecycly.Lifecycle lifecycle}
 * by shutting it down when the enclosing Spring application context is closed. It is therefore advisable
 * to <em>always</em> use this <code>FactoryBean</code> when creating an <code>SpringEmbeddedCacheManager</code>.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 * 
 * @see #setConfigurationFileLocation(Resource)
 * @see #destroy()
 * @see org.infinispan.spring.spi.SpringEmbeddedCacheManager
 * @see org.infinispan.manager.EmbeddedCacheManager
 * @see org.infinispan.config.Configuration
 *
 */
public class SpringEmbeddedCacheManagerFactoryBean extends
		AbstractInfinispanEmbeddedCacheManagerBackedCacheManagerFactory implements
		FactoryBean<SpringEmbeddedCacheManager>, InitializingBean, DisposableBean {

	private SpringEmbeddedCacheManager cacheManager;

	// ------------------------------------------------------------------------
	// org.springframework.beans.factory.InitializingBean
	// ------------------------------------------------------------------------

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.logger.info("Initializing SpringEmbeddedCacheManager instance ...");

		final EmbeddedCacheManager nativeEmbeddedCacheManager = createBackingEmbeddedCacheManager();
		this.cacheManager = new SpringEmbeddedCacheManager(nativeEmbeddedCacheManager);

		this.logger.info("Successfully initialized SpringEmbeddedCacheManager instance [" + this.cacheManager + "]");
	}

	// ------------------------------------------------------------------------
	// org.springframework.beans.factory.FactoryBean
	// ------------------------------------------------------------------------

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public SpringEmbeddedCacheManager getObject() throws Exception {
		return this.cacheManager;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<? extends SpringEmbeddedCacheManager> getObjectType() {
		return this.cacheManager != null ? this.cacheManager.getClass() : SpringEmbeddedCacheManager.class;
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

	// ------------------------------------------------------------------------
	// org.springframework.beans.factory.DisposableBean
	// ------------------------------------------------------------------------

	/**
	 * Shuts down the <code>SpringEmbeddedCacheManager</code> instance created by this <code>FactoryBean</code>.
	 * 
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 * @see org.infinispan.spring.spi.SpringEmbeddedCacheManager#stop()
	 */
	@Override
	public void destroy() throws Exception {
		// Probably being paranoid here ...
		if (this.cacheManager != null) {
			this.cacheManager.stop();
		}
	}
}
