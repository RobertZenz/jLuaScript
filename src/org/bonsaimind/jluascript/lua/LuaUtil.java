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

package org.bonsaimind.jluascript.lua;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bonsaimind.jluascript.lua.functions.ClassCreatingFunction;
import org.bonsaimind.jluascript.lua.functions.ConstructorInvokingFunction;
import org.bonsaimind.jluascript.lua.functions.ErrorThrowingFunction;
import org.bonsaimind.jluascript.lua.functions.ProxyInstanceCreatingFunction;
import org.bonsaimind.jluascript.lua.functions.StaticMethodInvokingFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * The {@link LuaUtil} provides various helper methods for interacting with
 * LuaJ.
 */
public final class LuaUtil {
	/**
	 * No instancing allowed.
	 */
	private LuaUtil() {
		// No instancing allowed.
	}
	
	/**
	 * Only adds the coerced {@link Class} to the given {@link LuaValue
	 * environment}.
	 * 
	 * @param environment The {@link LuaValue environment} to which to add the
	 *        coerced {@link Class}.
	 * @param clazz The {@link Class} which has been coerced.
	 * @param coercedStaticInstance The coerced {@link Class} to add.
	 */
	public static final void addStaticInstanceDirect(LuaValue environment, Class<?> clazz, LuaValue coercedStaticInstance) {
		environment.set(clazz.getSimpleName(), coercedStaticInstance);
	}
	
	/**
	 * Adds the coerced {@link Class} to the given {@link LuaValue environment}
	 * through its package path.
	 * 
	 * @param environment The {@link LuaValue environment} to which to add the
	 *        coerced {@link Class}.
	 * @param clazz The {@link Class} which has been coerced.
	 * @param coercedStaticInstance The coerced {@link Class} to add.
	 */
	public static final void addStaticInstancePackage(LuaValue environment, Class<?> clazz, LuaValue coercedStaticInstance) {
		LuaValue previousPackageTable = environment;
		
		for (String packagePart : clazz.getPackage().getName().split("\\.")) {
			LuaValue packageTable = previousPackageTable.get(packagePart);
			
			if (packageTable.isnil()) {
				packageTable = new LuaTable();
				previousPackageTable.set(packagePart, packageTable);
			}
			
			previousPackageTable = packageTable;
		}
		
		previousPackageTable.set(clazz.getSimpleName(), coercedStaticInstance);
	}
	
	/**
	 * Coerces the given {@link Object} array into an array of
	 * {@link LuaValue}s.
	 * 
	 * @param objects The {@link Object}s to coerce.
	 * @return The array of corresponding {@link LuaValue}s, {@code null} if
	 *         {@code objects} is {@code null}..
	 */
	public final static LuaValue[] coerce(Object[] objects) {
		if (objects == null) {
			return null;
		}
		
		LuaValue[] luaValues = new LuaValue[objects.length];
		
		for (int index = 0; index < objects.length; index++) {
			luaValues[index] = LuaUtil.coerceAsLuaValue(objects[index]);
		}
		
		return luaValues;
	}
	
	/**
	 * Coerces the given {@link LuaValue} as a Java Object.
	 * <p>
	 * This will test if the given {@link LuaValue} is one of the primitive
	 * classes and return it as such, additionally the {@link LuaValue} will be
	 * tested if it can be mapped to a Java class, and if yes, is returned as
	 * such. If the given {@link LuaValue} is {@code null} or
	 * {@code LuaValue#NIL} {@code null} is returned.
	 * 
	 * @param luaValue The {@link LuaValue} to coerce.
	 * @return The coerced Object.
	 */
	public final static Object coerceAsJavaObject(LuaValue luaValue) {
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
		
		return null;
	}
	
	/**
	 * Coerces the given {@link Object} as {@link LuaValue}.
	 * 
	 * @param object The {@link Object} to coerce.
	 * @return The coerced {@link LuaValue}.
	 */
	public final static LuaValue coerceAsLuaValue(Object object) {
		if (object == null) {
			return LuaValue.NIL;
		}
		
		if (object instanceof LuaValue) {
			return (LuaValue)object;
		}
		
		return CoerceJavaToLua.coerce(object);
	}
	
