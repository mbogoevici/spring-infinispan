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

import static org.infinispan.client.hotrod.impl.ConfigurationProperties.ASYNC_EXECUTOR_FACTORY;
import static org.infinispan.client.hotrod.impl.ConfigurationProperties.FORCE_RETURN_VALUES;
import static org.infinispan.client.hotrod.impl.ConfigurationProperties.KEY_SIZE_ESTIMATE;
import static org.infinispan.client.hotrod.impl.ConfigurationProperties.MARSHALLER;
import static org.infinispan.client.hotrod.impl.ConfigurationProperties.PING_ON_STARTUP;
import static org.infinispan.client.hotrod.impl.ConfigurationProperties.REQUEST_BALANCING_STRATEGY;
import static org.infinispan.client.hotrod.impl.ConfigurationProperties.SERVER_LIST;
import static org.infinispan.client.hotrod.impl.ConfigurationProperties.TCP_NO_DELAY;
import static org.infinispan.client.hotrod.impl.ConfigurationProperties.TRANSPORT_FACTORY;
import static org.infinispan.client.hotrod.impl.ConfigurationProperties.VALUE_SIZE_ESTIMATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.spring.AbstractInfinispanRemoteCacheManagerBackedCacheManagerFactory;
import org.infinispan.spring.mock.MockExecutorFatory;
import org.infinispan.spring.mock.MockMarshaller;
import org.infinispan.spring.mock.MockRequestBalancingStrategy;
import org.infinispan.spring.mock.MockTransportFactory;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * <p>
 * Test {@link AbstractInfinispanRemoteCacheManagerBackedCacheManagerFactory}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanRemoteCacheManagerFactoryBeanTest {

	private static final Resource HOTROD_CLIENT_PROPERTIES_LOCATION = new ClassPathResource("hotrod-client.properties",
			InfinispanRemoteCacheManagerFactoryBeanTest.class);

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#afterPropertiesSet()}.
	 * @throws Exception 
	 */
	@Test(expected = IllegalStateException.class)
	public final void shouldThrowAnIllegalStateExceptionIfBothConfigurationPropertiesAndConfifurationPropertiesFileLocationAreSet()
			throws Exception {
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setConfigurationProperties(new Properties());
		objectUnderTest.setConfigurationPropertiesFileLocation(new ClassPathResource("dummy", getClass()));

		objectUnderTest.afterPropertiesSet();
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#afterPropertiesSet()}.
	 * @throws Exception 
	 */
	@Test(expected = IllegalStateException.class)
	public final void shouldThrowAnIllegalStateExceptionIfConfigurationPropertiesAsWellAsSettersAreUsedToConfigureTheRemoteCacheManager()
			throws Exception {
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setConfigurationProperties(new Properties());
		objectUnderTest.setTransportFactory("test.TransportFactory");

		objectUnderTest.afterPropertiesSet();
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#getObjectType()}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanRemoteCacheFactoryBeanShouldReportTheMostDerivedObjectType() throws Exception {
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.afterPropertiesSet();

		assertEquals("getObjectType() should have returned the most derived class of the actual RemoteCache "
				+ "implementation returned from getObject(). However, it didn't.", objectUnderTest.getObject()
				.getClass(), objectUnderTest.getObjectType());
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#getObject()}.
	 * @throws Exception 
	 */
	@Test
	public final void shouldProduceARemoteCacheManagerConfiguredUsingDefaultSettingsIfNeitherConfigurationPropertiesNorConfigurationPropertiesFileLocationHasBeenSet()
			throws Exception {
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();

		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();
		assertEquals(
				"The configuration properties used by the RemoteCacheManager returned von getObject() should be equal "
						+ "to RemoteCacheManager's default settings since neither property 'configurationProperties' "
						+ "nor property 'configurationPropertiesFileLocation' has been set. However, those two are not equal.",
				new RemoteCacheManager().getProperties(), remoteCacheManager.getProperties());
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#isSingleton()}.
	 */
	@Test
	public final void isSingletonShouldAlwaysReturnTrue() {
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();

		assertTrue(
				"isSingleton() should always return true since each AbstractInfinispanRemoteCacheManagerBackedCacheManagerFactory will always produce "
						+ "the same RemoteCacheManager instance. However,it returned false.",
				objectUnderTest.isSingleton());
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#destroy()}.
	 * @throws Exception 
	 */
	@Test
	public final void destroyShouldStopTheProducedCache() throws Exception {
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.afterPropertiesSet();
		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		objectUnderTest.destroy();

		assertFalse(
				"destroy() should have stopped the RemoteCacheManager instance previously produced by "
						+ "AbstractInfinispanRemoteCacheManagerBackedCacheManagerFactory. However, the produced RemoteCacheManager is still running. ",
				remoteCacheManager.isStarted());
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#setConfigurationProperties(java.util.Properties)}.
	 * @throws Exception 
	 */
	@Test
	public final void shouldProduceACacheConfiguredUsingTheSuppliedConfigurationProperties() throws Exception {
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		final Properties configurationProperties = loadConfigurationProperties(HOTROD_CLIENT_PROPERTIES_LOCATION);
		objectUnderTest.setConfigurationProperties(configurationProperties);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();
		assertEquals(
				"The configuration properties used by the RemoteCacheManager returned von getObject() should be equal "
						+ "to those passed into InfinispanRemoteCacheMangerFactoryBean via setConfigurationProperties(props). "
						+ "However, those two are not equal.", configurationProperties,
				remoteCacheManager.getProperties());
	}

	private Properties loadConfigurationProperties(final Resource configurationPropertiesLocation) throws IOException {
		InputStream propsStream = null;
		try {
			propsStream = HOTROD_CLIENT_PROPERTIES_LOCATION.getInputStream();
			final Properties configurationProperties = new Properties();
			configurationProperties.load(propsStream);

			return configurationProperties;
		} finally {
			if (propsStream != null) {
				propsStream.close();
			}
		}
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#setConfigurationPropertiesFileLocation(org.springframework.core.io.Resource)}.
	 */
	@Test
	public final void shouldProduceACacheConfiguredUsingPropertiesLoadedFromALocationDeclaredThroughSetConfigurationPropertiesFileLocation()
			throws Exception {
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setConfigurationPropertiesFileLocation(HOTROD_CLIENT_PROPERTIES_LOCATION);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();
		assertEquals(
				"The configuration properties used by the RemoteCacheManager returned von getObject() should be equal "
						+ "to those passed into InfinispanRemoteCacheMangerFactoryBean via setConfigurationPropertiesFileLocation(propsFileLocation). "
						+ "However, those two are not equal.",
				loadConfigurationProperties(HOTROD_CLIENT_PROPERTIES_LOCATION), remoteCacheManager.getProperties());
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#setStartAutomatically(boolean)}.
	 * @throws Exception 
	 */
	@Test
	public final void shouldProduceAStoppedCacheIfStartAutomaticallyIsSetToFalse() throws Exception {
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setStartAutomatically(false);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManagerExpectedToBeInStateStopped = objectUnderTest.getObject();

		assertFalse(
				"AbstractInfinispanRemoteCacheManagerBackedCacheManagerFactory should have produced a RemoteCacheManager that is initially in state stopped "
						+ "since property 'startAutomatically' has been set to false. However, the produced RemoteCacheManager is already started.",
				remoteCacheManagerExpectedToBeInStateStopped.isStarted());
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#setTransportFactory(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void setTransportFactoryShouldOverrideDefaultTransportFactory() throws Exception {
		final String expectedTransportFactory = MockTransportFactory.class.getName();
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setTransportFactory(expectedTransportFactory);
		objectUnderTest.setStartAutomatically(false); // Otherwise, RemoteCacheManager will try to actually use our DummyTransportFactory
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setTransportFactory(" + expectedTransportFactory
				+ ") should have overridden property 'transportFactory'. However, it didn't.",
				expectedTransportFactory, remoteCacheManager.getProperties().get(TRANSPORT_FACTORY));
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#setServerList(java.util.Collection)}.
	 * @throws Exception 
	 */
	@Test
	public final void setServerListShouldOverrideDefaultServerList() throws Exception {
		final Collection<InetSocketAddress> expectedServerList = new ArrayList<InetSocketAddress>(1);
		expectedServerList.add(new InetSocketAddress("testhost", 4632));
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		final String expectedServerListString = "testhost:4632";
		objectUnderTest.setServerList(expectedServerList);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setServerList(" + expectedServerList
				+ ") should have overridden property 'serverList'. However, it didn't.", expectedServerListString,
				remoteCacheManager.getProperties().get(SERVER_LIST));
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#setMarshaller(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void setMarshallerShouldOverrideDefaultMarshaller() throws Exception {
		final String expectedMarshaller = MockMarshaller.class.getName();
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setMarshaller(expectedMarshaller);
		objectUnderTest.setStartAutomatically(false);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setMarshaller(" + expectedMarshaller
				+ ") should have overridden property 'marshaller'. However, it didn't.", expectedMarshaller,
				remoteCacheManager.getProperties().get(MARSHALLER));
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#setAsyncExecutorFactory(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void setAsyncExecutorFactoryShouldOverrideDefaultAsyncExecutorFactory() throws Exception {
		final String expectedAsyncExecutorFactory = MockExecutorFatory.class.getName();
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setAsyncExecutorFactory(expectedAsyncExecutorFactory);
		objectUnderTest.setStartAutomatically(false);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setAsyncExecutorFactory(" + expectedAsyncExecutorFactory
				+ ") should have overridden property 'asyncExecutorFactory'. However, it didn't.",
				expectedAsyncExecutorFactory, remoteCacheManager.getProperties().get(ASYNC_EXECUTOR_FACTORY));
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#setTcpNoDelay(boolean)}.
	 * @throws Exception 
	 */
	@Test
	public final void setTcpNoDelayShouldOverrideDefaultTcpNoDelay() throws Exception {
		final boolean expectedTcpNoDelay = true;
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setTcpNoDelay(expectedTcpNoDelay);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setTcpNoDelay(" + expectedTcpNoDelay
				+ ") should have overridden property 'tcpNoDelay'. However, it didn't.",
				String.valueOf(expectedTcpNoDelay), remoteCacheManager.getProperties().get(TCP_NO_DELAY));
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#setPingOnStartup(boolean)}.
	 * @throws Exception 
	 */
	@Test
	public final void setPingOnStartupShouldOverrideDefaultPingOnStartup() throws Exception {
		final boolean expectedPingOnStartup = true;
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setPingOnStartup(expectedPingOnStartup);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setPingOnStartup(" + expectedPingOnStartup
				+ ") should have overridden property 'transportFactory'. However, it didn't.",
				String.valueOf(expectedPingOnStartup), remoteCacheManager.getProperties().get(PING_ON_STARTUP));
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#setRequestBalancingStrategy(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void setRequestBalancingStrategyShouldOverrideDefaultRequestBalancingStrategy() throws Exception {
		final String expectedRequestBalancingStrategy = MockRequestBalancingStrategy.class.getName();
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setRequestBalancingStrategy(expectedRequestBalancingStrategy);
		objectUnderTest.setStartAutomatically(false);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setRequestBalancingStrategy(" + expectedRequestBalancingStrategy
				+ ") should have overridden property 'requestBalancingStrategy'. However, it didn't.",
				expectedRequestBalancingStrategy, remoteCacheManager.getProperties().get(REQUEST_BALANCING_STRATEGY));
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#setKeySizeEstimate(int)}.
	 * @throws Exception 
	 */
	@Test
	public final void setKeySizeEstimateShouldOverrideDefaultKeySizeEstimate() throws Exception {
		final int expectedKeySizeEstimate = -123456;
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setKeySizeEstimate(expectedKeySizeEstimate);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setKeySizeEstimate(" + expectedKeySizeEstimate
				+ ") should have overridden property 'keySizeEstimate'. However, it didn't.",
				String.valueOf(expectedKeySizeEstimate), remoteCacheManager.getProperties().get(KEY_SIZE_ESTIMATE));
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#setValueSizeEstimate(int)}.
	 * @throws Exception 
	 */
	@Test
	public final void setValueSizeEstimateShouldOverrideDefaultValueSizeEstimate() throws Exception {
		final int expectedValueSizeEstimate = -3456789;
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setValueSizeEstimate(expectedValueSizeEstimate);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setValueSizeEstimate(" + expectedValueSizeEstimate
				+ ") should have overridden property 'valueSizeEstimate'. However, it didn't.",
				String.valueOf(expectedValueSizeEstimate), remoteCacheManager.getProperties().get(VALUE_SIZE_ESTIMATE));
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean#setForceReturnValues(boolean)}.
	 * @throws Exception 
	 */
	@Test
	public final void setForceReturnValuesShouldOverrideDefaultForceReturnValues() throws Exception {
		final boolean expectedForceReturnValues = true;
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setForceReturnValues(expectedForceReturnValues);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setForceReturnValue(" + expectedForceReturnValues
				+ ") should have overridden property 'forceReturnValue'. However, it didn't.",
				String.valueOf(expectedForceReturnValues), remoteCacheManager.getProperties().get(FORCE_RETURN_VALUES));
	}
}
