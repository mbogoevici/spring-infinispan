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
 * <h1>Spring Infinispan - Integrating JBoss INFINISPAN Remote into Spring.</h1>
 * <p>
 * This package contains classes to be used when your application code accesses INFINISPAN nodes remotely, i.e. via
 * INFINISPAN's <em>hotrod</em> protocol. In this mode you do not interact directly with INFINISPAN Caches and CacheManagers
 * but rather through proxies, namely {@link org.infinispan.client.hotrod.RemoteCache <code>org.infinispan.client.hotrod.RemoteCache</code>}
 * and {@link org.infinispan.client.hotrod.RemoteCacheManager <code>org.infinispan.client.hotrod.RemoteCacheManager</code>}.
 * </p>
 * <p>
 * What you will typically want to do when accessing INFINISPAN remotely is
 * <ol>
 *   <li>
 *     configure and create an instance of {@link org.infinispan.client.hotrod.RemoteCacheManager 
 *     <code>org.infinispan.client.hotrod.RemoteCacheManager</code>} and
 *   </li>
 *   <li>
 *     use that {@code RemoteCacheManager} to create one or more {@link org.infinispan.client.hotrod.RemoteCache 
 *     <code>org.infinispan.client.hotrod.RemoteCache</code>} instances.
 *   </li>
 * </ol>
 * You achieve this through using {@link org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean
 * <code>InfinispanRemoteCacheManagerFactoryBean</code>} in conjunction with 
 * {@link org.infinispan.spring.support.remote.InfinispanNamedRemoteCacheFactoryBean <code>InfinispanNamedRemoteCacheFactoryBean</code>}
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
 *   &lt;bean id="infinispanRemoteCacheManagerWithDefaultConfiguration" 
 *      class="org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean" /&gt;
 *
 *   &lt;!--
 *   &lt;bean id="infinispanRemoteCacheManagerConfiguredFromConfigurationPropertiesFile" 
 *      class="org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean"
 *      p:configuration-properties-file-location="classpath:/org/infinispan/spring/support/remote/hotrod-client.properties" /&gt;
 *
 *   &lt;bean id="infinispanRemoteCacheManagerConfiguredFromConfigurationProperties" 
 *      class="org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean"&gt;
 *       &lt;property name="configurationProperties"&gt;
 *           &lt;props&gt;
 *               &lt;prop key="infinispan.client.hotrod.ping_on_startup"&gt;false&lt;/prop&gt;
 *               &lt;prop key="infinispan.client.hotrod.value_size_estimate"&gt;1024&lt;/prop&gt;
 *               &lt;prop key="infinispan.client.hotrod.tcp_no_delay"&gt;false&lt;/prop&gt;
 *           &lt;/props&gt;
 *       &lt;/property&gt;
 *   &lt;/bean&gt;
 *
 *   &lt;bean id="infinispanRemoteCacheManagerConfiguredUsingSetters" 
 *      class="org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean"
 *      p:key-size-estimate="768" 
 *      p:force-return-values="true" 
 *      p:start-automatically="false" /&gt;
 *   --&gt;
 *   
 *   &lt;!-- Default cache --&gt;
 *   &lt;bean id="infinispanRemoteDefaultCache" 
 *      class="org.infinispan.spring.support.InfinispanDefaultCacheFactoryBean"
 *      p:infinispan-cache-container-ref="infinispanRemoteCacheManagerWithDefaultConfiguration" /&gt;
 *   
 *   &lt;!-- Named cache --&gt;
 *   &lt;bean id="infinispanNamedRemoteCacheWithoutFurtherConfiguration" 
 *      class="org.infinispan.spring.support.remote.InfinispanNamedRemoteCacheFactoryBean"
 *      p:infinispan-remote-cache-manager-ref="infinispanRemoteCacheManagerWithDefaultConfiguration" 
 *      p:cache-name="customCacheNameWithoutFurtherConfiguration" /&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 * </p>
 * 
 * @see org.infinispan.client.hotrod.RemoteCache
 * @see org.infinispan.client.hotrod.RemoteCacheManager
 * @see org.infinispan.Cache
 * @see org.infinispan.spring.support.InfinispanDefaultCacheFactoryBean
 * @see org.infinispan.spring.support.remote.InfinispanRemoteCacheManagerFactoryBean
 * @see org.infinispan.spring.support.remote.InfinispanNamedRemoteCacheFactoryBean
 */
package org.infinispan.spring.support.remote;

