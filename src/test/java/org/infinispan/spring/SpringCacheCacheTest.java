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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.spring.embedded.InfinispanNamedEmbeddedCacheFactoryBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.Cache;

/**
 * <p>
 * An integration test for {@link SpringCache}.
 * </p>
 * <p>
 * <strong>CREDITS</strong> This test is a shameless copy of Costin Leau's
 * <code>org.springframework.cache.vendor.AbstractNativeCacheTest</code>. The additions
 * made to it are minor.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public class SpringCacheCacheTest {

	protected final static String CACHE_NAME = "testCache";

	private org.infinispan.Cache<Object, Object> nativeCache;

	private Cache<Object, Object> cache;

	@Before
	public void setUp() throws Exception {
		this.nativeCache = createNativeCache();
		this.cache = createCache(this.nativeCache);
		this.cache.clear();
	}

	@Test
	public void testCacheName() throws Exception {
		assertEquals(CACHE_NAME, this.cache.getName());
	}

	@Test
	public void testNativeCache() throws Exception {
		assertSame(this.nativeCache, this.cache.getNativeCache());
	}

	@Test
	public void testCachePut() throws Exception {
		final Object key = "enescu";
		final Object value = "george";

		assertNull(this.cache.get(key));
		this.cache.put(key, value);
		assertEquals(value, this.cache.get(key));
	}

	@Test
	public void testCacheContains() throws Exception {
		final Object key = "enescu";
		final Object value = "george";

		this.cache.put(key, value);

		assertTrue(this.cache.containsKey(key));
	}

	@Test
	public void testCacheRemove() throws Exception {
		final Object key = "enescu";
		final Object value = "george";

		assertNull(this.cache.get(key));
		this.cache.put(key, value);
		assertEquals(value, this.cache.remove(key));
		assertNull(this.cache.get(key));
	}

	@Test
	public void testCacheClear() throws Exception {
		assertNull(this.cache.get("enescu"));
		this.cache.put("enescu", "george");
		assertNull(this.cache.get("vlaicu"));
		this.cache.put("vlaicu", "aurel");
		this.cache.clear();
		assertNull(this.cache.get("vlaicu"));
		assertNull(this.cache.get("enescu"));
	}

	@Test
	public void testPutIfAbsent() throws Exception {
		final Object key = "enescu";
		final Object value1 = "george";
		final Object value2 = "geo";

		assertNull(this.cache.get(key));
		this.cache.put(key, value1);
		this.cache.putIfAbsent(key, value2);
		assertEquals(value1, this.cache.get(key));
	}

	@Test
	public void testConcurrentRemove() throws Exception {
		final Object key = "enescu";
		final Object value1 = "george";
		final Object value2 = "geo";

		assertNull(this.cache.get(key));
		this.cache.put(key, value1);
		// no remove
		this.cache.remove(key, value2);
		assertEquals(value1, this.cache.get(key));
		// one remove
		this.cache.remove(key, value1);
		assertNull(this.cache.get(key));
	}

	@Test
	public void testConcurrentReplace() throws Exception {
		final Object key = "enescu";
		final Object value1 = "george";
		final Object value2 = "geo";

		assertNull(this.cache.get(key));
		this.cache.put(key, value1);
		this.cache.replace(key, value2);
		assertEquals(value2, this.cache.get(key));
		this.cache.remove(key);
		this.cache.replace(key, value1);
		assertNull(this.cache.get(key));
	}

	@Test
	public void testConcurrentReplaceIfEqual() throws Exception {
		final Object key = "enescu";
		final Object value1 = "george";
		final Object value2 = "geo";

		assertNull(this.cache.get(key));
		this.cache.put(key, value1);
		assertEquals(value1, this.cache.get(key));
		// no replace
		this.cache.replace(key, value2, value1);
		assertEquals(value1, this.cache.get(key));
		this.cache.replace(key, value1, value2);
		assertEquals(value2, this.cache.get(key));
		this.cache.replace(key, value2, value1);
		assertEquals(value1, this.cache.get(key));
	}

	private org.infinispan.Cache<Object, Object> createNativeCache() throws Exception {
		final InfinispanNamedEmbeddedCacheFactoryBean fb = new InfinispanNamedEmbeddedCacheFactoryBean();
		fb.setInfinispanEmbeddedCacheManager(new DefaultCacheManager());
		fb.setBeanName(CACHE_NAME);
		fb.setCacheName(CACHE_NAME);
		fb.afterPropertiesSet();
		return fb.getObject();
	}

	private Cache<Object, Object> createCache(final org.infinispan.Cache<Object, Object> nativeCache) {
		return new SpringCache<Object, Object>(nativeCache);
	}

}
