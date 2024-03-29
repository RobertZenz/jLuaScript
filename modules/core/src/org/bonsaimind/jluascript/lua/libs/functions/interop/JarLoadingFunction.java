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

package org.bonsaimind.jluascript.lua.libs.functions.interop;

import java.net.MalformedURLException;
import java.nio.file.Path;

import org.bonsaimind.jluascript.lua.libs.functions.AbstractPathAcceptingFunction;
import org.bonsaimind.jluascript.support.DynamicClassLoader;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * The {@link JarLoadingFunction} is an {@link AbstractPathAcceptingFunction}
 * extension which allows to add jars to the given {@link ClassLoader}.
 */
public class JarLoadingFunction extends AbstractPathAcceptingFunction {
	/** The {@link ClassLoader} to use to load {@link Class}es. */
	protected DynamicClassLoader classLoader = null;
	
	/**
	 * Creates a new instance of {@link JarLoadingFunction}.
	 *
	 * @param classLoader The {@link ClassLoader} to use to load
	 *        {@link Class}es, cannot be {@code null}.
	 * @throws IllegalArgumentException If the {@code classLoader} is
	 *         {@code null}.
	 */
	public JarLoadingFunction(DynamicClassLoader classLoader) {
		super();
		
		Verifier.notNull("classLoader", classLoader);
		
		this.classLoader = classLoader;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Varargs performAction(Path path) {
		try {
			classLoader.addJar(path.toUri().toURL());
		} catch (MalformedURLException e) {
			throw new LuaError(e);
		}
		
		return LuaValue.NIL;
	}
}
