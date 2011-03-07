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
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.config.ConfigurationException;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.config.InfinispanConfiguration;
import org.infinispan.jmx.MBeanServerLookup;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * <p>
 * A {@link org.springframework.beans.factory.FactoryBean <code>FactoryBean</code>} for creating INFINISPAN
 * {@link org.infinispan.config.GlobalConfiguration <code>GlobalConfigurations</code>}. A <code>GlobalConfiguration</code>
 * may be defined in two <em>mutually exclusive</em> ways:
 * <ol>
 * <li>
 * If the property {@link #setConfigurationFileLocation(Resource) <code>configurationFileLocation</code>} is
 * set to a non-null value, this factory bean will attempt to load the INFINISPAN GlobalConfiguration to create
 * from that configuration file.
 * </li>
 * <li>
 * Otherwise, <code>InfinispanConfigurationFactoryBean</code> offers setters for explicitly defining the
 * INFINISPAN GlobalConfiguration to create directly within the application context.
 * </li>
 * </ol>
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 * @see org.infinispan.config.GlobalConfiguration
 */
public class InfinispanGlobalConfigurationFactoryBean implements FactoryBean<GlobalConfiguration>, InitializingBean {

	private final Log logger = LogFactory.getLog(getClass());

	private Resource configurationFileLocation;

	private GlobalConfiguration globalConfiguration = new GlobalConfiguration();

	// ------------------------------------------------------------------------
	// org.springframework.beans.factory.InitializingBean
	// ------------------------------------------------------------------------

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.configurationFileLocation != null) {
			this.logger.info("Loading global INFINISPAN configuration from configuration file located at ["
					+ this.configurationFileLocation + "]");
			this.globalConfiguration = loadConfigurationFromFile(this.configurationFileLocation);
			this.logger.info("Finished loading global INFINISPAN configuration from configuration file ["
					+ this.configurationFileLocation + "]");
		} else {
			this.logger.info("No configuration file location has been set. Creating INFINISPAN "
					+ "configuration using explicitly set properties.");
		}
	}

	private GlobalConfiguration loadConfigurationFromFile(final Resource configFileLocation)
			throws ConfigurationException, IOException {
		final InputStream configFileInputStream = configFileLocation.getInputStream();
		try {
			final InfinispanConfiguration infinispanConfiguration = InfinispanConfiguration
					.newInfinispanConfiguration(configFileInputStream);

			return infinispanConfiguration.parseGlobalConfiguration();
		} finally {
			configFileInputStream.close();
		}
	}

	// ------------------------------------------------------------------------
	// org.springframework.beans.factory.FactoryBean
	// ------------------------------------------------------------------------

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public GlobalConfiguration getObject() throws Exception {
		return this.globalConfiguration;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<? extends GlobalConfiguration> getObjectType() {
		return this.globalConfiguration.getClass();
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
	// Setter for location of configuration file
	// ------------------------------------------------------------------------

	/**
	 * Set the {@link org.springframework.core.io.Resource location} of the INFINISPAN configuration
	 * file to load our <code>GlobalConfiguration</code> from. If this property is set to a non-null value,
	 * this <code>FactoryBean</code> will attempt to load the <code>GlobalConfiguration</code> to create
	 * from a configuration file, ignoring any configuration properties that might have been set on
	 * this factory.
	 * 
	 * @param configurationFileLocation The location of the configuration file to load the global INFINISPAN
	 *                                  configuration from
	 */
	public void setConfigurationFileLocation(final Resource configurationFileLocation) {
		this.configurationFileLocation = configurationFileLocation;
	}

	// ------------------------------------------------------------------------
	// Setters for GlobalConfiguration properties
	// ------------------------------------------------------------------------

	/**
	 * @param exposeGlobalJmxStatistics
	 * @see org.infinispan.config.GlobalConfiguration#setExposeGlobalJmxStatistics(boolean)
	 */
	public void setExposeGlobalJmxStatistics(final boolean exposeGlobalJmxStatistics) {
		this.globalConfiguration.setExposeGlobalJmxStatistics(exposeGlobalJmxStatistics);
	}

	/**
	 * @param jmxObjectName
	 * @see org.infinispan.config.GlobalConfiguration#setJmxDomain(java.lang.String)
	 */
	public void setJmxDomain(final String jmxObjectName) {
		this.globalConfiguration.setJmxDomain(jmxObjectName);
	}

	/**
	 * @param properties
	 * @see org.infinispan.config.GlobalConfiguration#setMBeanServerProperties(java.util.Properties)
	 */
	public void setMBeanServerProperties(final Properties properties) {
		this.globalConfiguration.setMBeanServerProperties(properties);
	}

	/**
	 * @param mBeanServerLookupClass
	 * @see org.infinispan.config.GlobalConfiguration#setMBeanServerLookup(java.lang.String)
	 */
	public void setMBeanServerLookupClass(final String mBeanServerLookupClass) {
		this.globalConfiguration.setMBeanServerLookup(mBeanServerLookupClass);
	}

	/**
	 * @param mBeanServerLookup
	 * @see org.infinispan.config.GlobalConfiguration#setMBeanServerLookup(org.infinispan.jmx.MBeanServerLookup)
	 */
	public void setMBeanServerLookup(final MBeanServerLookup mBeanServerLookup) {
		this.globalConfiguration.setMBeanServerLookup(mBeanServerLookup);
	}

	/**
	 * @param allowDuplicateDomains
	 * @see org.infinispan.config.GlobalConfiguration#setAllowDuplicateDomains(boolean)
	 */
	public void setAllowDuplicateDomains(final boolean allowDuplicateDomains) {
		this.globalConfiguration.setAllowDuplicateDomains(allowDuplicateDomains);
	}

	/**
	 * @param cacheManagerName
	 * @see org.infinispan.config.GlobalConfiguration#setCacheManagerName(java.lang.String)
	 */
	public void setCacheManagerName(final String cacheManagerName) {
		this.globalConfiguration.setCacheManagerName(cacheManagerName);
	}

	/**
	 * @param strictPeerToPeer
	 * @see org.infinispan.config.GlobalConfiguration#setStrictPeerToPeer(boolean)
	 */
	public void setStrictPeerToPeer(final boolean strictPeerToPeer) {
		this.globalConfiguration.setStrictPeerToPeer(strictPeerToPeer);
	}

	/**
	 * @param asyncListenerExecutorFactoryClass
	 * @see org.infinispan.config.GlobalConfiguration#setAsyncListenerExecutorFactoryClass(java.lang.String)
	 */
	public void setAsyncListenerExecutorFactoryClass(final String asyncListenerExecutorFactoryClass) {
		this.globalConfiguration.setAsyncListenerExecutorFactoryClass(asyncListenerExecutorFactoryClass);
	}

	/**
	 * @param asyncTransportExecutorFactoryClass
	 * @see org.infinispan.config.GlobalConfiguration#setAsyncTransportExecutorFactoryClass(java.lang.String)
	 */
	public void setAsyncTransportExecutorFactoryClass(final String asyncTransportExecutorFactoryClass) {
		this.globalConfiguration.setAsyncTransportExecutorFactoryClass(asyncTransportExecutorFactoryClass);
	}

	/**
	 * @param evictionScheduledExecutorFactoryClass
	 * @see org.infinispan.config.GlobalConfiguration#setEvictionScheduledExecutorFactoryClass(java.lang.String)
	 */
	public void setEvictionScheduledExecutorFactoryClass(final String evictionScheduledExecutorFactoryClass) {
		this.globalConfiguration.setEvictionScheduledExecutorFactoryClass(evictionScheduledExecutorFactoryClass);
	}

	/**
	 * @param replicationQueueScheduledExecutorFactoryClass
	 * @see org.infinispan.config.GlobalConfiguration#setReplicationQueueScheduledExecutorFactoryClass(java.lang.String)
	 */
	public void setReplicationQueueScheduledExecutorFactoryClass(
			final String replicationQueueScheduledExecutorFactoryClass) {
		this.globalConfiguration
				.setReplicationQueueScheduledExecutorFactoryClass(replicationQueueScheduledExecutorFactoryClass);
	}

	/**
	 * @param marshallerClass
	 * @see org.infinispan.config.GlobalConfiguration#setMarshallerClass(java.lang.String)
	 */
	public void setMarshallerClass(final String marshallerClass) {
		this.globalConfiguration.setMarshallerClass(marshallerClass);
	}

	/**
	 * @param nodeName
	 * @see org.infinispan.config.GlobalConfiguration#setTransportNodeName(java.lang.String)
	 */
	public void setTransportNodeName(final String nodeName) {
		this.globalConfiguration.setTransportNodeName(nodeName);
	}

	/**
	 * @param transportClass
	 * @see org.infinispan.config.GlobalConfiguration#setTransportClass(java.lang.String)
	 */
	public void setTransportClass(final String transportClass) {
		this.globalConfiguration.setTransportClass(transportClass);
	}

	/**
	 * @param transportProperties
	 * @see org.infinispan.config.GlobalConfiguration#setTransportProperties(java.util.Properties)
	 */
	public void setTransportProperties(final Properties transportProperties) {
		this.globalConfiguration.setTransportProperties(transportProperties);
	}

	/**
	 * @param clusterName
	 * @see org.infinispan.config.GlobalConfiguration#setClusterName(java.lang.String)
	 */
	public void setClusterName(final String clusterName) {
		this.globalConfiguration.setClusterName(clusterName);
	}

	/**
	 * @param machineId
	 * @see org.infinispan.config.GlobalConfiguration#setMachineId(java.lang.String)
	 */
	public void setMachineId(final String machineId) {
		this.globalConfiguration.setMachineId(machineId);
	}

	/**
	 * @param rackId
	 * @see org.infinispan.config.GlobalConfiguration#setRackId(java.lang.String)
	 */
	public void setRackId(final String rackId) {
		this.globalConfiguration.setRackId(rackId);
	}

	/**
	 * @param siteId
	 * @see org.infinispan.config.GlobalConfiguration#setSiteId(java.lang.String)
	 */
	public void setSiteId(final String siteId) {
		this.globalConfiguration.setSiteId(siteId);
	}

	/**
	 * @param shutdownHookBehavior
	 * @see org.infinispan.config.GlobalConfiguration#setShutdownHookBehavior(java.lang.String)
	 */
	public void setShutdownHookBehavior(final String shutdownHookBehavior) {
		this.globalConfiguration.setShutdownHookBehavior(shutdownHookBehavior);
	}

	/**
	 * @param asyncListenerExecutorProperties
	 * @see org.infinispan.config.GlobalConfiguration#setAsyncListenerExecutorProperties(java.util.Properties)
	 */
	public void setAsyncListenerExecutorProperties(final Properties asyncListenerExecutorProperties) {
		this.globalConfiguration.setAsyncListenerExecutorProperties(asyncListenerExecutorProperties);
	}

	/**
	 * @param asyncTransportExecutorProperties
	 * @see org.infinispan.config.GlobalConfiguration#setAsyncTransportExecutorProperties(java.util.Properties)
	 */
	public void setAsyncTransportExecutorProperties(final Properties asyncTransportExecutorProperties) {
		this.globalConfiguration.setAsyncTransportExecutorProperties(asyncTransportExecutorProperties);
	}

	/**
	 * @param evictionScheduledExecutorProperties
	 * @see org.infinispan.config.GlobalConfiguration#setEvictionScheduledExecutorProperties(java.util.Properties)
	 */
	public void setEvictionScheduledExecutorProperties(final Properties evictionScheduledExecutorProperties) {
		this.globalConfiguration.setEvictionScheduledExecutorProperties(evictionScheduledExecutorProperties);
	}

	/**
	 * @param replicationQueueScheduledExecutorProperties
	 * @see org.infinispan.config.GlobalConfiguration#setReplicationQueueScheduledExecutorProperties(java.util.Properties)
	 */
	public void setReplicationQueueScheduledExecutorProperties(
			final Properties replicationQueueScheduledExecutorProperties) {
		this.globalConfiguration
				.setReplicationQueueScheduledExecutorProperties(replicationQueueScheduledExecutorProperties);
	}

	/**
	 * @param marshallVersion
	 * @see org.infinispan.config.GlobalConfiguration#setMarshallVersion(short)
	 */
	public void setMarshallVersion(final short marshallVersion) {
		this.globalConfiguration.setMarshallVersion(marshallVersion);
	}

	/**
	 * @param distributedSyncTimeout
	 * @see org.infinispan.config.GlobalConfiguration#setDistributedSyncTimeout(long)
	 */
	public void setDistributedSyncTimeout(final long distributedSyncTimeout) {
		this.globalConfiguration.setDistributedSyncTimeout(distributedSyncTimeout);
	}
}
