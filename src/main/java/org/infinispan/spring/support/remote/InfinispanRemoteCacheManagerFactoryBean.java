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

package org.infinispan.spring.support.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.spring.ConfigurationPropertiesOverrides;
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
 * <strong>Configuration</strong><br/>
 * <p>
 * A <code>RemoteCacheManager</code> is configured through a 
 * {@link java.util.Properties <code>Properties</code>} object. For an exhaustive list of valid properties
 * to be used see <code>RemoteCacheManager</code>'s {@link org.infinispan.client.hotrod.RemoteCacheManager javadocs}.
 * This <code>FactoryBean</code> provides means to either {@link #setConfigurationProperties(Properties) inject}
 * a user-defined <code>Properties</code> instance or to {@link #setConfigurationPropertiesFileLocation(Resource) set}
 * the location of a properties file to load those properties from. Note that it is <em>illegal</em> to use
 * both mechanisms simultaneously.<br/>
 * </p>
 * <p>
 * Alternatively or in combination with {@link #setConfigurationPropertiesFileLocation(Resource) setting} 
 * the location of a <code>Properties</code> file to load the configuration from, this <code>FactoryBean</code>
 * provides (typed) setters for all configuration settings. Settings thus defined take precedence over those
 * defined in the injected <code>Properties</code> instance. This flexibility enables users to use e.g. a
 * company-wide <code>Properties</code> file containing default settings while simultaneously overriding
 * select settings whenever special requirements warrant this.<br/>
 * Note that it is illegal to use setters in conjunction with {@link #setConfigurationProperties(Properties) injecting}
 * a <code>Properties</code> instance.
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

	private final ConfigurationPropertiesOverrides configurationPropertiesOverrides = new ConfigurationPropertiesOverrides();

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
		} else if ((this.configurationProperties != null) && !this.configurationPropertiesOverrides.isEmpty()) {
			throw new IllegalStateException(
					"You may only use either \"configurationProperties\" or setters on this FactoryBean "
							+ "to configure the RemoteCacheManager, not both.");
		}
	}

	private Properties configurationProperties() throws IOException {
		final Properties answer;
		if (this.configurationProperties != null) {
			answer = this.configurationPropertiesOverrides.override(this.configurationProperties);
			this.logger.debug("Using user-defined properties [" + this.configurationProperties
					+ "] for configuring RemoteCacheManager");
		} else if (this.configurationPropertiesFileLocation != null) {
			answer = loadPropertiesFromFile(this.configurationPropertiesFileLocation);
			this.logger.debug("Loading properties from file [" + this.configurationProperties
					+ "] for configuring RemoteCacheManager");
		} else if (!this.configurationPropertiesOverrides.isEmpty()) {
			answer = this.configurationPropertiesOverrides.override(new Properties());
			this.logger.debug("Using explicitly set configuration settings [" + answer
					+ "] for configuring RemoteCacheManager");
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

	/**
	 * @param TransportFactory
	 * @see org.infinispan.spring.ConfigurationPropertiesOverrides#setTransportFactory(java.lang.String)
	 */
	public void setTransportFactory(final String TransportFactory) {
		this.configurationPropertiesOverrides.setTransportFactory(TransportFactory);
	}

	/**
	 * @param serverList
	 * @see org.infinispan.spring.ConfigurationPropertiesOverrides#setServerList(java.util.Collection)
	 */
	public void setServerList(final Collection<InetSocketAddress> serverList) {
		this.configurationPropertiesOverrides.setServerList(serverList);
	}

	/**
	 * @param marshaller
	 * @see org.infinispan.spring.ConfigurationPropertiesOverrides#setMarshaller(java.lang.String)
	 */
	public void setMarshaller(final String marshaller) {
		this.configurationPropertiesOverrides.setMarshaller(marshaller);
	}

	/**
	 * @param asyncExecutorFactory
	 * @see org.infinispan.spring.ConfigurationPropertiesOverrides#setAsyncExecutorFactory(java.lang.String)
	 */
	public void setAsyncExecutorFactory(final String asyncExecutorFactory) {
		this.configurationPropertiesOverrides.setAsyncExecutorFactory(asyncExecutorFactory);
	}

	/**
	 * @param tcpNoDelay
	 * @see org.infinispan.spring.ConfigurationPropertiesOverrides#setTcpNoDelay(boolean)
	 */
	public void setTcpNoDelay(final boolean tcpNoDelay) {
		this.configurationPropertiesOverrides.setTcpNoDelay(tcpNoDelay);
	}

	/**
	 * @param pingOnStartup
	 * @see org.infinispan.spring.ConfigurationPropertiesOverrides#setPingOnStartup(boolean)
	 */
	public void setPingOnStartup(final boolean pingOnStartup) {
		this.configurationPropertiesOverrides.setPingOnStartup(pingOnStartup);
	}

	/**
	 * @param requestBalancingStrategy
	 * @see org.infinispan.spring.ConfigurationPropertiesOverrides#setRequestBalancingStrategy(java.lang.String)
	 */
	public void setRequestBalancingStrategy(final String requestBalancingStrategy) {
		this.configurationPropertiesOverrides.setRequestBalancingStrategy(requestBalancingStrategy);
	}

	/**
	 * @param keySizeEstimate
	 * @see org.infinispan.spring.ConfigurationPropertiesOverrides#setKeySizeEstimate(int)
	 */
	public void setKeySizeEstimate(final int keySizeEstimate) {
		this.configurationPropertiesOverrides.setKeySizeEstimate(keySizeEstimate);
	}

	/**
	 * @param valueSizeEstimate
	 * @see org.infinispan.spring.ConfigurationPropertiesOverrides#setValueSizeEstimate(int)
	 */
	public void setValueSizeEstimate(final int valueSizeEstimate) {
		this.configurationPropertiesOverrides.setValueSizeEstimate(valueSizeEstimate);
	}

	/**
	 * @param forceReturnValues
	 * @see org.infinispan.spring.ConfigurationPropertiesOverrides#setForceReturnValues(boolean)
	 */
	public void setForceReturnValues(final boolean forceReturnValues) {
		this.configurationPropertiesOverrides.setForceReturnValues(forceReturnValues);
	}
}
