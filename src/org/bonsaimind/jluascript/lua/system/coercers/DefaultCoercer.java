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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.bonsaimind.jluascript.lua.functions.ClassCreatingFunction;
import org.bonsaimind.jluascript.lua.functions.ConstructorInvokingFunction;
import org.bonsaimind.jluascript.lua.functions.ErrorThrowingFunction;
import org.bonsaimind.jluascript.lua.functions.ProxyInstanceCreatingFunction;
import org.bonsaimind.jluascript.lua.functions.StaticMethodInvokingFunction;
import org.bonsaimind.jluascript.lua.system.Coercer;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * The default implementation of {@link Coercer}.
 */
public class DefaultCoercer implements Coercer {
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
	public LuaValue coerceJavaToLua(Object object) throws LuaError {
		if (object == null) {
			return LuaValue.NIL;
		}
		
		if (object instanceof LuaValue) {
			return (LuaValue)object;
		}
		
		if (object.getClass().isArray()) {
			return coerceArray((Object[])object);
		}
		
		if (object instanceof Class<?>) {
			return coerceClass((Class<?>)object);
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
		
		if (luaValue.isboolean()) {
			return Boolean.valueOf(luaValue.toboolean());
		}
		
		if (luaValue.isint()) {
			return Integer.valueOf(luaValue.toint());
		}
		
		if (luaValue.islong()) {
			return Long.valueOf(luaValue.tolong());
		}
		
		if (luaValue.isnumber()) {
			return Double.valueOf(luaValue.todouble());
		}
		
		if (luaValue.isstring()) {
			return luaValue.tojstring();
		}
		
		if (luaValue.isuserdata()) {
			return luaValue.touserdata();
		}
		
		throw new LuaError("Could not convert object <" + luaValue + "> to a Java object.");
	}
	
	/**
	 * Adds the "special" methods (implement, implementNew, extend, extendNew)
	 * to the static {@link LuaValue} that represents a static class.
	 * 
	 * @param clazz The {@link Class} to use.
	 * @param staticTable The {@link LuaValue} to add to.
	 */
	protected void addSpecialMethods(Class<?> clazz, LuaTable staticTable) {
		if (clazz.isInterface()) {
			staticTable.set("implement", new ClassCreatingFunction(clazz, this));
			staticTable.set("implementNew", new ProxyInstanceCreatingFunction(clazz, this));
			staticTable.set("extend", new ErrorThrowingFunction(clazz.getSimpleName() + " is an interface and cannot be extended."));
			staticTable.set("extendNew", new ErrorThrowingFunction(clazz.getSimpleName() + " is an interface and cannot be extended."));
			staticTable.set("new", new ErrorThrowingFunction(clazz.getSimpleName() + " is an interface and cannot be instantiated."));
		} else {
			staticTable.set("implement", new ErrorThrowingFunction(clazz.getSimpleName() + " is not an interface."));
			staticTable.set("implementNew", new ErrorThrowingFunction(clazz.getSimpleName() + " is not an interface."));
			
			if (!Modifier.isFinal(clazz.getModifiers())) {
				staticTable.set("extend", new ClassCreatingFunction(clazz, this));
				staticTable.set("extendNew", new ProxyInstanceCreatingFunction(clazz, this));
			} else {
				staticTable.set("extend", new ErrorThrowingFunction(clazz.getSimpleName() + " is marked as final and cannot be extended."));
				staticTable.set("extendNew", new ErrorThrowingFunction(clazz.getSimpleName() + " is marked as final and cannot be extended."));
			}
			
			if (!Modifier.isAbstract(clazz.getModifiers())) {
				staticTable.set("new", new ConstructorInvokingFunction(clazz, this));
			} else {
				staticTable.set("new", new ErrorThrowingFunction(clazz.getSimpleName() + " is an abstract class and cannot be instantiated."));
			}
		}
	}
	
	/**
	 * Coerces the given array to a {@link LuaValue}.
	 * 
	 * @param array The array to coerce.
	 * @return The coerced array.
	 * @throw LuaError If the conversion has failed or is not possible.
	 */
	protected LuaValue coerceArray(Object[] array) throws LuaError {
		LuaTable luaTable = new LuaTable();
		
		for (int index = 0; index < array.length; index++) {
			luaTable.set(index + 1, coerceJavaToLua(array[index]));
		}
		
		return luaTable;
	}
	
	/**
	 * Coerces the given {@link Class} as {@link LuaValue}.
	 * 
	 * @param clazz The {@link Class} to coerce.
	 * @return The coerced {@link Class}.
	 * @throw LuaError If the conversion has failed or is not possible.
	 */
	protected LuaValue coerceClass(Class<?> clazz) throws LuaError {
		LuaTable staticTable = new LuaTable();
		staticTable.set("class", CoerceJavaToLua.coerce(clazz));
		
		coerceStaticFields(clazz, staticTable);
		coerceStaticMethods(clazz, staticTable);
		addSpecialMethods(clazz, staticTable);
		
		return staticTable;
	}
	
	/**
	 * Coerces the given {@link Object} as {@link LuaValue}.
	 * 
	 * @param object The {@link Object} to coerce.
	 * @return The coerced {@link LuaValue}.
	 * @throw LuaError If the conversion has failed or is not possible.
	 */
	protected LuaValue coerceInstance(Object object) throws LuaError {
		return CoerceJavaToLua.coerce(object);
	}
	
	/**
	 * Coerces all static fields of the given {@link Class} to the given
	 * {@link LuaValue} representing a static class.
	 * 
	 * @param clazz The {@link Class} to use.
	 * @param staticTable The {@link LuaValue} to add to.
	 */
	protected void coerceStaticFields(Class<?> clazz, LuaTable staticTable) {
		for (Field field : clazz.getFields()) {
			if (Modifier.isStatic(field.getModifiers())
					&& Modifier.isPublic(field.getModifiers())) {
				try {
					staticTable.set(field.getName(), coerceJavaToLua(field.get(null)));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// Ignore possible errors, as they should not happen.
				}
			}
		}
	}
	
	/**
	 * Coerces all static methods of the given {@link Class} to the given
	 * {@link LuaValue} representing a static class.
	 * 
	 * @param clazz The {@link Class} to use.
	 * @param staticTable The {@link LuaValue} to add to.
	 */
	protected void coerceStaticMethods(Class<?> clazz, LuaTable staticTable) {
		for (Method method : clazz.getMethods()) {
			if (Modifier.isStatic(method.getModifiers())
					&& Modifier.isPublic(method.getModifiers())) {
				if (staticTable.get(method.getName()).isnil()) {
					staticTable.set(method.getName(), new StaticMethodInvokingFunction(clazz, method.getName(), this));
				}
			}
		}
	}
}
