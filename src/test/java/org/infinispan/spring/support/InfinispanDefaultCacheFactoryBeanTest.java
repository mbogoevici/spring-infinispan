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

package org.infinispan.spring.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.infinispan.Cache;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.spring.support.InfinispanDefaultCacheFactoryBean;
import org.junit.Test;

/**
 * <p>
 * Test {@link InfinispanDefaultCacheFactoryBean}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanDefaultCacheFactoryBeanTest {

	/**
	 * Test method for {@link org.infinispan.spring.support.InfinispanDefaultCacheFactoryBean#afterPropertiesSet()}.
	 * @throws Exception 
	 */
	@Test(expected = IllegalStateException.class)
	public final void afterPropertiesSetShouldThrowAnIllegalStateExceptionIfNoCacheContainerHasBeenSet()
			throws Exception {
		final InfinispanDefaultCacheFactoryBean objectUnderTest = new InfinispanDefaultCacheFactoryBean();
		objectUnderTest.afterPropertiesSet();
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.InfinispanDefaultCacheFactoryBean#getObject()}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanDefaultCacheFactoryBeanShouldProduceANonNullInfinispanCache() throws Exception {
		final InfinispanDefaultCacheFactoryBean objectUnderTest = new InfinispanDefaultCacheFactoryBean();
		objectUnderTest.setInfinispanCacheContainer(new DefaultCacheManager());
		objectUnderTest.afterPropertiesSet();

		final Cache<Object, Object> cache = objectUnderTest.getObject();

		assertNotNull("InfinispanDefaultCacheFactoryBean should have produced a proper Infinispan cache. "
				+ "However, it produced a null Infinispan cache.", cache);
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.InfinispanDefaultCacheFactoryBean#getObjectType()}.
	 * @throws Exception 
	 */
	@Test
	public final void getObjectTypeShouldReturnTheMostDerivedTypeOfTheProducedInfinispanCache() throws Exception {
		final InfinispanDefaultCacheFactoryBean objectUnderTest = new InfinispanDefaultCacheFactoryBean();
		objectUnderTest.setInfinispanCacheContainer(new DefaultCacheManager());
		objectUnderTest.afterPropertiesSet();

		final Class<? extends Cache> cacheType = objectUnderTest.getObjectType();

		assertEquals("getObjectType() should have returned the produced INFINISPAN cache's most derived type. "
				+ "However, it returned a more generic type.", objectUnderTest.getObject().getClass(), cacheType);
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.InfinispanDefaultCacheFactoryBean#isSingleton()}.
	 */
	@Test
	public final void infinispanDefaultCacheFactoryBeanShouldDeclareItselfToBeSingleton() {
		final InfinispanDefaultCacheFactoryBean objectUnderTest = new InfinispanDefaultCacheFactoryBean();

		assertTrue(
				"InfinispanDefaultCacheFactoryBean should declare itself to produce a singleton. However, it didn't.",
				objectUnderTest.isSingleton());
	}

	/**
	 * Test method for {@link org.infinispan.spring.support.InfinispanDefaultCacheFactoryBean#destroy()}.
	 * @throws Exception 
	 */
	@Test
	public final void infinispanDefaultCacheFactoryBeanShouldStopTheCreatedInfinispanCacheWhenItIsDestroyed()
			throws Exception {
		final InfinispanDefaultCacheFactoryBean objectUnderTest = new InfinispanDefaultCacheFactoryBean();
		objectUnderTest.setInfinispanCacheContainer(new DefaultCacheManager());
		objectUnderTest.afterPropertiesSet();

		final Cache<Object, Object> cache = objectUnderTest.getObject();
		objectUnderTest.destroy();

		assertEquals(
				"InfinispanDefaultCacheFactoryBean should have stopped the created Infinispan cache when being destroyed. "
						+ "However, the created Infinispan is not yet terminated.", ComponentStatus.TERMINATED,
				cache.getStatus());
	}
}
