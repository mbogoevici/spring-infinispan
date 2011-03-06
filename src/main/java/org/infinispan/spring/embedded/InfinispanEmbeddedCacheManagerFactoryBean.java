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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * <p>
 * A {@link org.springframework.beans.factory.FactoryBean <code>FactoryBean</code>} for creating an
 * {@link org.infinispan.manager.EmbeddedCacheManager <code>INFINISPAN EmbeddedCacheManager</code>}
 * instance. The location of the INFINISPAN configuration file used to provide the default
 * {@link org.infinispan.config.Configuration configuration} for the <code>EmbeddedCacheManager</code>
 * instance created by this <code>FactoryBean</code> is {@link #setDefaultConfigurationLocation(Resource) configurable}.
 * </p>
 * <p>
 * If no configuration file location is set the <code>EmbeddedCacheManager</code> instance created by this
 * <code>FactoryBean</code> will use INFINISPAN's default settings. See INFINISPAN's 
 * <a href="http://www.jboss.org/infinispan/docs">documentation</a> for what those default settings are.
 * </p>
 * <p>
 * In addition to creating an <code>EmbeddedCacheManager</code> this <code>FactoryBean</code> does also
 * control that <code>EmbeddedCacheManagers</code>'s {@link org.infinispan.lifecycly.Lifecycle lifecycle}
 * by shutting it down when the enclosing Spring application context is closed. It is therefore advisable
 * to <em>always</em> use this <code>FactoryBean</code> when creating an <code>EmbeddedCacheManager</code>.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 * 
 * @see #setDefaultConfigurationLocation(Resource)
 * @see #destroy()
 * @see org.infinispan.manager.EmbeddedCacheManager
 * @see org.infinispan.config.Configuration
 *
 */
public class InfinispanEmbeddedCacheManagerFactoryBean implements FactoryBean<EmbeddedCacheManager>, InitializingBean,
		DisposableBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private Resource defaultConfigurationLocation;

	private EmbeddedCacheManager cacheManager;

	/**
	 * <p>
	 * Sets the {@link org.springframework.core.io.Resource <code>location</code>} of the configuration
	 * file which will be used to configure the {@link org.infinispan.manager.EmbeddedCacheManager <code>EmbeddedCacheManager</code>}
	 * created by this <code>FactoryBean</code>. If no location is supplied, <tt>INFINISPAN</tt>'s default configuration
	 * will be used. 
	 * </p>
	 * 
	 * @param defaultConfigurationLocation 
	 * 			The {@link org.springframework.core.io.Resource <code>location</code>} of the configuration	file which 
	 * 			will be used to configure the {@link org.infinispan.manager.EmbeddedCacheManager <code>EmbeddedCacheManager</code>}
	 * 			created by this <code>FactoryBean</code>
	 */
	public void setDefaultConfigurationLocation(final Resource defaultConfigurationLocation) {
		this.defaultConfigurationLocation = defaultConfigurationLocation;
	}

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.logger.info("Initializing INFINISPAN EmbeddedCacheManager instance from default configuration location ["
				+ this.defaultConfigurationLocation + "] ...");
		if (this.defaultConfigurationLocation == null) {
			this.cacheManager = createDefaultCacheManager();
		} else {
			this.cacheManager = createCustomCacheManager(this.defaultConfigurationLocation);
		}
		this.logger.info("Successfully initialized INFINISPAN EmbeddedCacheManager instance [" + this.cacheManager
				+ "]");
	}

	private DefaultCacheManager createDefaultCacheManager() {
		return new DefaultCacheManager();
	}

	private DefaultCacheManager createCustomCacheManager(final Resource defaultConfigLocation) throws IOException {
		final InputStream defaultConfigurationStream = defaultConfigLocation.getInputStream();
		try {
			return new DefaultCacheManager(defaultConfigurationStream);
		} finally {
			if (defaultConfigurationStream != null) {
				try {
					defaultConfigurationStream.close();
				} catch (final IOException e) {
					this.logger
							.warn("Caught exception while trying to close InputStream used to read in EmbeddedCacheManager's default configuration",
									e);
				}
			}
		}
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public EmbeddedCacheManager getObject() throws Exception {
		return this.cacheManager;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<? extends EmbeddedCacheManager> getObjectType() {
		return this.cacheManager != null ? this.cacheManager.getClass() : EmbeddedCacheManager.class;
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
	 * Shuts down the <code>EmbeddedCacheManager</code> instance created by this <code>FactoryBean</code>.
	 * 
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 * @see org.infinispan.manager.EmbeddedCacheManager#stop()
	 */
	@Override
	public void destroy() throws Exception {
		// Probably being paranoid here ...
		if (this.cacheManager != null) {
			this.cacheManager.stop();
		}
	}
}
