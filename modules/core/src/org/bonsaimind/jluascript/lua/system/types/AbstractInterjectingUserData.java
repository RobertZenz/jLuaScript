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

package org.bonsaimind.jluascript.lua.system.types;

import java.util.HashMap;
import java.util.Map;

import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;

/**
 * The {@link AbstractInterjectingUserData} is a {@link LuaUserdata} extension
 * which provides the means to intercept all actions on the Lua object and
 * return custom values for them. The provided values are then put into a cache
 * for subsequent calls.
 */
public abstract class AbstractInterjectingUserData extends LuaUserdata {
	/** The {@link Map} that is being used as a cache. */
	protected Map<String, LuaValue> cache = new HashMap<>();
	
	/**
	 * Creates a new instance of {@link AbstractInterjectingUserData}.
	 *
	 * @param object The {@link Object}, cannot be {@code null}.
	 * @throws IllegalArgumentException If the {@code object} is {@code null}.
	 */
	public AbstractInterjectingUserData(Object object) {
		super(Verifier.notNull("object", object));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LuaValue get(LuaValue key) {
		String keyString = key.tojstring();
		
		LuaValue luaValue = cache.get(keyString);
		
		if (luaValue == null) {
			luaValue = provide(keyString);
			
			if (luaValue == null) {
				luaValue = LuaValue.NIL;
			}
		}
		
		return luaValue;
	}
	
	/**
	 * Allows to put a {@link LuaValue} for the given {@link String key} into
	 * the cache.
	 * 
	 * @param key The {@link String key}, cannot be {@code null} or empty.
	 * @param value The value to provide, can be {@code null} to clear the cache
	 *        for this {@link String key}.
	 * @return This instance.
	 * @throws IllegalArgumentException If the {@code key} is {@code null} or
	 *         empty.
	 */
	public AbstractInterjectingUserData putIntoCache(String key, LuaValue value) {
		Verifier.notNullOrEmpty("key", key);
		
		cache.put(key, value);
		
		return this;
	}
	
	/**
	 * Provides the value for the given {@link String key}.
	 * 
	 * @param key The {@link String key}, should not be {@code null} or empty.
	 * @return The provided {@link LuaValue}, can be {@code null} to not provide
	 *         a value.
	 */
	protected abstract LuaValue provide(String key);
}
