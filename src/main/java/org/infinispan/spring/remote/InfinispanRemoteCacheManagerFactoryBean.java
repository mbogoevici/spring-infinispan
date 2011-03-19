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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * <p>
 * A {@link org.springframework.beans.factory.FactoryBean <code>FactoryBean</code>} for creating an
 * {@link org.infinispan.client.hotrod.RemoteCacheManager <code>INFINISPAN RemoteCacheManager</code>}
 * instance.
 * </p>
 * <p>
 * A <code>RemoteCacheManager</code> is configured through a {@link java.util.Properties <code>Properties</code>}
 * object. For an exhaustive list of valid properties to be used see <code>RemoteCacheManager</code>'s 
 * {@link org.infinispan.client.hotrod.RemoteCacheManager javadocs}. This <code>FactoryBean</code> provides means
 * to either {@link #setConfigurationProperties(Properties) pass in} a user-defined <code>Properties</code> 
 * instance or to {@link #setConfigurationPropertiesFileLocation(Resource) set} the location of a properties file
 * to load those properties from. Note that it is <em>illegal</em> to use both mechanisms simultaneously. 
 * </p>
 * <p>
 * In addition to creating a <code>RemoteCacheManager</code> this <code>FactoryBean</code> does also
 * control that <code>RemoteCacheManagers</code>'s lifecycle by shutting it down when the enclosing 
 * Spring application context is closed. It is therefore advisable to <em>always</em> use this 
 * <code>FactoryBean</code> when creating an <code>EmbeddedCacheManager</code>.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 * 
 * @see org.infinispan.client.hotrod.RemoteCacheManager
 * @see #destroy()
 */
public class InfinispanRemoteCacheManagerFactoryBean implements FactoryBean<RemoteCacheManager>, InitializingBean,
		DisposableBean {

	private final Log logger = LogFactory.getLog(getClass());

	private boolean startAutomatically = true;

	private Properties configurationProperties;

	private Resource configurationPropertiesFileLocation;

	private RemoteCacheManager nativeRemoteCacheManager;

	// ------------------------------------------------------------------------
	// org.springframework.beans.factory.InitializingBean
	// ------------------------------------------------------------------------

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		assertCorrectlyConfigured();
		this.logger.info("Creating new instance of RemoteCacheManager ...");
		final Properties configurationPropertiesToUse = configurationProperties();
		this.nativeRemoteCacheManager = new RemoteCacheManager(configurationPropertiesToUse, this.startAutomatically);
		this.logger.info("Finished creating new instance of RemoteCacheManager");
	}

	private void assertCorrectlyConfigured() throws IllegalStateException {
		if ((this.configurationProperties != null) && (this.configurationPropertiesFileLocation != null)) {
			throw new IllegalStateException(
					"You may only use either \"configurationProperties\" or \"configurationPropertiesFileLocation\" "
							+ "to configure the RemoteCacheManager, not both.");
		}
	}

	private Properties configurationProperties() throws IOException {
		final Properties answer;
		if (this.configurationProperties != null) {
			this.logger.debug("Using user-defined properties [" + this.configurationProperties
					+ "] for configuring RemoteCacheManager");
			answer = this.configurationProperties;
		} else if (this.configurationPropertiesFileLocation != null) {
			this.logger.debug("Loading properties from file [" + this.configurationProperties
					+ "] for configuring RemoteCacheManager");
			answer = loadPropertiesFromFile(this.configurationPropertiesFileLocation);
		} else {
			this.logger.debug("No configuration properties. RemoteCacheManager will use default configuration.");
			answer = null;
		}
		return answer;
	}

	private Properties loadPropertiesFromFile(final Resource propertiesFileLocation) throws IOException {
		InputStream propsStream = null;
		try {
			propsStream = propertiesFileLocation.getInputStream();
			final Properties answer = new Properties();
			answer.load(propsStream);

			return answer;
		} finally {
			if (propsStream != null) {
				try {
					propsStream.close();
				} catch (final IOException e) {
					this.logger.warn(
							"Failed to close InputStream used to load configuration properties: " + e.getMessage(), e);
				}
			}
		}
	}

	// ------------------------------------------------------------------------
	// org.springframework.beans.factory.FactoryBean
	// ------------------------------------------------------------------------

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public RemoteCacheManager getObject() throws Exception {
		return this.nativeRemoteCacheManager;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<? extends RemoteCacheManager> getObjectType() {
		return this.nativeRemoteCacheManager != null ? this.nativeRemoteCacheManager.getClass()
				: RemoteCacheManager.class;
	}

	/**
	 * Always return <code>true</code>.
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
	 * {@link org.infinispan.client.hotrod.RemoteCacheManager#stop() <code>stop</code>} the
	 * <code>RemoteCacheManager</code> created by this factory.
	 * 
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		// Being paranoid
		if (this.nativeRemoteCacheManager != null) {
			this.nativeRemoteCacheManager.stop();
		}
	}

	// ------------------------------------------------------------------------
	// Setters for configuring RemoteCacheManager
	// ------------------------------------------------------------------------

	/**
	 * @param configurationProperties the configurationProperties to set
	 */
	public void setConfigurationProperties(final Properties configurationProperties) {
		this.configurationProperties = configurationProperties;
	}

	/**
	 * @param configurationPropertiesFileLocation the configurationPropertiesFileLocation to set
	 */
	public void setConfigurationPropertiesFileLocation(final Resource configurationPropertiesFileLocation) {
		this.configurationPropertiesFileLocation = configurationPropertiesFileLocation;
	}

	/**
	 * @param startAutomatically the startAutomatically to set
	 */
	public void setStartAutomatically(final boolean startAutomatically) {
		this.startAutomatically = startAutomatically;
	}
}
