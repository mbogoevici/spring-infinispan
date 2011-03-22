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

package org.infinispan.spring.mock;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.client.hotrod.impl.transport.Transport;
import org.infinispan.client.hotrod.impl.transport.TransportFactory;

public final class MockTransportFactory implements TransportFactory {

	@Override
	public Transport getTransport() {
		return null;
	}

	@Override
	public void releaseTransport(final Transport transport) {
	}

	@Override
	public void start(final ConfigurationProperties props, final Collection<InetSocketAddress> staticConfiguredServers,
			final AtomicInteger topologyId) {
	}

	@Override
	public void updateServers(final Collection<InetSocketAddress> newServers) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void updateHashFunction(final LinkedHashMap<InetSocketAddress, Integer> servers2HashCode,
			final int numKeyOwners, final short hashFunctionVersion, final int hashSpace) {
	}

	@Override
	public Transport getTransport(final byte[] key) {
		return null;
	}

	@Override
	public boolean isTcpNoDelay() {
		return false;
	}

	@Override
	public int getTransportCount() {
		return 0;
	}
}
