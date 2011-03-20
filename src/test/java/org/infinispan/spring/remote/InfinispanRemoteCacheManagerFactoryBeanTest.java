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

package org.infinispan.spring.remote;

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
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.client.hotrod.impl.transport.Transport;
import org.infinispan.client.hotrod.impl.transport.TransportFactory;
import org.infinispan.client.hotrod.impl.transport.tcp.RequestBalancingStrategy;
import org.infinispan.executors.ExecutorFactory;
import org.infinispan.io.ByteBuffer;
import org.infinispan.marshall.Marshaller;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * <p>
 * Test {@link InfinispanRemoteCacheManagerFactoryBean}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanRemoteCacheManagerFactoryBeanTest {

	private static final Resource HOTROD_CLIENT_PROPERTIES_LOCATION = new ClassPathResource("hotrod-client.properties",
			InfinispanRemoteCacheManagerFactoryBeanTest.class);

	/**
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#afterPropertiesSet()}.
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
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#afterPropertiesSet()}.
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
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#getObjectType()}.
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
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#getObject()}.
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
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#isSingleton()}.
	 */
	@Test
	public final void isSingletonShouldAlwaysReturnTrue() {
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();

		assertTrue(
				"isSingleton() should always return true since each InfinispanRemoteCacheManagerFactoryBean will always produce "
						+ "the same RemoteCacheManager instance. However,it returned false.",
				objectUnderTest.isSingleton());
	}

	/**
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#destroy()}.
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
						+ "InfinispanRemoteCacheManagerFactoryBean. However, the produced RemoteCacheManager is still running. ",
				remoteCacheManager.isStarted());
	}

	/**
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#setConfigurationProperties(java.util.Properties)}.
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
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#setConfigurationPropertiesFileLocation(org.springframework.core.io.Resource)}.
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
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#setStartAutomatically(boolean)}.
	 * @throws Exception 
	 */
	@Test
	public final void shouldProduceAStoppedCacheIfStartAutomaticallyIsSetToFalse() throws Exception {
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setStartAutomatically(false);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManagerExpectedToBeInStateStopped = objectUnderTest.getObject();

		assertFalse(
				"InfinispanRemoteCacheManagerFactoryBean should have produced a RemoteCacheManager that is initially in state stopped "
						+ "since property 'startAutomatically' has been set to false. However, the produced RemoteCacheManager is already started.",
				remoteCacheManagerExpectedToBeInStateStopped.isStarted());
	}

	/**
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#setTransportFactory(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void setTransportFactoryShouldOverrideDefaultTransportFactory() throws Exception {
		final String expectedTransportFactory = DummyTransportFactory.class.getName();
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setTransportFactory(expectedTransportFactory);
		objectUnderTest.setStartAutomatically(false); // Otherwise, RemoteCacheManager will try to actually use our DummyTransportFactory
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setTransportFactory(" + expectedTransportFactory
				+ ") should have overridden property 'transportFactory'. However, it didn't.",
				expectedTransportFactory, remoteCacheManager.getProperties().get(TRANSPORT_FACTORY));
	}

	public final class DummyTransportFactory implements TransportFactory {
		@Override
		public Transport getTransport() {
			return null;
		}

		@Override
		public void releaseTransport(final Transport transport) {
		}

		@Override
		public void start(final ConfigurationProperties props,
				final Collection<InetSocketAddress> staticConfiguredServers, final AtomicInteger topologyId) {
		}

		@Override
		public void updateServers(final Collection<InetSocketAddress> newServers) {
		}

		@Override
		public void destroy() {
		}

		@Override
		public void updateHashFunction(final LinkedHashMap<InetSocketAddress, Integer> servers2HashCode,
				final int numKeyOwners, final short hashFunctionVersion, final int hashSpace) {
		}

		@Override
		public Transport getTransport(final byte[] key) {
			return null;
		}

		@Override
		public boolean isTcpNoDelay() {
			return false;
		}

		@Override
		public int getTransportCount() {
			return 0;
		}
	}

	/**
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#setServerList(java.util.Collection)}.
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
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#setMarshaller(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void setMarshallerShouldOverrideDefaultMarshaller() throws Exception {
		final String expectedMarshaller = DummyMarshaller.class.getName();
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setMarshaller(expectedMarshaller);
		objectUnderTest.setStartAutomatically(false);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setMarshaller(" + expectedMarshaller
				+ ") should have overridden property 'marshaller'. However, it didn't.", expectedMarshaller,
				remoteCacheManager.getProperties().get(MARSHALLER));
	}

	public final class DummyMarshaller implements Marshaller {

		@Override
		public byte[] objectToByteBuffer(final Object obj, final int estimatedSize) throws IOException,
				InterruptedException {
			return null;
		}

		@Override
		public byte[] objectToByteBuffer(final Object obj) throws IOException, InterruptedException {
			return null;
		}

		@Override
		public Object objectFromByteBuffer(final byte[] buf) throws IOException, ClassNotFoundException {
			return null;
		}

		@Override
		public Object objectFromByteBuffer(final byte[] buf, final int offset, final int length) throws IOException,
				ClassNotFoundException {
			return null;
		}

		@Override
		public ByteBuffer objectToBuffer(final Object o) throws IOException, InterruptedException {
			return null;
		}

		@Override
		public boolean isMarshallable(final Object o) {
			return false;
		}
	}

	/**
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#setAsyncExecutorFactory(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void setAsyncExecutorFactoryShouldOverrideDefaultAsyncExecutorFactory() throws Exception {
		final String expectedAsyncExecutorFactory = DummyAsyncExecutorFactory.class.getName();
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setAsyncExecutorFactory(expectedAsyncExecutorFactory);
		objectUnderTest.setStartAutomatically(false);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setAsyncExecutorFactory(" + expectedAsyncExecutorFactory
				+ ") should have overridden property 'asyncExecutorFactory'. However, it didn't.",
				expectedAsyncExecutorFactory, remoteCacheManager.getProperties().get(ASYNC_EXECUTOR_FACTORY));
	}

	public final class DummyAsyncExecutorFactory implements ExecutorFactory {

		@Override
		public ExecutorService getExecutor(final Properties p) {
			return null;
		}
	}

	/**
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#setTcpNoDelay(boolean)}.
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
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#setPingOnStartup(boolean)}.
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
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#setRequestBalancingStrategy(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void setRequestBalancingStrategyShouldOverrideDefaultRequestBalancingStrategy() throws Exception {
		final String expectedRequestBalancingStrategy = DummyRequestBalancingStrategy.class.getName();
		final InfinispanRemoteCacheManagerFactoryBean objectUnderTest = new InfinispanRemoteCacheManagerFactoryBean();
		objectUnderTest.setRequestBalancingStrategy(expectedRequestBalancingStrategy);
		objectUnderTest.setStartAutomatically(false);
		objectUnderTest.afterPropertiesSet();

		final RemoteCacheManager remoteCacheManager = objectUnderTest.getObject();

		assertEquals("setRequestBalancingStrategy(" + expectedRequestBalancingStrategy
				+ ") should have overridden property 'requestBalancingStrategy'. However, it didn't.",
				expectedRequestBalancingStrategy, remoteCacheManager.getProperties().get(REQUEST_BALANCING_STRATEGY));
	}

	public final class DummyRequestBalancingStrategy implements RequestBalancingStrategy {

		@Override
		public void setServers(final Collection<InetSocketAddress> servers) {
		}

		@Override
		public InetSocketAddress nextServer() {
			return null;
		}
	}

	/**
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#setKeySizeEstimate(int)}.
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
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#setValueSizeEstimate(int)}.
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
	 * Test method for {@link org.infinispan.spring.remote.InfinispanRemoteCacheManagerFactoryBean#setForceReturnValues(boolean)}.
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
