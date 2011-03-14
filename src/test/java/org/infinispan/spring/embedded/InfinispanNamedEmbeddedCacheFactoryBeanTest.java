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

import java.io.IOException;
import java.io.InputStream;

import org.infinispan.Cache;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * <p>
 * Test {@link InfinispanNamedEmbeddedCacheFactoryBean}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanNamedEmbeddedCacheFactoryBeanTest {

	private static final String CACHE_NAME_FROM_CONFIGURATION_FILE = "asyncCache";

	private static final ClassPathResource NAMED_ASYNC_CACHE_CONFIG_LOCATION = new ClassPathResource(
			"named-async-cache.xml", InfinispanNamedEmbeddedCacheFactoryBeanTest.class);

	private static final DefaultCacheManager PRECONFIGURED_DEFAULT_CACHE_MANAGER;

	static {
		InputStream configStream = null;
		try {
			configStream = NAMED_ASYNC_CACHE_CONFIG_LOCATION.getInputStream();
			PRECONFIGURED_DEFAULT_CACHE_MANAGER = new DefaultCacheManager(configStream);
		} catch (final IOException e) {
			throw new ExceptionInInitializerError(e);
		} finally {
			if (configStream != null) {
				try {
					configStream.close();
				} catch (final IOException e) {
					// Ignore
				}
			}
		}
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedEmbeddedCacheFactoryBean#afterPropertiesSet()}.
	 * @throws Exception 
	 */
	@Test(expected = IllegalStateException.class)
	public final void infinispanNamedEmbeddedCacheFactoryBeanShouldRecognizeThatNoCacheContainerHasBeenSet()
			throws Exception {
		final InfinispanNamedEmbeddedCacheFactoryBean objectUnderTest = new InfinispanNamedEmbeddedCacheFactoryBean();
		objectUnderTest.setCacheName("test.cache.Name");
		objectUnderTest.setBeanName("test.bean.Name");
		objectUnderTest.afterPropertiesSet();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedEmbeddedCacheFactoryBean#setBeanName(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanNamedEmbeddedCacheFactoryBeanShouldUseBeanNameAsCacheNameIfNoCacheNameHasBeenSet()
			throws Exception {
		final String beanName = "test.bean.Name";

		final InfinispanNamedEmbeddedCacheFactoryBean objectUnderTest = new InfinispanNamedEmbeddedCacheFactoryBean();
		objectUnderTest.setInfinispanEmbeddedCacheManager(new DefaultCacheManager());
		objectUnderTest.setBeanName(beanName);
		objectUnderTest.afterPropertiesSet();

		final Cache<Object, Object> cache = objectUnderTest.getObject();

		assertEquals("InfinispanNamedEmbeddedCacheFactoryBean should have used its bean name [" + beanName
				+ "] as the name of the created cache. However, it didn't.", beanName, cache.getName());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedEmbeddedCacheFactoryBean#setCacheName(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanNamedEmbeddedCacheFactoryBeanShouldPreferExplicitCacheNameToBeanName() throws Exception {
		final String cacheName = "test.cache.Name";
		final String beanName = "test.bean.Name";

		final InfinispanNamedEmbeddedCacheFactoryBean objectUnderTest = new InfinispanNamedEmbeddedCacheFactoryBean();
		objectUnderTest.setInfinispanEmbeddedCacheManager(new DefaultCacheManager());
		objectUnderTest.setCacheName(cacheName);
		objectUnderTest.setBeanName(beanName);
		objectUnderTest.afterPropertiesSet();

		final Cache<Object, Object> cache = objectUnderTest.getObject();

		assertEquals("InfinispanNamedEmbeddedCacheFactoryBean should have preferred its cache name [" + cacheName
				+ "] as the name of the created cache. However, it didn't.", cacheName, cache.getName());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedEmbeddedCacheFactoryBean#getObject()}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanNamedEmbeddedCacheFactoryBeanShouldProduceANonNullInfinispanCache() throws Exception {
		final String cacheName = "test.cache.Name";
		final String beanName = "test.bean.Name";

		final InfinispanNamedEmbeddedCacheFactoryBean objectUnderTest = new InfinispanNamedEmbeddedCacheFactoryBean();
		objectUnderTest.setInfinispanEmbeddedCacheManager(new DefaultCacheManager());
		objectUnderTest.setCacheName(cacheName);
		objectUnderTest.setBeanName(beanName);
		objectUnderTest.afterPropertiesSet();

		final Cache<Object, Object> cache = objectUnderTest.getObject();

		assertNotNull("InfinispanNamedEmbeddedCacheFactoryBean should have produced a proper Infinispan cache. "
				+ "However, it produced a null Infinispan cache.", cache);
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedEmbeddedCacheFactoryBean#isSingleton()}.
	 */
	@Test
	public final void infinispanNamedEmbeddedCacheFactoryBeanShouldDeclareItselfToBeSingleton() {
		final InfinispanNamedEmbeddedCacheFactoryBean objectUnderTest = new InfinispanNamedEmbeddedCacheFactoryBean();

		assertTrue(
				"InfinispanNamedEmbeddedCacheFactoryBean should declare itself to produce a singleton. However, it didn't.",
				objectUnderTest.isSingleton());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedEmbeddedCacheFactoryBean#destroy()}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanNamedEmbeddedCacheFactoryBeanShouldStopTheCreatedInfinispanCacheWhenItIsDestroyed()
			throws Exception {
		final String cacheName = "test.cache.Name";
		final String beanName = "test.bean.Name";

		final InfinispanNamedEmbeddedCacheFactoryBean objectUnderTest = new InfinispanNamedEmbeddedCacheFactoryBean();
		objectUnderTest.setInfinispanEmbeddedCacheManager(new DefaultCacheManager());
		objectUnderTest.setCacheName(cacheName);
		objectUnderTest.setBeanName(beanName);
		objectUnderTest.afterPropertiesSet();

		final Cache<Object, Object> cache = objectUnderTest.getObject();
		objectUnderTest.destroy();

		assertEquals(
				"InfinispanNamedEmbeddedCacheFactoryBean should have stopped the created Infinispan cache when being destroyed. "
						+ "However, the created Infinispan is not yet terminated.", ComponentStatus.TERMINATED,
				cache.getStatus());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedEmbeddedCacheFactoryBean#afterPropertiesSet()}.
	 * @throws Exception 
	 */
	@Test(expected = IllegalStateException.class)
	public final void infinispanNamedEmbeddedCacheFactoryBeanShouldRejectConfigurationTemplateModeNONEIfCacheConfigurationAlreadyExistsInConfigurationFile()
			throws Exception {
		final InfinispanNamedEmbeddedCacheFactoryBean objectUnderTest = new InfinispanNamedEmbeddedCacheFactoryBean();
		objectUnderTest.setInfinispanEmbeddedCacheManager(PRECONFIGURED_DEFAULT_CACHE_MANAGER);
		objectUnderTest.setCacheName(CACHE_NAME_FROM_CONFIGURATION_FILE);
		objectUnderTest.setBeanName(CACHE_NAME_FROM_CONFIGURATION_FILE);
		objectUnderTest.setConfigurationTemplateMode("NONE");
		objectUnderTest.afterPropertiesSet();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedEmbeddedCacheFactoryBean#afterPropertiesSet()}.
	 * @throws Exception 
	 */
	@Test(expected = IllegalStateException.class)
	public final void infinispanNamedEmbeddedCacheFactoryBeanShouldRejectConfigurationTemplateModeDEFAULTIfCacheConfigurationAlreadyExistsInConfigurationFile()
			throws Exception {
		final InfinispanNamedEmbeddedCacheFactoryBean objectUnderTest = new InfinispanNamedEmbeddedCacheFactoryBean();
		objectUnderTest.setInfinispanEmbeddedCacheManager(PRECONFIGURED_DEFAULT_CACHE_MANAGER);
		objectUnderTest.setCacheName(CACHE_NAME_FROM_CONFIGURATION_FILE);
		objectUnderTest.setBeanName(CACHE_NAME_FROM_CONFIGURATION_FILE);
		objectUnderTest.setConfigurationTemplateMode("DEFAULT");
		objectUnderTest.afterPropertiesSet();
	}
}
