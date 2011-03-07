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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * <p>
 * Test {@link InfinispanNamedConfigurationFactoryBean}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanNamedConfigurationFactoryBeanTest {

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedConfigurationFactoryBean#afterPropertiesSet()}.
	 */
	@Test
	public final void infinispanNamedConfigurationFactoryBeanShouldUseBeanNameAsCacheNameIfNoCacheNameHasBeenSet()
			throws Exception {
		final String beanName = "test.bean.Name";

		final InfinispanNamedConfigurationFactoryBean objectUnderTest = new InfinispanNamedConfigurationFactoryBean();
		objectUnderTest.setBeanName(beanName);
		objectUnderTest.afterPropertiesSet();

		final NamedConfiguration namedConfiguration = objectUnderTest.getObject();

		assertEquals("InfinispanNamedConfigurationFactoryBean should have used its bean name [" + beanName
				+ "] as the name of the created configuration. However, it didn't.", beanName,
				namedConfiguration.getName());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedConfigurationFactoryBean#afterPropertiesSet()}.
	 */
	@Test
	public final void infinispanNamedConfigurationFactoryBeanShouldPreferExplicitCacheNameToBeanName() throws Exception {
		final String cacheName = "test.cache.Name";
		final String beanName = "test.bean.Name";

		final InfinispanNamedConfigurationFactoryBean objectUnderTest = new InfinispanNamedConfigurationFactoryBean();
		objectUnderTest.setCacheName(cacheName);
		objectUnderTest.setBeanName(beanName);
		objectUnderTest.afterPropertiesSet();

		final NamedConfiguration namedConfiguration = objectUnderTest.getObject();

		assertEquals("InfinispanNamedConfigurationFactoryBean should have preferred its cache name [" + cacheName
				+ "] as the name of the created configuration. However, it didn't.", cacheName,
				namedConfiguration.getName());
	}

	/**
	 * Test method for {@link org.infinispan.spring.embedded.InfinispanNamedConfigurationFactoryBean#isSingleton()}.
	 */
	@Test
	public final void infinispanNamedConfigurationFactoryBeanShouldDeclareItselfToBeSingleton() {
		final InfinispanNamedConfigurationFactoryBean objectUnderTest = new InfinispanNamedConfigurationFactoryBean();

		assertTrue(
				"InfinispanNamedConfigurationFactoryBean should declare itself to produce a singleton. However, it didn't.",
				objectUnderTest.isSingleton());
	}

}
