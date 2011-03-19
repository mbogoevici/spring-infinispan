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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Properties;
import java.util.concurrent.ExecutorService;

import javax.management.MBeanServer;

import org.infinispan.Cache;
import org.infinispan.Version;
import org.infinispan.config.Configuration;
import org.infinispan.config.Configuration.CacheMode;
import org.infinispan.config.GlobalConfiguration.ShutdownHookBehavior;
import org.infinispan.executors.ExecutorFactory;
import org.infinispan.jmx.MBeanServerLookup;
import org.infinispan.jmx.PlatformMBeanServerLookup;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * <p>
 * Test {@link InfinispanEmbeddedCacheManagerFactoryBean}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanEmbeddedCacheManagerFactoryBeanTest {

	private static final String CACHE_NAME_FROM_CONFIGURATION_FILE = "asyncCache";

	private static final String NAMED_ASYNC_CACHE_CONFIG_LOCATION = "named-async-cache.xml";

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setConfigurationFileLocation(org.springframework.core.io.Resource)}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldCreateACacheManagerEvenIfNoDefaultConfigurationLocationHasBeenSet()
			throws Exception {
		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.afterPropertiesSet();

		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertNotNull(
				"getObject() should have returned a valid EmbeddedCacheManager, even if no defaulConfigurationLocation "
						+ "has been specified. However, it returned null.", embeddedCacheManager);
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setConfigurationFileLocation(org.springframework.core.io.Resource)}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldCreateACustomizedCacheManagerIfGivenADefaultConfigurationLocation()
			throws Exception {
		final Resource infinispanConfig = new ClassPathResource(NAMED_ASYNC_CACHE_CONFIG_LOCATION, getClass());

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setConfigurationFileLocation(infinispanConfig);
		objectUnderTest.afterPropertiesSet();

		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();
		assertNotNull(
				"getObject() should have returned a valid EmbeddedCacheManager, configured using the configuration file "
						+ "set on InfinispanEmbeddedCacheManagerFactoryBean. However, it returned null.",
				embeddedCacheManager);
		final Cache<Object, Object> cacheDefinedInCustomConfiguration = embeddedCacheManager
				.getCache(CACHE_NAME_FROM_CONFIGURATION_FILE);
		final Configuration configuration = cacheDefinedInCustomConfiguration.getConfiguration();
		assertEquals("The cache named [" + CACHE_NAME_FROM_CONFIGURATION_FILE
				+ "] is configured to have asynchonous replication cache mode. Yet, the cache returned from getCache("
				+ CACHE_NAME_FROM_CONFIGURATION_FILE
				+ ") has a different cache mode. Obviously, InfinispanEmbeddedCacheManagerFactoryBean did not use "
				+ "the configuration file when instantiating EmbeddedCacheManager.", CacheMode.REPL_ASYNC,
				configuration.getCacheMode());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#getObjectType()}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldReportTheCorrectObjectType() throws Exception {
		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.afterPropertiesSet();

		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals("getObjectType() should return the most derived class of the actual EmbeddedCacheManager "
				+ "implementation returned from getObject(). However, it didn't.", embeddedCacheManager.getClass(),
				objectUnderTest.getObjectType());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#isSingleton()}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldDeclareItselfToOnlyProduceSingletons() {
		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();

		assertTrue("isSingleton() should always return true. However, it returned false", objectUnderTest.isSingleton());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#destroy()}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldStopTheCreateEmbeddedCacheManagerWhenBeingDestroyed()
			throws Exception {
		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.afterPropertiesSet();

		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();
		embeddedCacheManager.getCache(); // Implicitly starts EmbeddedCacheManager
		objectUnderTest.destroy();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should stop the created EmbeddedCacheManager when being destroyed. "
						+ "However, the created EmbeddedCacheManager is still not terminated.",
				ComponentStatus.TERMINATED, embeddedCacheManager.getStatus());
		embeddedCacheManager.stop();
	}

	// ~~~~ Testing overriding setters

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setExposeGlobalJmxStatistics(boolean)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseExposeGlobalJmxStatisticsPropIfExplicitlySet()
			throws Exception {
		final boolean expectedExposeGlobalJmxStatistics = true;

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setExposeGlobalJmxStatistics(expectedExposeGlobalJmxStatistics);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set ExposeGlobalJmxStatistics. However, it didn't.",
				expectedExposeGlobalJmxStatistics, embeddedCacheManager.getGlobalConfiguration()
						.isExposeGlobalJmxStatistics());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setJmxDomain(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseJmxDomainPropIfExplicitlySet() throws Exception {
		final String expectedJmxDomain = "expected.jmx.Domain";

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setJmxDomain(expectedJmxDomain);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set JmxDomain. However, it didn't.",
				expectedJmxDomain, embeddedCacheManager.getGlobalConfiguration().getJmxDomain());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setMBeanServerProperties(java.util.Properties)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseMBeanServerPropertiesPropIfExplicitlySet()
			throws Exception {
		final Properties expectedMBeanServerProperties = new Properties();
		expectedMBeanServerProperties.setProperty("key", "value");

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setMBeanServerProperties(expectedMBeanServerProperties);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set MBeanServerProperties. However, it didn't.",
				expectedMBeanServerProperties, embeddedCacheManager.getGlobalConfiguration().getMBeanServerProperties());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setMBeanServerLookupClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseMBeanServerLookupClassPropIfExplicitlySet()
			throws Exception {
		final MBeanServerLookup expectedMBeanServerLookup = new MBeanServerLookup() {
			@Override
			public MBeanServer getMBeanServer(final Properties properties) {
				return null;
			}
		};

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setMBeanServerLookupClass(expectedMBeanServerLookup.getClass().getName());
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set MBeanServerLookupClass. However, it didn't.",
				expectedMBeanServerLookup.getClass().getName(), embeddedCacheManager.getGlobalConfiguration()
						.getMBeanServerLookup());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setMBeanServerLookup(org.infinispan.jmx.MBeanServerLookup)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseMBeanServerLookupPropIfExplicitlySet()
			throws Exception {
		final MBeanServerLookup expectedMBeanServerLookup = new PlatformMBeanServerLookup();

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setMBeanServerLookup(expectedMBeanServerLookup);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertSame(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set MBeanServerLookup. However, it didn't.",
				expectedMBeanServerLookup.getClass().getName(), embeddedCacheManager.getGlobalConfiguration()
						.getMBeanServerLookup());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setAllowDuplicateDomains(boolean)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseAllowDuplicateDomainsPropIfExplicitlySet()
			throws Exception {
		final boolean expectedAllowDuplicateDomains = true;

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setAllowDuplicateDomains(expectedAllowDuplicateDomains);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set AllowDuplicateDomains. However, it didn't.",
				expectedAllowDuplicateDomains, embeddedCacheManager.getGlobalConfiguration().isAllowDuplicateDomains());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setCacheManagerName(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseCacheManagerNamePropIfExplicitlySet()
			throws Exception {
		final String expectedCacheManagerName = "expected.cache.manager.Name";

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setCacheManagerName(expectedCacheManagerName);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set CacheManagerName. However, it didn't.",
				expectedCacheManagerName, embeddedCacheManager.getGlobalConfiguration().getCacheManagerName());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setStrictPeerToPeer(boolean)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseStrictPeerToPeerPropIfExplicitlySet()
			throws Exception {
		final boolean expectedStrictPeerToPeer = true;

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setStrictPeerToPeer(expectedStrictPeerToPeer);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set StrictPeerToPeer. However, it didn't.",
				expectedStrictPeerToPeer, embeddedCacheManager.getGlobalConfiguration().isStrictPeerToPeer());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setAsyncListenerExecutorFactoryClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseAsyncListenerExecutorFactoryClassPropIfExplicitlySet()
			throws Exception {
		final String expectedAsyncListenerExecutorFactoryClass = DummyExecutorFactory.class.getName();

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setAsyncListenerExecutorFactoryClass(expectedAsyncListenerExecutorFactoryClass);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set AsyncListenerExecutorFactoryClass. However, it didn't.",
				expectedAsyncListenerExecutorFactoryClass, embeddedCacheManager.getGlobalConfiguration()
						.getAsyncListenerExecutorFactoryClass());
		embeddedCacheManager.stop();
	}

	public static final class DummyExecutorFactory implements ExecutorFactory {
		@Override
		public ExecutorService getExecutor(final Properties p) {
			return null;
		}
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setAsyncTransportExecutorFactoryClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseAsyncTransportExecutorFactoryClassPropIfExplicitlySet()
			throws Exception {
		final String expectedAsyncTransportExecutorFactoryClass = "expected.async.transport.executor.Factory";

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setAsyncTransportExecutorFactoryClass(expectedAsyncTransportExecutorFactoryClass);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set AsyncTransportExecutorFactoryClass. However, it didn't.",
				expectedAsyncTransportExecutorFactoryClass, embeddedCacheManager.getGlobalConfiguration()
						.getAsyncTransportExecutorFactoryClass());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setEvictionScheduledExecutorFactoryClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseEvictionScheduledExecutorFactoryClassPropIfExplicitlySet()
			throws Exception {
		final String expectedEvictionScheduledExecutorFactoryClass = "expected.eviction.scheduler.Factory";

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setEvictionScheduledExecutorFactoryClass(expectedEvictionScheduledExecutorFactoryClass);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set EvictionScheduledExecutorFactoryClass. However, it didn't.",
				expectedEvictionScheduledExecutorFactoryClass, embeddedCacheManager.getGlobalConfiguration()
						.getEvictionScheduledExecutorFactoryClass());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setReplicationQueueScheduledExecutorFactoryClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseReplicationQueueScheduledExecutorFactoryClassPropIfExplicitlySet()
			throws Exception {
		final String expectedReplicationQueueScheduledExecutorFactoryClass = "expected.replication.queue.scheduled.executor.Factory";

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest
				.setReplicationQueueScheduledExecutorFactoryClass(expectedReplicationQueueScheduledExecutorFactoryClass);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set ReplicationQueueScheduledExecutorFactoryClass. However, it didn't.",
				expectedReplicationQueueScheduledExecutorFactoryClass, embeddedCacheManager.getGlobalConfiguration()
						.getReplicationQueueScheduledExecutorFactoryClass());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setMarshallerClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseMarshallerClassPropIfExplicitlySet()
			throws Exception {
		final String expectedMarshallerClass = "expected.marshaller.Class";

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setMarshallerClass(expectedMarshallerClass);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set MarshallerClass. However, it didn't.",
				expectedMarshallerClass, embeddedCacheManager.getGlobalConfiguration().getMarshallerClass());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setTransportNodeName(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseTransportNodeNamePropIfExplicitlySet()
			throws Exception {
		final String expectedTransportNodeName = "expected.transport.node.Name";

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setTransportNodeName(expectedTransportNodeName);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set TransportNodeName. However, it didn't.",
				expectedTransportNodeName, embeddedCacheManager.getGlobalConfiguration().getTransportNodeName());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setTransportClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseTransportClassPropIfExplicitlySet()
			throws Exception {
		final String expectedTransportClass = "expected.transport.Class";

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setTransportClass(expectedTransportClass);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set TransportClass. However, it didn't.",
				expectedTransportClass, embeddedCacheManager.getGlobalConfiguration().getTransportClass());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setTransportProperties(java.util.Properties)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseTransportPropertiesPropIfExplicitlySet()
			throws Exception {
		final Properties expectedTransportProperties = new Properties();
		expectedTransportProperties.setProperty("key", "value");

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setTransportProperties(expectedTransportProperties);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set TransportProperties. However, it didn't.",
				expectedTransportProperties, embeddedCacheManager.getGlobalConfiguration().getTransportProperties());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setClusterName(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseClusterNamePropIfExplicitlySet()
			throws Exception {
		final String expectedClusterName = "expected.cluster.Name";

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setClusterName(expectedClusterName);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set ClusterName. However, it didn't.",
				expectedClusterName, embeddedCacheManager.getGlobalConfiguration().getClusterName());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setMachineId(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseMachineIdPropIfExplicitlySet() throws Exception {
		final String expectedMachineId = "expected.machine.Id";

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setMachineId(expectedMachineId);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set MachineId. However, it didn't.",
				expectedMachineId, embeddedCacheManager.getGlobalConfiguration().getMachineId());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setRackId(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseRackIdPropIfExplicitlySet() throws Exception {
		final String expectedRackId = "expected.rack.Id";

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setRackId(expectedRackId);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set RackId. However, it didn't.",
				expectedRackId, embeddedCacheManager.getGlobalConfiguration().getRackId());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setSiteId(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseSiteIdPropIfExplicitlySet() throws Exception {
		final String expectedSiteId = "expected.site.Id";

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setSiteId(expectedSiteId);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set SiteId. However, it didn't.",
				expectedSiteId, embeddedCacheManager.getGlobalConfiguration().getSiteId());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setShutdownHookBehavior(java.lang.String)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseShutdownHookBehaviorPropIfExplicitlySet()
			throws Exception {
		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setShutdownHookBehavior(ShutdownHookBehavior.DONT_REGISTER.name());
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set ShutdownHookBehavior. However, it didn't.",
				ShutdownHookBehavior.DONT_REGISTER, embeddedCacheManager.getGlobalConfiguration()
						.getShutdownHookBehavior());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setAsyncListenerExecutorProperties(java.util.Properties)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseAsyncListenerExecutorPropertiesPropIfExplicitlySet()
			throws Exception {
		final Properties expectedAsyncListenerExecutorProperties = new Properties();
		expectedAsyncListenerExecutorProperties.setProperty("key", "value");

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setAsyncListenerExecutorProperties(expectedAsyncListenerExecutorProperties);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set AsyncListenerExecutorProperties. However, it didn't.",
				expectedAsyncListenerExecutorProperties, embeddedCacheManager.getGlobalConfiguration()
						.getAsyncListenerExecutorProperties());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setAsyncTransportExecutorProperties(java.util.Properties)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseAsyncTransportExecutorPropertiesPropIfExplicitlySet()
			throws Exception {
		final Properties expectedAsyncTransportExecutorProperties = new Properties();
		expectedAsyncTransportExecutorProperties.setProperty("key", "value");

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setAsyncTransportExecutorProperties(expectedAsyncTransportExecutorProperties);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set AsyncTransportExecutorProperties. However, it didn't.",
				expectedAsyncTransportExecutorProperties, embeddedCacheManager.getGlobalConfiguration()
						.getAsyncTransportExecutorProperties());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setEvictionScheduledExecutorProperties(java.util.Properties)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseEvictionScheduledExecutorPropertiesPropIfExplicitlySet()
			throws Exception {
		final Properties expectedEvictionScheduledExecutorProperties = new Properties();
		expectedEvictionScheduledExecutorProperties.setProperty("key", "value");

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setEvictionScheduledExecutorProperties(expectedEvictionScheduledExecutorProperties);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set EvictionScheduledExecutorProperties. However, it didn't.",
				expectedEvictionScheduledExecutorProperties, embeddedCacheManager.getGlobalConfiguration()
						.getEvictionScheduledExecutorProperties());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setReplicationQueueScheduledExecutorProperties(java.util.Properties)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseReplicationQueueScheduledExecutorPropertiesPropIfExplicitlySet()
			throws Exception {
		final Properties expectedReplicationQueueScheduledExecutorProperties = new Properties();
		expectedReplicationQueueScheduledExecutorProperties.setProperty("key", "value");

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest
				.setReplicationQueueScheduledExecutorProperties(expectedReplicationQueueScheduledExecutorProperties);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set ReplicationQueueScheduledExecutorProperties. However, it didn't.",
				expectedReplicationQueueScheduledExecutorProperties, embeddedCacheManager.getGlobalConfiguration()
						.getReplicationQueueScheduledExecutorProperties());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setMarshallVersion(short)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseMarshallVersionPropIfExplicitlySet()
			throws Exception {
		final short setMarshallVersion = 1234;
		final short expectedMarshallVersion = Version.getVersionShort(Version
				.decodeVersionForSerialization(setMarshallVersion));

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setMarshallVersion(setMarshallVersion);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set MarshallVersion. However, it didn't.",
				expectedMarshallVersion, embeddedCacheManager.getGlobalConfiguration().getMarshallVersion());
		embeddedCacheManager.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setDistributedSyncTimeout(long)}.
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldUseDistributedSyncTimeoutPropIfExplicitlySet()
			throws Exception {
		final long expectedDistributedSyncTimeout = 123456L;

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setDistributedSyncTimeout(expectedDistributedSyncTimeout);
		objectUnderTest.afterPropertiesSet();
		final EmbeddedCacheManager embeddedCacheManager = objectUnderTest.getObject();

		assertEquals(
				"InfinispanEmbeddedCacheManagerFactoryBean should have used explicitly set DistributedSyncTimeout. However, it didn't.",
				expectedDistributedSyncTimeout, embeddedCacheManager.getGlobalConfiguration()
						.getDistributedSyncTimeout());
		embeddedCacheManager.stop();
	}

}
