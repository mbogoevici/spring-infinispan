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

import java.util.Properties;

import javax.management.MBeanServer;

import org.infinispan.Version;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.config.GlobalConfiguration.ShutdownHookBehavior;
import org.infinispan.jmx.MBeanServerLookup;
import org.infinispan.jmx.PlatformMBeanServerLookup;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * <p>
 * Test {@link InfinispanGlobalConfigurationFactoryBean}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanGlobalConfigurationFactoryBeanTest {

	private static final Resource COMPREHENSIVE_CONFIG_FILE_LOCATION = new ClassPathResource(
			"comprehensive-infinispan-config.xml", InfinispanConfigurationFactoryBeanTest.class);

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#afterPropertiesSet()}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldPreferCreatingConfigurationFromExplicitConfigFileLocation()
			throws Exception {
		final String jmxDomainPropInConfigFile = "infinispan";
		// This property is set to true in our configuration file
		final String jmxDomainPropToBeIgnored = "to.be.ignored";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setConfigurationFileLocation(COMPREHENSIVE_CONFIG_FILE_LOCATION);
		objectUnderTest.setJmxDomain(jmxDomainPropToBeIgnored);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have preferred to create its global INFINISPAN configuration "
						+ "from the configuration file located at [" + COMPREHENSIVE_CONFIG_FILE_LOCATION
						+ "]. However, an explicitly set property overrides the corresponding property "
						+ "from the configuration file.", jmxDomainPropInConfigFile, config.getJmxDomain());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#isSingleton()}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldDeclareItselfToProduceSingletons() {
		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();

		assertTrue("InfinispanGlobalConfigurationFactoryBean should declare itself to always produce singletons. "
				+ "However, isSingleton() returned false.", objectUnderTest.isSingleton());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setExposeGlobalJmxStatistics(boolean)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseExposeGlobalJmxStatisticsPropIfExplicitlySet()
			throws Exception {
		final boolean expectedExposeGlobalJmxStatistics = true;

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setExposeGlobalJmxStatistics(expectedExposeGlobalJmxStatistics);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set ExposeGlobalJmxStatistics. However, it didn't.",
				expectedExposeGlobalJmxStatistics, config.isExposeGlobalJmxStatistics());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setJmxDomain(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseJmxDomainPropIfExplicitlySet() throws Exception {
		final String expectedJmxDomain = "expected.jmx.Domain";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setJmxDomain(expectedJmxDomain);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set JmxDomain. However, it didn't.",
				expectedJmxDomain, config.getJmxDomain());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setMBeanServerProperties(java.util.Properties)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseMBeanServerPropertiesPropIfExplicitlySet()
			throws Exception {
		final Properties expectedMBeanServerProperties = new Properties();
		expectedMBeanServerProperties.setProperty("key", "value");

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setMBeanServerProperties(expectedMBeanServerProperties);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set MBeanServerProperties. However, it didn't.",
				expectedMBeanServerProperties, config.getMBeanServerProperties());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setMBeanServerLookupClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseMBeanServerLookupClassPropIfExplicitlySet()
			throws Exception {
		final MBeanServerLookup expectedMBeanServerLookup = new MBeanServerLookup() {
			@Override
			public MBeanServer getMBeanServer(final Properties properties) {
				return null;
			}
		};

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setMBeanServerLookupClass(expectedMBeanServerLookup.getClass().getName());
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set MBeanServerLookupClass. However, it didn't.",
				expectedMBeanServerLookup.getClass().getName(), config.getMBeanServerLookup());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setMBeanServerLookup(org.infinispan.jmx.MBeanServerLookup)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseMBeanServerLookupPropIfExplicitlySet()
			throws Exception {
		final MBeanServerLookup expectedMBeanServerLookup = new PlatformMBeanServerLookup();

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setMBeanServerLookup(expectedMBeanServerLookup);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertSame(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set MBeanServerLookup. However, it didn't.",
				expectedMBeanServerLookup.getClass().getName(), config.getMBeanServerLookup());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setAllowDuplicateDomains(boolean)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseAllowDuplicateDomainsPropIfExplicitlySet()
			throws Exception {
		final boolean expectedAllowDuplicateDomains = true;

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setAllowDuplicateDomains(expectedAllowDuplicateDomains);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set AllowDuplicateDomains. However, it didn't.",
				expectedAllowDuplicateDomains, config.isAllowDuplicateDomains());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setCacheManagerName(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseCacheManagerNamePropIfExplicitlySet()
			throws Exception {
		final String expectedCacheManagerName = "expected.cache.manager.Name";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setCacheManagerName(expectedCacheManagerName);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set CacheManagerName. However, it didn't.",
				expectedCacheManagerName, config.getCacheManagerName());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setStrictPeerToPeer(boolean)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseStrictPeerToPeerPropIfExplicitlySet()
			throws Exception {
		final boolean expectedStrictPeerToPeer = true;

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setStrictPeerToPeer(expectedStrictPeerToPeer);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set StrictPeerToPeer. However, it didn't.",
				expectedStrictPeerToPeer, config.isStrictPeerToPeer());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setAsyncListenerExecutorFactoryClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseAsyncListenerExecutorFactoryClassPropIfExplicitlySet()
			throws Exception {
		final String expectedAsyncListenerExecutorFactoryClass = "expected.async.listener.executor.Factory";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setAsyncListenerExecutorFactoryClass(expectedAsyncListenerExecutorFactoryClass);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set AsyncListenerExecutorFactoryClass. However, it didn't.",
				expectedAsyncListenerExecutorFactoryClass, config.getAsyncListenerExecutorFactoryClass());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setAsyncTransportExecutorFactoryClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseAsyncTransportExecutorFactoryClassPropIfExplicitlySet()
			throws Exception {
		final String expectedAsyncTransportExecutorFactoryClass = "expected.async.transport.executor.Factory";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setAsyncTransportExecutorFactoryClass(expectedAsyncTransportExecutorFactoryClass);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set AsyncTransportExecutorFactoryClass. However, it didn't.",
				expectedAsyncTransportExecutorFactoryClass, config.getAsyncTransportExecutorFactoryClass());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setEvictionScheduledExecutorFactoryClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseEvictionScheduledExecutorFactoryClassPropIfExplicitlySet()
			throws Exception {
		final String expectedEvictionScheduledExecutorFactoryClass = "expected.eviction.scheduler.Factory";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setEvictionScheduledExecutorFactoryClass(expectedEvictionScheduledExecutorFactoryClass);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set EvictionScheduledExecutorFactoryClass. However, it didn't.",
				expectedEvictionScheduledExecutorFactoryClass, config.getEvictionScheduledExecutorFactoryClass());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setReplicationQueueScheduledExecutorFactoryClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseReplicationQueueScheduledExecutorFactoryClassPropIfExplicitlySet()
			throws Exception {
		final String expectedReplicationQueueScheduledExecutorFactoryClass = "expected.replication.queue.scheduled.executor.Factory";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest
				.setReplicationQueueScheduledExecutorFactoryClass(expectedReplicationQueueScheduledExecutorFactoryClass);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set ReplicationQueueScheduledExecutorFactoryClass. However, it didn't.",
				expectedReplicationQueueScheduledExecutorFactoryClass,
				config.getReplicationQueueScheduledExecutorFactoryClass());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setMarshallerClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseMarshallerClassPropIfExplicitlySet()
			throws Exception {
		final String expectedMarshallerClass = "expected.marshaller.Class";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setMarshallerClass(expectedMarshallerClass);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set MarshallerClass. However, it didn't.",
				expectedMarshallerClass, config.getMarshallerClass());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setTransportNodeName(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseTransportNodeNamePropIfExplicitlySet()
			throws Exception {
		final String expectedTransportNodeName = "expected.transport.node.Name";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setTransportNodeName(expectedTransportNodeName);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set TransportNodeName. However, it didn't.",
				expectedTransportNodeName, config.getTransportNodeName());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setTransportClass(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseTransportClassPropIfExplicitlySet()
			throws Exception {
		final String expectedTransportClass = "expected.transport.Class";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setTransportClass(expectedTransportClass);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set TransportClass. However, it didn't.",
				expectedTransportClass, config.getTransportClass());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setTransportProperties(java.util.Properties)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseTransportPropertiesPropIfExplicitlySet()
			throws Exception {
		final Properties expectedTransportProperties = new Properties();
		expectedTransportProperties.setProperty("key", "value");

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setTransportProperties(expectedTransportProperties);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set TransportProperties. However, it didn't.",
				expectedTransportProperties, config.getTransportProperties());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setClusterName(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseClusterNamePropIfExplicitlySet()
			throws Exception {
		final String expectedClusterName = "expected.cluster.Name";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setClusterName(expectedClusterName);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set ClusterName. However, it didn't.",
				expectedClusterName, config.getClusterName());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setMachineId(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseMachineIdPropIfExplicitlySet() throws Exception {
		final String expectedMachineId = "expected.machine.Id";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setMachineId(expectedMachineId);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set MachineId. However, it didn't.",
				expectedMachineId, config.getMachineId());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setRackId(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseRackIdPropIfExplicitlySet() throws Exception {
		final String expectedRackId = "expected.rack.Id";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setRackId(expectedRackId);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set RackId. However, it didn't.",
				expectedRackId, config.getRackId());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setSiteId(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseSiteIdPropIfExplicitlySet() throws Exception {
		final String expectedSiteId = "expected.site.Id";

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setSiteId(expectedSiteId);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set SiteId. However, it didn't.",
				expectedSiteId, config.getSiteId());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setShutdownHookBehavior(java.lang.String)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseShutdownHookBehaviorPropIfExplicitlySet()
			throws Exception {
		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setShutdownHookBehavior(ShutdownHookBehavior.DONT_REGISTER.name());
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set ShutdownHookBehavior. However, it didn't.",
				ShutdownHookBehavior.DONT_REGISTER, config.getShutdownHookBehavior());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setAsyncListenerExecutorProperties(java.util.Properties)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseAsyncListenerExecutorPropertiesPropIfExplicitlySet()
			throws Exception {
		final Properties expectedAsyncListenerExecutorProperties = new Properties();
		expectedAsyncListenerExecutorProperties.setProperty("key", "value");

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setAsyncListenerExecutorProperties(expectedAsyncListenerExecutorProperties);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set AsyncListenerExecutorProperties. However, it didn't.",
				expectedAsyncListenerExecutorProperties, config.getAsyncListenerExecutorProperties());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setAsyncTransportExecutorProperties(java.util.Properties)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseAsyncTransportExecutorPropertiesPropIfExplicitlySet()
			throws Exception {
		final Properties expectedAsyncTransportExecutorProperties = new Properties();
		expectedAsyncTransportExecutorProperties.setProperty("key", "value");

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setAsyncTransportExecutorProperties(expectedAsyncTransportExecutorProperties);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set AsyncTransportExecutorProperties. However, it didn't.",
				expectedAsyncTransportExecutorProperties, config.getAsyncTransportExecutorProperties());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setEvictionScheduledExecutorProperties(java.util.Properties)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseEvictionScheduledExecutorPropertiesPropIfExplicitlySet()
			throws Exception {
		final Properties expectedEvictionScheduledExecutorProperties = new Properties();
		expectedEvictionScheduledExecutorProperties.setProperty("key", "value");

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setEvictionScheduledExecutorProperties(expectedEvictionScheduledExecutorProperties);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set EvictionScheduledExecutorProperties. However, it didn't.",
				expectedEvictionScheduledExecutorProperties, config.getEvictionScheduledExecutorProperties());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setReplicationQueueScheduledExecutorProperties(java.util.Properties)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseReplicationQueueScheduledExecutorPropertiesPropIfExplicitlySet()
			throws Exception {
		final Properties expectedReplicationQueueScheduledExecutorProperties = new Properties();
		expectedReplicationQueueScheduledExecutorProperties.setProperty("key", "value");

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest
				.setReplicationQueueScheduledExecutorProperties(expectedReplicationQueueScheduledExecutorProperties);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set ReplicationQueueScheduledExecutorProperties. However, it didn't.",
				expectedReplicationQueueScheduledExecutorProperties,
				config.getReplicationQueueScheduledExecutorProperties());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setMarshallVersion(short)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseMarshallVersionPropIfExplicitlySet()
			throws Exception {
		final short setMarshallVersion = 1234;
		final short expectedMarshallVersion = Version.getVersionShort(Version
				.decodeVersionForSerialization(setMarshallVersion));

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setMarshallVersion(setMarshallVersion);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set MarshallVersion. However, it didn't.",
				expectedMarshallVersion, config.getMarshallVersion());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanGlobalConfigurationFactoryBean#setDistributedSyncTimeout(long)}.
	 */
	@Test
	public final void infinispanGlobalConfigurationFactoryBeanShouldUseDistributedSyncTimeoutPropIfExplicitlySet()
			throws Exception {
		final long expectedDistributedSyncTimeout = 123456L;

		final InfinispanGlobalConfigurationFactoryBean objectUnderTest = new InfinispanGlobalConfigurationFactoryBean();
		objectUnderTest.setDistributedSyncTimeout(expectedDistributedSyncTimeout);
		objectUnderTest.afterPropertiesSet();
		final GlobalConfiguration config = objectUnderTest.getObject();

		assertEquals(
				"InfinispanGlobalConfigurationFactoryBean should have used explicitly set DistributedSyncTimeout. However, it didn't.",
				expectedDistributedSyncTimeout, config.getDistributedSyncTimeout());
	}

}
