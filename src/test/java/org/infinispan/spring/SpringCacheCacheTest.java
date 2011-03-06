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

import org.infinispan.manager.DefaultCacheManager;
import org.springframework.cache.Cache;

/**
 * <p>
 * An integration test for {@link SpringCache}.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class SpringCacheCacheTest extends AbstractNativeCacheTest<org.infinispan.Cache<Object, Object>> {

	@Override
	protected org.infinispan.Cache<Object, Object> createNativeCache() throws Exception {
		final InfinispanCacheFactoryBean fb = new InfinispanCacheFactoryBean();
		fb.setInfinispanCacheContainer(new DefaultCacheManager());
		fb.setBeanName(CACHE_NAME);
		fb.setCacheName(CACHE_NAME);
		fb.afterPropertiesSet();
		return fb.getObject();
	}

	@Override
	protected Cache<Object, Object> createCache(final org.infinispan.Cache<Object, Object> nativeCache) {
		return new SpringCache(nativeCache);
	}
}
