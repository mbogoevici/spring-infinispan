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

package org.infinispan.spring.embedded;

import org.infinispan.config.Configuration;
import org.springframework.util.Assert;

/**
 * <p>
 * TODO: Document NamedConfiguration.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 *
 */
public final class NamedConfiguration {

	private final String name;

	private final Configuration configuration;

	/**
	 * @param name
	 * @param configuration
	 */
	NamedConfiguration(final String name, final Configuration configuration) {
		Assert.hasText(name, "A non-empty name is required");
		Assert.notNull(configuration, "A non-null Configuration is required");
		this.name = name;
		this.configuration = configuration;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the configuration
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.configuration == null) ? 0 : this.configuration.hashCode());
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final NamedConfiguration other = (NamedConfiguration) obj;
		if (this.configuration == null) {
			if (other.configuration != null) {
				return false;
			}
		} else if (!this.configuration.equals(other.configuration)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NamedConfiguration [name=" + this.name + ", configuration=" + this.configuration + "]";
	}
}
