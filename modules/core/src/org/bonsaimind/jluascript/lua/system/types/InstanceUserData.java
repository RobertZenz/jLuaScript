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
import java.util.List;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.lua.system.types.functions.InstanceMethodInvokingFunction;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaValue;

/**
 * The {@link InstanceUserData} is an {@link AbstractReflectiveUserData}
 * extension which makes the instance context of an {@link Object} accessible
 * through Lua.
 */
public class InstanceUserData extends AbstractReflectiveUserData {
	/**
	 * Creates a new instance of {@link StaticUserData}.
	 *
	 * @param object The {@link Object}, cannot be {@code null}.
	 * @param coercer The {@link Coercer}, cannot be {@code null}.
	 * @throws IllegalArgumentException If the {@code obejct} or the
	 *         {@code coercer} is {@code null}.
	 */
	public InstanceUserData(Object object, Coercer coercer) {
		super(Verifier.notNull("object", object), object, object.getClass(), coercer);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LuaValue coerceMethodList(List<Method> methods) {
		return new InstanceMethodInvokingFunction(methods, coercer);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean fieldIsAvailable(Field field) {
		return !Modifier.isStatic(field.getModifiers())
				&& Modifier.isPublic(field.getModifiers());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean methodIsAvailable(Method method) {
		return !Modifier.isStatic(method.getModifiers())
				&& Modifier.isPublic(method.getModifiers());
	}
}
