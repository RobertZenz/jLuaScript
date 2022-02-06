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

import org.bonsaimind.jluascript.lua.LuaUtil;
import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * The {@link DefaultImportsLib} is a {@link TwoArgFunction} extension which
 * provides imports for "default" classes, like {@link String} and {@link Long}.
 */
public class DefaultImportsLib extends TwoArgFunction {
	/** The {@link Coercer} to use. */
	protected Coercer coercer = null;
	
	/**
	 * Creates a new instance of {@link DefaultImportsLib}.
	 *
	 * @param coercer The {@link Coercer} to use, cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code coercer} is
	 *         {@code null}.
	 */
	public DefaultImportsLib(Coercer coercer) {
		super();
		
		Verifier.notNull("coercer", coercer);
		
		this.coercer = coercer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LuaValue call(LuaValue modname, LuaValue environment) {
		importDefaults(environment);
		
		return environment;
	}
	
	/**
	 * Imports the given {@link Class} into the given {@link LuaValue Lua
	 * enviroment}.
	 * 
	 * @param environment The {@link LuaValue Lua enviroment} to import into,
	 *        cannot be {@code null}.
	 * @param clazz The {@link Class} to import, cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code enviroment} or
	 *         {@code clazz} is {@code null}.
	 */
	protected void importClass(LuaValue environment, Class<?> clazz) {
		Verifier.notNull("environment", environment);
		Verifier.notNull("clazz", clazz);
		
		LuaValue staticInstance = coercer.coerceClassToStaticLuaInstance(clazz);
		
		environment.set(clazz.getSimpleName(), staticInstance);
		LuaUtil.addClassByPackage(environment, clazz, staticInstance);
	}
	
	/**
	 * Imports the default {@link Class}es into the given {@link LuaValue Lua
	 * enviroment}.
	 * 
	 * @param environment The {@link LuaValue Lua enviroment} to import into,
	 *        cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code enviroment} is
	 *         {@code null}.
	 */
	protected void importDefaults(LuaValue environment) {
		Verifier.notNull("environment", environment);
		
		importClass(environment, Byte.class);
		importClass(environment, Character.class);
		importClass(environment, Double.class);
		importClass(environment, Float.class);
		importClass(environment, Integer.class);
		importClass(environment, Long.class);
		importClass(environment, Object.class);
		importClass(environment, Short.class);
		importClass(environment, String.class);
		importClass(environment, System.class);
	}
}
