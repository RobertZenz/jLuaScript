
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
			
			cache.put(keyString, luaValue);
		}
		
		return luaValue;
	}
	
	public AbstractInterjectingUserData provide(String name, LuaValue value) {
		cache.put(name, value);
		
		return this;
	}
	
	protected abstract LuaValue provide(String name);
}
