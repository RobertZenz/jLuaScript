/*
 * Copyright 2018, Robert 'Bobby' Zenz
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.bonsaimind.jluascript.lua.system.types.functions;

import java.lang.reflect.Constructor;
import java.util.List;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.utils.Verifier;

/**
 * The {@link ConstructorInvokingFunction} is an
 * {@link AbstractExecutableInvokingFunction} extension which invokes
 * {@link Constructor}s.
 */
public class ConstructorInvokingFunction extends AbstractExecutableInvokingFunction<Constructor<?>> {
	/**
	 * Creates a new instance of {@link ConstructorInvokingFunction}.
	 *
	 * @param constructors The {@link List} of {@link Constructor}s to invoke,
	 *        cannot be {@code null} or empty.
	 * @param coercer The {@link Coercer} to use, cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code clazz},
	 *         {@code constructors} are {@code null} or empty or {@code coercer}
	 *         is {@code null}.
	 */
	public ConstructorInvokingFunction(List<Constructor<?>> constructors, Coercer coercer) {
		super(Verifier.notNullOrEmpty("constructors", constructors), Verifier.notNull("coercer", coercer));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object execute(Constructor<?> executable, List<Object> parameters) throws Exception {
		return executable.newInstance(parameters.toArray());
	}
}
