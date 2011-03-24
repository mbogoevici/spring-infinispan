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

/**
 * <h1>Spring Infinispan - Integrating JBoss INFINISPAN into Spring.</h1>
 * <p>
 * Besides implementing Spring 3.1's {@link org.springframework.cache <code><Cache SPI</code>} for JBoss INFINISPAN
 * <strong>Spring Infinispan</strong> offers classes generally useful when using JBoss INFINISPAN <i>natively</i> within
 * the Spring programming model. By natively we mean that application code is exposed to INFINISPAN's very own cache
 * abstractions, i.e. {@link org.infinispan.Cache <code>org.infinispan.Cache</code>}, {@link org.infinispan.manager.CacheContainer 
 * <code>org.infinispan.manager.CacheContainer</code>} and its various implementations. Contrast this with <strong>Spring Infinispan</strong>'s
 * role as a Spring Cache provider where in the majority of cases application code utilizes Spring Cache's
 * {@link org.springframework.cache.annotation.Cacheable <code>@Cacheable</code>} and {@link org.springframework.cache.annotation.CacheEvict 
 * <code>@CacheEvict</code>} annotations and is usually <em>not</em> directly exposed to any cache abstraction.
 * <p>
 * <p>
 * This package contains classes to be used when using JBoss INFINISPAN natively that are common to the two major use cases
 * INFINISPAN covers:
 * <ol>
 *   <li>
 *     <strong>Embedded</strong>: Embed your Spring-powered application into the same JVM running an INFINISPAN node, i.e. every
 *     communication between application code and INFINISPAN is in-process. INFINISPAN supports this use case via the interface
 *     {@link org.infinispan.manager.EmbeddedCacheManager <code>org.infinispan.manager.EmbeddedCacheManager</code>} and its default
 *     implementation {@link org.infinispan.manager.DefaultCacheManager <code>org.infinispan.manager.DefaultCacheManager</code>}.<br/><br/>
 *     See package {@link org.infinispan.spring.support.embedded <code>org.infinispan.spring.support.embedded</code>}.<br/><br/>
 *   </li>
 *   <li>
 *     <strong>Remote</strong>: Application code accesses INFINISPAN nodes remotely using INFINISPAN's own <em>hotrod</em>
 *     protocol. INFINISPAN supports this use case via {@link org.infinispan.client.hotrod.RemoteCacheManager 
 *     <code>org.infinispan.client.hotrod.RemoteCacheManager</code>}.<br/><br/>
 *     See package {@link org.infinispan.spring.support.remote <code>org.infinispan.spring.support.remote</code>}.<br/><br/>
 *   </li>
 * </ol>
 * As indicated above code supporting these use cases is located in subpackages.
 * </p>
 */
package org.infinispan.spring.support;

