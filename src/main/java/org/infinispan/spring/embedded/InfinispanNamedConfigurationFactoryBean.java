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

import org.springframework.beans.factory.BeanNameAware;

/**
 * <p>
 * A {@link org.springframework.beans.factory.FactoryBean <code>FactoryBean</code>} for creating an INFINISPAN 
 * configuration that is specific to a <em>named</em> INFINISPAN {@link org.infinispan.Cache <code>Cache</code>}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class InfinispanNamedConfigurationFactoryBean extends InfinispanConfigurationFactoryBean implements
		BeanNameAware {

	private String cacheName;

	private String beanName;

	/**
	 * Sets the name of the {@link org.infinispan.Cache <code>Cache</code>} to create a
	 * {@link org.infinispan.config.Configuration <code>Configuration</code>} for. If this property is not
	 * set <code>InfinispanNamedConfigurationFactoryBean</code> will fall back to using its 
	 * {@link #setBeanName(String) <code>beanName</code>} as the cache name.
	 * 
	 * @param cacheName The name of the cache to create a configuration for
	 */
	public void setCacheName(final String cacheName) {
		this.cacheName = cacheName;
	}

	/**
	 * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
	 */
	@Override
	public void setBeanName(final String name) {
		this.beanName = name;
	}

}