	/**
	 * Coerces the given {@link LuaValue} to a {@link Path}.
	 * <p>
	 * This includes converting a {@link String} or {@link File} and correctly
	 * handling {@code null} and {@code LuaValue#NIL}.
	 * 
	 * @param value The {@link LuaValue} to coerce.
	 * @return The value coerced as {@link Path}.
	 */
	public final static Path coerceAsPath(LuaValue value) {
		if (value == null || value.isnil()) {
			return null;
		}
		
		Object arg = LuaUtil.coerceAsJavaObject(value);
		
		if (arg instanceof String) {
			return Paths.get((String)arg);
		} else if (arg instanceof File) {
			return ((File)arg).toPath();
		} else if (arg instanceof Path) {
			return (Path)arg;
		}
		
		return null;
	}
	
	/**
	 * Coerces the given {@link Class} into a {@link LuaValue} representing that
	 * {@link Class}.
	 * <p>
	 * That means that all (public) static fields and methods will be added and
	 * also the special {@code new} method for creating a new instance.
	 * 
	 * @param clazz The {@link Class} to coerce, canot be {@code null}.
	 * @return The coerced {@link Class}.
	 * @throws IllegalArgumentException If the given {@link Class} is
	 *         {@code null}.
	 */
	public final static LuaValue coerceStaticIstance(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("<clazz> cannot be null.");
		}
		
		LuaTable staticTable = new LuaTable();
		staticTable.set("class", LuaUtil.coerceAsLuaValue(clazz));
		
		coerceStaticFields(clazz, staticTable);
		coerceStaticMethods(clazz, staticTable);
		addSpecialMethods(clazz, staticTable);
		
		return staticTable;
	}
	
	/**
	 * Adds the "special" methods (implement, implementNew, extend, extendNew)
	 * to the static {@link LuaValue} that represents a static class.
	 * 
	 * @param clazz The {@link Class} to use.
	 * @param staticTable The {@link LuaValue} to add to.
	 */
	private static final void addSpecialMethods(Class<?> clazz, LuaTable staticTable) {
		if (clazz.isInterface()) {
			staticTable.set("implement", new ClassCreatingFunction(clazz));
			staticTable.set("implementNew", new ProxyInstanceCreatingFunction(clazz));
			staticTable.set("extend", new ErrorThrowingFunction(clazz.getSimpleName() + " is an interface and cannot be extended."));
			staticTable.set("extendNew", new ErrorThrowingFunction(clazz.getSimpleName() + " is an interface and cannot be extended."));
			staticTable.set("new", new ErrorThrowingFunction(clazz.getSimpleName() + " is an interface and cannot be instantiated."));
		} else {
			staticTable.set("implement", new ErrorThrowingFunction(clazz.getSimpleName() + " is not an interface."));
			staticTable.set("implementNew", new ErrorThrowingFunction(clazz.getSimpleName() + " is not an interface."));
			
			if (!Modifier.isFinal(clazz.getModifiers())) {
				staticTable.set("extend", new ClassCreatingFunction(clazz));
				staticTable.set("extendNew", new ProxyInstanceCreatingFunction(clazz));
			} else {
				staticTable.set("extend", new ErrorThrowingFunction(clazz.getSimpleName() + " is marked as final and cannot be extended."));
				staticTable.set("extendNew", new ErrorThrowingFunction(clazz.getSimpleName() + " is marked as final and cannot be extended."));
			}
			
			if (!Modifier.isAbstract(clazz.getModifiers())) {
				staticTable.set("new", new ConstructorInvokingFunction(clazz));
			} else {
				staticTable.set("new", new ErrorThrowingFunction(clazz.getSimpleName() + " is an abstract class and cannot be instantiated."));
			}
		}
	}
	
	/**
	 * Coerces all static fields of the given {@link Class} to the given
	 * {@link LuaValue} representing a static class.
	 * 
	 * @param clazz The {@link Class} to use.
	 * @param staticTable The {@link LuaValue} to add to.
	 */
	private static final void coerceStaticFields(Class<?> clazz, LuaTable staticTable) {
		for (Field field : clazz.getFields()) {
			if (Modifier.isStatic(field.getModifiers())
					&& Modifier.isPublic(field.getModifiers())) {
				try {
					staticTable.set(field.getName(), LuaUtil.coerceAsLuaValue(field.get(null)));
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
	private static final void coerceStaticMethods(Class<?> clazz, LuaTable staticTable) {
		for (Method method : clazz.getMethods()) {
			if (Modifier.isStatic(method.getModifiers())
					&& Modifier.isPublic(method.getModifiers())) {
				if (staticTable.get(method.getName()).isnil()) {
					staticTable.set(method.getName(), new StaticMethodInvokingFunction(clazz, method.getName()));
				}
			}
		}
	}
}
