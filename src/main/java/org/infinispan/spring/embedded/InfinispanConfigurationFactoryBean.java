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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.config.CacheLoaderManagerConfig;
import org.infinispan.config.Configuration;
import org.infinispan.config.ConfigurationException;
import org.infinispan.config.CustomInterceptorConfig;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.config.InfinispanConfiguration;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.eviction.EvictionThreadPolicy;
import org.infinispan.transaction.lookup.TransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * <p>
 * A {@link org.springframework.beans.factory.FactoryBean <code>FactoryBean</code>} for creating INFINISPAN
 * {@link org.infinispan.config.Configuration <code>Configurations</code>}. A <code>Configuration</code> may
 * be defined in two <em>mutually exclusive</em> ways:
 * <ol>
 * <li>
 * If the property {@link #setConfigurationFileLocation(Resource) <code>configurationFileLocation</code>} is
 * set to a non-null value, this factory bean will attempt to load the INFINISPAN configuration to create from
 * that configuration file.
 * </li>
 * <li>
 * Otherwise, <code>InfinispanConfigurationFactoryBean</code> offers setters for explicitly defining the
 * INFINISPAN configuration to create directly within the application context.
 * </li>
 * </ol>
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanConfigurationFactoryBean implements FactoryBean<Configuration>, InitializingBean {

	private final Log logger = LogFactory.getLog(getClass());

	private Resource configurationFileLocation;

	private final Configuration infinispanConfiguration = new Configuration();

	// ------------------------------------------------------------------------
	// org.springframework.beans.factory.InitializingBean
	// ------------------------------------------------------------------------

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.configurationFileLocation != null) {
			this.logger.info("Loading INFINISPAN configuration from configuration file located at ["
					+ this.configurationFileLocation + "]");
			final Configuration loadedConfiguration = loadConfigurationFromFile(this.configurationFileLocation);
			this.infinispanConfiguration.applyOverrides(loadedConfiguration);
			this.logger.info("Finished loading INFINISPAN configuration from configuration file ["
					+ this.configurationFileLocation + "]");
		} else {
			this.logger
					.info("No configuration file location has been set. Creating INFINISPAN configuration using explicitly set properties.");
		}
		this.infinispanConfiguration.assertValid();
	}

	private Configuration loadConfigurationFromFile(final Resource configFileLocation) throws ConfigurationException,
			IOException {
		final InputStream configFileInputStream = configFileLocation.getInputStream();
		try {
			return InfinispanConfiguration.newInfinispanConfiguration(configFileInputStream)
					.parseDefaultConfiguration();
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
	public Configuration getObject() throws Exception {
		return this.infinispanConfiguration;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<? extends Configuration> getObjectType() {
		return this.infinispanConfiguration != null ? this.infinispanConfiguration.getClass() : Configuration.class;
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
	 * file to load our <code>Configuration</code> from. If this property is set to a non-null value,
	 * this <code>FactoryBean</code> will attempt to load the <code>Configuration</code> to create
	 * from a configuration file, ignoring any configuration properties that might have been set on
	 * this factory.
	 * 
	 * @param configurationFileLocation The location of the configuration file to load the INFINISPAN
	 *                                  configuration from
	 */
	public void setConfigurationFileLocation(final Resource configurationFileLocation) {
		this.configurationFileLocation = configurationFileLocation;
	}

	// ------------------------------------------------------------------------
	// Setters for Configuration properties
	// ------------------------------------------------------------------------

	/**
	 * @param gc
	 * @see org.infinispan.config.Configuration#setGlobalConfiguration(org.infinispan.config.GlobalConfiguration)
	 */
	public void setGlobalConfiguration(final GlobalConfiguration gc) {
		this.infinispanConfiguration.setGlobalConfiguration(gc);
	}

	/**
	 * @param eagerDeadlockSpinDuration
	 * @see org.infinispan.config.Configuration#setDeadlockDetectionSpinDuration(long)
	 */
	public void setDeadlockDetectionSpinDuration(final long eagerDeadlockSpinDuration) {
		this.infinispanConfiguration.setDeadlockDetectionSpinDuration(eagerDeadlockSpinDuration);
	}

	/**
	 * @param useEagerDeadlockDetection
	 * @see org.infinispan.config.Configuration#setEnableDeadlockDetection(boolean)
	 */
	public void setEnableDeadlockDetection(final boolean useEagerDeadlockDetection) {
		this.infinispanConfiguration.setEnableDeadlockDetection(useEagerDeadlockDetection);
	}

	/**
	 * @param useLockStriping
	 * @see org.infinispan.config.Configuration#setUseLockStriping(boolean)
	 */
	public void setUseLockStriping(final boolean useLockStriping) {
		this.infinispanConfiguration.setUseLockStriping(useLockStriping);
	}

	/**
	 * @param unsafeUnreliableReturnValues
	 * @see org.infinispan.config.Configuration#setUnsafeUnreliableReturnValues(boolean)
	 */
	public void setUnsafeUnreliableReturnValues(final boolean unsafeUnreliableReturnValues) {
		this.infinispanConfiguration.setUnsafeUnreliableReturnValues(unsafeUnreliableReturnValues);
	}

	/**
	 * @param rehashRpcTimeout
	 * @see org.infinispan.config.Configuration#setRehashRpcTimeout(long)
	 */
	public void setRehashRpcTimeout(final long rehashRpcTimeout) {
		this.infinispanConfiguration.setRehashRpcTimeout(rehashRpcTimeout);
	}

	/**
	 * @param writeSkewCheck
	 * @see org.infinispan.config.Configuration#setWriteSkewCheck(boolean)
	 */
	public void setWriteSkewCheck(final boolean writeSkewCheck) {
		this.infinispanConfiguration.setWriteSkewCheck(writeSkewCheck);
	}

	/**
	 * @param concurrencyLevel
	 * @see org.infinispan.config.Configuration#setConcurrencyLevel(int)
	 */
	public void setConcurrencyLevel(final int concurrencyLevel) {
		this.infinispanConfiguration.setConcurrencyLevel(concurrencyLevel);
	}

	/**
	 * @param replQueueMaxElements
	 * @see org.infinispan.config.Configuration#setReplQueueMaxElements(int)
	 */
	public void setReplQueueMaxElements(final int replQueueMaxElements) {
		this.infinispanConfiguration.setReplQueueMaxElements(replQueueMaxElements);
	}

	/**
	 * @param replQueueInterval
	 * @see org.infinispan.config.Configuration#setReplQueueInterval(long)
	 */
	public void setReplQueueInterval(final long replQueueInterval) {
		this.infinispanConfiguration.setReplQueueInterval(replQueueInterval);
	}

	/**
	 * @param classname
	 * @see org.infinispan.config.Configuration#setReplQueueClass(java.lang.String)
	 */
	public void setReplQueueClass(final String classname) {
		this.infinispanConfiguration.setReplQueueClass(classname);
	}

	/**
	 * @param useMbean
	 * @see org.infinispan.config.Configuration#setExposeJmxStatistics(boolean)
	 */
	public void setExposeJmxStatistics(final boolean useMbean) {
		this.infinispanConfiguration.setExposeJmxStatistics(useMbean);
	}

	/**
	 * @param enabled
	 * @see org.infinispan.config.Configuration#setInvocationBatchingEnabled(boolean)
	 */
	public void setInvocationBatchingEnabled(final boolean enabled) {
		this.infinispanConfiguration.setInvocationBatchingEnabled(enabled);
	}

	/**
	 * @param fetchInMemoryState
	 * @see org.infinispan.config.Configuration#setFetchInMemoryState(boolean)
	 */
	public void setFetchInMemoryState(final boolean fetchInMemoryState) {
		this.infinispanConfiguration.setFetchInMemoryState(fetchInMemoryState);
	}

	/**
	 * @param alwaysProvideInMemoryState
	 * @see org.infinispan.config.Configuration#setAlwaysProvideInMemoryState(boolean)
	 */
	public void setAlwaysProvideInMemoryState(final boolean alwaysProvideInMemoryState) {
		this.infinispanConfiguration.setAlwaysProvideInMemoryState(alwaysProvideInMemoryState);
	}

	/**
	 * @param lockAcquisitionTimeout
	 * @see org.infinispan.config.Configuration#setLockAcquisitionTimeout(long)
	 */
	public void setLockAcquisitionTimeout(final long lockAcquisitionTimeout) {
		this.infinispanConfiguration.setLockAcquisitionTimeout(lockAcquisitionTimeout);
	}

	/**
	 * @param syncReplTimeout
	 * @see org.infinispan.config.Configuration#setSyncReplTimeout(long)
	 */
	public void setSyncReplTimeout(final long syncReplTimeout) {
		this.infinispanConfiguration.setSyncReplTimeout(syncReplTimeout);
	}

	/**
	 * @param cacheMode
	 * @see org.infinispan.config.Configuration#setCacheMode(java.lang.String)
	 */
	public void setCacheMode(final String cacheMode) {
		this.infinispanConfiguration.setCacheMode(cacheMode);
	}

	/**
	 * @param cacheMode
	 * @see org.infinispan.config.Configuration#setCacheModeString(java.lang.String)
	 */
	public void setCacheModeString(final String cacheMode) {
		this.infinispanConfiguration.setCacheModeString(cacheMode);
	}

	/**
	 * @param evictionWakeUpInterval
	 * @see org.infinispan.config.Configuration#setEvictionWakeUpInterval(long)
	 */
	public void setEvictionWakeUpInterval(final long evictionWakeUpInterval) {
		this.infinispanConfiguration.setEvictionWakeUpInterval(evictionWakeUpInterval);
	}

	/**
	 * @param evictionStrategy
	 * @see org.infinispan.config.Configuration#setEvictionStrategy(org.infinispan.eviction.EvictionStrategy)
	 */
	public void setEvictionStrategy(final EvictionStrategy evictionStrategy) {
		this.infinispanConfiguration.setEvictionStrategy(evictionStrategy);
	}

	/**
	 * @param eStrategy
	 * @see org.infinispan.config.Configuration#setEvictionStrategy(java.lang.String)
	 */
	public void setEvictionStrategyClass(final String eStrategy) {
		this.infinispanConfiguration.setEvictionStrategy(eStrategy);
	}

	/**
	 * @param policy
	 * @see org.infinispan.config.Configuration#setEvictionThreadPolicy(org.infinispan.eviction.EvictionThreadPolicy)
	 */
	public void setEvictionThreadPolicy(final EvictionThreadPolicy policy) {
		this.infinispanConfiguration.setEvictionThreadPolicy(policy);
	}

	/**
	 * @param policy
	 * @see org.infinispan.config.Configuration#setEvictionThreadPolicy(java.lang.String)
	 */
	public void setEvictionThreadPolicyClass(final String policy) {
		this.infinispanConfiguration.setEvictionThreadPolicy(policy);
	}

	/**
	 * @param evictionMaxEntries
	 * @see org.infinispan.config.Configuration#setEvictionMaxEntries(int)
	 */
	public void setEvictionMaxEntries(final int evictionMaxEntries) {
		this.infinispanConfiguration.setEvictionMaxEntries(evictionMaxEntries);
	}

	/**
	 * @param expirationLifespan
	 * @see org.infinispan.config.Configuration#setExpirationLifespan(long)
	 */
	public void setExpirationLifespan(final long expirationLifespan) {
		this.infinispanConfiguration.setExpirationLifespan(expirationLifespan);
	}

	/**
	 * @param expirationMaxIdle
	 * @see org.infinispan.config.Configuration#setExpirationMaxIdle(long)
	 */
	public void setExpirationMaxIdle(final long expirationMaxIdle) {
		this.infinispanConfiguration.setExpirationMaxIdle(expirationMaxIdle);
	}

	/**
	 * @param transactionManagerLookupClass
	 * @see org.infinispan.config.Configuration#setTransactionManagerLookupClass(java.lang.String)
	 */
	public void setTransactionManagerLookupClass(final String transactionManagerLookupClass) {
		this.infinispanConfiguration.setTransactionManagerLookupClass(transactionManagerLookupClass);
	}

	/**
	 * @param transactionManagerLookup
	 * @see org.infinispan.config.Configuration#setTransactionManagerLookup(org.infinispan.transaction.lookup.TransactionManagerLookup)
	 */
	public void setTransactionManagerLookup(final TransactionManagerLookup transactionManagerLookup) {
		this.infinispanConfiguration.setTransactionManagerLookup(transactionManagerLookup);
	}

	/**
	 * @param cacheLoaderManagerConfig
	 * @see org.infinispan.config.Configuration#setCacheLoaderManagerConfig(org.infinispan.config.CacheLoaderManagerConfig)
	 */
	public void setCacheLoaderManagerConfig(final CacheLoaderManagerConfig cacheLoaderManagerConfig) {
		this.infinispanConfiguration.setCacheLoaderManagerConfig(cacheLoaderManagerConfig);
	}

	/**
	 * @param syncCommitPhase
	 * @see org.infinispan.config.Configuration#setSyncCommitPhase(boolean)
	 */
	public void setSyncCommitPhase(final boolean syncCommitPhase) {
		this.infinispanConfiguration.setSyncCommitPhase(syncCommitPhase);
	}

	/**
	 * @param syncRollbackPhase
	 * @see org.infinispan.config.Configuration#setSyncRollbackPhase(boolean)
	 */
	public void setSyncRollbackPhase(final boolean syncRollbackPhase) {
		this.infinispanConfiguration.setSyncRollbackPhase(syncRollbackPhase);
	}

	/**
	 * @param useEagerLocking
	 * @see org.infinispan.config.Configuration#setUseEagerLocking(boolean)
	 */
	public void setUseEagerLocking(final boolean useEagerLocking) {
		this.infinispanConfiguration.setUseEagerLocking(useEagerLocking);
	}

	/**
	 * @param eagerLockSingleNode
	 * @see org.infinispan.config.Configuration#setEagerLockSingleNode(boolean)
	 */
	public void setEagerLockSingleNode(final boolean eagerLockSingleNode) {
		this.infinispanConfiguration.setEagerLockSingleNode(eagerLockSingleNode);
	}

	/**
	 * @param useReplQueue
	 * @see org.infinispan.config.Configuration#setUseReplQueue(boolean)
	 */
	public void setUseReplQueue(final boolean useReplQueue) {
		this.infinispanConfiguration.setUseReplQueue(useReplQueue);
	}

	/**
	 * @param isolationLevel
	 * @see org.infinispan.config.Configuration#setIsolationLevel(org.infinispan.util.concurrent.IsolationLevel)
	 */
	public void setIsolationLevel(final IsolationLevel isolationLevel) {
		this.infinispanConfiguration.setIsolationLevel(isolationLevel);
	}

	/**
	 * @param stateRetrievalTimeout
	 * @see org.infinispan.config.Configuration#setStateRetrievalTimeout(long)
	 */
	public void setStateRetrievalTimeout(final long stateRetrievalTimeout) {
		this.infinispanConfiguration.setStateRetrievalTimeout(stateRetrievalTimeout);
	}

	/**
	 * @param logFlushTimeout
	 * @see org.infinispan.config.Configuration#setStateRetrievalLogFlushTimeout(long)
	 */
	public void setStateRetrievalLogFlushTimeout(final long logFlushTimeout) {
		this.infinispanConfiguration.setStateRetrievalLogFlushTimeout(logFlushTimeout);
	}

	/**
	 * @param maxNonProgressingLogWrites
	 * @see org.infinispan.config.Configuration#setStateRetrievalMaxNonProgressingLogWrites(int)
	 */
	public void setStateRetrievalMaxNonProgressingLogWrites(final int maxNonProgressingLogWrites) {
		this.infinispanConfiguration.setStateRetrievalMaxNonProgressingLogWrites(maxNonProgressingLogWrites);
	}

	/**
	 * @param initialRetryWaitTime
	 * @see org.infinispan.config.Configuration#setStateRetrievalInitialRetryWaitTime(long)
	 */
	public void setStateRetrievalInitialRetryWaitTime(final long initialRetryWaitTime) {
		this.infinispanConfiguration.setStateRetrievalInitialRetryWaitTime(initialRetryWaitTime);
	}

	/**
	 * @param retryWaitTimeIncreaseFactor
	 * @see org.infinispan.config.Configuration#setStateRetrievalRetryWaitTimeIncreaseFactor(int)
	 */
	public void setStateRetrievalRetryWaitTimeIncreaseFactor(final int retryWaitTimeIncreaseFactor) {
		this.infinispanConfiguration.setStateRetrievalRetryWaitTimeIncreaseFactor(retryWaitTimeIncreaseFactor);
	}

	/**
	 * @param numRetries
	 * @see org.infinispan.config.Configuration#setStateRetrievalNumRetries(int)
	 */
	public void setStateRetrievalNumRetries(final int numRetries) {
		this.infinispanConfiguration.setStateRetrievalNumRetries(numRetries);
	}

	/**
	 * @param isolationLevel
	 * @see org.infinispan.config.Configuration#setIsolationLevel(java.lang.String)
	 */
	public void setIsolationLevelClass(final String isolationLevel) {
		this.infinispanConfiguration.setIsolationLevel(isolationLevel);
	}

	/**
	 * @param useLazyDeserialization
	 * @see org.infinispan.config.Configuration#setUseLazyDeserialization(boolean)
	 */
	public void setUseLazyDeserialization(final boolean useLazyDeserialization) {
		this.infinispanConfiguration.setUseLazyDeserialization(useLazyDeserialization);
	}

	/**
	 * @param l1CacheEnabled
	 * @see org.infinispan.config.Configuration#setL1CacheEnabled(boolean)
	 */
	public void setL1CacheEnabled(final boolean l1CacheEnabled) {
		this.infinispanConfiguration.setL1CacheEnabled(l1CacheEnabled);
	}

	/**
	 * @param l1Lifespan
	 * @see org.infinispan.config.Configuration#setL1Lifespan(long)
	 */
	public void setL1Lifespan(final long l1Lifespan) {
		this.infinispanConfiguration.setL1Lifespan(l1Lifespan);
	}

	/**
	 * @param l1OnRehash
	 * @see org.infinispan.config.Configuration#setL1OnRehash(boolean)
	 */
	public void setL1OnRehash(final boolean l1OnRehash) {
		this.infinispanConfiguration.setL1OnRehash(l1OnRehash);
	}

	/**
	 * @param consistentHashClass
	 * @see org.infinispan.config.Configuration#setConsistentHashClass(java.lang.String)
	 */
	public void setConsistentHashClass(final String consistentHashClass) {
		this.infinispanConfiguration.setConsistentHashClass(consistentHashClass);
	}

	/**
	 * @param numOwners
	 * @see org.infinispan.config.Configuration#setNumOwners(int)
	 */
	public void setNumOwners(final int numOwners) {
		this.infinispanConfiguration.setNumOwners(numOwners);
	}

	/**
	 * @param rehashEnabled
	 * @see org.infinispan.config.Configuration#setRehashEnabled(boolean)
	 */
	public void setRehashEnabled(final boolean rehashEnabled) {
		this.infinispanConfiguration.setRehashEnabled(rehashEnabled);
	}

	/**
	 * @param rehashWaitTime
	 * @see org.infinispan.config.Configuration#setRehashWaitTime(long)
	 */
	public void setRehashWaitTime(final long rehashWaitTime) {
		this.infinispanConfiguration.setRehashWaitTime(rehashWaitTime);
	}

	/**
	 * @param useAsyncMarshalling
	 * @see org.infinispan.config.Configuration#setUseAsyncMarshalling(boolean)
	 */
	public void setUseAsyncMarshalling(final boolean useAsyncMarshalling) {
		this.infinispanConfiguration.setUseAsyncMarshalling(useAsyncMarshalling);
	}

	/**
	 * @param enabled
	 * @see org.infinispan.config.Configuration#setIndexingEnabled(boolean)
	 */
	public void setIndexingEnabled(final boolean enabled) {
		this.infinispanConfiguration.setIndexingEnabled(enabled);
	}

	/**
	 * @param indexLocalOnly
	 * @see org.infinispan.config.Configuration#setIndexLocalOnly(boolean)
	 */
	public void setIndexLocalOnly(final boolean indexLocalOnly) {
		this.infinispanConfiguration.setIndexLocalOnly(indexLocalOnly);
	}

	/**
	 * @param customInterceptors
	 * @see org.infinispan.config.Configuration#setCustomInterceptors(java.util.List)
	 */
	public void setCustomInterceptors(final List<CustomInterceptorConfig> customInterceptors) {
		this.infinispanConfiguration.setCustomInterceptors(customInterceptors);
	}

}
