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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.infinispan.config.CacheLoaderManagerConfig;
import org.infinispan.config.Configuration;
import org.infinispan.config.Configuration.CacheMode;
import org.infinispan.config.CustomInterceptorConfig;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.eviction.EvictionThreadPolicy;
import org.infinispan.transaction.lookup.JBossTransactionManagerLookup;
import org.infinispan.transaction.lookup.TransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * <p>
 * Test {@link InfinispanConfigurationFactoryBean}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanConfigurationFactoryBeanTest {

	private static final Resource COMPREHENSIVE_CONFIG_FILE_LOCATION = new ClassPathResource(
			"comprehensive-infinispan-config.xml", InfinispanConfigurationFactoryBeanTest.class);

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#afterPropertiesSet()}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldPreferCreatingConfigurationFromExplicitConfigFileLocation()
			throws Exception {
		final boolean invocationBatchingEnabledPropInConfigFile = true;
		// This property is set to true in our configuration file
		final boolean invocationBatchingEnabledPropToBeIgnored = false;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setConfigurationFileLocation(COMPREHENSIVE_CONFIG_FILE_LOCATION);
		objectUnderTest.setInvocationBatchingEnabled(invocationBatchingEnabledPropToBeIgnored);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals("InfinispanConfigurationFactoryBean should have preferred to create its INFINISPAN configuration "
				+ "from the configuration file located at [" + COMPREHENSIVE_CONFIG_FILE_LOCATION
				+ "]. However, an explicitly set property overrides the corresponding property "
				+ "from the configuration file.", invocationBatchingEnabledPropInConfigFile,
				config.isInvocationBatchingEnabled());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#isSingleton()}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldDeclareItselfToProduceSingletons() {
		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();

		assertTrue("InfinispanConfigurationFactoryBean should declare itself to always produce singletons. "
				+ "However, isSingleton() returned false.", objectUnderTest.isSingleton());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setGlobalConfiguration(org.infinispan.config.GlobalConfiguration)}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseGlobalConfigurationIfExplicitlySet() throws Exception {
		final GlobalConfiguration expectedGlobalConfiguration = new GlobalConfiguration();

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setGlobalConfiguration(expectedGlobalConfiguration);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertSame(
				"InfinispanConfigurationFactoryBean should have used explicitly set GlobalConfiguration. However, it didn't.",
				expectedGlobalConfiguration, config.getGlobalConfiguration());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setDeadlockDetectionSpinDuration(long)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseDeadlockSpinDetectionDurationPropIfExplicitlySet()
			throws Exception {
		final long expectedDeadlockSpinDetectionDuration = 100000L;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setDeadlockDetectionSpinDuration(expectedDeadlockSpinDetectionDuration);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set deadlockDetectionSpinDuration. However, it didn't.",
				expectedDeadlockSpinDetectionDuration, config.getDeadlockDetectionSpinDuration());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEnableDeadlockDetection(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseEnableDeadlockDetectionPropIfExplicitlySet()
			throws Exception {
		final boolean expectedEnableDeadlockDetection = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setEnableDeadlockDetection(expectedEnableDeadlockDetection);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set enableDeadlockDetection property. However, it didn't.",
				expectedEnableDeadlockDetection, config.isEnableDeadlockDetection());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setUseLockStriping(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseUseLockStripingPropIfExplicitlySet() throws Exception {
		final boolean expectedUseLockStriping = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setUseLockStriping(expectedUseLockStriping);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set useLockStriping property. However, it didn't.",
				expectedUseLockStriping, config.isUseLockStriping());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setUnsafeUnreliableReturnValues(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseUnsafeUnreliableReturnValuesPropIfExplicitlySet()
			throws Exception {
		final boolean expectedUnsafeUnreliableReturnValues = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setUnsafeUnreliableReturnValues(expectedUnsafeUnreliableReturnValues);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set unsafeUnreliableReturnValues property. However, it didn't.",
				expectedUnsafeUnreliableReturnValues, config.isUnsafeUnreliableReturnValues());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setRehashRpcTimeout(long)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseRehashRpcTimeoutPropIfExplicitlySet() throws Exception {
		final long expectedRehashRpcTimeout = 100000L;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setRehashRpcTimeout(expectedRehashRpcTimeout);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set rehashRpcTimeout property. However, it didn't.",
				expectedRehashRpcTimeout, config.getRehashRpcTimeout());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setWriteSkewCheck(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseWriteSkewCheckPropIfExplicitlySet() throws Exception {
		final boolean expectedWriteSkewCheck = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setWriteSkewCheck(expectedWriteSkewCheck);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set writeSkewCheck property. However, it didn't.",
				expectedWriteSkewCheck, config.isWriteSkewCheck());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setConcurrencyLevel(int)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseConcurrencyLevelPropIfExplicitlySet() throws Exception {
		final int expectedConcurrencyLevel = 10000;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setConcurrencyLevel(expectedConcurrencyLevel);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set ConcurrencyLevel property. However, it didn't.",
				expectedConcurrencyLevel, config.getConcurrencyLevel());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setReplQueueMaxElements(int)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseReplQueueMaxElementsPropIfExplicitlySet()
			throws Exception {
		final int expectedReplQueueMaxElements = 10000;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setReplQueueMaxElements(expectedReplQueueMaxElements);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set ReplQueueMaxElements property. However, it didn't.",
				expectedReplQueueMaxElements, config.getReplQueueMaxElements());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setReplQueueInterval(long)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseReplQueueIntervalPropIfExplicitlySet()
			throws Exception {
		final long expectedReplQueueInterval = 10000L;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setReplQueueInterval(expectedReplQueueInterval);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set ReplQueueInterval property. However, it didn't.",
				expectedReplQueueInterval, config.getReplQueueInterval());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setReplQueueClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseReplQueueClassPropIfExplicitlySet() throws Exception {
		final String expectedReplQueueClass = "repl.queue.Class";

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setReplQueueClass(expectedReplQueueClass);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set ReplQueueClass property. However, it didn't.",
				expectedReplQueueClass, config.getReplQueueClass());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setExposeJmxStatistics(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseExposeJmxStatisticsPropIfExplicitlySet()
			throws Exception {
		final boolean expectedExposeJmxStatistics = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setExposeJmxStatistics(expectedExposeJmxStatistics);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set ExposeJmxStatistics property. However, it didn't.",
				expectedExposeJmxStatistics, config.isExposeJmxStatistics());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setInvocationBatchingEnabled(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseInvocationBatchingEnabledPropIfExplicitlySet()
			throws Exception {
		final boolean expectedInvocationBatchingEnabled = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setInvocationBatchingEnabled(expectedInvocationBatchingEnabled);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set InvocationBatchingEnabled property. However, it didn't.",
				expectedInvocationBatchingEnabled, config.isInvocationBatchingEnabled());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setFetchInMemoryState(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseFetchInMemoryStatePropIfExplicitlySet()
			throws Exception {
		final boolean expectedFetchInMemoryState = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setFetchInMemoryState(expectedFetchInMemoryState);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set FetchInMemoryState property. However, it didn't.",
				expectedFetchInMemoryState, config.isFetchInMemoryState());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setAlwaysProvideInMemoryState(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseAlwaysProvideInMemoryStatePropIfExplicitlySet()
			throws Exception {
		final boolean expectedAlwaysProvideInMemoryState = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setAlwaysProvideInMemoryState(expectedAlwaysProvideInMemoryState);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set AlwaysProvideInMemoryState property. However, it didn't.",
				expectedAlwaysProvideInMemoryState, config.isAlwaysProvideInMemoryState());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setLockAcquisitionTimeout(long)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseLockAcquisitionTimeoutPropIfExplicitlySet()
			throws Exception {
		final long expectedLockAcquisitionTimeout = 1000000L;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setLockAcquisitionTimeout(expectedLockAcquisitionTimeout);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set LockAcquisitionTimeout property. However, it didn't.",
				expectedLockAcquisitionTimeout, config.getLockAcquisitionTimeout());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setSyncReplTimeout(long)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseSyncReplTimeoutPropIfExplicitlySet() throws Exception {
		final long expectedSyncReplTimeout = 100000L;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setSyncReplTimeout(expectedSyncReplTimeout);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set SyncReplTimeout property. However, it didn't.",
				expectedSyncReplTimeout, config.getSyncReplTimeout());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setCacheModeString(java.lang.String)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseCacheModeStringPropIfExplicitlySet() throws Exception {
		final String expectedCacheModeString = CacheMode.LOCAL.name();

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setCacheModeString(expectedCacheModeString);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set CacheModeString property. However, it didn't.",
				expectedCacheModeString, config.getCacheModeString());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEvictionWakeUpInterval(long)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseEvictionWakeUpIntervalPropIfExplicitlySet()
			throws Exception {
		final long expectedEvictionWakeUpInterval = 100000L;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setEvictionWakeUpInterval(expectedEvictionWakeUpInterval);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set EvictionWakeUpInterval property. However, it didn't.",
				expectedEvictionWakeUpInterval, config.getEvictionWakeUpInterval());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEvictionStrategy(org.infinispan.eviction.EvictionStrategy)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseEvictionStrategyPropIfExplicitlySet() throws Exception {
		final EvictionStrategy expectedEvictionStrategy = EvictionStrategy.LIRS;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setEvictionStrategy(expectedEvictionStrategy);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set EvictionStrategy property. However, it didn't.",
				expectedEvictionStrategy, config.getEvictionStrategy());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEvictionStrategyClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseEvictionStrategyClassPropIfExplicitlySet()
			throws Exception {
		final String expectedEvictionStrategyClass = "FIFO";

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setEvictionStrategyClass(expectedEvictionStrategyClass);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set EvictionStrategyClass property. However, it didn't.",
				EvictionStrategy.FIFO, config.getEvictionStrategy());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEvictionThreadPolicy(org.infinispan.eviction.EvictionThreadPolicy)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseEvictionThreadPolicyPropIfExplicitlySet()
			throws Exception {
		final EvictionThreadPolicy expectedEvictionThreadPolicy = EvictionThreadPolicy.PIGGYBACK;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setEvictionThreadPolicy(expectedEvictionThreadPolicy);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set EvictionThreadPolicy property. However, it didn't.",
				expectedEvictionThreadPolicy, config.getEvictionThreadPolicy());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEvictionThreadPolicyClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseEvictionThreadPolicyClassPropIfExplicitlySet()
			throws Exception {
		final String expectedEvictionThreadPolicyClass = "PIGGYBACK";

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setEvictionThreadPolicyClass(expectedEvictionThreadPolicyClass);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set EvictionThreadPolicyClass property. However, it didn't.",
				EvictionThreadPolicy.PIGGYBACK, config.getEvictionThreadPolicy());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEvictionMaxEntries(int)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseEvictionMaxEntriesPropIfExplicitlySet()
			throws Exception {
		final int expectedEvictionMaxEntries = 1000000;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setEvictionMaxEntries(expectedEvictionMaxEntries);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set EvictionMaxEntries property. However, it didn't.",
				expectedEvictionMaxEntries, config.getEvictionMaxEntries());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setExpirationLifespan(long)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseExpirationLifespanPropIfExplicitlySet()
			throws Exception {
		final long expectedExpirationLifespan = 1000000L;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setExpirationLifespan(expectedExpirationLifespan);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set ExpirationLifespan property. However, it didn't.",
				expectedExpirationLifespan, config.getExpirationLifespan());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setExpirationMaxIdle(long)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseExpirationMaxIdlePropIfExplicitlySet()
			throws Exception {
		final long expectedExpirationMaxIdle = 100000L;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setExpirationMaxIdle(expectedExpirationMaxIdle);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set ExpirationMaxIdle property. However, it didn't.",
				expectedExpirationMaxIdle, config.getExpirationMaxIdle());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setTransactionManagerLookupClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseTransactionManagerLookupClassPropIfExplicitlySet()
			throws Exception {
		final String expectedTransactionManagerLookupClass = "expected.transaction.manager.lookup.Class";

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setTransactionManagerLookupClass(expectedTransactionManagerLookupClass);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set TransactionManagerLookupClass property. However, it didn't.",
				expectedTransactionManagerLookupClass, config.getTransactionManagerLookupClass());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setTransactionManagerLookup(org.infinispan.transaction.lookup.TransactionManagerLookup)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseTransactionManagerLookupPropIfExplicitlySet()
			throws Exception {
		final TransactionManagerLookup expectedTransactionManagerLookup = new JBossTransactionManagerLookup();

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setTransactionManagerLookup(expectedTransactionManagerLookup);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set TransactionManagerLookup property. However, it didn't.",
				expectedTransactionManagerLookup, config.getTransactionManagerLookup());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setCacheLoaderManagerConfig(org.infinispan.config.CacheLoaderManagerConfig)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseCacheLoaderManagerConfigPropIfExplicitlySet()
			throws Exception {
		final CacheLoaderManagerConfig expectedCacheLoaderManagerConfig = new CacheLoaderManagerConfig();

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setCacheLoaderManagerConfig(expectedCacheLoaderManagerConfig);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertSame(
				"InfinispanConfigurationFactoryBean should have used explicitly set CacheLoaderManagerConfig property. However, it didn't.",
				expectedCacheLoaderManagerConfig, config.getCacheLoaderManagerConfig());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setSyncCommitPhase(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseSyncCommitPhasePropIfExplicitlySet() throws Exception {
		final boolean expectedSyncCommitPhase = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setSyncCommitPhase(expectedSyncCommitPhase);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set SyncCommitPhase property. However, it didn't.",
				expectedSyncCommitPhase, config.isSyncCommitPhase());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setSyncRollbackPhase(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseSyncRollbackPhasePropIfExplicitlySet()
			throws Exception {
		final boolean expectedSyncRollbackPhase = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setSyncRollbackPhase(expectedSyncRollbackPhase);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set SyncRollbackPhase property. However, it didn't.",
				expectedSyncRollbackPhase, config.isSyncRollbackPhase());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setUseEagerLocking(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseUseEagerLockingPropIfExplicitlySet() throws Exception {
		final boolean expectedUseEagerLocking = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setUseEagerLocking(expectedUseEagerLocking);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set UseEagerLocking property. However, it didn't.",
				expectedUseEagerLocking, config.isUseEagerLocking());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setEagerLockSingleNode(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseEagerLockSingleNodePropIfExplicitlySet()
			throws Exception {
		final boolean expectedEagerLockSingleNode = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setEagerLockSingleNode(expectedEagerLockSingleNode);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set EagerLockSingleNode property. However, it didn't.",
				expectedEagerLockSingleNode, config.isEagerLockSingleNode());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setUseReplQueue(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseUseReplQueuePropIfExplicitlySet() throws Exception {
		final boolean expectedUseReplQueue = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setUseReplQueue(expectedUseReplQueue);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set UseReplQueue property. However, it didn't.",
				expectedUseReplQueue, config.isUseReplQueue());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setIsolationLevel(org.infinispan.util.concurrent.IsolationLevel)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseIsolationLevelPropIfExplicitlySet() throws Exception {
		final IsolationLevel expectedIsolationLevel = IsolationLevel.SERIALIZABLE;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setIsolationLevel(expectedIsolationLevel);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set IsolationLevel property. However, it didn't.",
				expectedIsolationLevel, config.getIsolationLevel());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setStateRetrievalTimeout(long)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseStateRetrievalTimeoutPropIfExplicitlySet()
			throws Exception {
		final long expectedStateRetrievalTimeout = 1000000L;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setStateRetrievalTimeout(expectedStateRetrievalTimeout);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set StateRetrievalTimeout property. However, it didn't.",
				expectedStateRetrievalTimeout, config.getStateRetrievalTimeout());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setStateRetrievalLogFlushTimeout(long)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseStateRetrievalLogFlushTimeoutPropIfExplicitlySet()
			throws Exception {
		final long expectedStateRetrievalLogFlushTimeout = 1000000L;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setStateRetrievalLogFlushTimeout(expectedStateRetrievalLogFlushTimeout);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set StateRetrievalLogFlushTimeout property. However, it didn't.",
				expectedStateRetrievalLogFlushTimeout, config.getStateRetrievalLogFlushTimeout());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setStateRetrievalMaxNonProgressingLogWrites(int)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseStateRetrievalMaxNonProgressingLogWritesPropIfExplicitlySet()
			throws Exception {
		final int expectedStateRetrievalMaxNonProgressingLogWrites = 123456;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setStateRetrievalMaxNonProgressingLogWrites(expectedStateRetrievalMaxNonProgressingLogWrites);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set StateRetrievalMaxNonProgressingLogWrites property. However, it didn't.",
				expectedStateRetrievalMaxNonProgressingLogWrites, config.getStateRetrievalMaxNonProgressingLogWrites());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setStateRetrievalInitialRetryWaitTime(long)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseStateRetrievalInitialRetryWaitTimePropIfExplicitlySet()
			throws Exception {
		final long expectedStateRetrievalInitialRetryWaitTime = 987665L;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setStateRetrievalInitialRetryWaitTime(expectedStateRetrievalInitialRetryWaitTime);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set StateRetrievalInitialRetryWaitTime property. However, it didn't.",
				expectedStateRetrievalInitialRetryWaitTime, config.getStateRetrievalInitialRetryWaitTime());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setStateRetrievalRetryWaitTimeIncreaseFactor(int)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseStateRetrievalRetryWaitTimeIncreaseFactorPropIfExplicitlySet()
			throws Exception {
		final int expectedStateRetrievalRetryWaitTimeIncreaseFactor = 987432;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setStateRetrievalRetryWaitTimeIncreaseFactor(expectedStateRetrievalRetryWaitTimeIncreaseFactor);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set StateRetrievalRetryWaitTimeIncreaseFactor property. However, it didn't.",
				expectedStateRetrievalRetryWaitTimeIncreaseFactor,
				config.getStateRetrievalRetryWaitTimeIncreaseFactor());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setStateRetrievalNumRetries(int)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseStateRetrievalNumRetriesPropIfExplicitlySet()
			throws Exception {
		final int expectedStateRetrievalNumRetries = 765123;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setStateRetrievalNumRetries(expectedStateRetrievalNumRetries);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set StateRetrievalNumRetries property. However, it didn't.",
				expectedStateRetrievalNumRetries, config.getStateRetrievalNumRetries());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setIsolationLevelClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseIsolationLevelClassPropIfExplicitlySet()
			throws Exception {
		final String expectedIsolationLevelClass = "REPEATABLE_READ";

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setIsolationLevelClass(expectedIsolationLevelClass);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set IsolationLevelClass property. However, it didn't.",
				IsolationLevel.REPEATABLE_READ, config.getIsolationLevel());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setUseLazyDeserialization(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseUseLazyDeserializationPropIfExplicitlySet()
			throws Exception {
		final boolean expectedUseLazyDeserialization = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setUseLazyDeserialization(expectedUseLazyDeserialization);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set UseLazyDeserialization property. However, it didn't.",
				expectedUseLazyDeserialization, config.isUseLazyDeserialization());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setL1CacheEnabled(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseL1CacheEnabledPropIfExplicitlySet() throws Exception {
		final boolean expectedL1CacheEnabled = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setL1CacheEnabled(expectedL1CacheEnabled);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set L1CacheEnabled property. However, it didn't.",
				expectedL1CacheEnabled, config.isL1CacheEnabled());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setL1Lifespan(long)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseL1LifespanPropIfExplicitlySet() throws Exception {
		final long expectedL1Lifespan = 2300446L;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setL1Lifespan(expectedL1Lifespan);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set L1Lifespan property. However, it didn't.",
				expectedL1Lifespan, config.getL1Lifespan());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setL1OnRehash(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseL1OnRehashPropIfExplicitlySet() throws Exception {
		final boolean expectedL1OnRehash = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setL1OnRehash(expectedL1OnRehash);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set L1OnRehash property. However, it didn't.",
				expectedL1OnRehash, config.isL1OnRehash());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setConsistentHashClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseConsistentHashClassPropIfExplicitlySet()
			throws Exception {
		final String expectedConsistentHashClass = "expected.consistent.hash.Class";

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setConsistentHashClass(expectedConsistentHashClass);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set ConsistentHashClass property. However, it didn't.",
				expectedConsistentHashClass, config.getConsistentHashClass());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setNumOwners(int)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseNumOwnersPropIfExplicitlySet() throws Exception {
		final int expectedNumOwners = 675443;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setNumOwners(expectedNumOwners);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set NumOwners property. However, it didn't.",
				expectedNumOwners, config.getNumOwners());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setRehashEnabled(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseRehashEnabledPropIfExplicitlySet() throws Exception {
		final boolean expectedRehashEnabled = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setRehashEnabled(expectedRehashEnabled);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set RehashEnabled property. However, it didn't.",
				expectedRehashEnabled, config.isRehashEnabled());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setRehashWaitTime(long)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseRehashWaitTimePropIfExplicitlySet() throws Exception {
		final long expectedRehashWaitTime = 1232778L;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setRehashWaitTime(expectedRehashWaitTime);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set RehashWaitTime property. However, it didn't.",
				expectedRehashWaitTime, config.getRehashWaitTime());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setUseAsyncMarshalling(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseUseAsyncMarshallingPropIfExplicitlySet()
			throws Exception {
		final boolean expectedUseAsyncMarshalling = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setUseAsyncMarshalling(expectedUseAsyncMarshalling);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set UseAsyncMarshalling property. However, it didn't.",
				expectedUseAsyncMarshalling, config.isUseAsyncMarshalling());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setIndexingEnabled(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseIndexingEnabledPropIfExplicitlySet() throws Exception {
		final boolean expectedIndexingEnabled = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setIndexingEnabled(expectedIndexingEnabled);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set IndexingEnabled property. However, it didn't.",
				expectedIndexingEnabled, config.isIndexingEnabled());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setIndexLocalOnly(boolean)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseIndexLocalOnlyPropIfExplicitlySet() throws Exception {
		final boolean expectedIndexLocalOnly = true;

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setIndexLocalOnly(expectedIndexLocalOnly);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set IndexLocalOnly property. However, it didn't.",
				expectedIndexLocalOnly, config.isIndexLocalOnly());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanConfigurationFactoryBean#setCustomInterceptors(java.util.List)}.
	 */
	@Test
	public final void infinispanConfigurationFactoryBeanShouldUseCustomInterceptorsPropIfExplicitlySet()
			throws Exception {
		final CustomInterceptorConfig customInterceptor = new CustomInterceptorConfig();
		final List<CustomInterceptorConfig> expectedCustomInterceptors = Arrays.asList(customInterceptor);

		final InfinispanConfigurationFactoryBean objectUnderTest = new InfinispanConfigurationFactoryBean();
		objectUnderTest.setCustomInterceptors(expectedCustomInterceptors);
		objectUnderTest.afterPropertiesSet();
		final Configuration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanConfigurationFactoryBean should have used explicitly set CustomInterceptors property. However, it didn't.",
				expectedCustomInterceptors, config.getCustomInterceptors());
	}
}
