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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.infinispan.commands.ReplicableCommand;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.marshall.StreamingMarshaller;
import org.infinispan.notifications.cachemanagerlistener.CacheManagerNotifier;
import org.infinispan.remoting.InboundInvocationHandler;
import org.infinispan.remoting.responses.Response;
import org.infinispan.remoting.rpc.ResponseFilter;
import org.infinispan.remoting.rpc.ResponseMode;
import org.infinispan.remoting.transport.Address;
import org.infinispan.remoting.transport.DistributedSync;
import org.infinispan.remoting.transport.Transport;
import org.infinispan.statetransfer.StateTransferException;
import org.infinispan.util.logging.Log;

public final class MockTransport implements Transport {

	@Override
	public void setConfiguration(final GlobalConfiguration gc) {
	}

	@Override
	public void initialize(final StreamingMarshaller marshaller, final ExecutorService asyncExecutor,
			final InboundInvocationHandler handler, final CacheManagerNotifier notifier) {
	}

	@Override
	public List<Response> invokeRemotely(final Collection<Address> recipients, final ReplicableCommand rpcCommand,
			final ResponseMode mode, final long timeout, final boolean usePriorityQueue,
			final ResponseFilter responseFilter, final boolean supportReplay) throws Exception {
		return null;
	}

	@Override
	public boolean isCoordinator() {
		return false;
	}

	@Override
	public Address getCoordinator() {
		return null;
	}

	@Override
	public Address getAddress() {
		return null;
	}

	@Override
	public List<Address> getPhysicalAddresses() {
		return null;
	}

	@Override
	public List<Address> getMembers() {
		return null;
	}

	@Override
	public boolean retrieveState(final String cacheName, final Address address, final long timeout)
			throws StateTransferException {
		return false;
	}

	@Override
	public DistributedSync getDistributedSync() {
		return null;
	}

	@Override
	public boolean isSupportStateTransfer() {
		return false;
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public int getViewId() {
		return 0;
	}

	@Override
	public Log getLog() {
		return null;
	}
}
