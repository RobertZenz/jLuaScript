/*
 * Copyright 2020, Robert 'Bobby' Zenz
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

package org.bonsaimind.jluascript.lua.system;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

/**
 * A {@link Coercer} allows to coerce Java to Lua objects and the other way
 * round.
 */
public interface Coercer {
	/**
	 * Coerce the given {@link Class} to a static Lua instance.
	 * 
	 * @param clazz The {@link Class} to coerce.
	 * @return The {@link LuaValue} representing the static instance.
	 * @throws LuaError If the given {@link Class} could not be converted.
	 */
	public abstract LuaValue coerceClassToStaticLuaInstance(Class<?> clazz) throws LuaError;
	
	/**
	 * Coerce the given {@link Object} to a {@link LuaValue}.
	 * <p>
	 * Implementing methods should never return {@code null}, but instead
	 * {@link LuaValue#NIL} to represent Java {@code null}.
	 * 
	 * @param object The {@link Object} to convert.
	 * @return The {@link Object} as {@link LuaValue}.
	 * @throws LuaError If the given {@link Object} could not be converted.
	 */
	public abstract LuaValue coerceJavaToLua(Object object) throws LuaError;
	
	/**
	 * Coerce the given {@link LuaValue} to an {@link Object}.
	 * 
	 * @param luaValue The {@link LuaValue} to convert.
	 * @return The {@link LuaValue} as {@link Object}.
	 * @throws LuaError If the given {@link LuaValue} could not be converted.
	 */
	public abstract Object coerceLuaToJava(LuaValue luaValue) throws LuaError;
}
