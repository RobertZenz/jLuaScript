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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonsaimind.jluascript.lua.libs.functions.ErrorThrowingFunction;
import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.lua.system.types.functions.ClassCreatingFunction;
import org.bonsaimind.jluascript.lua.system.types.functions.ConstructorInvokingFunction;
import org.bonsaimind.jluascript.lua.system.types.functions.ProxyInstanceCreatingFunction;
import org.bonsaimind.jluascript.lua.system.types.functions.StaticMethodInvokingFunction;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class StaticUserData extends AbstractInterjectingUserData {
	public static final String CLASS_FIELD_NAME = "class";
	public static final String CONSTRUCTOR_NAME = "new";
	public static final String EXTEND_NAME = "extend";
	public static final String EXTEND_NEW_NAME = "extendNew";
	public static final String IMPLEMENT_NAME = "implement";
	public static final String IMPLEMENT_NEW_NAME = "implementNew";
	protected Coercer coercer = null;
	protected Map<String, Field> fieldCache = new HashMap<>();
	
	public StaticUserData(Class<?> clazz, Coercer coercer) {
		super(clazz);
		
		this.coercer = coercer;
		
		setup();
	}
	
	protected Field findField(String name) {
		for (Field field : ((Class<?>)m_instance).getFields()) {
			if (Modifier.isStatic(field.getModifiers())
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
			LuaValue luaValue = CoerceJavaToLua.coerce(field.get(null));
			
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
		
		for (Method method : ((Class<?>)m_instance).getMethods()) {
			if (Modifier.isStatic(method.getModifiers())
					&& Modifier.isPublic(method.getModifiers())
					&& method.getName().equals(name)) {
				methods.add(method);
			}
		}
		
		if (!methods.isEmpty()) {
			LuaValue luaFunction = new StaticMethodInvokingFunction(
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
			luaValue = getMethod(name);
		}
		
		return luaValue;
	}
	
	protected void setup() {
		Class<?> clazz = (Class<?>)m_instance;
		
		cache.put(CLASS_FIELD_NAME, new InstanceUserData(clazz, coercer));
		
		if (clazz.isInterface()) {
			cache.put(IMPLEMENT_NAME, new ClassCreatingFunction(clazz, coercer));
			cache.put(IMPLEMENT_NEW_NAME, new ProxyInstanceCreatingFunction(clazz, coercer));
			cache.put(EXTEND_NAME, new ErrorThrowingFunction(clazz.getSimpleName() + " is an interface and cannot be extended."));
			cache.put(EXTEND_NEW_NAME, new ErrorThrowingFunction(clazz.getSimpleName() + " is an interface and cannot be extended."));
			cache.put(CONSTRUCTOR_NAME, new ErrorThrowingFunction(clazz.getSimpleName() + " is an interface and cannot be instantiated."));
		} else {
			cache.put(IMPLEMENT_NAME, new ErrorThrowingFunction(clazz.getSimpleName() + " is not an interface."));
			cache.put(IMPLEMENT_NEW_NAME, new ErrorThrowingFunction(clazz.getSimpleName() + " is not an interface."));
			
			if (!Modifier.isFinal(clazz.getModifiers())) {
				cache.put(EXTEND_NAME, new ClassCreatingFunction(clazz, coercer));
				cache.put(EXTEND_NEW_NAME, new ProxyInstanceCreatingFunction(clazz, coercer));
			} else {
				cache.put(EXTEND_NAME, new ErrorThrowingFunction(clazz.getSimpleName() + " is marked as final and cannot be extended."));
				cache.put(EXTEND_NEW_NAME, new ErrorThrowingFunction(clazz.getSimpleName() + " is marked as final and cannot be extended."));
			}
			
			if (!Modifier.isAbstract(clazz.getModifiers())) {
				cache.put(CONSTRUCTOR_NAME, new ConstructorInvokingFunction(clazz, Arrays.asList(clazz.getConstructors()), coercer));
			} else {
				cache.put(CONSTRUCTOR_NAME, new ErrorThrowingFunction(clazz.getSimpleName() + " is an abstract class and cannot be instantiated."));
			}
		}
	}
}
