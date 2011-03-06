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
import static org.junit.Assert.assertTrue;

import org.infinispan.Cache;
import org.infinispan.config.Configuration;
import org.infinispan.config.Configuration.CacheMode;
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
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setDefaultConfigurationLocation(org.springframework.core.io.Resource)}.
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
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanEmbeddedCacheManagerFactoryBean#setDefaultConfigurationLocation(org.springframework.core.io.Resource)}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanEmbeddedCacheManagerFactoryBeanShouldCreateACustomizedCacheManagerIfGivenADefaultConfigurationLocation()
			throws Exception {
		final Resource infinispanConfig = new ClassPathResource(NAMED_ASYNC_CACHE_CONFIG_LOCATION, getClass());

		final InfinispanEmbeddedCacheManagerFactoryBean objectUnderTest = new InfinispanEmbeddedCacheManagerFactoryBean();
		objectUnderTest.setDefaultConfigurationLocation(infinispanConfig);
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
	}

}
