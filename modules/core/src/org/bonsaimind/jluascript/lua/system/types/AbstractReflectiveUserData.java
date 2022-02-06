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
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

// TODO: Auto-generated Javadoc
/**
 * The {@link AbstractReflectiveUserData} is an
 * {@link AbstractInterjectingUserData} which provides all the necessary
 * meachnisms to wrap a Java {@link Class} or an instance thereof.
 */
public abstract class AbstractReflectiveUserData extends AbstractInterjectingUserData {
	/** The {@link Class} that is being bound. */
	protected Class<?> clazz = null;
	/** The {@link Coercer} to use. */
	protected Coercer coercer = null;
	/** The {@link Map} that is being used to cache {@link Field}s. */
	protected Map<String, Field> fieldCache = new HashMap<>();
	/** The wrapped {@link Object instance}. */
	protected Object instance = null;
	
	/**
	 * Creates a new instance of {@link AbstractReflectiveUserData}.
	 *
	 * @param userDataInstance The {@link Object instance} to wrap, cannot be
	 *        {@code null}.
	 * @param instance The {@link Object instance} that is being wrapped, can be
	 *        {@code null} for a static context.
	 * @param clazz The {@link Class} of the wrapped {@link Object}, cannot be
	 *        {@code null}.
	 * @param coercer The {@link Coercer} to use, canot be {@code null}.
	 * @throws IllegalArgumentException If the {@code userDataInstance},
	 *         {@code clazz} or {@code coercer} is {@code null}.
	 */
	protected AbstractReflectiveUserData(Object userDataInstance, Object instance, Class<?> clazz, Coercer coercer) {
		super(Verifier.notNull("userDataInstance", userDataInstance));
		
		Verifier.notNull("clazz", clazz);
		Verifier.notNull("coercer", coercer);
		
		this.instance = instance;
		this.clazz = clazz;
		this.coercer = coercer;
	}
	
	/**
	 * Coerces the given {@link List} of {@link Method}s into an
	 * {@link LuaValue} which can be used to invoke the given {@link Method}s.
	 *
	 * @param methods The {@link List} of {@link Method}s, cannot be
	 *        {@code null} or empty.
	 * @return The {@link LuaValue} used to invoke the given {@link Method}s.
	 */
	protected abstract LuaValue coerceMethodList(List<Method> methods);
	
	/**
	 * Returns whether the given {@link Field} should be available for the Lua
	 * context.
	 *
	 * @param field The {@link Field} to check, cannot be {@code null}.
	 * @return {@code true} if the given {@link Field} should be available for
	 *         the Lua context.
	 */
	protected abstract boolean fieldIsAvailable(Field field);
	
	/**
	 * Finds the (super-)class oder interface from the given {@link Class} which
	 * declares the given {@link Method} and returns the declared instance of
	 * the {@link Method}.
	 *
	 * @param method The {@link Method} to find the declared {@link Method}
	 *        from, cannot be {@code null}.
	 * @param clazz The {@link Class} at which to start the searc, cannot be
	 *        {@code null}.
	 * @return The declared {@link Method}, may be {@code null} if there was an
	 *         reflection exception.
	 * @throws IllegalArgumentException If the given {@code method} or
	 *         {@code clazz} is {@code null}.
	 */
	protected Method findDeclaredMethod(Method method, Class<?> clazz) {
		Verifier.notNull("method", method);
		Verifier.notNull("clazz", clazz);
		
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
	
	/**
	 * Finds the {@link Field} with the given name.
	 * 
	 * @param name The name of the {@link Field} to find, cannot be {@code null}
	 *        or empty.
	 * @return The {@link Field} if found, otherwise {@code null}.
	 * @throws IllegalArgumentException If the given {@code name} is
	 *         {@code null} or empty.
	 */
	protected Field findField(String name) {
		Verifier.notNullOrEmpty("name", name);
		
		for (Field field : clazz.getFields()) {
			if (field.getName().equals(name) && fieldIsAvailable(field)) {
				return field;
			}
		}
		
		return null;
	}
	
	/**
	 * Finds the (super-)class oder interface from the given {@link Class} which
	 * declares the given {@link Method} and returns the declared instance of
	 * the {@link Method}.
	 *
	 * @param method The {@link Method} to find the declared {@link Method}
	 *        from, cannot be {@code null}.
	 * @return The declared {@link Method}, may be {@code null} if there was an
	 *         reflection exception.
	 * @throws IllegalArgumentException If the given {@code method} is
	 *         {@code null}.
	 */
	protected Method getDeclaredMethod(Method method) {
		Verifier.notNull("method", method);
		
		return findDeclaredMethod(method, method.getDeclaringClass());
	}
	
	/**
	 * Gets the value of the field with the given name as {@link LuaValue}.
	 * 
	 * @param name The name of the {@link Field}, cannot be {@code null} or
	 *        empty.
	 * @return The value of the field with the given name as {@link LuaValue},
	 *         {@code null} if there is no field with that name.
	 * @throws IllegalArgumentException If the given {@code name} is
	 *         {@code null} or empty.
	 * @throws LuaError If
	 */
	protected LuaValue getFieldValue(String name) {
		Verifier.notNullOrEmpty("name", name);
		
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
			LuaValue luaValue = coercer.coerceJavaToLua(field.get(instance));
			
			if (Modifier.isFinal(field.getModifiers())) {
				// If the field is final, we can cache the LuaValue, because it
				// points at the same instance. If it is a primitive, the value
				// will not change.
				putIntoCache(name, luaValue);
			}
			
			return luaValue;
		} catch (IllegalAccessException e) {
			throw new LuaError(e);
		}
	}
	
	/**
	 * Gets the method(s) with the given name as invokable {@link LuaValue}.
	 * 
	 * @param name The name of the method(s), cannot be {@code null} or empty.
	 * @return The {@link LuaValue} which can be used to invoke the method(s),
	 *         {@code null} if no methods with that name were found or are
	 *         available.
	 * @throws IllegalArgumentException If the given {@code name} is
	 *         {@code null} or empty.
	 */
	protected LuaValue getMethodsInvokable(String name) {
		Verifier.notNullOrEmpty("name", name);
		
		List<Method> methods = new ArrayList<>();
		
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(name) && methodIsAvailable(method)) {
				methods.add(getDeclaredMethod(method));
			}
		}
		
		if (!methods.isEmpty()) {
			LuaValue luaFunction = coerceMethodList(methods);
			
			putIntoCache(name, luaFunction);
			
			return luaFunction;
		}
		
		return null;
	}
	
	/**
	 * Returns whether the given {@link Method} should be available for the Lua
	 * context.
	 *
	 * @param method The {@link Method} to check, cannot be {@code null}.
	 * @return {@code true} if the given {@link Method} should be available for
	 *         the Lua context.
	 */
	protected abstract boolean methodIsAvailable(Method method);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LuaValue provide(String name) {
		LuaValue luaValue = getFieldValue(name);
		
		if (luaValue == null) {
			return getMethodsInvokable(name);
		}
		
		return luaValue;
	}
}
