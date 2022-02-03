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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import org.bonsaimind.jluascript.lua.libs.functions.ErrorThrowingFunction;
import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.lua.system.types.functions.ClassCreatingFunction;
import org.bonsaimind.jluascript.lua.system.types.functions.ConstructorInvokingFunction;
import org.bonsaimind.jluascript.lua.system.types.functions.ProxyInstanceCreatingFunction;
import org.bonsaimind.jluascript.lua.system.types.functions.StaticMethodInvokingFunction;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaValue;

/**
 * The {@link StaticUserData} is an {@link AbstractReflectiveUserData} extension
 * which makes the static context of a {@link Class} accessible through Lua.
 */
public class StaticUserData extends AbstractReflectiveUserData {
	/** The key of the field which holds the class. */
	public static final String CLASS_FIELD_NAME = "class";
	/** The key of the field for the constructor. */
	public static final String CONSTRUCTOR_NAME = "new";
	/** The key of the field for the extend method. */
	public static final String EXTEND_NAME = "extend";
	/** The key of the field for the extend and instantiate method. */
	public static final String EXTEND_NEW_NAME = "extendNew";
	/** The key of the field for the implement method. */
	public static final String IMPLEMENT_NAME = "implement";
	/** The key of the field for the implement and instantiate method. */
	public static final String IMPLEMENT_NEW_NAME = "implementNew";
	
	/**
	 * Creates a new instance of {@link StaticUserData}.
	 *
	 * @param clazz The {@link Class}, cannot be {@code null}.
	 * @param coercer The {@link Coercer}, cannot be {@code null}.
	 * @throws IllegalArgumentException If the {@code clazz} or the
	 *         {@code coercer} is {@code null}.
	 */
	public StaticUserData(Class<?> clazz, Coercer coercer) {
		super(clazz, null, Verifier.notNull("clazz", clazz), Verifier.notNull("coercer", coercer));
		
		setup();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LuaValue coerceMethodList(List<Method> methods) {
		return new StaticMethodInvokingFunction(methods, coercer);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean fieldIsAvailable(Field field) {
		return Modifier.isStatic(field.getModifiers())
				&& Modifier.isPublic(field.getModifiers());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean methodIsAvailable(Method method) {
		return Modifier.isStatic(method.getModifiers())
				&& Modifier.isPublic(method.getModifiers());
	}
	
	/**
	 * Sets this static context up.
	 */
	protected void setup() {
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
				Constructor<?>[] constructors = clazz.getConstructors();
				
				if (constructors.length > 0) {
					cache.put(CONSTRUCTOR_NAME, new ConstructorInvokingFunction(Arrays.asList(constructors), coercer));
				} else {
					cache.put(CONSTRUCTOR_NAME, new ErrorThrowingFunction(clazz.getSimpleName() + " does not have any public constructors."));
				}
			} else {
				cache.put(CONSTRUCTOR_NAME, new ErrorThrowingFunction(clazz.getSimpleName() + " is an abstract class and cannot be instantiated."));
			}
		}
	}
}
