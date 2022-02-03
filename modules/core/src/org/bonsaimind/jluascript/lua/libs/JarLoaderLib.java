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

package org.bonsaimind.jluascript.lua.libs;

import org.bonsaimind.jluascript.lua.libs.functions.interop.JarLoadingFunction;
import org.bonsaimind.jluascript.support.DynamicClassLoader;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * The {@link JarLoaderLib} is a {@link TwoArgFunction} extension which adds the
 * facilities to load jars from the filesystem into the environment.
 */
public class JarLoaderLib extends TwoArgFunction {
	/** The {@link DynamicClassLoader} to append the jars to. */
	protected DynamicClassLoader classLoader = null;
	
	/**
	 * Creates a new instance of {@link JarLoaderLib}.
	 *
	 * @param classLoader The {@link DynamicClassLoader} which to use load the
	 *        {@link Class}es, cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code classLoader} is
	 *         {@code null}.
	 */
	public JarLoaderLib(DynamicClassLoader classLoader) {
		super();
		
		Verifier.notNull("classLoader", classLoader);
		
		this.classLoader = classLoader;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LuaValue call(LuaValue modname, LuaValue environment) {
		environment.set("loadJar", new JarLoadingFunction(classLoader));
		
		return environment;
	}
}
