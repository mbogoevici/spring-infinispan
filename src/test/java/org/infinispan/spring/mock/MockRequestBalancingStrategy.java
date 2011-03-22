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

import org.infinispan.client.hotrod.impl.transport.tcp.RequestBalancingStrategy;

public final class MockRequestBalancingStrategy implements RequestBalancingStrategy {

	@Override
	public void setServers(final Collection<InetSocketAddress> servers) {
	}

	@Override
	public InetSocketAddress nextServer() {
		return null;
	}
}
