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
 * <h1><em>Spring Infinispan</em> - Integrating JBoss INFINISPAN into Spring</h1>
 * <p>
 * Spring Infinispan's purpose is twofold:
 * <ol>
 *   <li>
 *     Implement a provider for <a href="http://www.springsource.com">Spring</a> 3.1's Cache abstraction backed by the open-source 
 *     high-performance distributed cache <a href="http://www.jboss.org/infinispan">JBoss INFINISPAN</a>.
 *   </li>
 *   <li>
 *     Provide implementations of Spring's {@link org.springframework.beans.factory.FactoryBean <code>FactoryBean</code>}
 *     interface for easing usage of JBoss INFINISPAN within the Spring programming model.
 *   </li>
 * </ol>
 * </p>
 * <h2><em>Spring Infinispan</em> as an implementation of Spring's Cache SPI</h2>
 * <p>
 * Spring 3.1 introduces caching capabilities a user may comfortably utilize via a set of custom annotations, thus telling
 * the Spring runtime which objects to cache under which circumstances.</br>
 * Out of the box, Spring ships with <a href="">EHCache</a> as the caching provider to delegate to. It defines, however, a
 * simple SPI vendors may implement for their own caching solution, thus enabling Spring users to swap out the default
 * EHCache for another cache of their choosing. This SPI comprises two interfaces:
 * <ul>
 *   <li>
 *     {@link org.springframework.cache.Cache <code>Cache</code>}, Spring's cache abstraction itself, and
 *   </li>
 *   <li>
 *     {@link org.springframework.cache.CacheManager <code>CacheManager</code>}, a service for creating <code>Cache</code>
 *     instances
 *   </li>
 * </ul>
 * <em>Spring Infinispan</em> implements this SPI for JBoss INFINISPAN.
 * </p>
 * <p>
 * While <em>Spring Infinispan</em> offers only one implementation of <code>org.springframework.cache.Cache</code>, namely
 * {@link org.infinispan.spring.SpringCache <code>org.infinispan.spring.SpringCache</code>}, there are two implementations
 * of <code>org.springframework.cache.CacheManager</code>:
 * <ol>
 *   <li>
 *     {@link org.infinispan.spring.SpringEmbeddedCacheManager <code>org.infinispan.spring.SpringEmbeddedCacheManager</code>}
 *     and
 *   </li>
 *   <li>
 *     {@link org.infinispan.spring.SpringRemoteCacheManager <code>org.infinispan.spring.SpringRemoteCacheManager</code>}
 *     and
 *   </li>
 * </ol>
 * These two implementations cover two distinct use cases:
 * <ol>
 *   <li>
 *     <em>Embedded</em>: Embed your Spring-powered application into the same JVM that is running an INFINISPAN node, i.e. every
 *     communication between application code and INFINISPAN is in-process. INFINISPAN supports this use case via the interface
 *     {@link org.infinispan.manager.EmbeddedCacheManager <code>org.infinispan.manager.EmbeddedCacheManager</code>} and its default
 *     implementation {@link org.infinispan.manager.DefaultCacheManager <code>org.infinispan.manager.DefaultCacheManager</code>}. The
 *     latter backs {@link org.infinispan.spring.SpringEmbeddedCacheManager <code>SpringEmbeddedCacheManager</code>}.
 *   </li>
 *   <li>
 *     <em>Remote</em>: Your Spring-powered application accesses INFINISPAN nodes remotely using INFINISPAN's own <em>hotrod</em>
 *     protocol. INFINISPAN supports this use case via {@link org.infinispan.client.hotrod.RemoteCacheManager 
 *     <code>org.infinispan.client.hotrod.RemoteCacheManager</code>}, and this is the class that backs 
 *     {@link org.infinispan.spring.SpringRemoteCacheManager <code>SpringRemoteCacheManager</code>}.
 *   </li>
 * </ol>
 * </p>
 * <strong>Usage</strong>
 * <p>
 * Using <em>Spring Infinispan</em> as a Spring Cache provider may be divided into two broad areas:
 * <ol>
 *   <li>
 *     Telling the Spring runtime to use <em>Spring Infinispan</em> and therefore INFINISPAN as its caching provider.
 *   </li>
 *   <li>
 *     Using Spring's caching annotations in you application code.
 *   </li>
 * </ol>
 * </p>
 * <p>
 * <em>Register Spring Infinispan with the Spring runtime</em><br/>
 * Suppose we want to use <em>Spring Infinispan</em> as our caching provider, and suppose further that we want to create two named
 * cache instances, &quot;cars&quot; and &quot;planes&quot;. To that end, we put
 * <pre>
 * &lt;bean id="cacheManager" class="org.infinispan.spring.SpringEmbeddedCacheManager"/&gt;
 * </pre>
 * </p>
 */
package org.infinispan.spring;

