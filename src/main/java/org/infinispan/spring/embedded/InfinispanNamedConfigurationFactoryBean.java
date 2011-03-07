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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.config.CacheLoaderManagerConfig;
import org.infinispan.config.CustomInterceptorConfig;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.eviction.EvictionThreadPolicy;
import org.infinispan.transaction.lookup.TransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/**
 * <p>
 * A {@link org.springframework.beans.factory.FactoryBean <code>FactoryBean</code>} for creating an INFINISPAN 
 * configuration that is specific to a <em>named</em> INFINISPAN {@link org.infinispan.Cache <code>Cache</code>}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanNamedConfigurationFactoryBean implements FactoryBean<NamedConfiguration>, BeanNameAware,
		InitializingBean {

	private final Log logger = LogFactory.getLog(getClass());

	private final InfinispanConfigurationFactoryBean configurationFactory = new InfinispanConfigurationFactoryBean();

	private String cacheName;

	private String beanName;

	private NamedConfiguration namedConfiguration;

	/**
	 * Sets the name of the {@link org.infinispan.Cache <code>Cache</code>} to create a
	 * {@link org.infinispan.config.Configuration <code>Configuration</code>} for. If this property is not
	 * set <code>InfinispanNamedConfigurationFactoryBean</code> will fall back to using its 
	 * {@link #setBeanName(String) <code>beanName</code>} as the cache name.
	 * 
	 * @param cacheName The name of the cache to create a configuration for
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
	 * @see org.springframework.beans.factory.InitializingBean
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.logger.info("Creating named INFINISPAN configuration ...");

		this.configurationFactory.afterPropertiesSet();

		this.namedConfiguration = new NamedConfiguration(getEffectiveCacheName(), this.configurationFactory.getObject());

		this.logger.info("Named INFINISPAN configuration [" + this.namedConfiguration + "] created.");
	}

	private String getEffectiveCacheName() {
		return StringUtils.hasText(this.cacheName) ? this.cacheName : this.beanName;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public NamedConfiguration getObject() throws Exception {
		return this.namedConfiguration;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<NamedConfiguration> getObjectType() {
		return NamedConfiguration.class;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * @param configurationFileLocation
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setConfigurationFileLocation(org.springframework.core.io.Resource)
	 */
	public void setConfigurationFileLocation(final Resource configurationFileLocation) {
		this.configurationFactory.setConfigurationFileLocation(configurationFileLocation);
	}

	/**
	 * @param gc
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setGlobalConfiguration(org.infinispan.config.GlobalConfiguration)
	 */
	public void setGlobalConfiguration(final GlobalConfiguration gc) {
		this.configurationFactory.setGlobalConfiguration(gc);
	}

	/**
	 * @param eagerDeadlockSpinDuration
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setDeadlockDetectionSpinDuration(long)
	 */
	public void setDeadlockDetectionSpinDuration(final long eagerDeadlockSpinDuration) {
		this.configurationFactory.setDeadlockDetectionSpinDuration(eagerDeadlockSpinDuration);
	}

	/**
	 * @param useEagerDeadlockDetection
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEnableDeadlockDetection(boolean)
	 */
	public void setEnableDeadlockDetection(final boolean useEagerDeadlockDetection) {
		this.configurationFactory.setEnableDeadlockDetection(useEagerDeadlockDetection);
	}

	/**
	 * @param useLockStriping
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setUseLockStriping(boolean)
	 */
	public void setUseLockStriping(final boolean useLockStriping) {
		this.configurationFactory.setUseLockStriping(useLockStriping);
	}

	/**
	 * @param unsafeUnreliableReturnValues
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setUnsafeUnreliableReturnValues(boolean)
	 */
	public void setUnsafeUnreliableReturnValues(final boolean unsafeUnreliableReturnValues) {
		this.configurationFactory.setUnsafeUnreliableReturnValues(unsafeUnreliableReturnValues);
	}

	/**
	 * @param rehashRpcTimeout
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setRehashRpcTimeout(long)
	 */
	public void setRehashRpcTimeout(final long rehashRpcTimeout) {
		this.configurationFactory.setRehashRpcTimeout(rehashRpcTimeout);
	}

	/**
	 * @param writeSkewCheck
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setWriteSkewCheck(boolean)
	 */
	public void setWriteSkewCheck(final boolean writeSkewCheck) {
		this.configurationFactory.setWriteSkewCheck(writeSkewCheck);
	}

	/**
	 * @param concurrencyLevel
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setConcurrencyLevel(int)
	 */
	public void setConcurrencyLevel(final int concurrencyLevel) {
		this.configurationFactory.setConcurrencyLevel(concurrencyLevel);
	}

	/**
	 * @param replQueueMaxElements
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setReplQueueMaxElements(int)
	 */
	public void setReplQueueMaxElements(final int replQueueMaxElements) {
		this.configurationFactory.setReplQueueMaxElements(replQueueMaxElements);
	}

	/**
	 * @param replQueueInterval
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setReplQueueInterval(long)
	 */
	public void setReplQueueInterval(final long replQueueInterval) {
		this.configurationFactory.setReplQueueInterval(replQueueInterval);
	}

	/**
	 * @param classname
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setReplQueueClass(java.lang.String)
	 */
	public void setReplQueueClass(final String classname) {
		this.configurationFactory.setReplQueueClass(classname);
	}

	/**
	 * @param useMbean
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setExposeJmxStatistics(boolean)
	 */
	public void setExposeJmxStatistics(final boolean useMbean) {
		this.configurationFactory.setExposeJmxStatistics(useMbean);
	}

	/**
	 * @param enabled
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setInvocationBatchingEnabled(boolean)
	 */
	public void setInvocationBatchingEnabled(final boolean enabled) {
		this.configurationFactory.setInvocationBatchingEnabled(enabled);
	}

	/**
	 * @param fetchInMemoryState
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setFetchInMemoryState(boolean)
	 */
	public void setFetchInMemoryState(final boolean fetchInMemoryState) {
		this.configurationFactory.setFetchInMemoryState(fetchInMemoryState);
	}

	/**
	 * @param alwaysProvideInMemoryState
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setAlwaysProvideInMemoryState(boolean)
	 */
	public void setAlwaysProvideInMemoryState(final boolean alwaysProvideInMemoryState) {
		this.configurationFactory.setAlwaysProvideInMemoryState(alwaysProvideInMemoryState);
	}

	/**
	 * @param lockAcquisitionTimeout
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setLockAcquisitionTimeout(long)
	 */
	public void setLockAcquisitionTimeout(final long lockAcquisitionTimeout) {
		this.configurationFactory.setLockAcquisitionTimeout(lockAcquisitionTimeout);
	}

	/**
	 * @param syncReplTimeout
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setSyncReplTimeout(long)
	 */
	public void setSyncReplTimeout(final long syncReplTimeout) {
		this.configurationFactory.setSyncReplTimeout(syncReplTimeout);
	}

	/**
	 * @param cacheMode
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setCacheModeString(java.lang.String)
	 */
	public void setCacheModeString(final String cacheMode) {
		this.configurationFactory.setCacheModeString(cacheMode);
	}

	/**
	 * @param evictionWakeUpInterval
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEvictionWakeUpInterval(long)
	 */
	public void setEvictionWakeUpInterval(final long evictionWakeUpInterval) {
		this.configurationFactory.setEvictionWakeUpInterval(evictionWakeUpInterval);
	}

	/**
	 * @param evictionStrategy
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEvictionStrategy(org.infinispan.eviction.EvictionStrategy)
	 */
	public void setEvictionStrategy(final EvictionStrategy evictionStrategy) {
		this.configurationFactory.setEvictionStrategy(evictionStrategy);
	}

	/**
	 * @param eStrategy
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEvictionStrategyClass(java.lang.String)
	 */
	public void setEvictionStrategyClass(final String eStrategy) {
		this.configurationFactory.setEvictionStrategyClass(eStrategy);
	}

	/**
	 * @param policy
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEvictionThreadPolicy(org.infinispan.eviction.EvictionThreadPolicy)
	 */
	public void setEvictionThreadPolicy(final EvictionThreadPolicy policy) {
		this.configurationFactory.setEvictionThreadPolicy(policy);
	}

	/**
	 * @param policy
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEvictionThreadPolicyClass(java.lang.String)
	 */
	public void setEvictionThreadPolicyClass(final String policy) {
		this.configurationFactory.setEvictionThreadPolicyClass(policy);
	}

	/**
	 * @param evictionMaxEntries
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEvictionMaxEntries(int)
	 */
	public void setEvictionMaxEntries(final int evictionMaxEntries) {
		this.configurationFactory.setEvictionMaxEntries(evictionMaxEntries);
	}

	/**
	 * @param expirationLifespan
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setExpirationLifespan(long)
	 */
	public void setExpirationLifespan(final long expirationLifespan) {
		this.configurationFactory.setExpirationLifespan(expirationLifespan);
	}

	/**
	 * @param expirationMaxIdle
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setExpirationMaxIdle(long)
	 */
	public void setExpirationMaxIdle(final long expirationMaxIdle) {
		this.configurationFactory.setExpirationMaxIdle(expirationMaxIdle);
	}

	/**
	 * @param transactionManagerLookupClass
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setTransactionManagerLookupClass(java.lang.String)
	 */
	public void setTransactionManagerLookupClass(final String transactionManagerLookupClass) {
		this.configurationFactory.setTransactionManagerLookupClass(transactionManagerLookupClass);
	}

	/**
	 * @param transactionManagerLookup
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setTransactionManagerLookup(org.infinispan.transaction.lookup.TransactionManagerLookup)
	 */
	public void setTransactionManagerLookup(final TransactionManagerLookup transactionManagerLookup) {
		this.configurationFactory.setTransactionManagerLookup(transactionManagerLookup);
	}

	/**
	 * @param cacheLoaderManagerConfig
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setCacheLoaderManagerConfig(org.infinispan.config.CacheLoaderManagerConfig)
	 */
	public void setCacheLoaderManagerConfig(final CacheLoaderManagerConfig cacheLoaderManagerConfig) {
		this.configurationFactory.setCacheLoaderManagerConfig(cacheLoaderManagerConfig);
	}

	/**
	 * @param syncCommitPhase
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setSyncCommitPhase(boolean)
	 */
	public void setSyncCommitPhase(final boolean syncCommitPhase) {
		this.configurationFactory.setSyncCommitPhase(syncCommitPhase);
	}

	/**
	 * @param syncRollbackPhase
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setSyncRollbackPhase(boolean)
	 */
	public void setSyncRollbackPhase(final boolean syncRollbackPhase) {
		this.configurationFactory.setSyncRollbackPhase(syncRollbackPhase);
	}

	/**
	 * @param useEagerLocking
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setUseEagerLocking(boolean)
	 */
	public void setUseEagerLocking(final boolean useEagerLocking) {
		this.configurationFactory.setUseEagerLocking(useEagerLocking);
	}

	/**
	 * @param eagerLockSingleNode
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEagerLockSingleNode(boolean)
	 */
	public void setEagerLockSingleNode(final boolean eagerLockSingleNode) {
		this.configurationFactory.setEagerLockSingleNode(eagerLockSingleNode);
	}

	/**
	 * @param useReplQueue
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setUseReplQueue(boolean)
	 */
	public void setUseReplQueue(final boolean useReplQueue) {
		this.configurationFactory.setUseReplQueue(useReplQueue);
	}

	/**
	 * @param isolationLevel
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setIsolationLevel(org.infinispan.util.concurrent.IsolationLevel)
	 */
	public void setIsolationLevel(final IsolationLevel isolationLevel) {
		this.configurationFactory.setIsolationLevel(isolationLevel);
	}

	/**
	 * @param stateRetrievalTimeout
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setStateRetrievalTimeout(long)
	 */
	public void setStateRetrievalTimeout(final long stateRetrievalTimeout) {
		this.configurationFactory.setStateRetrievalTimeout(stateRetrievalTimeout);
	}

	/**
	 * @param logFlushTimeout
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setStateRetrievalLogFlushTimeout(long)
	 */
	public void setStateRetrievalLogFlushTimeout(final long logFlushTimeout) {
		this.configurationFactory.setStateRetrievalLogFlushTimeout(logFlushTimeout);
	}

	/**
	 * @param maxNonProgressingLogWrites
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setStateRetrievalMaxNonProgressingLogWrites(int)
	 */
	public void setStateRetrievalMaxNonProgressingLogWrites(final int maxNonProgressingLogWrites) {
		this.configurationFactory.setStateRetrievalMaxNonProgressingLogWrites(maxNonProgressingLogWrites);
	}

	/**
	 * @param initialRetryWaitTime
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setStateRetrievalInitialRetryWaitTime(long)
	 */
	public void setStateRetrievalInitialRetryWaitTime(final long initialRetryWaitTime) {
		this.configurationFactory.setStateRetrievalInitialRetryWaitTime(initialRetryWaitTime);
	}

	/**
	 * @param retryWaitTimeIncreaseFactor
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setStateRetrievalRetryWaitTimeIncreaseFactor(int)
	 */
	public void setStateRetrievalRetryWaitTimeIncreaseFactor(final int retryWaitTimeIncreaseFactor) {
		this.configurationFactory.setStateRetrievalRetryWaitTimeIncreaseFactor(retryWaitTimeIncreaseFactor);
	}

	/**
	 * @param numRetries
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setStateRetrievalNumRetries(int)
	 */
	public void setStateRetrievalNumRetries(final int numRetries) {
		this.configurationFactory.setStateRetrievalNumRetries(numRetries);
	}

	/**
	 * @param isolationLevel
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setIsolationLevelClass(java.lang.String)
	 */
	public void setIsolationLevelClass(final String isolationLevel) {
		this.configurationFactory.setIsolationLevelClass(isolationLevel);
	}

	/**
	 * @param useLazyDeserialization
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setUseLazyDeserialization(boolean)
	 */
	public void setUseLazyDeserialization(final boolean useLazyDeserialization) {
		this.configurationFactory.setUseLazyDeserialization(useLazyDeserialization);
	}

	/**
	 * @param l1CacheEnabled
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setL1CacheEnabled(boolean)
	 */
	public void setL1CacheEnabled(final boolean l1CacheEnabled) {
		this.configurationFactory.setL1CacheEnabled(l1CacheEnabled);
	}

	/**
	 * @param l1Lifespan
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setL1Lifespan(long)
	 */
	public void setL1Lifespan(final long l1Lifespan) {
		this.configurationFactory.setL1Lifespan(l1Lifespan);
	}

	/**
	 * @param l1OnRehash
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setL1OnRehash(boolean)
	 */
	public void setL1OnRehash(final boolean l1OnRehash) {
		this.configurationFactory.setL1OnRehash(l1OnRehash);
	}

	/**
	 * @param consistentHashClass
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setConsistentHashClass(java.lang.String)
	 */
	public void setConsistentHashClass(final String consistentHashClass) {
		this.configurationFactory.setConsistentHashClass(consistentHashClass);
	}

	/**
	 * @param numOwners
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setNumOwners(int)
	 */
	public void setNumOwners(final int numOwners) {
		this.configurationFactory.setNumOwners(numOwners);
	}

	/**
	 * @param rehashEnabled
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setRehashEnabled(boolean)
	 */
	public void setRehashEnabled(final boolean rehashEnabled) {
		this.configurationFactory.setRehashEnabled(rehashEnabled);
	}

	/**
	 * @param rehashWaitTime
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setRehashWaitTime(long)
	 */
	public void setRehashWaitTime(final long rehashWaitTime) {
		this.configurationFactory.setRehashWaitTime(rehashWaitTime);
	}

	/**
	 * @param useAsyncMarshalling
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setUseAsyncMarshalling(boolean)
	 */
	public void setUseAsyncMarshalling(final boolean useAsyncMarshalling) {
		this.configurationFactory.setUseAsyncMarshalling(useAsyncMarshalling);
	}

	/**
	 * @param enabled
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setIndexingEnabled(boolean)
	 */
	public void setIndexingEnabled(final boolean enabled) {
		this.configurationFactory.setIndexingEnabled(enabled);
	}

	/**
	 * @param indexLocalOnly
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setIndexLocalOnly(boolean)
	 */
	public void setIndexLocalOnly(final boolean indexLocalOnly) {
		this.configurationFactory.setIndexLocalOnly(indexLocalOnly);
	}

	/**
	 * @param customInterceptors
	 * @see org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setCustomInterceptors(java.util.List)
	 */
	public void setCustomInterceptors(final List<CustomInterceptorConfig> customInterceptors) {
		this.configurationFactory.setCustomInterceptors(customInterceptors);
	}

}
