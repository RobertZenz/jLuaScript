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

package org.bonsaimind.jluascript.lua.system.coercers;

import java.util.IdentityHashMap;
import java.util.Map;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.lua.system.types.ArrayUserData;
import org.bonsaimind.jluascript.lua.system.types.InstanceUserData;
import org.bonsaimind.jluascript.lua.system.types.StaticUserData;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;

/**
 * The default implementation of {@link Coercer}.
 */
public class DefaultCoercer implements Coercer {
	/** The cache used for storing static instances. */
	protected Map<Class<?>, LuaValue> classStaticInstaceCache = new IdentityHashMap<>();
	
	/**
	 * Creates a new instance of {@link DefaultCoercer}.
	 */
	public DefaultCoercer() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LuaValue coerceClassToStaticLuaInstance(Class<?> clazz) throws LuaError {
		if (clazz == null) {
			return LuaValue.NIL;
		}
		
		LuaValue staticInstance = classStaticInstaceCache.get(clazz);
		
		if (staticInstance == null) {
			staticInstance = new StaticUserData(clazz, this);
			classStaticInstaceCache.put(clazz, staticInstance);
		}
		
		return staticInstance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LuaValue coerceJavaToLua(Object object) throws LuaError {
		if (object == null) {
			return LuaValue.NIL;
		}
		
		if (object instanceof LuaValue) {
			return (LuaValue)object;
		}
		
		if (object.getClass().isArray()) {
			return coerceArray(object);
		}
		
		if (object instanceof String) {
			return LuaString.valueOf((String)object);
		} else if (object instanceof Boolean) {
			return LuaBoolean.valueOf(((Boolean)object).booleanValue());
		} else if (object instanceof Byte
				|| object instanceof Short
				|| object instanceof Integer
				|| object instanceof Long) {
			return LuaNumber.valueOf(((Number)object).intValue());
		} else if (object instanceof Float
				|| object instanceof Double) {
			return LuaNumber.valueOf(((Number)object).doubleValue());
		}
		
		return coerceInstance(object);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object coerceLuaToJava(LuaValue luaValue) throws LuaError {
		if (luaValue == null || luaValue.isnil()) {
			return null;
		}
		
		switch (luaValue.type()) {
			case LuaValue.TBOOLEAN:
				return Boolean.valueOf(luaValue.toboolean());
			
			case LuaValue.TFUNCTION:
				break;
			
			case LuaValue.TINT:
				return Integer.valueOf(luaValue.toint());
			
			case LuaValue.TLIGHTUSERDATA:
				break;
			
			case LuaValue.TNIL:
				break;
			
			case LuaValue.TNONE:
				break;
			
			case LuaValue.TNUMBER:
				if (luaValue.isint()) {
					return Integer.valueOf(luaValue.toint());
				} else {
					return Double.valueOf(luaValue.todouble());
				}
				
			case LuaValue.TSTRING:
				return luaValue.tojstring();
			
			case LuaValue.TTABLE:
				break;
			
			case LuaValue.TTHREAD:
				break;
			
			case LuaValue.TUSERDATA:
				return luaValue.touserdata();
			
			case LuaValue.TVALUE:
				break;
		}
		
		throw new LuaError("Could not convert object <" + luaValue.typename() + "> to a Java object.");
	}
	
	/**
	 * Coerces the given array to a {@link LuaValue}.
	 * 
	 * @param array The array to coerce.
	 * @return The coerced array.
	 * @throw LuaError If the conversion has failed or is not possible.
	 */
	protected LuaValue coerceArray(Object array) throws LuaError {
		return new ArrayUserData(array, this);
	}
	
	/**
	 * Coerces the given {@link Object} as {@link LuaValue}.
	 * 
	 * @param object The {@link Object} to coerce.
	 * @return The coerced {@link LuaValue}.
	 * @throw LuaError If the conversion has failed or is not possible.
	 */
	protected LuaValue coerceInstance(Object object) throws LuaError {
		return new InstanceUserData(object, this);
	}
	
	/**
	 * Coerces the given {@link Class}/static instance as {@link LuaValue}.
	 * 
	 * @param clazz The {@link Class} to coerce.
	 * @return The coerced {@link Class}.
	 * @throw LuaError If the conversion has failed or is not possible.
	 */
	protected LuaValue coerceStaticInstance(Class<?> clazz) throws LuaError {
		return new StaticUserData(clazz, this);
	}
}
