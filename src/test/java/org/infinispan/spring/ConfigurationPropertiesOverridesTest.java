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

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.spring.ConfigurationPropertiesOverrides;
import org.junit.Test;

/**
 * <p>
 * Test {@link ConfigurationPropertiesOverrides}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class ConfigurationPropertiesOverridesTest {

	private final Properties defaultConfigurationProperties = new ConfigurationProperties().getProperties();

	/**
	 * Test method for {@link org.infinispan.spring.ConfigurationPropertiesOverrides#isEmpty()}.
	 */
	@Test
	public final void isEmptyShouldRecognizeThatConfigurationPropertiesOverridesAreEmpty() {
		final ConfigurationPropertiesOverrides objectUnderTest = new ConfigurationPropertiesOverrides();

		assertTrue(
				"isEmpty() should have noticed that the ConfigurationPropertiesOverrides instance is indeed empty. However, it didn't.",
				objectUnderTest.isEmpty());
	}

	/**
	 * Test method for {@link org.infinispan.spring.ConfigurationPropertiesOverrides#isEmpty()}.
	 */
	@Test
	public final void isEmptyShouldRecognizeThatConfigurationPropertiesOverridesAreNotEmpty() {
		final ConfigurationPropertiesOverrides objectUnderTest = new ConfigurationPropertiesOverrides();
		objectUnderTest.setTransportFactory("test.TransportFactory");

		assertFalse(
				"isEmpty() should have noticed that the ConfigurationPropertiesOverrides instance is not empty. However, it didn't.",
				objectUnderTest.isEmpty());
	}

	/**
	 * Test method for {@link org.infinispan.spring.ConfigurationPropertiesOverrides#setTransportFactory(java.lang.String)}.
	 */
	@Test
	public final void setTransportFactoryShouldOverrideDefaultTransportFactory() {
		final String expectedTransportFactory = "test.TransportFactory";
		final ConfigurationPropertiesOverrides objectUnderTest = new ConfigurationPropertiesOverrides();

		objectUnderTest.setTransportFactory(expectedTransportFactory);
		final Properties overriddenConfigurationProperties = objectUnderTest
				.override(this.defaultConfigurationProperties);

		assertEquals("override(" + this.defaultConfigurationProperties
				+ ") should have overridden property 'transportFactory'. However, it didn't.",
				expectedTransportFactory, overriddenConfigurationProperties.getProperty(TRANSPORT_FACTORY));
	}

	/**
	 * Test method for {@link org.infinispan.spring.ConfigurationPropertiesOverrides#setServerList(java.util.Collection)}.
	 */
	@Test
	public final void setServerListShouldOverrideDefaultServerList() {
		final Collection<InetSocketAddress> expectedServerList = new ArrayList<InetSocketAddress>(1);
		expectedServerList.add(new InetSocketAddress("testhost", 4632));
		final ConfigurationPropertiesOverrides objectUnderTest = new ConfigurationPropertiesOverrides();
		final String expectedServerListString = "testhost:4632";

		objectUnderTest.setServerList(expectedServerList);
		final Properties overriddenConfigurationProperties = objectUnderTest
				.override(this.defaultConfigurationProperties);

		assertEquals("override(" + this.defaultConfigurationProperties
				+ ") should have overridden property 'transportFactory'. However, it didn't.",
				expectedServerListString, overriddenConfigurationProperties.getProperty(SERVER_LIST));
	}

	/**
	 * Test method for {@link org.infinispan.spring.ConfigurationPropertiesOverrides#setMarshaller(java.lang.String)}.
	 */
	@Test
	public final void setMarshallerShouldOverrideDefaultMarshaller() {
		final String expectedMarshaller = "test.Marshaller";
		final ConfigurationPropertiesOverrides objectUnderTest = new ConfigurationPropertiesOverrides();

		objectUnderTest.setMarshaller(expectedMarshaller);
		final Properties overriddenConfigurationProperties = objectUnderTest
				.override(this.defaultConfigurationProperties);

		assertEquals("override(" + this.defaultConfigurationProperties
				+ ") should have overridden property 'transportFactory'. However, it didn't.", expectedMarshaller,
				overriddenConfigurationProperties.getProperty(MARSHALLER));
	}

	/**
	 * Test method for {@link org.infinispan.spring.ConfigurationPropertiesOverrides#setAsyncExecutorFactory(java.lang.String)}.
	 */
	@Test
	public final void setAsyncExecutorFactoryShouldOverrideDefaultAsyncExecutorFactory() {
		final String expectedAsyncExecutorFactory = "test.AsyncExecutorFactor";
		final ConfigurationPropertiesOverrides objectUnderTest = new ConfigurationPropertiesOverrides();

		objectUnderTest.setAsyncExecutorFactory(expectedAsyncExecutorFactory);
		final Properties overriddenConfigurationProperties = objectUnderTest
				.override(this.defaultConfigurationProperties);

		assertEquals("override(" + this.defaultConfigurationProperties
				+ ") should have overridden property 'transportFactory'. However, it didn't.",
				expectedAsyncExecutorFactory, overriddenConfigurationProperties.getProperty(ASYNC_EXECUTOR_FACTORY));
	}

	/**
	 * Test method for {@link org.infinispan.spring.ConfigurationPropertiesOverrides#setTcpNoDelay(boolean)}.
	 */
	@Test
	public final void setTcpNoDelayShouldOverrideDefaultTcpNoDelay() {
		final boolean expectedTcpNoDelay = true;
		final ConfigurationPropertiesOverrides objectUnderTest = new ConfigurationPropertiesOverrides();

		objectUnderTest.setTcpNoDelay(expectedTcpNoDelay);
		final Properties overriddenConfigurationProperties = objectUnderTest
				.override(this.defaultConfigurationProperties);

		assertEquals("override(" + this.defaultConfigurationProperties
				+ ") should have overridden property 'transportFactory'. However, it didn't.",
				String.valueOf(expectedTcpNoDelay), overriddenConfigurationProperties.getProperty(TCP_NO_DELAY));
	}

	/**
	 * Test method for {@link org.infinispan.spring.ConfigurationPropertiesOverrides#setPingOnStartup(boolean)}.
	 */
	@Test
	public final void setPingOnStartupShouldOverrideDefaultPingOnStartup() {
		final boolean expectedPingOnStartup = true;
		final ConfigurationPropertiesOverrides objectUnderTest = new ConfigurationPropertiesOverrides();

		objectUnderTest.setPingOnStartup(expectedPingOnStartup);
		final Properties overriddenConfigurationProperties = objectUnderTest
				.override(this.defaultConfigurationProperties);

		assertEquals("override(" + this.defaultConfigurationProperties
				+ ") should have overridden property 'transportFactory'. However, it didn't.",
				String.valueOf(expectedPingOnStartup), overriddenConfigurationProperties.getProperty(PING_ON_STARTUP));
	}

	/**
	 * Test method for {@link org.infinispan.spring.ConfigurationPropertiesOverrides#setRequestBalancingStrategy(java.lang.String)}.
	 */
	@Test
	public final void setRequestBalancingStrategyShouldOverrideDefaultRequestBalancingStrategy() {
		final String expectedRequestBalancingStrategy = "test.RequestBalancingStrategy";
		final ConfigurationPropertiesOverrides objectUnderTest = new ConfigurationPropertiesOverrides();

		objectUnderTest.setRequestBalancingStrategy(expectedRequestBalancingStrategy);
		final Properties overriddenConfigurationProperties = objectUnderTest
				.override(this.defaultConfigurationProperties);

		assertEquals("override(" + this.defaultConfigurationProperties
				+ ") should have overridden property 'transportFactory'. However, it didn't.",
				expectedRequestBalancingStrategy,
				overriddenConfigurationProperties.getProperty(REQUEST_BALANCING_STRATEGY));
	}

	/**
	 * Test method for {@link org.infinispan.spring.ConfigurationPropertiesOverrides#setKeySizeEstimate(int)}.
	 */
	@Test
	public final void setKeySizeEstimateShouldOverrideDefaultKeySizeEstimate() {
		final int expectedKeySizeEstimate = -123456;
		final ConfigurationPropertiesOverrides objectUnderTest = new ConfigurationPropertiesOverrides();

		objectUnderTest.setKeySizeEstimate(expectedKeySizeEstimate);
		final Properties overriddenConfigurationProperties = objectUnderTest
				.override(this.defaultConfigurationProperties);

		assertEquals("override(" + this.defaultConfigurationProperties
				+ ") should have overridden property 'transportFactory'. However, it didn't.",
				String.valueOf(expectedKeySizeEstimate),
				overriddenConfigurationProperties.getProperty(KEY_SIZE_ESTIMATE));
	}

	/**
	 * Test method for {@link org.infinispan.spring.ConfigurationPropertiesOverrides#setValueSizeEstimate(int)}.
	 */
	@Test
	public final void setValueSizeEstimateShouldOverrideDefaultValueSizeEstimate() {
		final int expectedValueSizeEstimate = -3456789;
		final ConfigurationPropertiesOverrides objectUnderTest = new ConfigurationPropertiesOverrides();

		objectUnderTest.setValueSizeEstimate(expectedValueSizeEstimate);
		final Properties overriddenConfigurationProperties = objectUnderTest
				.override(this.defaultConfigurationProperties);

		assertEquals("override(" + this.defaultConfigurationProperties
				+ ") should have overridden property 'transportFactory'. However, it didn't.",
				String.valueOf(expectedValueSizeEstimate),
				overriddenConfigurationProperties.getProperty(VALUE_SIZE_ESTIMATE));
	}

	/**
	 * Test method for {@link org.infinispan.spring.ConfigurationPropertiesOverrides#setForceReturnValues(boolean)}.
	 */
	@Test
	public final void setForceReturnValuesShouldOverrideDefaultForceReturnValues() {
		final boolean expectedForceReturnValues = true;
		final ConfigurationPropertiesOverrides objectUnderTest = new ConfigurationPropertiesOverrides();

		objectUnderTest.setForceReturnValues(expectedForceReturnValues);
		final Properties overriddenConfigurationProperties = objectUnderTest
				.override(this.defaultConfigurationProperties);

		assertEquals("override(" + this.defaultConfigurationProperties
				+ ") should have overridden property 'transportFactory'. However, it didn't.",
				String.valueOf(expectedForceReturnValues),
				overriddenConfigurationProperties.getProperty(FORCE_RETURN_VALUES));
	}
}
