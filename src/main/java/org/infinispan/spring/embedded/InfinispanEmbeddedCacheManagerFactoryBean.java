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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.config.Configuration;
import org.infinispan.config.ConfigurationException;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.config.InfinispanConfiguration;
import org.infinispan.jmx.MBeanServerLookup;
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
 * instance created by this <code>FactoryBean</code> is {@link #setConfigurationFileLocation(Resource) configurable}.
 * </p>
 * <p>
 * If no configuration file location is set the <code>EmbeddedCacheManager</code> instance created by this
 * <code>FactoryBean</code> will use INFINISPAN's default settings. See INFINISPAN's 
 * <a href="http://www.jboss.org/infinispan/docs">documentation</a> for what those default settings are.
 * </p>
 * <p>
 * A user may further customize the <code>EmbeddedCacheManager</code>'s configuration using explicit setters
 * on this <code>FactoryBean</code>. The properties thus defined will be applied either to the configuration
 * loaded from INFINISPAN's configuration file in case one has been specified, or to a configuration initialized
 * with INFINISPAN's default settings. Either way, the net effect is that explicitly set configuration
 * properties take precedence over both those loaded from a configuration file as well as INFNISPAN's
 * default settings.
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
 * @see #setConfigurationFileLocation(Resource)
 * @see #destroy()
 * @see org.infinispan.manager.EmbeddedCacheManager
 * @see org.infinispan.config.Configuration
 *
 */
public class InfinispanEmbeddedCacheManagerFactoryBean implements FactoryBean<EmbeddedCacheManager>, InitializingBean,
		DisposableBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private Resource configurationFileLocation;

	private EmbeddedCacheManager cacheManager;

	private final GlobalConfigurationOverrides globalConfigurationOverrides = new GlobalConfigurationOverrides();

	// ------------------------------------------------------------------------
	// org.springframework.beans.factory.InitializingBean
	// ------------------------------------------------------------------------

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.logger.info("Initializing INFINISPAN EmbeddedCacheManager instance ...");

		final ConfigurationContainer templateConfiguration = createTemplateConfiguration();

		this.globalConfigurationOverrides.applyOverridesTo(templateConfiguration.globalConfiguration);

		this.cacheManager = new DefaultCacheManager(templateConfiguration.globalConfiguration,
				templateConfiguration.defaultConfiguration);
		for (final Map.Entry<String, Configuration> namedCacheConfig : templateConfiguration.namedCaches.entrySet()) {
			this.cacheManager.defineConfiguration(namedCacheConfig.getKey(), namedCacheConfig.getValue());
		}

		this.logger.info("Successfully initialized INFINISPAN EmbeddedCacheManager instance [" + this.cacheManager
				+ "]");
	}

	private ConfigurationContainer createTemplateConfiguration() throws ConfigurationException, IOException {
		final ConfigurationContainer templateConfiguration;
		if (this.configurationFileLocation == null) {
			this.logger.info("No configuration file has been given. Using INFINISPAN's default settings.");
			final GlobalConfiguration standardGlobalConfiguration = new GlobalConfiguration();
			final Configuration standardDefaultConfiguration = new Configuration();
			templateConfiguration = new ConfigurationContainer(standardGlobalConfiguration,
					standardDefaultConfiguration, new HashMap<String, Configuration>());
		} else {
			this.logger.info("Using INFINISPAN configuration file located at [" + this.configurationFileLocation + "]");
			templateConfiguration = loadConfigurationFromFile(this.configurationFileLocation);
		}
		return templateConfiguration;
	}

	private ConfigurationContainer loadConfigurationFromFile(final Resource configFileLocation)
			throws ConfigurationException, IOException {
		final InputStream configFileInputStream = configFileLocation.getInputStream();
		try {
			final InfinispanConfiguration infinispanConfiguration = InfinispanConfiguration
					.newInfinispanConfiguration(configFileInputStream);

			return new ConfigurationContainer(infinispanConfiguration);
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

	// ------------------------------------------------------------------------
	// Setter for location of configuration file
	// ------------------------------------------------------------------------

	/**
	 * <p>
	 * Sets the {@link org.springframework.core.io.Resource <code>location</code>} of the configuration
	 * file which will be used to configure the {@link org.infinispan.manager.EmbeddedCacheManager <code>EmbeddedCacheManager</code>}
	 * created by this <code>FactoryBean</code>. If no location is supplied, <tt>INFINISPAN</tt>'s default configuration
	 * will be used. 
	 * </p>
	 * <p>
	 * Note that configuration settings defined via using explicit setters exposed by this <code>FactoryBean</code>
	 * take precedence over those defined in the configuration file pointed to by <code>configurationFileLocation</code>.
	 * </p>
	 * 
	 * @param configurationFileLocation 
	 * 			The {@link org.springframework.core.io.Resource <code>location</code>} of the configuration	file which 
	 * 			will be used to configure the {@link org.infinispan.manager.EmbeddedCacheManager <code>EmbeddedCacheManager</code>}
	 * 			created by this <code>FactoryBean</code>
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
		this.globalConfigurationOverrides.exposeGlobalJmxStatistics = exposeGlobalJmxStatistics;
	}

	/**
	 * @param jmxObjectName
	 * @see org.infinispan.config.GlobalConfiguration#setJmxDomain(java.lang.String)
	 */
	public void setJmxDomain(final String jmxObjectName) {
		this.globalConfigurationOverrides.jmxDomain = jmxObjectName;
	}

	/**
	 * @param properties
	 * @see org.infinispan.config.GlobalConfiguration#setMBeanServerProperties(java.util.Properties)
	 */
	public void setMBeanServerProperties(final Properties properties) {
		this.globalConfigurationOverrides.mBeanServerProperties = properties;
	}

	/**
	 * @param mBeanServerLookupClass
	 * @see org.infinispan.config.GlobalConfiguration#setMBeanServerLookup(java.lang.String)
	 */
	public void setMBeanServerLookupClass(final String mBeanServerLookupClass) {
		this.globalConfigurationOverrides.mBeanServerLookupClass = mBeanServerLookupClass;
	}

	/**
	 * @param mBeanServerLookup
	 * @see org.infinispan.config.GlobalConfiguration#setMBeanServerLookup(org.infinispan.jmx.MBeanServerLookup)
	 */
	public void setMBeanServerLookup(final MBeanServerLookup mBeanServerLookup) {
		this.globalConfigurationOverrides.mBeanServerLookup = mBeanServerLookup;
	}

	/**
	 * @param allowDuplicateDomains
	 * @see org.infinispan.config.GlobalConfiguration#setAllowDuplicateDomains(boolean)
	 */
	public void setAllowDuplicateDomains(final boolean allowDuplicateDomains) {
		this.globalConfigurationOverrides.allowDuplicateDomains = allowDuplicateDomains;
	}

	/**
	 * @param cacheManagerName
	 * @see org.infinispan.config.GlobalConfiguration#setCacheManagerName(java.lang.String)
	 */
	public void setCacheManagerName(final String cacheManagerName) {
		this.globalConfigurationOverrides.cacheManagerName = cacheManagerName;
	}

	/**
	 * @param strictPeerToPeer
	 * @see org.infinispan.config.GlobalConfiguration#setStrictPeerToPeer(boolean)
	 */
	public void setStrictPeerToPeer(final boolean strictPeerToPeer) {
		this.globalConfigurationOverrides.strictPeerToPeer = strictPeerToPeer;
	}

	/**
	 * @param asyncListenerExecutorFactoryClass
	 * @see org.infinispan.config.GlobalConfiguration#setAsyncListenerExecutorFactoryClass(java.lang.String)
	 */
	public void setAsyncListenerExecutorFactoryClass(final String asyncListenerExecutorFactoryClass) {
		this.globalConfigurationOverrides.asyncListenerExecutorFactoryClass = asyncListenerExecutorFactoryClass;
	}

	/**
	 * @param asyncTransportExecutorFactoryClass
	 * @see org.infinispan.config.GlobalConfiguration#setAsyncTransportExecutorFactoryClass(java.lang.String)
	 */
	public void setAsyncTransportExecutorFactoryClass(final String asyncTransportExecutorFactoryClass) {
		this.globalConfigurationOverrides.asyncTransportExecutorFactoryClass = asyncTransportExecutorFactoryClass;
	}

	/**
	 * @param evictionScheduledExecutorFactoryClass
	 * @see org.infinispan.config.GlobalConfiguration#setEvictionScheduledExecutorFactoryClass(java.lang.String)
	 */
	public void setEvictionScheduledExecutorFactoryClass(final String evictionScheduledExecutorFactoryClass) {
		this.globalConfigurationOverrides.evictionScheduledExecutorFactoryClass = evictionScheduledExecutorFactoryClass;
	}

	/**
	 * @param replicationQueueScheduledExecutorFactoryClass
	 * @see org.infinispan.config.GlobalConfiguration#setReplicationQueueScheduledExecutorFactoryClass(java.lang.String)
	 */
	public void setReplicationQueueScheduledExecutorFactoryClass(
			final String replicationQueueScheduledExecutorFactoryClass) {
		this.globalConfigurationOverrides.replicationQueueScheduledExecutorFactoryClass = replicationQueueScheduledExecutorFactoryClass;
	}

	/**
	 * @param marshallerClass
	 * @see org.infinispan.config.GlobalConfiguration#setMarshallerClass(java.lang.String)
	 */
	public void setMarshallerClass(final String marshallerClass) {
		this.globalConfigurationOverrides.marshallerClass = marshallerClass;
	}

	/**
	 * @param nodeName
	 * @see org.infinispan.config.GlobalConfiguration#setTransportNodeName(java.lang.String)
	 */
	public void setTransportNodeName(final String nodeName) {
		this.globalConfigurationOverrides.transportNodeName = nodeName;
	}

	/**
	 * @param transportClass
	 * @see org.infinispan.config.GlobalConfiguration#setTransportClass(java.lang.String)
	 */
	public void setTransportClass(final String transportClass) {
		this.globalConfigurationOverrides.transportClass = transportClass;
	}

	/**
	 * @param transportProperties
	 * @see org.infinispan.config.GlobalConfiguration#setTransportProperties(java.util.Properties)
	 */
	public void setTransportProperties(final Properties transportProperties) {
		this.globalConfigurationOverrides.transportProperties = transportProperties;
	}

	/**
	 * @param clusterName
	 * @see org.infinispan.config.GlobalConfiguration#setClusterName(java.lang.String)
	 */
	public void setClusterName(final String clusterName) {
		this.globalConfigurationOverrides.clusterName = clusterName;
	}

	/**
	 * @param machineId
	 * @see org.infinispan.config.GlobalConfiguration#setMachineId(java.lang.String)
	 */
	public void setMachineId(final String machineId) {
		this.globalConfigurationOverrides.machineId = machineId;
	}

	/**
	 * @param rackId
	 * @see org.infinispan.config.GlobalConfiguration#setRackId(java.lang.String)
	 */
	public void setRackId(final String rackId) {
		this.globalConfigurationOverrides.rackId = rackId;
	}

	/**
	 * @param siteId
	 * @see org.infinispan.config.GlobalConfiguration#setSiteId(java.lang.String)
	 */
	public void setSiteId(final String siteId) {
		this.globalConfigurationOverrides.siteId = siteId;
	}

	/**
	 * @param shutdownHookBehavior
	 * @see org.infinispan.config.GlobalConfiguration#setShutdownHookBehavior(java.lang.String)
	 */
	public void setShutdownHookBehavior(final String shutdownHookBehavior) {
		this.globalConfigurationOverrides.shutdownHookBehavior = shutdownHookBehavior;
	}

	/**
	 * @param asyncListenerExecutorProperties
	 * @see org.infinispan.config.GlobalConfiguration#setAsyncListenerExecutorProperties(java.util.Properties)
	 */
	public void setAsyncListenerExecutorProperties(final Properties asyncListenerExecutorProperties) {
		this.globalConfigurationOverrides.asyncListenerExecutorProperties = asyncListenerExecutorProperties;
	}

	/**
	 * @param asyncTransportExecutorProperties
	 * @see org.infinispan.config.GlobalConfiguration#setAsyncTransportExecutorProperties(java.util.Properties)
	 */
	public void setAsyncTransportExecutorProperties(final Properties asyncTransportExecutorProperties) {
		this.globalConfigurationOverrides.asyncTransportExecutorProperties = asyncTransportExecutorProperties;
	}

	/**
	 * @param evictionScheduledExecutorProperties
	 * @see org.infinispan.config.GlobalConfiguration#setEvictionScheduledExecutorProperties(java.util.Properties)
	 */
	public void setEvictionScheduledExecutorProperties(final Properties evictionScheduledExecutorProperties) {
		this.globalConfigurationOverrides.evictionScheduledExecutorProperties = evictionScheduledExecutorProperties;
	}

	/**
	 * @param replicationQueueScheduledExecutorProperties
	 * @see org.infinispan.config.GlobalConfiguration#setReplicationQueueScheduledExecutorProperties(java.util.Properties)
	 */
	public void setReplicationQueueScheduledExecutorProperties(
			final Properties replicationQueueScheduledExecutorProperties) {
		this.globalConfigurationOverrides.replicationQueueScheduledExecutorProperties = replicationQueueScheduledExecutorProperties;
	}

	/**
	 * @param marshallVersion
	 * @see org.infinispan.config.GlobalConfiguration#setMarshallVersion(short)
	 */
	public void setMarshallVersion(final short marshallVersion) {
		this.globalConfigurationOverrides.marshallVersion = marshallVersion;
	}

	/**
	 * @param distributedSyncTimeout
	 * @see org.infinispan.config.GlobalConfiguration#setDistributedSyncTimeout(long)
	 */
	public void setDistributedSyncTimeout(final long distributedSyncTimeout) {
		this.globalConfigurationOverrides.distributedSyncTimeout = distributedSyncTimeout;
	}

	// ------------------------------------------------------------------------
	// Helper classes
	// ------------------------------------------------------------------------

	private static final class ConfigurationContainer {

		final GlobalConfiguration globalConfiguration;

		final Configuration defaultConfiguration;

		final Map<String, Configuration> namedCaches;

		ConfigurationContainer(final GlobalConfiguration globalConfiguration, final Configuration defaultConfiguration,
				final Map<String, Configuration> namedCaches) {
			this.globalConfiguration = globalConfiguration;
			this.defaultConfiguration = defaultConfiguration;
			this.namedCaches = namedCaches;
		}

		ConfigurationContainer(final InfinispanConfiguration infinispanConfiguration) {
			this(infinispanConfiguration.parseGlobalConfiguration(), infinispanConfiguration
					.parseDefaultConfiguration(), infinispanConfiguration.parseNamedConfigurations());
		}
	}

	private static final class GlobalConfigurationOverrides {

		private final Log logger = LogFactory.getLog(getClass());

		private Boolean exposeGlobalJmxStatistics;

		private Properties mBeanServerProperties;

		private String jmxDomain;

		private String mBeanServerLookupClass;

		private MBeanServerLookup mBeanServerLookup;

		private Boolean allowDuplicateDomains;

		private String cacheManagerName;

		private String clusterName;

		private String machineId;

		private String rackId;

		private String siteId;

		private Boolean strictPeerToPeer;

		private Long distributedSyncTimeout;

		private String transportClass;

		private String transportNodeName;

		private String asyncListenerExecutorFactoryClass;

		private String asyncTransportExecutorFactoryClass;

		private String evictionScheduledExecutorFactoryClass;

		private String replicationQueueScheduledExecutorFactoryClass;

		private String marshallerClass;

		private Properties transportProperties;

		private String shutdownHookBehavior;

		private Properties asyncListenerExecutorProperties;

		private Properties asyncTransportExecutorProperties;

		private Properties evictionScheduledExecutorProperties;

		private Properties replicationQueueScheduledExecutorProperties;

		private Short marshallVersion;

		void applyOverridesTo(final GlobalConfiguration globalConfigurationToOverride) {
			this.logger.debug("Applying configuration overrides to GlobalConfiguration ["
					+ globalConfigurationToOverride + "] ...");

			if (this.exposeGlobalJmxStatistics != null) {
				this.logger.debug("Overriding property [exposeGlobalJmxStatistics] with new value ["
						+ this.exposeGlobalJmxStatistics + "]");
				globalConfigurationToOverride.setExposeGlobalJmxStatistics(this.exposeGlobalJmxStatistics);
			}
			if (this.mBeanServerProperties != null) {
				this.logger.debug("Overriding property [mBeanServerProperties] with new value ["
						+ this.mBeanServerProperties + "]");
				globalConfigurationToOverride.setMBeanServerProperties(this.mBeanServerProperties);
			}
			if (this.jmxDomain != null) {
				this.logger.debug("Overriding property [jmxDomain] with new value [" + this.jmxDomain + "]");
				globalConfigurationToOverride.setJmxDomain(this.jmxDomain);
			}
			if (this.mBeanServerLookupClass != null) {
				this.logger.debug("Overriding property [mBeanServerLookupClass] with new value ["
						+ this.mBeanServerLookupClass + "]");
				globalConfigurationToOverride.setMBeanServerLookup(this.mBeanServerLookupClass);
			}
			if (this.mBeanServerLookup != null) {
				this.logger.debug("Overriding property [mBeanServerLookup] with new value [" + this.mBeanServerLookup
						+ "]");
				globalConfigurationToOverride.setMBeanServerLookup(this.mBeanServerLookup);
			}
			if (this.allowDuplicateDomains != null) {
				this.logger.debug("Overriding property [allowDuplicateDomains] with new value ["
						+ this.allowDuplicateDomains + "]");
				globalConfigurationToOverride.setAllowDuplicateDomains(this.allowDuplicateDomains);
			}
			if (this.cacheManagerName != null) {
				this.logger.debug("Overriding property [cacheManagerName] with new value [" + this.cacheManagerName
						+ "]");
				globalConfigurationToOverride.setCacheManagerName(this.cacheManagerName);
			}
			if (this.clusterName != null) {
				this.logger.debug("Overriding property [clusterName] with new value [" + this.clusterName + "]");
				globalConfigurationToOverride.setClusterName(this.clusterName);
			}
			if (this.machineId != null) {
				this.logger.debug("Overriding property [machineId] with new value [" + this.machineId + "]");
				globalConfigurationToOverride.setMachineId(this.machineId);
			}
			if (this.rackId != null) {
				this.logger.debug("Overriding property [rackId] with new value [" + this.rackId + "]");
				globalConfigurationToOverride.setRackId(this.rackId);
			}
			if (this.siteId != null) {
				this.logger.debug("Overriding property [siteId] with new value [" + this.siteId + "]");
				globalConfigurationToOverride.setSiteId(this.siteId);
			}
			if (this.strictPeerToPeer != null) {
				this.logger.debug("Overriding property [strictPeerToPeer] with new value [" + this.strictPeerToPeer
						+ "]");
				globalConfigurationToOverride.setStrictPeerToPeer(this.strictPeerToPeer);
			}
			if (this.distributedSyncTimeout != null) {
				this.logger.debug("Overriding property [distributedSyncTimeout] with new value ["
						+ this.distributedSyncTimeout + "]");
				globalConfigurationToOverride.setDistributedSyncTimeout(this.distributedSyncTimeout);
			}
			if (this.transportClass != null) {
				this.logger.debug("Overriding property [transportClass] with new value [" + this.transportClass + "]");
				globalConfigurationToOverride.setTransportClass(this.transportClass);
			}
			if (this.transportNodeName != null) {
				this.logger.debug("Overriding property [transportNodeName] with new value [" + this.transportNodeName
						+ "]");
				globalConfigurationToOverride.setTransportNodeName(this.transportNodeName);
			}
			if (this.asyncListenerExecutorFactoryClass != null) {
				this.logger.debug("Overriding property [asyncListenerExecutorFactoryClass] with new value ["
						+ this.asyncListenerExecutorFactoryClass + "]");
				globalConfigurationToOverride
						.setAsyncListenerExecutorFactoryClass(this.asyncListenerExecutorFactoryClass);
			}
			if (this.asyncTransportExecutorFactoryClass != null) {
				this.logger.debug("Overriding property [asyncTransportExecutorFactoryClass] with new value ["
						+ this.asyncTransportExecutorFactoryClass + "]");
				globalConfigurationToOverride
						.setAsyncTransportExecutorFactoryClass(this.asyncTransportExecutorFactoryClass);
			}
			if (this.evictionScheduledExecutorFactoryClass != null) {
				this.logger.debug("Overriding property [evictionScheduledExecutorFactoryClass] with new value ["
						+ this.evictionScheduledExecutorFactoryClass + "]");
				globalConfigurationToOverride
						.setEvictionScheduledExecutorFactoryClass(this.evictionScheduledExecutorFactoryClass);
			}
			if (this.replicationQueueScheduledExecutorFactoryClass != null) {
				this.logger
						.debug("Overriding property [replicationQueueScheduledExecutorFactoryClass] with new value ["
								+ this.replicationQueueScheduledExecutorFactoryClass + "]");
				globalConfigurationToOverride
						.setReplicationQueueScheduledExecutorFactoryClass(this.replicationQueueScheduledExecutorFactoryClass);
			}
			if (this.marshallerClass != null) {
				this.logger
						.debug("Overriding property [marshallerClass] with new value [" + this.marshallerClass + "]");
				globalConfigurationToOverride.setMarshallerClass(this.marshallerClass);
			}
			if (this.transportProperties != null) {
				this.logger.debug("Overriding property [transportProperties] with new value ["
						+ this.transportProperties + "]");
				globalConfigurationToOverride.setTransportProperties(this.transportProperties);
			}
			if (this.shutdownHookBehavior != null) {
				this.logger.debug("Overriding property [shutdownHookBehavior] with new value ["
						+ this.shutdownHookBehavior + "]");
				globalConfigurationToOverride.setShutdownHookBehavior(this.shutdownHookBehavior);
			}
			if (this.asyncListenerExecutorProperties != null) {
				this.logger.debug("Overriding property [asyncListenerExecutorProperties] with new value ["
						+ this.asyncListenerExecutorProperties + "]");
				globalConfigurationToOverride.setAsyncListenerExecutorProperties(this.asyncListenerExecutorProperties);
			}
			if (this.asyncTransportExecutorProperties != null) {
				this.logger.debug("Overriding property [asyncTransportExecutorProperties] with new value ["
						+ this.asyncTransportExecutorProperties + "]");
				globalConfigurationToOverride
						.setAsyncTransportExecutorProperties(this.asyncTransportExecutorProperties);
			}
			if (this.evictionScheduledExecutorProperties != null) {
				this.logger.debug("Overriding property [evictionScheduledExecutorProperties] with new value ["
						+ this.evictionScheduledExecutorProperties + "]");
				globalConfigurationToOverride
						.setEvictionScheduledExecutorProperties(this.evictionScheduledExecutorProperties);
			}
			if (this.replicationQueueScheduledExecutorProperties != null) {
				this.logger.debug("Overriding property [replicationQueueScheduledExecutorProperties] with new value ["
						+ this.replicationQueueScheduledExecutorProperties + "]");
				globalConfigurationToOverride
						.setReplicationQueueScheduledExecutorProperties(this.replicationQueueScheduledExecutorProperties);
			}
			if (this.marshallVersion != null) {
				this.logger
						.debug("Overriding property [marshallVersion] with new value [" + this.marshallVersion + "]");
				globalConfigurationToOverride.setMarshallVersion(this.marshallVersion);
			}

			this.logger.debug("Finished applying configuration overrides to GlobalConfiguration ["
					+ globalConfigurationToOverride + "]");
		}
	}
}
