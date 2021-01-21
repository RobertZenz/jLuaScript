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
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public abstract class AbstractReflectiveUserData extends AbstractInterjectingUserData {
	protected Class<?> clazz = null;
	protected Coercer coercer = null;
	protected Map<String, Field> fieldCache = new HashMap<>();
	protected Object instance = null;
	
	protected AbstractReflectiveUserData(Object userDataInstance, Object instance, Class<?> clazz, Coercer coercer) {
		super(userDataInstance);
		
		this.instance = instance;
		this.clazz = clazz;
		this.coercer = coercer;
	}
	
	protected abstract LuaValue coerceMethodList(List<Method> methods);
	
	protected abstract boolean fieldMatches(Field field);
	
	protected Method findDeclaredMethod(Method method, Class<?> clazz) {
		if (!clazz.equals(Object.class) && clazz.getSuperclass() != null) {
			Method declaredMethod = findDeclaredMethod(method, clazz.getSuperclass());
			
			if (declaredMethod != null) {
				return declaredMethod;
			}
		}
		
		for (Class<?> superInterface : clazz.getInterfaces()) {
			Method declaredMethod = findDeclaredMethod(method, superInterface);
			
			if (declaredMethod != null) {
				return declaredMethod;
			}
		}
		
		try {
			return clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}
	
	protected Field findField(String name) {
		for (Field field : clazz.getFields()) {
			if (field.getName().equals(name) && fieldMatches(field)) {
				return field;
			}
		}
		
		return null;
	}
	
	protected Method getDeclaredMethod(Method method) {
		return findDeclaredMethod(method, method.getDeclaringClass());
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
			LuaValue luaValue = CoerceJavaToLua.coerce(field.get(instance));
			
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
	
	protected LuaValue getMethods(String name) {
		List<Method> methods = new ArrayList<>();
		
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(name) && methodMatches(method)) {
				methods.add(getDeclaredMethod(method));
			}
		}
		
		if (!methods.isEmpty()) {
			LuaValue luaFunction = coerceMethodList(methods);
			
			cache(name, luaFunction);
			
			return luaFunction;
		}
		
		return null;
	}
	
	protected abstract boolean methodMatches(Method method);
	
	@Override
	protected LuaValue provide(String name) {
		LuaValue luaValue = getField(name);
		
		if (luaValue == null) {
			return getMethods(name);
		}
		
		return luaValue;
	}
}
