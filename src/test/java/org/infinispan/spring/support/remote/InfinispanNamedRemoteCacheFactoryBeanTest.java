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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.infinispan.Cache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>
 * Test {@link InfinispanNamedRemoteCacheFactoryBean}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanNamedRemoteCacheFactoryBeanTest {

	private static final RemoteCacheManager DEFAULT_CACHE_MANAGER = new RemoteCacheManager(false);

	@BeforeClass
	public static void startCacheManagers() {
		DEFAULT_CACHE_MANAGER.start();
	}

	@AfterClass
	public static void stopCacheManagers() {
		DEFAULT_CACHE_MANAGER.stop();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedRemoteCacheFactoryBean#afterPropertiesSet()}.
	 * @throws Exception 
	 */
	@Test(expected = IllegalStateException.class)
	public final void infinispanNamedRemoteCacheFactoryBeanShouldRecognizeThatNoCacheContainerHasBeenSet()
			throws Exception {
		final InfinispanNamedRemoteCacheFactoryBean<String, Object> objectUnderTest = new InfinispanNamedRemoteCacheFactoryBean<String, Object>();
		objectUnderTest.setCacheName("test.cache.Name");
		objectUnderTest.setBeanName("test.bean.Name");
		objectUnderTest.afterPropertiesSet();
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedRemoteCacheFactoryBean#setBeanName(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanNamedRemoteCacheFactoryBeanShouldUseBeanNameAsCacheNameIfNoCacheNameHasBeenSet()
			throws Exception {
		final String beanName = "test.bean.Name";

		final InfinispanNamedRemoteCacheFactoryBean<String, Object> objectUnderTest = new InfinispanNamedRemoteCacheFactoryBean<String, Object>();
		objectUnderTest.setInfinispanRemoteCacheManager(DEFAULT_CACHE_MANAGER);
		objectUnderTest.setBeanName(beanName);
		objectUnderTest.afterPropertiesSet();

		final Cache<String, Object> cache = objectUnderTest.getObject();

		assertEquals("InfinispanNamedRemoteCacheFactoryBean should have used its bean name [" + beanName
				+ "] as the name of the created cache. However, it didn't.", beanName, cache.getName());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedRemoteCacheFactoryBean#setCacheName(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanNamedRemoteCacheFactoryBeanShouldPreferExplicitCacheNameToBeanName() throws Exception {
		final String cacheName = "test.cache.Name";
		final String beanName = "test.bean.Name";

		final InfinispanNamedRemoteCacheFactoryBean<String, Object> objectUnderTest = new InfinispanNamedRemoteCacheFactoryBean<String, Object>();
		objectUnderTest.setInfinispanRemoteCacheManager(DEFAULT_CACHE_MANAGER);
		objectUnderTest.setCacheName(cacheName);
		objectUnderTest.setBeanName(beanName);
		objectUnderTest.afterPropertiesSet();

		final Cache<String, Object> cache = objectUnderTest.getObject();

		assertEquals("InfinispanNamedRemoteCacheFactoryBean should have preferred its cache name [" + cacheName
				+ "] as the name of the created cache. However, it didn't.", cacheName, cache.getName());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedRemoteCacheFactoryBean#getObjectType()}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanNamedRemoteCacheFactoryBeanShouldReportTheMostDerivedObjectType() throws Exception {
		final InfinispanNamedRemoteCacheFactoryBean<Object, Object> objectUnderTest = new InfinispanNamedRemoteCacheFactoryBean<Object, Object>();
		objectUnderTest.setInfinispanRemoteCacheManager(DEFAULT_CACHE_MANAGER);
		objectUnderTest.setBeanName("test.bean.Name");
		objectUnderTest.afterPropertiesSet();

		assertEquals("getObjectType() should have returned the most derived class of the actual Cache "
				+ "implementation returned from getObject(). However, it didn't.", objectUnderTest.getObject()
				.getClass(), objectUnderTest.getObjectType());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedRemoteCacheFactoryBean#getObject()}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanNamedRemoteCacheFactoryBeanShouldProduceANonNullInfinispanCache() throws Exception {
		final String cacheName = "test.cache.Name";
		final String beanName = "test.bean.Name";

		final InfinispanNamedRemoteCacheFactoryBean<String, Object> objectUnderTest = new InfinispanNamedRemoteCacheFactoryBean<String, Object>();
		objectUnderTest.setInfinispanRemoteCacheManager(DEFAULT_CACHE_MANAGER);
		objectUnderTest.setCacheName(cacheName);
		objectUnderTest.setBeanName(beanName);
		objectUnderTest.afterPropertiesSet();

		final Cache<String, Object> cache = objectUnderTest.getObject();

		assertNotNull("InfinispanNamedRemoteCacheFactoryBean should have produced a proper Infinispan cache. "
				+ "However, it produced a null Infinispan cache.", cache);
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedRemoteCacheFactoryBean#isSingleton()}.
	 */
	@Test
	public final void infinispanNamedRemoteCacheFactoryBeanShouldDeclareItselfToBeSingleton() {
		final InfinispanNamedRemoteCacheFactoryBean<String, Object> objectUnderTest = new InfinispanNamedRemoteCacheFactoryBean<String, Object>();

		assertTrue(
				"InfinispanNamedRemoteCacheFactoryBean should declare itself to produce a singleton. However, it didn't.",
				objectUnderTest.isSingleton());
	}
}
