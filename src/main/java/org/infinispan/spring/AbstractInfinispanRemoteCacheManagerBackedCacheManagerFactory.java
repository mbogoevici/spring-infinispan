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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

/**
 * <p>
 * An abstract base class for factories creating cache manager that are backed by an INFINISPAN RemoteCacheManager.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 * 
 * @see org.infinispan.client.hotrod.RemoteCacheManager
 */
public abstract class AbstractInfinispanRemoteCacheManagerBackedCacheManagerFactory {

	protected final Log logger = LogFactory.getLog(getClass());

	protected boolean startAutomatically = true;

	private Properties configurationProperties;

	private Resource configurationPropertiesFileLocation;

	private final ConfigurationPropertiesOverrides configurationPropertiesOverrides = new ConfigurationPropertiesOverrides();

	protected void assertCorrectlyConfigured() throws IllegalStateException {
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

	protected Properties configurationProperties() throws IOException {
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
