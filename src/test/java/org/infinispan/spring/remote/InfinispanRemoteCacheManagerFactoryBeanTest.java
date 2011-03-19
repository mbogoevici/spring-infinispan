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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.infinispan.client.hotrod.RemoteCacheManager;
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
}
