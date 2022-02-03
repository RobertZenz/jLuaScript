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

package org.bonsaimind.jluascript.javassist.handlers;

import java.lang.reflect.Method;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import javassist.util.proxy.MethodHandler;

/**
 * The {@link LuaInvokingMethodHandler} is a {@link MethodHandler}
 * implementation which invokes a {@link LuaValue Lua function}.
 */
public class LuaInvokingMethodHandler implements MethodHandler {
	/** The {@link Coercer} to use. */
	protected Coercer coercer = null;
	/** The {@link LuaValue Lua function} to vall. */
	protected LuaValue luaFunctions = null;
	
	/**
	 * Creates a new instance of {@link LuaInvokingMethodHandler}.
	 *
	 * @param luaFunctions The {@link LuaValue Lua function} to call.
	 * @param coercer The {@link Coercer} to use.
	 */
	public LuaInvokingMethodHandler(LuaValue luaFunctions, Coercer coercer) {
		super();
		
		this.luaFunctions = luaFunctions;
		this.coercer = coercer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object invoke(Object instance, Method superMethod, Method thisMethod, Object[] arguments) throws Throwable {
		if (luaFunctions.isfunction()) {
			return coercer.coerceLuaToJava(luaFunctions.invoke(coerceArguments(arguments)).arg1());
		} else if (luaFunctions.istable()) {
			LuaValue luaFunction = luaFunctions.get(superMethod.getName());
			
			if (luaFunction.isfunction()) {
				return coercer.coerceLuaToJava(luaFunction.invoke(coerceArguments(arguments)).arg1());
			} else if (thisMethod != null) {
				return thisMethod.invoke(instance, arguments);
			}
		}
		
		throw new LuaError("Method <" + superMethod.getName() + "> has not been provided but is required.");
	}
	
	/**
	 * Coerces the given array.
	 * 
	 * @param array The array to coerce.
	 * @return The coerced array.
	 */
	protected LuaValue[] coerceArguments(Object[] array) {
		if (array == null) {
			return null;
		}
		
		LuaValue[] luaValueArray = new LuaValue[array.length];
		
		for (int index = 0; index < array.length; index++) {
			luaValueArray[index] = coercer.coerceJavaToLua(array[index]);
		}
		
		return luaValueArray;
	}
}
