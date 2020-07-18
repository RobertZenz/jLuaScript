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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.lua.system.types.functions.InstanceMethodInvokingFunction;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class InstanceUserData extends AbstractInterjectingUserData {
	protected Coercer coercer = null;
	protected Map<String, Field> fieldCache = new HashMap<>();
	
	public InstanceUserData(Object object, Coercer coercer) {
		super(object);
		
		this.coercer = coercer;
	}
	
	protected Field findField(String name) {
		for (Field field : m_instance.getClass().getFields()) {
			if (!Modifier.isStatic(field.getModifiers())
					&& Modifier.isPublic(field.getModifiers())
					&& field.getName().equals(name)) {
				return field;
			}
		}
		
		return null;
	}
	
	protected LuaValue getField(String name) {
		Field field = null;
		
		if (fieldCache.containsKey(name)) {
			field = fieldCache.get(name);
		} else {
			field = findField(name);
			
			fieldCache.put(name, field);
		}
		
		if (field == null) {
			return null;
		}
		
		try {
			LuaValue luaValue = CoerceJavaToLua.coerce(field.get(m_instance));
			
			if (Modifier.isFinal(field.getModifiers())) {
				cache(name, luaValue);
			}
			
			return luaValue;
		} catch (IllegalArgumentException e) {
			throw new LuaError(e);
		} catch (IllegalAccessException e) {
			throw new LuaError(e);
		}
	}
	
	protected LuaValue getMethod(String name) {
		List<Method> methods = new ArrayList<>();
		
		for (Method method : m_instance.getClass().getMethods()) {
			if (!Modifier.isStatic(method.getModifiers())
					&& Modifier.isPublic(method.getModifiers())
					&& method.getName().equals(name)) {
				// Workaround that certain methods might not be accessible for
				// us even though they should.
				method.setAccessible(true);
				methods.add(method);
			}
		}
		
		if (!methods.isEmpty()) {
			LuaValue luaFunction = new InstanceMethodInvokingFunction(
					m_instance.getClass(),
					methods,
					coercer);
			
			cache(name, luaFunction);
			
			return luaFunction;
		}
		
		return null;
	}
	
	@Override
	protected LuaValue provide(String name) {
		LuaValue luaValue = getField(name);
		
		if (luaValue == null) {
			return luaValue = getMethod(name);
		}
		
		return luaValue;
	}
}
