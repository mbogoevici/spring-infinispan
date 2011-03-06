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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.infinispan.Cache;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.Test;

/**
 * <p>
 * Test {@link NativeCacheFactoryBean}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class NativeCacheFactoryBeanTest {

	/**
	 * Test method for {@link org.infinispan.spring.NativeCacheFactoryBean#afterPropertiesSet()}.
	 * @throws Exception 
	 */
	@Test(expected = IllegalStateException.class)
	public final void nativeCacheFactoryBeanShouldRecognizeThatNoCacheContainerHasBeenSet() throws Exception {
		final NativeCacheFactoryBean objectUnderTest = new NativeCacheFactoryBean();
		objectUnderTest.setCacheName("test.cache.Name");
		objectUnderTest.setBeanName("test.bean.Name");
		objectUnderTest.afterPropertiesSet();
	}

	/**
	 * Test method for {@link org.infinispan.spring.NativeCacheFactoryBean#setBeanName(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void nativeCacheFactoryBeanShouldUseBeanNameAsCacheNameIfNoCacheNameHasBeenSet() throws Exception {
		final String beanName = "test.bean.Name";

		final NativeCacheFactoryBean objectUnderTest = new NativeCacheFactoryBean();
		objectUnderTest.setInfinispanCacheContainer(new DefaultCacheManager());
		objectUnderTest.setBeanName(beanName);
		objectUnderTest.afterPropertiesSet();

		final Cache<Object, Object> cache = objectUnderTest.getObject();

		assertEquals("NativeCacheFactoryBean should have used its bean name [" + beanName
				+ "] as the name of the created cache. However, it didn't.", beanName, cache.getName());
	}

	/**
	 * Test method for {@link org.infinispan.spring.NativeCacheFactoryBean#setCacheName(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public final void nativeCacheFactoryBeanShouldPreferExplicitCacheNameToBeanName() throws Exception {
		final String cacheName = "test.cache.Name";
		final String beanName = "test.bean.Name";

		final NativeCacheFactoryBean objectUnderTest = new NativeCacheFactoryBean();
		objectUnderTest.setInfinispanCacheContainer(new DefaultCacheManager());
		objectUnderTest.setCacheName(cacheName);
		objectUnderTest.setBeanName(beanName);
		objectUnderTest.afterPropertiesSet();

		final Cache<Object, Object> cache = objectUnderTest.getObject();

		assertEquals("NativeCacheFactoryBean should have preferred its cache name [" + cacheName
				+ "] as the name of the created cache. However, it didn't.", cacheName, cache.getName());
	}

	/**
	 * Test method for {@link org.infinispan.spring.NativeCacheFactoryBean#getObject()}.
	 * @throws Exception 
	 */
	@Test
	public final void nativeCacheFactoryBeanShouldProduceANonNullInfinispanCache() throws Exception {
		final String cacheName = "test.cache.Name";
		final String beanName = "test.bean.Name";

		final NativeCacheFactoryBean objectUnderTest = new NativeCacheFactoryBean();
		objectUnderTest.setInfinispanCacheContainer(new DefaultCacheManager());
		objectUnderTest.setCacheName(cacheName);
		objectUnderTest.setBeanName(beanName);
		objectUnderTest.afterPropertiesSet();

		final Cache<Object, Object> cache = objectUnderTest.getObject();

		assertNotNull("NativeCacheFactoryBean should have produced a proper Infinispan cache. "
				+ "However, it produced a null Infinispan cache.", cache);
	}

	/**
	 * Test method for {@link org.infinispan.spring.NativeCacheFactoryBean#isSingleton()}.
	 */
	@Test
	public final void nativeCacheFactoryBeanShouldDeclareItselfToBeSingleton() {
		final NativeCacheFactoryBean objectUnderTest = new NativeCacheFactoryBean();

		assertTrue("NativeCacheFactoryBean should declare itself to produce a singleton. However, it didn't.",
				objectUnderTest.isSingleton());
	}

	/**
	 * Test method for {@link org.infinispan.spring.NativeCacheFactoryBean#destroy()}.
	 * @throws Exception 
	 */
	@Test
	public final void nativeCacheFactoryBeanShouldStopTheCreatedInfinispanCacheWhenItIsDestroyed() throws Exception {
		final String cacheName = "test.cache.Name";
		final String beanName = "test.bean.Name";

		final NativeCacheFactoryBean objectUnderTest = new NativeCacheFactoryBean();
		objectUnderTest.setInfinispanCacheContainer(new DefaultCacheManager());
		objectUnderTest.setCacheName(cacheName);
		objectUnderTest.setBeanName(beanName);
		objectUnderTest.afterPropertiesSet();

		final Cache<Object, Object> cache = objectUnderTest.getObject();
		objectUnderTest.destroy();

		assertEquals("NativeCacheFactoryBean should have stopped the created Infinispan cache when being destroyed. "
				+ "However, the created Infinispan is not yet terminated.", ComponentStatus.TERMINATED,
				cache.getStatus());
	}

}
