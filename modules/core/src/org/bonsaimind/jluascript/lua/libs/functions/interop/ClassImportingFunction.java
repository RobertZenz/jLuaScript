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

import org.bonsaimind.jluascript.lua.LuaUtil;
import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaValue;

/**
 * The {@link ClassImportingFunction} is a {@link ClassLoadingFunction}
 * extension which allows to import {@link Class}es with its invocation.
 */
public class ClassImportingFunction extends ClassLoadingFunction {
	/** The Lua environment into which to import the {@link Class}es. */
	protected LuaValue environment = null;
	
	/**
	 * Creates a new instance of {@link ClassImportingFunction}.
	 *
	 * @param environment The {@link LuaValue Lua enviroment} into which to
	 *        import the {@link Class}es, cannot be {@code null}.
	 * @param classLoader The {@link ClassLoader} to use to load
	 *        {@link Class}es, cannot be {@code null}.
	 * @param coercer The {@link Coercer} to use, cannot be {@code null}.
	 * @throws IllegalArgumentException If the {@code environment} or
	 *         {@code classLoader} or {@code coercer} is {@code null}.
	 */
	public ClassImportingFunction(LuaValue environment, ClassLoader classLoader, Coercer coercer) {
		super(Verifier.notNull("classLoader", classLoader), Verifier.notNull("coercer", coercer));
		
		Verifier.notNull("environment", environment);
		
		this.environment = environment;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LuaValue call(LuaValue arg) {
		LuaValue coercedStaticClass = super.call(arg);
		Class<?> clazz = (Class<?>)coercedStaticClass.touserdata(Class.class);
		
		environment.set(clazz.getSimpleName(), coercedStaticClass);
		LuaUtil.addClassByPackage(environment, clazz, coercedStaticClass);
		
		return coercedStaticClass;
	}
}
