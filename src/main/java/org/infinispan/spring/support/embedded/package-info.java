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
 * <h1>Spring Infinispan - Integrating JBoss INFINISPAN Embedded into Spring.</h1>
 * <p>
 * This package contains classes to be used when running JBoss INFINISPAN natively in embedded mode. As its name suggests,
 * INFINISPAN's embedded mode means running an INFINISPAN node and application code within the same JVM so that all communication
 * between INFINISPAN and your application is in-process. Of course, INFINSIPAN nodes on different machines will still
 * communicate with each other remotely yet this is transparent to your code.
 * </p>
 * <p>
 * What you will typically want to do when running INFINISPAN in embedded mode is
 * <ol>
 *   <li>
 *     configure and create an implementation of {@link org.infinispan.manager.EmbeddedCacheManager 
 *     <code>org.infinispan.manager.EmbeddedCacheManager</code>} and
 *   </li>
 *   <li>
 *     use that {@code EmbeddedCacheManager} to create one or more {@link org.infinispan.Cache <code>org.infinispan.Cache</code>}
 *     instances.
 *   </li>
 * </ol>
 * You achieve this through using {@link org.infinispan.spring.support.embedded.InfinispanEmbeddedCacheManagerFactoryBean
 * <code>InfinispanEmbeddedCacheManagerFactoryBean</code>} in conjunction with 
 * {@link org.infinispan.spring.support.embedded.InfinispanNamedEmbeddedCacheFactoryBean <code>InfinispanNamedEmbeddedCacheFactoryBean</code>}
 * (for <i>named</i> caches) or alternatively/additionally with {@link org.infinispan.spring.support.InfinispanDefaultCacheFactoryBean
 * <code>InfinispanDefaultCacheFactoryBean</code>} (for <i>default</i> caches). 
 * </p>
 * <strong>Usage</strong>
 * <p>
 * <pre>
 * &lt;beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *        xmlns:p="http://www.springframework.org/schema/p" 
 *        xsi:schemaLocation="http://www.springframework.org/schema/beans 
 *                            http://www.springframework.org/schema/beans/spring-beans.xsd"&gt;
 *
 *   &lt;bean id="infinispanEmbeddedCacheManagerWithDefaultConfiguration" 
 *      class="org.infinispan.spring.support.embedded.InfinispanEmbeddedCacheManagerFactoryBean" /&gt;
 *  
 *   &lt;!-- Alternatively:
 *   &lt;bean id="infinispanEmbeddedCacheManagerConfiguredFromConfigurationFile" 
 *      class="org.infinispan.spring.support.embedded.InfinispanEmbeddedCacheManagerFactoryBean"
 *      p:configuration-file-location="classpath:/org/infinispan/spring/support/embedded/named-async-cache.xml" /&gt;
 *
 *   &lt;bean id="infinispanEmbeddedCacheManagerConfiguredUsingSetters" 
 *      class="org.infinispan.spring.support.embedded.InfinispanEmbeddedCacheManagerFactoryBean"
 *       p:machine-id="test.machineId" 
 *       p:expiration-lifespan="45600" 
 *       p:cluster-name="test.clusterName" /&gt;
 *   --&gt;
 *   
 *   &lt;!-- Default cache --&gt;
 *   &lt;bean id="infinispanEmbeddedDefaultCache" 
 *      class="org.infinispan.spring.support.InfinispanDefaultCacheFactoryBean"
 *      p:infinispanCacheContainer-ref="infinispanEmbeddedCacheManagerWithDefaultConfiguration" /&gt;
 *   
 *   &lt;!-- Named caches --&gt;
 *   &lt;bean id="infinispanNamedEmbeddedCacheWithoutFurtherConfiguration" 
 *      class="org.infinispan.spring.support.embedded.InfinispanNamedEmbeddedCacheFactoryBean"
 *       p:infinispanEmbeddedCacheManager-ref="infinispanEmbeddedCacheManagerWithDefaultConfiguration" 
 *       p:cache-name="customCacheNameWithoutFurtherConfiguration" /&gt;
 *
 *   &lt;bean id="infinispanNamedEmbeddedCacheConfiguredUsingModeNONE" 
 *      class="org.infinispan.spring.support.embedded.InfinispanNamedEmbeddedCacheFactoryBean"
 *       p:infinispanEmbeddedCacheManager-ref="infinispanEmbeddedCacheManagerWithDefaultConfiguration" 
 *       p:cache-name="customCacheNameConfiguredUsingModeNONE"
 *       p:configuration-template-mode="NONE" 
 *       p:enable-deadlock-detection="true" 
 *       p:use-eager-locking="true" /&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 * </p>
 * 
 * @see org.infinispan.manager.EmbeddedCacheManager
 * @see org.infinispan.Cache
 * @see org.infinispan.spring.support.InfinispanDefaultCacheFactoryBean
 * @see org.infinispan.spring.support.embedded.InfinispanEmbeddedCacheManagerFactoryBean
 * @see org.infinispan.spring.support.embedded.InfinispanNamedEmbeddedCacheFactoryBean
 */
package org.infinispan.spring.support.embedded;

