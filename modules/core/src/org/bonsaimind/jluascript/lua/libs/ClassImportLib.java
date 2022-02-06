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

import org.bonsaimind.jluascript.lua.libs.functions.interop.ClassImportingFunction;
import org.bonsaimind.jluascript.lua.libs.functions.interop.ClassLoadingFunction;
import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * The {@link ClassImportLib} is a {@link TwoArgFunction} extension which adds
 * the facilities to easily import new classes into the environment.
 */
public class ClassImportLib extends TwoArgFunction {
	/** The {@link ClassLoader} that will be used to load {@link Class}es. */
	protected ClassLoader classLoader = null;
	/** The {@link Coercer} to use. */
	protected Coercer coercer = null;
	
	/**
	 * Creates a new instance of {@link ClassImportLib}.
	 *
	 * @param classLoader The {@link ClassLoader} which will be used to load
	 *        {@link Class}es, cannot be {@code null}.
	 * @param coercer The {@link Coercer} to use, cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code classLoader} or
	 *         {@code coercer} is {@code null}.
	 */
	public ClassImportLib(ClassLoader classLoader, Coercer coercer) {
		super();
		
		Verifier.notNull("classLoader", classLoader);
		Verifier.notNull("coercer", coercer);
		
		this.classLoader = classLoader;
		this.coercer = coercer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LuaValue call(LuaValue modname, LuaValue environment) {
		environment.set("import", new ClassImportingFunction(environment, classLoader, coercer));
		environment.set("loadClass", new ClassLoadingFunction(classLoader, coercer));
		
		return environment;
	}
}
