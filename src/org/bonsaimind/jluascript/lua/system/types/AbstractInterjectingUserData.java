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

import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;

public abstract class AbstractInterjectingUserData extends LuaUserdata {
	protected Map<String, LuaValue> cache = new HashMap<>();
	
	public AbstractInterjectingUserData(Object object) {
		super(object);
	}
	
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
	
	public AbstractInterjectingUserData provide(String name, LuaValue value) {
		cache.put(name, value);
		
		return this;
	}
	
	protected void cache(String key, LuaValue value) {
		cache.put(key, value);
	}
	
	protected abstract LuaValue provide(String name);
}
