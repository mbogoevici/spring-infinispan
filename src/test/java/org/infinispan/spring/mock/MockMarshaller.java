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

import java.io.IOException;

import org.infinispan.io.ByteBuffer;
import org.infinispan.marshall.Marshaller;

public final class MockMarshaller implements Marshaller {

	@Override
	public byte[] objectToByteBuffer(final Object obj, final int estimatedSize) throws IOException,
			InterruptedException {
		return null;
	}

	@Override
	public byte[] objectToByteBuffer(final Object obj) throws IOException, InterruptedException {
		return null;
	}

	@Override
	public Object objectFromByteBuffer(final byte[] buf) throws IOException, ClassNotFoundException {
		return null;
	}

	@Override
	public Object objectFromByteBuffer(final byte[] buf, final int offset, final int length) throws IOException,
			ClassNotFoundException {
		return null;
	}

	@Override
	public ByteBuffer objectToBuffer(final Object o) throws IOException, InterruptedException {
		return null;
	}

	@Override
	public boolean isMarshallable(final Object o) {
		return false;
	}
}
